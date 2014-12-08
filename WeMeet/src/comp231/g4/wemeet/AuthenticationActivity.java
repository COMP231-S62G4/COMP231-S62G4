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
	private HashMap<String, String> questionAnswers;
	public static final String KEY_PASSWORD = "APPLICATION_HASH";

	private Dialog dialogResetPsssword;
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

		// loading qa from db if user already have donw password setup
		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (prefs.contains(SettingsFragment.KEY_SECURITY_SETUP)) {
			loadSecurityQA();

			// initializing reset password dialog
			dialogResetPsssword = new Dialog(this);
			dialogResetPsssword.setContentView(R.layout.dialog_setup_password);

			tvQuestion1 = (TextView) dialogResetPsssword
					.findViewById(R.id.tvQuestion1);
			tvQuestion2 = (TextView) dialogResetPsssword
					.findViewById(R.id.tvQuestion2);
			tvQuestion3 = (TextView) dialogResetPsssword
					.findViewById(R.id.tvQuestion3);

			etAnswer1 = (EditText) dialogResetPsssword
					.findViewById(R.id.etAnswer1);
			etAnswer2 = (EditText) dialogResetPsssword
					.findViewById(R.id.etAnswer2);
			etAnswer3 = (EditText) dialogResetPsssword
					.findViewById(R.id.etAnswer3);

			btnCancel = (Button) dialogResetPsssword
					.findViewById(R.id.btnCancel);
			btnCancel.setOnClickListener(this);

			btnReset = (Button) dialogResetPsssword.findViewById(R.id.btnSetup);
			btnReset.setText("Reset");
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
		case R.id.btnCancel:
			if (dialogResetPsssword != null) {
				resetDialog();
				
				dialogResetPsssword.dismiss();
			}
			break;
		case R.id.btnSetup:
			if (dialogResetPsssword != null) {
				if (validateAnswers()) {

					String answer1 = etAnswer1.getText().toString().trim();
					String answer2 = etAnswer2.getText().toString().trim();
					String answer3 = etAnswer3.getText().toString().trim();

					String question1 = tvQuestion1.getText().toString().trim();
					String question2 = tvQuestion2.getText().toString().trim();
					String question3 = tvQuestion3.getText().toString().trim();

					if (questionAnswers.get(question1).equals(answer1)) {
						if (questionAnswers.get(question2).equals(answer2)) {
							if (questionAnswers.get(question3).equals(answer3)) {
								//code to reset password
								//basically remove password key from the preferences
								Editor editor = prefs.edit();
								editor.remove(KEY_PASSWORD);
								editor.commit();
								
								resetDialog();
								
								dialogResetPsssword.dismiss();
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
