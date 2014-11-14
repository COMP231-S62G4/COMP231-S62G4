package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.List;

import comp231.g4.wemeet.helpers.ContactFetcher;
import comp231.g4.wemeet.helpers.RegisteredContactsDataSource;
import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsFragment extends Fragment {
	public static final String DATA_CONTACT_ID = "CONTACT_ID";
	public static final String DATA_CONTACT_NAME = "CONTACT_NAME";
	public static final String DATA_CONTACT_PHONE_NUMBERS = "CONTACT_PHONE_NUMBERS";
	
	private ImageButton imgbtnSearch;
	private EditText etSearch;
	private ListView lvContacts;
	private ArrayList<Contact> contacts;
	private ArrayList<Contact> listContacts;
	private Dialog dialogLoading;
	
	private Contact currentContact = null;
	private Contact currentRegisteredContact = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_contacts, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// initializing components
		InitializeComponents();
	}

	private void InitializeComponents() {
		// showing loading dialog
		dialogLoading = new Dialog(getActivity());
		dialogLoading.setTitle(R.string.title_loading_contacts);
		dialogLoading.setContentView(R.layout.dialog_loading);

		TextView tvMessage = (TextView) dialogLoading
				.findViewById(R.id.tvMessage);
		tvMessage.setText(R.string.msg_wait);

		dialogLoading.setCancelable(false);
		dialogLoading.show();

		// setting on clink listener for search button
		imgbtnSearch = (ImageButton) getActivity().findViewById(R.id.btnSearch);
		imgbtnSearch.setOnClickListener(new View.OnClickListener() {

			// code to execute on button click
			@Override
			public void onClick(View v) {
				final String query = etSearch.getText().toString();

				// creating new array list to hold data
				listContacts = new ArrayList<Contact>();

				if (query.length() == 0) {
					listContacts = new ArrayList<Contact>(contacts);
				} else {
					// filtering contacts
					for (int i = 0; i < contacts.size(); i++) {
						Contact contact = contacts.get(i);

						if (contact.name.toLowerCase().contains(query.toLowerCase()))
							listContacts.add(contact);
					}
				}

				// setting new adapter
				ContactsAdapter adapterContacts = new ContactsAdapter(
						getActivity(), listContacts);

				// Sets the adapter for the ListView
				lvContacts.setAdapter(adapterContacts);

				lvContacts
						.setOnCreateContextMenuListener(ContactsFragment.this);

				Log.e("WeMeet_Test : Contact Search", query);
			}
		});

		// initializing UI elements
		etSearch = (EditText) getActivity().findViewById(R.id.etSearch);
		lvContacts = (ListView) getActivity().findViewById(R.id.lvContacts);

		// Gets the ListView from the View list of the parent activity
		lvContacts = (ListView) getActivity().findViewById(R.id.lvContacts);

		fetchContacts();
	}

	private void fetchContacts() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();

				// fetch all contacts
				contacts = new ContactFetcher(getActivity()).fetchAll();

				listContacts = new ArrayList<Contact>(contacts);
				
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {

						ContactsAdapter adapterContacts = new ContactsAdapter(
								getActivity(), listContacts);

						// Sets the adapter for the ListView
						lvContacts.setAdapter(adapterContacts);

						lvContacts
								.setOnCreateContextMenuListener(ContactsFragment.this);

						// closing dialog
						dialogLoading.dismiss();
					}
				});
			}
		});

		t.start();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		//checking for registration status for selected contact
		RegisteredContactsDataSource dsRegisteredContacts = new RegisteredContactsDataSource(
				getActivity());
		
		currentContact = listContacts.get(((AdapterContextMenuInfo)menuInfo).position);
		
		dsRegisteredContacts.open();

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.menu_contacts, menu);

		menu.setHeaderTitle(currentContact.name);
		
		//removing share location menu item if contact is not registered with WeMeet
		currentRegisteredContact = dsRegisteredContacts.exists(currentContact);
		if (currentRegisteredContact==null) {
			menu.removeItem(R.id.menu_item_share_location);
		}
		//closing database connection
		dsRegisteredContacts.close();
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_view_contact:
			Intent i = new Intent(getActivity(), ViewContactActivity.class);
			i.putExtra(DATA_CONTACT_ID, currentContact.id);
			i.putExtra(DATA_CONTACT_NAME, currentContact.name);
			
			ArrayList<String> phoneNumbers = new ArrayList<String>();
			for(int j=0;j<currentContact.numbers.size();j++){
				phoneNumbers.add(currentContact.numbers.get(j).number);
			}
			
			i.putStringArrayListExtra(DATA_CONTACT_PHONE_NUMBERS, phoneNumbers);
			startActivity(i);
			break;
		case R.id.menu_item_share_location:
			if(currentRegisteredContact!=null){
				shareLocation(currentRegisteredContact);
			}
		default:
			break;
		}
		return true;
	}

	private void shareLocation(final Contact contact) {
		
		Thread t =new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean result = false;
				try{
					AndroidClient client = new AndroidClient();
					
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
					String fromPhoneNumber = prefs.getString(MainActivity.KEY_PHONE_NUMBER, "");
					
					result = client.SendLocationSharingRequest(fromPhoneNumber,ValidationHelper.SanitizePhoneNumber(contact.numbers.get(0).number));
				}catch (Exception e) {
					Log.e("WeMeet_Exception", e.getMessage());
					result = false;
				}
				finally{
					final String msg;
					
					if(result)
					{
						msg = "Request sent.";
					}
					else{
						msg = "Unable to send request!";
					}
					
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
						}
					});
				}
				
			}
		});
		
		t.start();//starting thread
		
	}
}
