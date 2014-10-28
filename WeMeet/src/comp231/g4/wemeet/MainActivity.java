package comp231.g4.wemeet;

import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private Button btnRegister;
	private EditText etPhoneNumber;
	protected static final String TAG_FIRST_LAUNCH = "FIRST_TIME_LAUNCH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);

		btnRegister = (Button) findViewById(R.id.btn_register);
		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					final String phoneNumber = etPhoneNumber.getText()
							.toString();
					if (ValidationHelper.IsValidPhoneNumber(phoneNumber)) {
						Thread thread = new Thread(new Runnable() {

							@Override
							public void run() {
								AndroidClient client = new AndroidClient();
								// checking for registration
								if (client.IsRegisteredPhoneNumber(phoneNumber)) {

								} else {
									if (client.RegisterPhoneNumber(phoneNumber)) {

										// code to launch FriendsNearBy activity
										Intent i = new Intent(
												MainActivity.this,
												FriendsNearByActivity.class);
										i.putExtra(TAG_FIRST_LAUNCH, true);
										startActivity(i);
									}
								}

							}
						});

						thread.start();

					} else {
						etPhoneNumber.setError("Invalid phone number.");
					}
				} catch (Exception e) {
					Log.e("WeMeet_Exception", e.getMessage());
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_exit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_exit) {
			finishAffinity();
		}
		return super.onOptionsItemSelected(item);
	}
}
