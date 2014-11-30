package comp231.g4.wemeet;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import comp231.g4.wemeet.helpers.NearbyContactsDataSource;
import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.model.NearbyContact;
import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FriendsNearByFragment extends Fragment implements
		OnInfoWindowClickListener, OnMyLocationChangeListener {
	// Google Map
	private static GoogleMap googleMap;
	private Timer updateTimer;
	private final static int UPDATE_DELAY = 1000 * 60;// updating at every
														// minute
	private static View view;
	private boolean isFirst = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.activity_friends_nearby, null);
		}
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		try {
			// initializing google map
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
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
							NearbyContactsDataSource dsNearby = new NearbyContactsDataSource(
									getActivity());
							dsNearby.open();
							List<NearbyContact> contacts = dsNearby
									.getNearbyContacts();
							dsNearby.close();

							for (int i = 0; i < contacts.size(); i++) {

								NearbyContact currentContact = contacts.get(i);

								final MarkerOptions marker = GetContactDetails(
										currentContact.phoneNumber,
										currentContact.distance,
										currentContact.location);

								Handler handler = new Handler(Looper
										.getMainLooper());
								handler.post(new Runnable() {

									@Override
									public void run() {
										googleMap.addMarker(marker);
									}
								});

							}
						} catch (Exception e) {
							Log.e("WeMeet_Exception", "");
						}
					}
				}
			}, UPDATE_DELAY / 5, UPDATE_DELAY);

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

				marker.title(name + " - " + ValidationHelper.RoundTwoDecimals(distance) + " km");
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
	public void onInfoWindowClick(Marker arg0) {
		Toast.makeText(getActivity(), arg0.getTitle(), Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onMyLocationChange(Location location) {
		LatLng coordinate = new LatLng(location.getLatitude(),
				location.getLongitude());
		if (isFirst) {
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate,
					12));
			isFirst = false;
			googleMap.setOnMyLocationChangeListener(null);
		}
	}
}
