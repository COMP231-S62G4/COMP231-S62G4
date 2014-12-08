package comp231.g4.wemeet;

import java.util.HashMap;

import comp231.g4.wemeet.helpers.PasswordRecoveryDataSource;
import comp231.g4.wemeet.helpers.ValidationHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationActivity extends Activity implements OnClickListener {
	private EditText etPassword;
	private Button btnEnter;
	private Button btnResetPassword;
	private HashMap<String, String> questionAnswers;
	public static final String KEY_PASSWORD = "APPLICATION_HASH";

	private Dialog dialogResetPassword;
	private TextView tvQuestion1, tvQuestion2, tvQuestion3;
	private EditText etAnswer1, etAnswer2, etAnswer3;
	private Button btnReset, btnCancel;

	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);

		// initialize components
		InitializeComponents();
	}

	private void InitializeComponents() {

		etPassword = (EditText) findViewById(R.id.etPassword);

		btnEnter = (Button) findViewById(R.id.btnEnter);
		btnEnter.setOnClickListener(this);
		
		btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
		btnResetPassword.setOnClickListener(this);
		btnResetPassword.setVisibility(View.INVISIBLE);

		// loading qa from db if user already have donw password setup
		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (prefs.contains(SettingsFragment.KEY_SECURITY_SETUP)) {
			//showing reset password button
			btnResetPassword.setVisibility(View.VISIBLE);
			
			//loading question answer
			loadSecurityQA();
			
			// initializing reset password dialog
			dialogResetPassword = new Dialog(this);
			dialogResetPassword.setContentView(R.layout.dialog_reset_password);
			dialogResetPassword.setTitle("Reset Password");
			dialogResetPassword.setCancelable(false);

			tvQuestion1 = (TextView) dialogResetPassword
					.findViewById(R.id.tvQuestion1);
			tvQuestion2 = (TextView) dialogResetPassword
					.findViewById(R.id.tvQuestion2);
			tvQuestion3 = (TextView) dialogResetPassword
					.findViewById(R.id.tvQuestion3);

			etAnswer1 = (EditText) dialogResetPassword
					.findViewById(R.id.etAnswer1);
			etAnswer2 = (EditText) dialogResetPassword
					.findViewById(R.id.etAnswer2);
			etAnswer3 = (EditText) dialogResetPassword
					.findViewById(R.id.etAnswer3);

			btnCancel = (Button) dialogResetPassword
					.findViewById(R.id.btnCancel);
			btnCancel.setOnClickListener(this);

			btnReset = (Button) dialogResetPassword.findViewById(R.id.btnReset);
			btnReset.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnEnter:
			// code to verify password
			String password = etPassword.getText().toString();
			String encodedPassword = ValidationHelper.EncodeString(password);

			etPassword.setText("");

			SharedPreferences pManager = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			String userPassword = pManager.getString(KEY_PASSWORD, "");
			if (encodedPassword.equals(userPassword)) {
				// code to launch FriendsNearBy activity
				Intent i = new Intent(AuthenticationActivity.this,
						HomeActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
				startActivity(i);

				Toast.makeText(AuthenticationActivity.this,
						"Entering into secure zone!", Toast.LENGTH_SHORT)
						.show();
			} else {
				etPassword.setError("Invalid password!");
				etPassword.setBackgroundColor(Color.parseColor("#C20000"));
				etPassword.setTextColor(Color.WHITE);
				Toast.makeText(AuthenticationActivity.this,
						"Invalid password!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btnResetPassword:
			if(dialogResetPassword!=null){
				dialogResetPassword.show();
			}
			break;
		case R.id.btnCancel:
			if (dialogResetPassword != null) {
				resetDialog();
				
				dialogResetPassword.dismiss();
			}
			break;
		case R.id.btnReset:
			if (dialogResetPassword != null) {
				if (validateAnswers()) {

					String answer1 = etAnswer1.getText().toString().trim();
					String answer2 = etAnswer2.getText().toString().trim();
					String answer3 = etAnswer3.getText().toString().trim();

					String question1 = tvQuestion1.getText().toString().trim();
					String question2 = tvQuestion2.getText().toString().trim();
					String question3 = tvQuestion3.getText().toString().trim();

					if (questionAnswers.get(question1).toLowerCase().equals(answer1.toLowerCase())) {
						if (questionAnswers.get(question2).toLowerCase().equals(answer2.toLowerCase())) {
							if (questionAnswers.get(question3).toLowerCase().equals(answer3.toLowerCase())) {
								//code to reset password
								//basically remove password key from the preferences
								Editor editor = prefs.edit();
								editor.remove(KEY_PASSWORD);
								editor.commit();
								
								resetDialog();
								
								dialogResetPassword.dismiss();
								
								// code to launch FriendsNearBy activity
								Intent i = new Intent(AuthenticationActivity.this,
										HomeActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
								startActivity(i);

								Toast.makeText(AuthenticationActivity.this,
										"Entering into secure zone!", Toast.LENGTH_SHORT)
										.show();
							} else {
								etAnswer3.setError("Invalid answer!");
							}
						} else {
							etAnswer2.setError("Invalid answer!");
						}
					} else {
						etAnswer1.setError("Invalid answer!");
					}

				}
			}
			break;
		default:
			break;
		}
	}

	private void resetDialog() {
		//resetting text boxes
		etAnswer1.setText("");
		etAnswer2.setText("");
		etAnswer3.setText("");
	}

	private void loadSecurityQA() {
		PasswordRecoveryDataSource dsPasswordRecovery = new PasswordRecoveryDataSource(
				this);
		dsPasswordRecovery.open();

		questionAnswers = dsPasswordRecovery.getAll();

		dsPasswordRecovery.close();
	}

	private boolean validateAnswers() {
		String answer1 = etAnswer1.getText().toString().trim();
		String answer2 = etAnswer2.getText().toString().trim();
		String answer3 = etAnswer3.getText().toString().trim();

		if (answer1.length() == 0) {
			etAnswer1.setError("Invalid answer!");
			return false;
		}
		if (answer2.length() == 0) {
			etAnswer2.setError("Invalid answer!");
			return false;
		}
		if (answer3.length() == 0) {
			etAnswer3.setError("Invalid answer!");
			return false;
		}

		return true;
	}
}
