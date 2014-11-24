package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.List;

import comp231.g4.wemeet.helpers.GroupsDataSource;
import comp231.g4.wemeet.helpers.SharedLocationDataSource;
import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.model.Group;
import comp231.g4.wemeet.model.GroupMemeber;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class GroupMembersFragment extends Fragment implements
		android.content.DialogInterface.OnClickListener, OnClickListener {
	private ListView lvGroupMembers, lvSharedLocationContacts;
	private List<GroupMemeber> groupMembers;
	private AlertDialog dialog;
	private AlertDialog addGroupMemberDialog;

	// variables to hold group info
	private int groupId;
	private String groupName;
	private List<Contact> sharedLocationContacts;

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

		// set list adapter
		GroupMembersAdapter adapter = new GroupMembersAdapter(getActivity(), 0,
				0, groupMembers);
		lvGroupMembers.setAdapter(adapter);

		// initializing add group member dialog
		initializeAddGroupMemberDialog();

		setHasOptionsMenu(true);
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setIcon(R.drawable.ic_groups);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_groups, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_add_group:
			if (sharedLocationContacts.size() == 0) {
				Toast.makeText(getActivity(),
						"Your location sharing list is empty.",
						Toast.LENGTH_SHORT).show();
			} else {
				addGroupMemberDialog.show();// showing add group members dialog
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initializeAddGroupMemberDialog() {
		addGroupMemberDialog = new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_info)
				.setCancelable(false).create();

		// reusing contacts layout instead of creating new one
		addGroupMemberDialog.setContentView(R.layout.activity_contacts);
		addGroupMemberDialog.setCancelable(true);
		// setting title for dialog
		addGroupMemberDialog.setTitle(groupName);

		addGroupMemberDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
				this);
		addGroupMemberDialog
				.setButton(AlertDialog.BUTTON_POSITIVE, "Add", this);

		// hiding search bar
		LinearLayout llSearchBar = (LinearLayout) addGroupMemberDialog
				.findViewById(R.id.llSearchBar);
		llSearchBar.setVisibility(View.GONE);

		// setting shared location contacts as list
		lvSharedLocationContacts = (ListView) addGroupMemberDialog
				.findViewById(R.id.lvContacts);

		// instantiating shared contact list
		SharedLocationDataSource dsSharedLocation = new SharedLocationDataSource(
				getActivity());

		dsSharedLocation.open(); // opening db

		sharedLocationContacts = dsSharedLocation.getAllContacts();

		dsSharedLocation.close(); // closing db

		// setting adapter
		AddGroupMemeberAdapter adapter = new AddGroupMemeberAdapter(
				getActivity(), new ArrayList<Contact>(sharedLocationContacts));
		lvSharedLocationContacts.setAdapter(adapter);
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
				for (int i = 0; i < selectedContacts.size(); i++) {
					// adding contacts as a group member

					// incrementing counter
					addedContacts++;
				}
				Toast.makeText(getActivity(),
						addedContacts + " members added.", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {

	}
}
