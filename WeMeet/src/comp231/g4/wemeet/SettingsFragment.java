package comp231.g4.wemeet;

import comp231.g4.wemeet.helpers.ValidationHelper;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment implements
		OnCheckedChangeListener, OnClickListener {
	private Switch switchNotificationSound, switchNotification;
	private Button btnAbout, btnChangePassword, btnHelp;
	private Dialog dialogChangePassword;

	// buttons from change password dialog
	private Button btnChange, btnCancel;
	// edit texts from chnage password dialog
	private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
	private TextView tvCurrentPassword;

	private SharedPreferences prefs;

	public static final String KEY_NOTIFICATION = "NOTIFICATION_ENABLED";
	public static final String KEY_NOTIFICATION_SOUND = "NOTIFICATION_SOUND_ENABLED";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_settings, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// initializing components
		InitializeComponenets();
	}

	private void InitializeComponenets() {
		try {
			prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()
					.getApplicationContext());

			switchNotification = (Switch) getActivity().findViewById(
					R.id.switchNotification);

			switchNotificationSound = (Switch) getActivity().findViewById(
					R.id.switchNotificationSound);

			// loading default values
			boolean enableNotification = prefs.getBoolean(KEY_NOTIFICATION,
					true);
			switchNotification.setChecked(enableNotification);

			boolean enableNotificationSound = prefs.getBoolean(
					KEY_NOTIFICATION_SOUND, true);

			switchNotificationSound.setChecked(enableNotificationSound);

			switchNotification.setOnCheckedChangeListener(this);
			switchNotificationSound.setOnCheckedChangeListener(this);

			btnHelp = (Button) getActivity().findViewById(R.id.btnHelp);
			btnHelp.setOnClickListener(this);
			
			btnAbout = (Button) getActivity().findViewById(R.id.btnAbout);
			btnAbout.setOnClickListener(this);

			btnChangePassword = (Button) getActivity().findViewById(
					R.id.btnChangePassword);
			btnChangePassword.setOnClickListener(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Editor editor = prefs.edit();

		if (switchNotificationSound.isChecked()) {
			editor.putBoolean(KEY_NOTIFICATION_SOUND, true);
		} else {
			editor.putBoolean(KEY_NOTIFICATION_SOUND, false);
		}

		if (switchNotification.isChecked()) {
			editor.putBoolean(KEY_NOTIFICATION, true);
		} else {
			editor.putBoolean(KEY_NOTIFICATION, false);
		}

		editor.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle("Settings");
		getActivity().getActionBar().setIcon(R.drawable.ic_sysbar_quicksettings);
	}

	@Override
	public void onClick(View v) {
		if (v == btnAbout) {
			Fragment fragment = new AboutFragment();

			FragmentManager manager = getFragmentManager();
			manager.beginTransaction().replace(R.id.content_frame, fragment)
					.addToBackStack(null).commit();
		} else if (v == btnHelp) {
			Fragment fragment = new HelpFragment();

			FragmentManager manager = getFragmentManager();
			manager.beginTransaction().replace(R.id.content_frame, fragment)
					.addToBackStack(null).commit();
		} else if (v == btnChangePassword) {
			// initialize change password dialog
			InitializeChangePasswordDialog();

			dialogChangePassword.show();
		} else if (v == btnCancel) {
			dialogChangePassword.dismiss();
		} else if (v == btnChange) {
			if (validateEditTexts()) {

				String currentPassword = "";
				if (prefs.contains(AuthenticationActivity.KEY_PASSWORD)) {
					currentPassword = ValidationHelper.EncodeString(etCurrentPassword.getText().toString()
							.trim());
				}

				String newPassword = etNewPassword.getText().toString().trim();

				String userPassword = prefs.getString(
						AuthenticationActivity.KEY_PASSWORD, "");
				if (userPassword.equals(currentPassword)) {
					Editor editor = prefs.edit();
					editor.putString(AuthenticationActivity.KEY_PASSWORD,
							ValidationHelper.EncodeString(newPassword));
					editor.commit();

					Toast.makeText(getActivity(),
							"Password changed successfully!",
							Toast.LENGTH_SHORT).show();
					dialogChangePassword.dismiss();
				} else {
					etCurrentPassword.setError("Invalid Password!");
				}
			}
		}
	}

	private void InitializeChangePasswordDialog() {
		// initializing change password dialog
		dialogChangePassword = new Dialog(getActivity());
		dialogChangePassword.setContentView(R.layout.dialog_change_password);
		dialogChangePassword.setTitle("Change Password");

		tvCurrentPassword = (TextView) dialogChangePassword
				.findViewById(R.id.tvOldPassword);
		etCurrentPassword = (EditText) dialogChangePassword
				.findViewById(R.id.etOldPassword);
		etNewPassword = (EditText) dialogChangePassword
				.findViewById(R.id.etNewPassword);
		etConfirmPassword = (EditText) dialogChangePassword
				.findViewById(R.id.etConfirmPassword);

		btnCancel = (Button) dialogChangePassword.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);

		btnChange = (Button) dialogChangePassword
				.findViewById(R.id.btnChangePassword);
		btnChange.setOnClickListener(this);

		// initializing dialog
		if (!prefs.contains(AuthenticationActivity.KEY_PASSWORD)) {
			etCurrentPassword.setVisibility(View.GONE);
			tvCurrentPassword.setVisibility(View.GONE);
		}

	}

	private boolean validateEditTexts() {
		String current = etCurrentPassword.getText().toString().trim();
		String newPassword = etNewPassword.getText().toString().trim();
		String confirmPassword = etConfirmPassword.getText().toString().trim();

		if (prefs.contains(AuthenticationActivity.KEY_PASSWORD)
				&& current.length() == 0) {
			etCurrentPassword.setError("Invalid current password!");
			return false;
		}
		if (newPassword.length() == 0) {
			etNewPassword.setError("Invalid password!");
			return false;
		}
		if (!newPassword.equals(confirmPassword)) {
			etConfirmPassword.setError("Password do not match!");
			return false;
		}

		return true;
	}
}