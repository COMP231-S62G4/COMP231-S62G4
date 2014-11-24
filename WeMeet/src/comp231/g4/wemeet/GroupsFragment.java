package comp231.g4.wemeet;

import java.util.List;

import comp231.g4.wemeet.helpers.GroupsDataSource;
import comp231.g4.wemeet.model.Group;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class GroupsFragment extends Fragment implements OnClickListener,
		android.content.DialogInterface.OnClickListener {
	private ListView lvGroups;
	private List<Group> groups;
	private Dialog addGroupDialog;
	private AlertDialog dialog, deleteGroupDialog;
	private EditText etGroupName;

	// to keep track of selected group
	private int selectedGroupPosition = -1;

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
		// initializing add group dialog
		dialog = new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_info)
				.setCancelable(false).create();
		dialog.setMessage("You do not have any group.");
		dialog.setIcon(android.R.drawable.ic_dialog_alert);

		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", this);

		deleteGroupDialog = new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(true).create();

		deleteGroupDialog.setTitle("Are you sure?");
		deleteGroupDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", this);
		deleteGroupDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", this);

		initializeAddGroupDialog();

		lvGroups = (ListView) getActivity().findViewById(R.id.lvGroups);

		// loading groups
		loadGroups();

		// set list adapter
		GroupsAdapter adapter = new GroupsAdapter(getActivity(), 0, 0, groups);
		lvGroups.setAdapter(adapter);

		setHasOptionsMenu(true);

		// setting item click listener for context menu
		lvGroups.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				selectedGroupPosition = ((AdapterContextMenuInfo) menuInfo).position;

				MenuInflater inflater = getActivity().getMenuInflater();
				inflater.inflate(R.menu.menu_context_groups, menu);
			}
		});

		// setting item click listener to open group details
		lvGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Group group = (Group) lvGroups.getAdapter().getItem(position);

				Fragment fragment = new GroupMembersFragment(group);
				android.app.FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fragment).addToBackStack(null).commit();
			}
		});
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_delete_group:
			if (selectedGroupPosition != -1) {
				deleteGroupDialog.show();
			}
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
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
			addGroupDialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initializeAddGroupDialog() {
		addGroupDialog = new Dialog(getActivity());
		addGroupDialog.setContentView(R.layout.dialog_single_text);
		addGroupDialog.setCancelable(true);

		TextView tvTitle = (TextView) addGroupDialog.findViewById(R.id.tvTitle);
		tvTitle.setText("Group name");

		etGroupName = (EditText) addGroupDialog.findViewById(R.id.etName);

		addGroupDialog.setTitle("Add Group");
		Button btnCancel = (Button) addGroupDialog.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);

		Button btnAdd = (Button) addGroupDialog.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
	}

	private void loadGroups() {
		GroupsDataSource dsGroups = new GroupsDataSource(getActivity());
		dsGroups.open();

		groups = dsGroups.getAllGroups();

		dsGroups.close();

		if (groups.size() == 0) {
			dialog.show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			addGroupDialog.dismiss();
			break;
		case R.id.btnAdd:
			if (etGroupName.getText().toString().trim().length() == 0) {
				etGroupName.setError("Empty group name!");
				return;
			}
			GroupsDataSource dsGroups = new GroupsDataSource(getActivity());
			try {
				dsGroups.open();

				// if group already exists
				if (dsGroups.exists(etGroupName.getText().toString())) {
					etGroupName.setError("Group already exists");
				} else {
					if (dsGroups.addGroup(etGroupName.getText().toString())) {
						// if everything is successful
						addGroupDialog.dismiss();

						// updating groups list
						groups = dsGroups.getAllGroups();

						lvGroups.setAdapter(new GroupsAdapter(getActivity(), 0,
								0, groups));

						Toast.makeText(getActivity(), "Group created.",
								Toast.LENGTH_SHORT).show();

						etGroupName.setText("");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dsGroups.close();
			}

		default:
			break;
		}

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == deleteGroupDialog) {
			switch (which) {
			case AlertDialog.BUTTON_NEGATIVE:
				deleteGroupDialog.dismiss();
				break;
			case AlertDialog.BUTTON_POSITIVE:
				Group currentGroup = (Group) lvGroups.getAdapter().getItem(
						selectedGroupPosition);
				if (currentGroup != null) {
					GroupsDataSource dsGroups = new GroupsDataSource(
							getActivity());
					dsGroups.open(); // opening db

					if (dsGroups.deleteGroup(currentGroup.name)) {

						groups = dsGroups.getAllGroups();// updating group list

						Toast.makeText(getActivity(), "Group deleted.",
								Toast.LENGTH_SHORT).show();

						lvGroups.setAdapter(new GroupsAdapter(getActivity(), 0,
								0, groups));

						if (groups.size() == 0) {
							this.dialog.show();
						}
					}
					dsGroups.close();// closing db
				}

				selectedGroupPosition = -1; // resetting value
				break;
			}
		} else {
			dialog.dismiss();
		}

	}
}
