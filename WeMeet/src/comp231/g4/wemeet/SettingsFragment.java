package comp231.g4.wemeet;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingsFragment extends Fragment implements
		OnCheckedChangeListener {
	private Switch switchNotificationSound, switchNotification;

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
			switchNotification = (Switch) getActivity().findViewById(
					R.id.switchNotification);
			switchNotification.setOnCheckedChangeListener(this);

			switchNotificationSound = (Switch) getActivity().findViewById(
					R.id.switchNotificationSound);
			switchNotificationSound.setOnCheckedChangeListener(this);

			prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()
					.getApplicationContext());

			//loading default values
			boolean enableNotification = prefs.getBoolean(KEY_NOTIFICATION,
					true);
			switchNotification.setChecked(enableNotification);
			
			boolean enableNotificationSound = prefs.getBoolean(KEY_NOTIFICATION_SOUND,
					true);
			switchNotification.setChecked(enableNotificationSound);

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
}
