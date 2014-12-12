package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.model.NearbyContact;
import comp231.g4.wemeet.servicehelper.AndroidClient;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FriendsNearByFragment extends Fragment implements
		OnInfoWindowClickListener, OnMyLocationChangeListener {
	// Google Map
	private GoogleMap googleMap;
	private MapFragment mapFragment;
	private Timer updateTimer;
	private final static int UPDATE_DELAY = 1000 * 60;// updating at every

	private MenuItem miLoading;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map));
		View v;
		if (mapFragment == null) {
			v = inflater.inflate(R.layout.activity_friends_nearby, null);
		} else {
			v = mapFragment.getView().getRootView();
		}
		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (!getActivity().isFinishing() && mapFragment != null) {
			getFragmentManager().beginTransaction().remove(mapFragment)
					.commit();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		try {
			// initializing google map
			mapFragment = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map));
			googleMap = mapFragment.getMap();
			googleMap.setOnInfoWindowClickListener(this);

			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			googleMap.getUiSettings().setZoomGesturesEnabled(true);

			googleMap.setOnMyLocationChangeListener(this);

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getActivity(), "Sorry! unable to create maps",
						Toast.LENGTH_SHORT).show();
			}

			setHasOptionsMenu(true);

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("WeMeetException", e.getMessage());
		}
	}

	public MarkerOptions GetContactDetails(String phoneNumber, double distance,
			LatLng location) {
		MarkerOptions marker = new MarkerOptions();
		marker.position(location);

		String[] columns = { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.PHOTO_URI };

		Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));

		Cursor cursor = getActivity().getContentResolver().query(contactUri,
				columns, null, null, null);
		if (cursor == null) {
			return null;
		}
		try {
			int ColumeIndex_DISPLAY_NAME = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			int ColumeIndex_PHOTO_THUMBNAIL_URI = cursor
					.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

			if (cursor.moveToFirst()) {
				String name = cursor.getString(ColumeIndex_DISPLAY_NAME);
				String photo_uri = cursor
						.getString(ColumeIndex_PHOTO_THUMBNAIL_URI);

				marker.title(name + " - "
						+ ValidationHelper.RoundTwoDecimals(distance) + " km");
				Bitmap bitmap;

				if (photo_uri != null) {
					bitmap = MediaStore.Images.Media.getBitmap(getActivity()
							.getContentResolver(), Uri.parse(photo_uri));
					bitmap = Bitmap.createScaledBitmap(bitmap, 48, 48, true);
				} else {
					bitmap = BitmapFactory.decodeResource(getResources(),
							R.drawable.photo);
				}

				marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
			}

		} catch (Exception ex) {
			Log.e("WeMeet_Exception", ex.getMessage());
		} finally {
			cursor.close();
		}
		return marker;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (updateTimer != null) {
			updateTimer.cancel();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// creating menu item
		miLoading = menu.add("Loading");
		miLoading.setIcon(R.drawable.spinner);
		miLoading.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		miLoading.setVisible(false);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		Toast.makeText(getActivity(), arg0.getTitle(), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onMyLocationChange(Location location) {
		LatLng coordinate = new LatLng(location.getLatitude(),
				location.getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12));
		googleMap.setOnMyLocationChangeListener(null);
	}

	@Override
	public void onResume() {
		super.onResume();

		getActivity().setTitle("Friends Nearby");
		getActivity().getActionBar().setIcon(R.drawable.ic_friends_nearby);

		updateTimer = new Timer();

		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (googleMap != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {

						@Override
						public void run() {
							if (miLoading != null) {
								miLoading.setVisible(true);
							}
							googleMap.clear();// clearing map

						}
					});
					try {
						List<NearbyContact> contacts = new ArrayList<NearbyContact>();

						AndroidClient client = new AndroidClient();

						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(getActivity()
										.getApplicationContext());

						JSONArray data = client.GetFriendsNearBy(prefs
								.getString(MainActivity.KEY_PHONE_NUMBER, ""));

						for (int i = 0; i < data.length(); i++) {

							try {
								JSONObject individualData = new JSONObject(data
										.get(i).toString());

								double distance = Double
										.parseDouble(individualData
												.getString("Distance"));
								JSONObject location = individualData
										.getJSONObject("Location");
								String phoneNumber = individualData
										.getString("PhoneNumber");

								LatLng iLocation = new LatLng(
										location.getDouble("Latitude"),
										location.getDouble("Longitude"));
								String lastSeen = location.getString("Date");

								contacts.add(new NearbyContact(phoneNumber,
										iLocation, distance, lastSeen));

							} catch (Exception e) {
							}
						}
						if (contacts.size() == 0) {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(getActivity(),
											"No one is nearby.",
											Toast.LENGTH_LONG).show();
									if (miLoading != null) {
										miLoading.setVisible(false);
									}
								}
							});

						}
						for (int i = 0; i < contacts.size(); i++) {

							NearbyContact currentContact = contacts.get(i);

							final MarkerOptions marker = GetContactDetails(
									currentContact.phoneNumber,
									currentContact.distance,
									currentContact.location);

							Handler handler = new Handler(
									Looper.getMainLooper());
							handler.post(new Runnable() {

								@Override
								public void run() {
									if (miLoading != null) {
										miLoading.setVisible(false);
									}
									googleMap.addMarker(marker);
								}
							});

						}
					} catch (Exception e) {
						Log.e("WeMeet_Exception", "");
					}
				}
			}
		};

		updateTimer.schedule(task, 0, UPDATE_DELAY);
	}
}
