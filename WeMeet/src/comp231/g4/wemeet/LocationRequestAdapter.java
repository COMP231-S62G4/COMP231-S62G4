package comp231.g4.wemeet;

import java.util.ArrayList;

import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationRequestAdapter extends ArrayAdapter<Contact> implements
		OnClickListener {
	private ListView _lvContacts;
	private Activity _activity;

	public LocationRequestAdapter(Context context, ArrayList<Contact> contacts,
			ListView listView, Activity activity) {
		super(context, 0, contacts);
		_lvContacts = listView;
		_activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item
		Contact contact = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			view = inflater.inflate(R.layout.list_item_location_request,
					parent, false);
		}
		// Populate the data into the template view using the data object
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);

		Button btnAccept = (Button) view.findViewById(R.id.btnAccept);
		btnAccept.setOnClickListener(this);

		Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(this);

		tvName.setText(contact.name);
		tvPhone.setText("");

		if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
			tvPhone.setText(contact.numbers.get(0).number);
		}
		return view;
	}

	
}
