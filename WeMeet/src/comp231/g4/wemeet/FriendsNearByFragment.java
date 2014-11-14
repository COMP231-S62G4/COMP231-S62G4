package comp231.g4.wemeet;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FriendsNearByFragment extends Fragment implements
		OnMarkerClickListener, OnInfoWindowClickListener {
	// Google Map
	private GoogleMap googleMap;
	private Timer updateTimer;
	private final static int UPDATE_DELAY = 1000 * 60;// updating at every 3
														// minutes

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_friends_nearby, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		try {

			updateTimer = new Timer();
			updateTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (googleMap != null) {
						new Handler(Looper.getMainLooper())
								.post(new Runnable() {

									@Override
									public void run() {
										googleMap.clear();// clearing map
									}
								});
						try {
							AndroidClient client = new AndroidClient();
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(getActivity().getApplicationContext());
							JSONArray data = client.GetFriendsNearBy(prefs
									.getString(MainActivity.KEY_PHONE_NUMBER,
											"16472787694"));

							for (int i = 0; i < data.length(); i++) {

								JSONObject individualData = new JSONObject(data
										.get(i).toString());

								String distance = individualData
										.getString("Distance");
								JSONObject location = individualData
										.getJSONObject("Location");
								String phoneNumber = individualData
										.getString("PhoneNumber");

								LatLng iLocation = new LatLng(location
										.getDouble("Latitude"), location
										.getDouble("Longitude"));

								final MarkerOptions marker = GetContactDetails(
										phoneNumber,
										Double.parseDouble(distance), iLocation);

								Handler handler = new Handler(Looper
										.getMainLooper());
								handler.post(new Runnable() {

									@Override
									public void run() {
										googleMap.addMarker(marker);
										Log.e("Marker adder", marker.getTitle());
									}
								});

							}
						} catch (JSONException e) {
							Log.e("WeMeet_Exception", "");

						}catch (Exception e) {
							Log.e("WeMeet_Exception", "");
						}
					}
				}
			}, UPDATE_DELAY / 5, UPDATE_DELAY);

			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
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

				marker.title(name + " - " + roundTwoDecimals(distance) + " km");
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

	public double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			googleMap.getUiSettings().setZoomGesturesEnabled(true);

			Location location = googleMap.getMyLocation();
			LatLng coordinate = new LatLng(location.getLatitude(),
					location.getLongitude());
			CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(
					coordinate, 5);
			googleMap.animateCamera(myLocation);

			googleMap.setOnMarkerClickListener(this);
			googleMap.setOnInfoWindowClickListener(this);

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getActivity(), "Sorry! unable to create maps",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(updateTimer!=null){
			updateTimer.cancel();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		Toast.makeText(getActivity(), arg0.getTitle(), Toast.LENGTH_SHORT)
				.show();
		return true;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		Toast.makeText(getActivity(), arg0.getTitle(), Toast.LENGTH_SHORT)
				.show();
	}
}
