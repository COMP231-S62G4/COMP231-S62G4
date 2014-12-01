package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import comp231.g4.wemeet.helpers.ContactFetcher;
import comp231.g4.wemeet.helpers.NearbyContactsDataSource;
import comp231.g4.wemeet.helpers.RegisteredContactsDataSource;
import comp231.g4.wemeet.helpers.SharedLocationDataSource;
import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.model.ContactPhone;
import comp231.g4.wemeet.model.NearbyContact;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

public class WeMeetService extends Service implements LocationListener {
	private Notification notification;
	private LocationManager lManager;
	private static final int NOTIFICATION_ID = 101;
	public static final String KEY_LAST_SYNC = "LAST_SYNC_TIME";
	private static final String NOTIFICATION_TITLE = "WeMeet";
	private SharedPreferences prefs;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		prefs = PreferenceManager
				.getDefaultSharedPreferences(WeMeetService.this
						.getApplicationContext());

		// showing notification
		showNotification("Service started.");

		// creating instance of client
		AndroidClient client = new AndroidClient();

		if (isNetworkAvailable() && !syncedToday()) {

			syncContacts(client); // syncing contacts

		}
		// sync shared location list
		syncSharedLocationList(client);

		setLocationListener(); // updating location on server

		// updating near by contacts
		syncNearbyContacts();
		return super.onStartCommand(intent, flags, startId);
	}

	private void syncNearbyContacts() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					AndroidClient client = new AndroidClient();

					JSONArray data = client.GetFriendsNearBy(prefs.getString(
							MainActivity.KEY_PHONE_NUMBER, ""));

					NearbyContactsDataSource dsNearbyContacts = new NearbyContactsDataSource(
							WeMeetService.this);

					// removing old list from the table
					dsNearbyContacts.open();
					dsNearbyContacts.deleteAll();
					dsNearbyContacts.close();

					for (int i = 0; i < data.length(); i++) {

						try {
							JSONObject individualData = new JSONObject(data
									.get(i).toString());

							double distance = Double.parseDouble(individualData
									.getString("Distance"));
							JSONObject location = individualData
									.getJSONObject("Location");
							String phoneNumber = individualData
									.getString("PhoneNumber");

							LatLng iLocation = new LatLng(location
									.getDouble("Latitude"), location
									.getDouble("Longitude"));
							String lastSeen = location.getString("Date");

							NearbyContact contact = new NearbyContact(
									phoneNumber, iLocation, distance, lastSeen);

							dsNearbyContacts.open();
							dsNearbyContacts.addNearbyContact(contact);
							dsNearbyContacts.close();

						} catch (Exception e) {
							dsNearbyContacts.close(); // closing database
						}
					}

				} catch (JSONException e) {
					Log.e("WeMeet_Exception", "");

				} catch (Exception e) {
					Log.e("WeMeet_Exception", "");

				}
			}
		});

		t.start();
	}

	// method to set location listener
	private void setLocationListener() {
		lManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				(long) 600000, 200f, WeMeetService.this);
		lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				(long) 600000, 200f, WeMeetService.this);

		Location location = lManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			UpdateLocation(location);
		} else {
			location = lManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				UpdateLocation(location);
			}
		}

	}

	private void syncContacts(final AndroidClient client) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// getting shared preferences
				Editor editor = prefs.edit();

				// list to hold contacts
				Looper.prepare();
				ArrayList<Contact> contacts = new ContactFetcher(
						WeMeetService.this).fetchAll();

				Log.e("WeMeet_Exception", String.valueOf(contacts.size()));

				// creating instance of RegisteredContactsDataSource
				RegisteredContactsDataSource dsRegisteredContacts = new RegisteredContactsDataSource(
						WeMeetService.this);

				// removing all old contacts
				dsRegisteredContacts.open();
				dsRegisteredContacts.deleteAll();
				dsRegisteredContacts.close();

				// iterating through all contacts
				for (int index = 0; index < contacts.size(); index++) {
					try {
						Contact contact = contacts.get(index);

						// iterating through all contact numbers for current
						// contact
						ArrayList<ContactPhone> phoneNumbers = new ArrayList<ContactPhone>(
								contact.numbers);

						// removing all numbers from contact
						contact.numbers.clear();

						for (ContactPhone phoneNumber : phoneNumbers) {

							// Log.e("WeMeet_Exception", contact.name +
							// " "+phoneNumber.number);

							if (client.IsRegisteredPhoneNumber(ValidationHelper
									.SanitizePhoneNumber(phoneNumber.number))) {
								contact.numbers.add(new ContactPhone(
										phoneNumber.number, phoneNumber.type));
							}
						}

						if (contact.numbers.size() > 0) {
							dsRegisteredContacts.open();
							dsRegisteredContacts.addContact(contact);
							dsRegisteredContacts.close();
						}

					} catch (Exception e) {
						Log.e("WeMeet_Exception", e.getMessage());
					}
				}

				editor.putString(KEY_LAST_SYNC, new Date().toString());
				editor.commit();
			}
		});

		thread.start();

	}

	private void showNotification(String message) {
		if (prefs.getBoolean(SettingsFragment.KEY_NOTIFICATION, true)) {
			Intent i = new Intent(this, MainActivity.class);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);

			notification = new Notification.Builder(WeMeetService.this)
					.setContentTitle(NOTIFICATION_TITLE)
					.setContentText(message)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(pIntent).build();

						NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// hide the notification after its selected
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notificationManager.notify(NOTIFICATION_ID, notification);
		}
	}

	private void syncSharedLocationList(final AndroidClient client) {

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				if (isNetworkAvailable()) {

					String phoneNumber = prefs.getString(
							MainActivity.KEY_PHONE_NUMBER, "");
					phoneNumber = ValidationHelper
							.SanitizePhoneNumber(phoneNumber);

					try {
						String sharedLocationList = client
								.getSharedLocationList(phoneNumber);

						SharedLocationDataSource dsSharedLocation = new SharedLocationDataSource(
								WeMeetService.this);
						dsSharedLocation.open();

						// removing all old contacts
						dsSharedLocation.deleteAll();

						ContactFetcher fetcher = new ContactFetcher(
								WeMeetService.this);

						String[] sharedLocationListItem = sharedLocationList
								.split(",");
						for (int i = 0; i < sharedLocationListItem.length; i++) {
							if (sharedLocationListItem[i].length() > 2) {
								Contact contact = fetcher
										.GetContactDetails(sharedLocationListItem[i]);
								dsSharedLocation.open();
								dsSharedLocation.addContact(contact);
							}
						}

						dsSharedLocation.close();
					} catch (Exception e) {
						Log.e("WeMeet_Exception", e.getMessage());
					}
				}
			}
		});

		t.start();// starting thread
	}

	@SuppressWarnings("deprecation")
	private boolean syncedToday() {
		if (prefs.contains(KEY_LAST_SYNC)) {
			Date current = new Date();

			Date synced = new Date(prefs.getString(KEY_LAST_SYNC,
					current.toString()));

			if (current.getYear() - synced.getYear() > 0
					|| current.getMonth() - synced.getMonth() > 0
					|| current.getDay() - synced.getDay() > 0
					|| current.getHours() - synced.getHours() > 1) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Intent i = new Intent(getApplicationContext(), WeMeetService.class);
		PendingIntent pi = PendingIntent.getService(getApplicationContext(),
				1111, i, PendingIntent.FLAG_ONE_SHOT);

		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.MINUTE, 30);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(),
				pi);

		if (lManager != null) {
			lManager.removeUpdates(this);
		}
	}

	@Override
	public void onLocationChanged(final Location location) {
		UpdateLocation(location);
	}

	/*
	 * private void ShowLocUpdateNotification(final Location location) { Intent
	 * i = new Intent(this, MainActivity.class); PendingIntent pIntent =
	 * PendingIntent.getActivity(this, 0, i, 0);
	 * 
	 * // Build notification // Actions are just fake notification = new
	 * Notification.Builder(this) .setContentTitle("WeMeet Service")
	 * .setContentText( String.valueOf(location.getLatitude()) + " " +
	 * String.valueOf(location.getLongitude()))
	 * .setContentIntent(pIntent).setSmallIcon(R.drawable.ic_launcher)
	 * .setContentIntent(pIntent).build(); NotificationManager
	 * notificationManager = (NotificationManager)
	 * getSystemService(NOTIFICATION_SERVICE); // hide the notification after
	 * its selected notification.flags |= Notification.FLAG_AUTO_CANCEL;
	 * 
	 * notificationManager.notify(NOTIFICATION_ID, notification); }
	 */

	private void UpdateLocation(final Location location) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				AndroidClient client = new AndroidClient();
				try {
					if (isNetworkAvailable()
							&& prefs.getBoolean(MainActivity.KEY_IS_REGISTERED,
									false)) {
						client.UpdateLocation(prefs.getString(
								MainActivity.KEY_PHONE_NUMBER, ""), String
								.valueOf(location.getLatitude()), String
								.valueOf(location.getLongitude()));
					}

				} catch (Exception e) {
					Log.e("Location_Exception", e.getMessage());
				}
			}
		});

		thread.start();
	}

	// method to check Internet availability
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
