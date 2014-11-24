package comp231.g4.wemeet;

import java.util.List;

import comp231.g4.wemeet.helpers.GroupsDataSource;
import comp231.g4.wemeet.model.Group;
import comp231.g4.wemeet.model.GroupMemeber;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class GroupMembersFragment extends Fragment implements android.content.DialogInterface.OnClickListener {
	private ListView lvGroupMembers;
	private List<GroupMemeber> groupMembers;
	private AlertDialog dialog;

	//variables to hold group info
	private int groupId;
	private String groupName;

	public GroupMembersFragment(Group group){
		super();
		
		this.groupId = group.id;
		this.groupName = group.name;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_group_members, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// initializing components
		InitializeComponents();
	}

	private void InitializeComponents() {
		//set activity title to group name
		getActivity().setTitle(groupName);
		
		// initializing add group dialog
		dialog = new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_info)
				.setCancelable(false).create();
		dialog.setMessage("You do not have any group members.");
		dialog.setIcon(android.R.drawable.ic_dialog_alert);

		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", this);

		lvGroupMembers = (ListView) getActivity().findViewById(R.id.lvGroupMembers);

		// loading groups
		loadGroupMembers();

		// set list adapter
		GroupMembersAdapter adapter = new GroupMembersAdapter(getActivity(), 0, 0, groupMembers);
		lvGroupMembers.setAdapter(adapter);
	}

	private void loadGroupMembers() {
		GroupsDataSource dsGroups = new GroupsDataSource(getActivity());
		dsGroups.open();

		groupMembers = dsGroups.getGroupMemebers(groupId);

		dsGroups.close();

		if (groupMembers.size() == 0) {
			dialog.show();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		
	}
}
