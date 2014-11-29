package comp231.g4.wemeet;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import comp231.g4.wemeet.model.Group;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LocateGroupFragment extends Fragment implements
		OnMyLocationChangeListener {
	private GoogleMap map;
	private Group group;
	private View view;
	
	public LocateGroupFragment(Group group){
		this.group = group;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
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
	}

	@Override
	public void onMyLocationChange(Location arg0) {
		// zooming to current location
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(arg0.getLatitude(), arg0.getLongitude()), 12));
	}
}
