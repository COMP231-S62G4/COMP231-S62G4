package comp231.g4.wemeet;

import java.util.List;

import comp231.g4.wemeet.helpers.GroupsDataSource;
import comp231.g4.wemeet.model.Group;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupsFragment extends Fragment implements OnClickListener {
	private ListView lvGroups;
	private List<Group> groups;
	private Dialog addGroupDialog;
	private AlertDialog dialog;
	private EditText etGroupName;

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

		initializeAddGroupDialog();

		lvGroups = (ListView) getActivity().findViewById(R.id.lvGroups);

		// loading groups
		loadGroups();

		// set list adapter
		GroupsAdapter adapter = new GroupsAdapter(getActivity(), 0, 0, groups);
		lvGroups.setAdapter(adapter);
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
			dialog.setMessage("You have not created any groups yet.");
			dialog.setIcon(android.R.drawable.ic_dialog_alert);

			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

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
			GroupsDataSource dsGroups = new GroupsDataSource(getActivity());
			try{
				dsGroups.open();
				
				//if group already exists
				if(dsGroups.exists(etGroupName.getText().toString())){
					etGroupName.setError("Group already exists");
				}else{
					if(dsGroups.addGroup(etGroupName.getText().toString())){
						//if everything is successful
						addGroupDialog.dismiss();	
						
						//updating groups list
						groups = dsGroups.getAllGroups();
						
						lvGroups.setAdapter(new GroupsAdapter(getActivity(), 0, 0, groups));
						
						Toast.makeText(getActivity(), "Group created.", Toast.LENGTH_SHORT).show();
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				dsGroups.close();
			}

		default:
			break;
		}
		
	}
}
