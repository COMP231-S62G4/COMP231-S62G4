package comp231.g4.wemeet;

import java.util.HashMap;

import comp231.g4.wemeet.helpers.PasswordRecoveryDataSource;
import comp231.g4.wemeet.helpers.ValidationHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthenticationActivity extends Activity implements OnClickListener {
	private EditText etPassword;
	private Button btnEnter;
	public static final String KEY_PASSWORD = "APPLICATION_HASH";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		
		//initialize components
		InitializeComponents();
	}

	private void InitializeComponents() {
		
		etPassword = (EditText) findViewById(R.id.etPassword);
		btnEnter = (Button) findViewById(R.id.btnEnter);
		
		btnEnter.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnEnter:
			//code to verify password
			String password = etPassword.getText().toString();
			String encodedPassword = ValidationHelper.EncodeString(password);
			
			etPassword.setText("");
			
			SharedPreferences pManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String userPassword = pManager.getString(KEY_PASSWORD, "");
			if(encodedPassword.equals(userPassword)){
				// code to launch FriendsNearBy activity
				Intent i = new Intent(AuthenticationActivity.this,
						HomeActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
				startActivity(i);
				
				Toast.makeText(AuthenticationActivity.this, "Entering into secure zone!", Toast.LENGTH_SHORT).show();
			}else{
				etPassword.setError("Invalid password!");
				etPassword.setBackgroundColor(Color.parseColor("#C20000"));
				etPassword.setTextColor(Color.WHITE);
				Toast.makeText(AuthenticationActivity.this, "Invalid password!", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}
	
	private HashMap<String, String> getSecurityQA(){
		HashMap<String, String> qa = null;
		
		PasswordRecoveryDataSource dsPasswordRecovery = new PasswordRecoveryDataSource(this);
		dsPasswordRecovery.open();
		
		qa = dsPasswordRecovery.getAll();
		
		dsPasswordRecovery.close();
		
		return qa;
	}
}
