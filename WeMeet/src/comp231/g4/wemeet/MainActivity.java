package comp231.g4.wemeet;

import comp231.g4.wemeet.helpers.ValidationHelper;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;

public class MainActivity extends Activity {
	private Button btnRegister;
	private EditText etPhoneNumber;
	private Spinner spCountry;

	protected static final String TAG_FIRST_LAUNCH = "FIRST_TIME_LAUNCH";
	protected static final String KEY_IS_REGISTERED = "IS_REGISTERED";
	protected static final String KEY_PHONE_NUMBER = "PHONE_NUMBER";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		layout.setGravity(Gravity.CENTER);

		ProgressBar bar = new ProgressBar(this);
		bar.setScrollBarStyle(android.R.attr.progressBarStyleSmall);

		layout.addView(bar);

		setContentView(layout);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				int registerationStatus = -1;

				try {

					if (IsRegisterd()) {
						AndroidClient client = new AndroidClient();
						registerationStatus = client
								.IsRegisteredPhoneNumber(PreferenceManager
										.getDefaultSharedPreferences(
												MainActivity.this
														.getApplicationContext())
										.getString(
												MainActivity.KEY_PHONE_NUMBER,
												"")) ? 1 : -1;
					} else {
						registerationStatus = -1;
					}

				} catch (Exception e) {
					registerationStatus = 0;
				} finally {

					final int status = registerationStatus;

					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							if (status == 0) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										showErrorDialog();
									}
								});

							} else if (status == -1) {
								setContentView(R.layout.activity_main);

								etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);

								spCountry = (Spinner) findViewById(R.id.spinner_country_code);

								btnRegister = (Button) findViewById(R.id.btn_register);
								btnRegister
										.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												try {

													String countryCode = spCountry
															.getSelectedItem()
															.toString();
													countryCode = countryCode
															.substring(countryCode
																	.indexOf('+') + 1);
													final String phoneNumber = countryCode
															+ etPhoneNumber
																	.getText()
																	.toString();

													if (ValidationHelper
															.IsValidPhoneNumber(etPhoneNumber
																	.getText()
																	.toString())) {
														btnRegister
																.setEnabled(false);

														Thread thread = new Thread(
																new Runnable() {

																	@Override
																	public void run() {
																		try {
																			AndroidClient client = new AndroidClient();
																			// registering
																			// device
																			client.RegisterPhoneNumber(phoneNumber);

																			SharedPreferences prefs = PreferenceManager
																					.getDefaultSharedPreferences(getApplicationContext());
																			Editor editor = prefs
																					.edit();
																			editor.putBoolean(
																					KEY_IS_REGISTERED,
																					true);
																			editor.putString(
																					KEY_PHONE_NUMBER,
																					phoneNumber);
																			editor.commit();

																			// code
																			// to
																			// launch
																			// FriendsNearBy
																			// activity
																			Intent i = new Intent(
																					MainActivity.this,
																					HomeActivity.class);
																			i.putExtra(
																					TAG_FIRST_LAUNCH,
																					true);
																			startActivity(i);
																		} catch (Exception e) {
																			runOnUiThread(new Runnable() {

																				@Override
																				public void run() {
																					showErrorDialog();
																				}
																			});
																		}
																	}
																});

														thread.start();

													} else {
														etPhoneNumber
																.setError("Invalid phone number.");
													}
												} catch (Exception e) {
													Log.e("WeMeet_Exception",
															e.getMessage());
												}
											}
										});
							} else {
								// code to check whether password is enable or not
								if (IsPasswordEnabled()) {
									// code to launch Authentication activity
									Intent i = new Intent(MainActivity.this,
											AuthenticationActivity.class);
									i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
									startActivity(i);
								} else {
									// code to launch FriendsNearBy activity
									Intent i = new Intent(MainActivity.this,
											HomeActivity.class);
									i.putExtra(TAG_FIRST_LAUNCH, true);
									i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
									startActivity(i);
								}
							}

						}
					});
				}
			}
		});

		t.start();
	}

	private boolean IsRegisterd() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		return prefs.contains(KEY_IS_REGISTERED);

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

	private boolean IsPasswordEnabled() {
		return true;
	}
	
	private void showErrorDialog() {
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setCancelable(false).create();

		dialog.setTitle("ERROR");
		dialog.setMessage("Unable to connect to server.");
		dialog.setIcon(android.R.drawable.ic_dialog_alert);

		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();

						// closing
						// application
						MainActivity.this.finishAffinity();

					}
				});

		dialog.show();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		finishAffinity();
	}
}
