package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.List;

import comp231.g4.wemeet.helpers.GroupsDataSource;
import comp231.g4.wemeet.helpers.SharedLocationDataSource;
import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.model.Group;
import comp231.g4.wemeet.model.GroupMember;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class GroupMembersFragment extends Fragment implements
		android.content.DialogInterface.OnClickListener {
	private ListView lvGroupMembers, lvSharedLocationContacts;
	private List<GroupMember> groupMembers;
	private AlertDialog dialog;
	private AlertDialog addGroupMemberDialog;

	// variables to hold group info
	private int groupId;
	private String groupName;
	private List<Contact> sharedLocationContacts;

	// variables to hold current selected group member
	private int selectedGroupMemberPos = -1;

	public GroupMembersFragment(Group group) {
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
		// set activity title to group name
		getActivity().setTitle(groupName);

		// initializing add group dialog
		dialog = new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false).create();
		dialog.setMessage("You do not have any group members.");

		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", this);

		lvGroupMembers = (ListView) getActivity().findViewById(
				R.id.lvGroupMembers);

		// loading groups
		loadGroupMembers();

		// setting context menu for list view
		lvGroupMembers.setOnCreateContextMenuListener(this);

		// initializing add group member dialog
		initializeAddGroupMemberDialog();

		setHasOptionsMenu(true);

		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setIcon(R.drawable.ic_groups);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		// set activity title to group name
		getActivity().setTitle(groupName);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		selectedGroupMemberPos = ((AdapterContextMenuInfo) menuInfo).position;

		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.menu_context_group_members, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_delete_group_member:
			if (selectedGroupMemberPos != -1) {
				GroupMember member = groupMembers.get(selectedGroupMemberPos);
				if (member != null) {
					GroupsDataSource dsGroups = new GroupsDataSource(
							getActivity());
					dsGroups.open();// opening db

					dsGroups.removeGroupMemeber(groupId, member);

					dsGroups.close();// closing db

					Toast.makeText(getActivity(), "Member deleted.",
							Toast.LENGTH_SHORT).show();

					// reloading list
					loadGroupMembers();
				}
				// resetting position
				selectedGroupMemberPos = -1;
			}
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_group_members, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_add_group_member:

			// instantiating shared contact list
			SharedLocationDataSource dsSharedLocation = new SharedLocationDataSource(
					getActivity());

			dsSharedLocation.open(); // opening db

			sharedLocationContacts = dsSharedLocation.getAllContacts();

			if (sharedLocationContacts.size() == 0) {
				Toast.makeText(getActivity(),
						"Your location sharing list is empty.",
						Toast.LENGTH_SHORT).show();
			}else{
				// setting adapter
				AddGroupMemeberAdapter adapter = new AddGroupMemeberAdapter(
						getActivity(), new ArrayList<Contact>(
								sharedLocationContacts));
				lvSharedLocationContacts.setAdapter(adapter);

				addGroupMemberDialog.show();// showing add group members dialog
			}

			dsSharedLocation.close(); // closing db

			return true;
		case R.id.item_locate_members:

			//opening locate group members fragment
			Group group = new Group(groupId, groupName);
			LocateGroupFragment fragment = new LocateGroupFragment(group);
			
			FragmentManager manager = getFragmentManager();
			manager.beginTransaction()
			.replace(R.id.content_frame, fragment).addToBackStack(null).commit();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initializeAddGroupMemberDialog() {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.activity_contacts, null);
		// hiding search bar
		LinearLayout llSearchBar = (LinearLayout) view
				.findViewById(R.id.llSearchBar);
		llSearchBar.setVisibility(View.GONE);

		// setting shared location contacts as list
		lvSharedLocationContacts = (ListView) view
				.findViewById(R.id.lvContacts);

		addGroupMemberDialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.ic_groups).setCancelable(false)
				.setView(view).create();

		addGroupMemberDialog.setCancelable(true);
		// setting title for dialog
		addGroupMemberDialog.setTitle(groupName);

		addGroupMemberDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
				this);
		addGroupMemberDialog
				.setButton(AlertDialog.BUTTON_POSITIVE, "Add", this);
	}

	private void loadGroupMembers() {
		GroupsDataSource dsGroups = new GroupsDataSource(getActivity());
		dsGroups.open();

		groupMembers = dsGroups.getGroupMemebers(groupId);

		dsGroups.close();

		if (groupMembers.size() == 0) {
			dialog.show();
		}

		// set list adapter
		GroupMembersAdapter adapter = new GroupMembersAdapter(getActivity(), 0,
				0, groupMembers);
		lvGroupMembers.setAdapter(adapter);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == addGroupMemberDialog) {
			switch (which) {
			case AlertDialog.BUTTON_NEGATIVE:
				addGroupMemberDialog.dismiss();// hiding add group member dialog
				break;
			case AlertDialog.BUTTON_POSITIVE:
				// code to add selected contacts into group
				int addedContacts = 0;

				List<Contact> selectedContacts = ((AddGroupMemeberAdapter) lvSharedLocationContacts
						.getAdapter()).selectedContacts;

				GroupsDataSource dsGroups = new GroupsDataSource(getActivity());
				dsGroups.open();

				for (int i = 0; i < selectedContacts.size(); i++) {
					// adding contacts as a group member
					if (dsGroups.addGroupMember(groupId,
							selectedContacts.get(i).name,
							selectedContacts.get(i).numbers.get(0).number)) {
						// incrementing counter
						addedContacts++;
					}
				}

				dsGroups.close();
				Toast.makeText(getActivity(),
						addedContacts + " members added.", Toast.LENGTH_SHORT)
						.show();

				// reloading list
				loadGroupMembers();
				break;
			default:
				break;
			}
		}
	}
}
