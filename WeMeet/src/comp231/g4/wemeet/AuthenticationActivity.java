package comp231.g4.wemeet;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AuthenticationActivity extends Activity implements OnClickListener {
	private EditText etPassword;
	private Button btnEnter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
			break;

		default:
			break;
		}
	}
}
