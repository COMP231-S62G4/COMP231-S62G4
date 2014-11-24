package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.List;

import comp231.g4.wemeet.model.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class AddGroupMemeberAdapter extends ArrayAdapter<Contact> implements
		OnClickListener {

	public List<Contact> selectedContacts;

	public AddGroupMemeberAdapter(Context context, ArrayList<Contact> contacts) {
		super(context, 0, contacts);

		selectedContacts = new ArrayList<Contact>();
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
		CheckBox cbSelected = (CheckBox) view.findViewById(R.id.cbSelected);
		cbSelected.setVisibility(View.VISIBLE);

		view.setOnClickListener(this);// setting on click listener
		view.setTag(position);

		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
		tvName.setText(contact.name);
		tvPhone.setText("");

		if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
			tvPhone.setText(contact.numbers.get(0).number);
		}
		return view;
	}

	@Override
	public void onClick(View view) {
		//getting view position
		int position = Integer.parseInt(String.valueOf(view.getTag()));
		Contact contact = getItem(position);

		//updating ui
		CheckBox cbSelected = (CheckBox) view.findViewById(R.id.cbSelected);
		if (cbSelected.isChecked()) {
			cbSelected.setChecked(false);
			selectedContacts.remove(contact);
		} else {
			cbSelected.setChecked(true);
			selectedContacts.add(contact);
		}
	}
}