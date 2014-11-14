package comp231.g4.wemeet;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import comp231.g4.wemeet.helpers.RegisteredContactsDataSource;
import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ViewContactActivity extends Activity {
	private TextView tvName;
	private ListView lvPhoneNumbers;
	private GoogleMap map;

	private Contact registeredContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_contact);

		// initializing components
		InitializeComponents();
	}

	private void InitializeComponents() {
		tvName = (TextView) findViewById(R.id.tvContactName);
		tvName.setText(getIntent().getStringExtra(
				ContactsFragment.DATA_CONTACT_NAME));

		Contact contact = new Contact(getIntent().getStringExtra(
				ContactsFragment.DATA_CONTACT_ID), getIntent().getStringExtra(
				ContactsFragment.DATA_CONTACT_NAME));

		ArrayList<String> phonenumbers = getIntent().getStringArrayListExtra(
				ContactsFragment.DATA_CONTACT_PHONE_NUMBERS);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);

		for (int i = 0; i < phonenumbers.size(); i++) {
			adapter.add(phonenumbers.get(i));
			contact.addNumber(phonenumbers.get(i), "");
		}

		lvPhoneNumbers = (ListView) findViewById(R.id.lvPhoneNumbers);
		lvPhoneNumbers.setAdapter(adapter);

		RegisteredContactsDataSource dsRegisteredContacts = new RegisteredContactsDataSource(
				this);
		dsRegisteredContacts.open();
		registeredContact = dsRegisteredContacts.exists(contact);
		dsRegisteredContacts.close();

		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		if (registeredContact != null) {
			map.setMyLocationEnabled(false);
			map.getUiSettings().setZoomControlsEnabled(true);
			map.getUiSettings().setZoomGesturesEnabled(false);
		}
		else{
			((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getView().setVisibility(View.GONE);
		}

		// load contact on map
		loadContact();
	}

	private void loadContact() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				if (map != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {

						@Override
						public void run() {
							map.clear();// clearing map
						}
					});

					try {
						AndroidClient client = new AndroidClient();
						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(ViewContactActivity.this
										.getApplicationContext());
						LatLng position = client.GetLocation(prefs.getString(
								MainActivity.KEY_PHONE_NUMBER, ""),
								ValidationHelper.SanitizePhoneNumber(registeredContact.numbers.get(0).number));

						final MarkerOptions marker = new MarkerOptions();
						marker.position(position);
						marker.title(registeredContact.name);

						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {

							@Override
							public void run() {
								map.addMarker(marker);
								Log.e("Marker adder", marker.getTitle());
							}
						});

					} catch (Exception e) {
						Log.e("WeMeet_Exception", "");
					}
				}

			}
		});

		t.start();
	}
}
