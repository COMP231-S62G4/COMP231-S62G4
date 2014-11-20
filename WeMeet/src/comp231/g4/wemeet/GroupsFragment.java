package comp231.g4.wemeet;

import java.util.List;

import comp231.g4.wemeet.helpers.GroupsDataSource;
import comp231.g4.wemeet.model.Group;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class GroupsFragment extends Fragment {
	private ListView lvGroups;
	private List<Group> groups;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_groups, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// initializing components
		InitializeComponents();
	}

	private void InitializeComponents() {
		lvGroups = (ListView) getActivity().findViewById(R.id.lvGroups);
		
		//loading groups
		loadGroups();
		
		//set list adapter
		GroupsAdapter adapter = new GroupsAdapter(getActivity(), 0, 0, groups);
		lvGroups.setAdapter(adapter);
	}

	private void loadGroups() {
		GroupsDataSource dsGroups = new GroupsDataSource(getActivity());
		dsGroups.open();
		
		groups = dsGroups.getAllGroups();
		
		dsGroups.close();
	}
}
