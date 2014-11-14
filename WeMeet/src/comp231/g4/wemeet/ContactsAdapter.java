package comp231.g4.wemeet;

import java.util.ArrayList;

import comp231.g4.wemeet.model.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactsAdapter extends ArrayAdapter<Contact> {

	public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
		super(context, 0, contacts);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item
		Contact contact = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			view = inflater.inflate(R.layout.list_item_contact, parent, false);
		}
		// Populate the data into the template view using the data object
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
		tvName.setText(contact.name);
		tvPhone.setText("");

		if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
			tvPhone.setText(contact.numbers.get(0).number);
		}
		return view;
	}

}