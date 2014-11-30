package comp231.g4.wemeet;

import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import comp231.g4.wemeet.helpers.GroupsDataSource;
import comp231.g4.wemeet.helpers.NearbyContactsDataSource;
import comp231.g4.wemeet.model.Group;
import comp231.g4.wemeet.model.GroupMember;
import comp231.g4.wemeet.model.NearbyContact;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LocateGroupFragment extends Fragment implements
		OnMyLocationChangeListener{
	private GoogleMap map;
	private Group group;
	private static View view;

	public LocateGroupFragment(Group group) {
		this.group = group;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.activity_locate_group, null);
		}
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// initializing components
		InitializeComponents();
	}

	private void InitializeComponents() {
		map = ((MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.locateMembersMap)).getMap();

		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(this);

		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setIcon(R.drawable.ic_groups);
		getActivity().setTitle(group.name);

		// locating group members
		locateGroupMembers();
	}

	private void locateGroupMembers() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// loading near by contacts list
				NearbyContactsDataSource dsNearby = new NearbyContactsDataSource(
						getActivity());
				dsNearby.open();
				List<NearbyContact> nearbyContacts = dsNearby
						.getNearbyContacts();
				dsNearby.close();

				if (nearbyContacts.size() > 0) {
					// loading list of group members
					GroupsDataSource dsGroups = new GroupsDataSource(
							getActivity());
					dsGroups.open();
					List<GroupMember> groupMembers = dsGroups
							.getGroupMemebers(group.id);
					dsGroups.close();

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
							if (nearbyContact.phoneNumber == member.number) {
								//code to add marker on map
								break;
							}
						}
					}
				}
			}
		});

		t.start();// starting thread

	}

	@Override
	public void onMyLocationChange(Location arg0) {
		// zooming to current location
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(arg0.getLatitude(), arg0.getLongitude()), 12));

		map.setOnMyLocationChangeListener(null);
	}
}
