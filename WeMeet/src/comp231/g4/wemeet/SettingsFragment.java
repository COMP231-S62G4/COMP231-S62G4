package comp231.g4.wemeet;

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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingsFragment extends Fragment implements
		OnCheckedChangeListener, OnClickListener {
	private Switch switchNotificationSound, switchNotification;
	private Button btnAbout;

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
			
			btnAbout = (Button) getActivity().findViewById(R.id.btnAbout);
			btnAbout.setOnClickListener(this);

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
	}

	@Override
	public void onClick(View v) {
		if (v == btnAbout) {
			Fragment fragment = new AboutFragment();

			FragmentManager manager = getFragmentManager();
			manager.beginTransaction().replace(R.id.content_frame, fragment)
					.addToBackStack(null).commit();
		}
	}
}
