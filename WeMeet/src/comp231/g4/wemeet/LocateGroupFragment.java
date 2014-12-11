package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.List;

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

import comp231.g4.wemeet.helpers.GroupsDataSource;
import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.model.Group;
import comp231.g4.wemeet.model.GroupMember;
import comp231.g4.wemeet.model.NearbyContact;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class LocateGroupFragment extends Fragment implements
		OnMyLocationChangeListener, OnClickListener, OnInfoWindowClickListener {
	private GoogleMap map;
	private Group group;
	private AlertDialog dialog;
	private MapFragment mapFragment;
	private MenuItem miLoading;

	public LocateGroupFragment(Group group) {
		this.group = group;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.activity_locate_group, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// initializing components
		InitializeComponents();
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
	public void onDestroyView() {

		try {

			FragmentTransaction ft = getActivity().getFragmentManager()
					.beginTransaction();
			ft.remove(mapFragment).commit();
		} catch (Exception e) {

		} finally {
			try {
				super.onDestroy();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void InitializeComponents() {
		mapFragment = ((MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.locateMembersMap));
		map = mapFragment.getMap();

		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);
		map.setOnInfoWindowClickListener(this);

		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setIcon(R.drawable.ic_groups);
		getActivity().setTitle(group.name);

		// initializing zero group member nearby alert dialog
		dialog = new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false).create();
		dialog.setMessage("No one is nearby.");

		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", this);

		setHasOptionsMenu(true);

		// locating group members
		locateGroupMembers();
	}

	private void locateGroupMembers() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// loading near by contacts list

				List<NearbyContact> nearbyContacts = new ArrayList<NearbyContact>();

				AndroidClient client = new AndroidClient();

				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getActivity()
								.getApplicationContext());
				try {

					JSONArray data = client.GetFriendsNearBy(prefs.getString(
							MainActivity.KEY_PHONE_NUMBER, ""));

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

							nearbyContacts.add(new NearbyContact(phoneNumber,
									iLocation, distance, lastSeen));

						} catch (Exception e) {
						}
					}

				} catch (Exception e) {

				}

				if (nearbyContacts.size() > 0) {
					// loading list of group members
					GroupsDataSource dsGroups = new GroupsDataSource(
							getActivity());
					dsGroups.open();
					List<GroupMember> groupMembers = dsGroups
							.getGroupMemebers(group.id);
					dsGroups.close();

					int markersAdded = 0;
					for (int i = 0; i < groupMembers.size(); i++) {
						GroupMember member = groupMembers.get(i); // fetching
																	// current
																	// group
																	// member

						for (int j = 0; j < nearbyContacts.size(); j++) {
							NearbyContact nearbyContact = nearbyContacts.get(j); // fetching
																					// current
																					// near
																					// by
																					// contact
							if (nearbyContact.phoneNumber.equals(member.number)) {
								markersAdded++;

								final MarkerOptions marker = GetContactDetails(
										nearbyContact.phoneNumber,
										nearbyContact.distance,
										nearbyContact.location);

								getActivity().runOnUiThread(new Runnable() {

									@Override
									public void run() {
										if (map != null) {
											map.addMarker(marker);
										}
										if (miLoading != null) {
											miLoading.setVisible(false);
										}
									}
								});
								break;
							}
						}
					}
					if (markersAdded == 0) {
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (miLoading != null) {
									miLoading.setVisible(false);
								}
								dialog.show();// no one is nearby
							}
						});
					}
				} else {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (miLoading != null) {
								miLoading.setVisible(false);
							}
							dialog.show();
						}
					});
				}
			}
		});

		t.start();// starting thread

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
	public void onMyLocationChange(Location arg0) {
		// zooming to current location
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(arg0.getLatitude(), arg0.getLongitude()), 12));

		map.setOnMyLocationChangeListener(null);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == this.dialog) {
			dialog.dismiss();// hiding dialog
		}

	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		Toast.makeText(getActivity(), arg0.getTitle(), Toast.LENGTH_SHORT)
				.show();
	}
}