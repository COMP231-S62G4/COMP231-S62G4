package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import comp231.g4.wemeet.helpers.ContactFetcher;
import comp231.g4.wemeet.helpers.RegisteredContactsDataSource;
import comp231.g4.wemeet.helpers.SharedLocationDataSource;
import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.model.ContactPhone;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.MailTo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

public class WeMeetService extends Service implements LocationListener {
	private Notification notification;
	private static final int NOTIFICATION_ID = 101;
	public static final String KEY_LAST_SYNC = "LAST_SYNC_TIME";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent i = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);

		// Build notification
		// Actions are just fake
		notification = new Notification.Builder(this)
				.setContentTitle("WeMeet Service")
				.setContentText("Service started.")
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// notificationManager.notify(NOTIFICATION_ID, notification);

		if (!syncedToday()) {
			syncContacts(); // syncing contacts
		}

		setLocationListener(); // updating location on server

		return super.onStartCommand(intent, flags, startId);

	}

	// method to set location listener
	private void setLocationListener() {
		LocationManager lManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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

	private void syncContacts() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// getting shared preferences
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(WeMeetService.this
								.getApplicationContext());

				Editor editor = prefs.edit();

				// list to hold contacts
				Looper.prepare();
				ArrayList<Contact> contacts = new ContactFetcher(
						WeMeetService.this).fetchAll();

				Log.e("WeMeet_Exception", String.valueOf(contacts.size()));

				// creating instance of RegisteredContactsDataSource
				RegisteredContactsDataSource dsRegisteredContacts = new RegisteredContactsDataSource(
						WeMeetService.this);

				// creating instance of client
				AndroidClient client = new AndroidClient();

				// sync contacts
				SyncContacts(contacts, dsRegisteredContacts, client);

				// sync shared location list
				SyncSharedLocationList(client);

				editor.putString(KEY_LAST_SYNC, new Date().toString());
				editor.commit();

				notification = new Notification.Builder(WeMeetService.this)
						.setContentTitle("WeMeet Service")
						.setContentText("Contacts synced.")
						.setSmallIcon(R.drawable.ic_launcher).build();

				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				// hide the notification after its selected
				notification.flags |= Notification.FLAG_AUTO_CANCEL;

				// notificationManager.notify(NOTIFICATION_ID, notification);
			}

			private void SyncSharedLocationList(AndroidClient client) {

				String phoneNumber = PreferenceManager
						.getDefaultSharedPreferences(
								WeMeetService.this.getApplicationContext())
						.getString(MainActivity.KEY_PHONE_NUMBER, "16472787694");
				phoneNumber = ValidationHelper.SanitizePhoneNumber(phoneNumber);

				try {
					String sharedLocationList = client
							.getSharedLocationList(phoneNumber);

					SharedLocationDataSource dsSharedLocation = new SharedLocationDataSource(
							WeMeetService.this);
					dsSharedLocation.open();

					ContactFetcher fetcher = new ContactFetcher(
							WeMeetService.this);

					String[] sharedLocationListItem = sharedLocationList
							.split(",");
					for (int i = 0; i < sharedLocationListItem.length; i++) {
						if (sharedLocationListItem[i].length() > 2) {
							Contact contact = fetcher
									.GetContactDetails(sharedLocationListItem[i]);
							dsSharedLocation.addContact(contact);
						}
					}

					dsSharedLocation.close();
				} catch (Exception e) {
					Log.e("WeMeet_Exception", e.getMessage());
				}
			}

			private void SyncContacts(ArrayList<Contact> contacts,
					RegisteredContactsDataSource dsRegisteredContacts,
					AndroidClient client) {
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
			}
		});

		thread.start();

	}

	@SuppressWarnings("deprecation")
	private boolean syncedToday() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(WeMeetService.this
						.getApplicationContext());
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
	}

	@Override
	public void onLocationChanged(final Location location) {
		UpdateLocation(location);

		// ShowLocUpdateNotification(location);
	}

	private void ShowLocUpdateNotification(final Location location) {
		Intent i = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);

		// Build notification
		// Actions are just fake
		notification = new Notification.Builder(this)
				.setContentTitle("WeMeet Service")
				.setContentText(
						String.valueOf(location.getLatitude()) + " "
								+ String.valueOf(location.getLongitude()))
				.setContentIntent(pIntent).setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(pIntent).build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	private void UpdateLocation(final Location location) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				AndroidClient client = new AndroidClient();
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(WeMeetService.this
								.getApplicationContext());
				try {
					if (prefs.getBoolean(MainActivity.KEY_IS_REGISTERED, false)) {
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

}
