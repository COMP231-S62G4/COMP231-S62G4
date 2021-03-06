package comp231.g4.wemeet;

import java.util.List;

import comp231.g4.wemeet.model.Group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GroupsAdapter extends ArrayAdapter<Group> {

	public GroupsAdapter(Context context, int resource, int textViewResourceId,
			List<Group> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item
		Group group = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			
			// Populate the data into the template view using the data object
			//TextView tvName = (TextView) view.findViewById(android.R.layout.simple_list_item_1);
			TextView tvName = (TextView) view;
			tvName.setText(group.name);
		}
		return view;
	}
}
