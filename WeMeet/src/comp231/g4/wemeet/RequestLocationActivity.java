package comp231.g4.wemeet;

import java.util.ArrayList;

import comp231.g4.wemeet.helpers.ContactFetcher;
import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.model.ContactPhone;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class RequestLocationActivity extends Activity {
	private ImageButton imgbtnSearch;
	private EditText etSearch;
	private ListView lvContacts;
	private ArrayList<Contact> listContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);

		// setting on clink listener for search button
		imgbtnSearch = (ImageButton) findViewById(R.id.btnSearch);
		imgbtnSearch.setVisibility(View.GONE);

		// initializing UI elements
		etSearch = (EditText) findViewById(R.id.etSearch);
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == KeyEvent.ACTION_DOWN) {
					final String query = v.getText().toString();
					ArrayList<Contact> filteredContacts = new ArrayList<Contact>();

					if (query.length() == 0) {
						filteredContacts = listContacts;
					} else {
						for (int i = 0; i < listContacts.size(); i++) {
							Contact contact = listContacts.get(i);

							if (contact.name.contains(query))
								filteredContacts.add(contact);
						}
					}

					ContactsAdapter adapterContacts = new ContactsAdapter(
							RequestLocationActivity.this, filteredContacts);

					// Sets the adapter for the ListView
					lvContacts.setAdapter(adapterContacts);

					Log.e("WeMeet_Test : Contact Search", query);
				}

				return true;
			}

		});

		lvContacts = (ListView) findViewById(R.id.lvContacts);

		// Gets the ListView from the View list of the parent activity
		lvContacts = (ListView) findViewById(R.id.lvContacts);
		// Gets a CursorAdapter
		listContacts = new ArrayList<Contact>();

		ContactsAdapter adapterContacts = new ContactsAdapter(this,
				listContacts);

		// Sets the adapter for the ListView
		lvContacts.setAdapter(adapterContacts);

		// loading registered contacts
		locadRegisteredContacts();

	}

	private void locadRegisteredContacts() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				// list to hold contacts
				Looper.prepare();
				ArrayList<Contact> contacts = new ContactFetcher(
						RequestLocationActivity.this).fetchAll();
				Log.e("WeMeet_Exception", String.valueOf(contacts.size()));

				AndroidClient client = new AndroidClient();

				// iterating through all contacts
				for (int index = 0; index < contacts.size(); index++) {
					try {
						Contact contact = contacts.get(index);

						// iterating through all contact numbers for current
						// contact
						ArrayList<ContactPhone> phoneNumbers = contact.numbers;

						int registeredPhoneNumbers = 0;

						for (ContactPhone phoneNumber : phoneNumbers) {

							// Log.e("WeMeet_Exception", contact.name +
							// " "+phoneNumber.number);

							if (client.IsRegisteredPhoneNumber(ValidationHelper
									.SanitizePhoneNumber(phoneNumber.number))) {
								registeredPhoneNumbers++;
							}
						}

						if (registeredPhoneNumbers != 0) {
							listContacts.add(contact);
						}

					} catch (Exception e) {
						Log.e("WeMeet_Exception", e.getMessage());
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// updating UI
						ContactsAdapter adapterContacts = new ContactsAdapter(
								RequestLocationActivity.this, listContacts);

						// Sets the adapter for the ListView
						lvContacts.setAdapter(adapterContacts);
					}
				});

			}
		});

		t.start();
	}
}
