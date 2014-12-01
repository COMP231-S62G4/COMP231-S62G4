package comp231.g4.wemeet;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingsFragment extends Fragment implements OnCheckedChangeListener {
	private Switch switchNotificationSound, switchNotification;
	
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
			switchNotificationSound = (Switch) getActivity().findViewById(R.id.switchNotificationSound);
			switchNotificationSound.setOnCheckedChangeListener(this);
			
			switchNotification = (Switch) getActivity().findViewById(R.id.switchNotification);
			switchNotification.setOnCheckedChangeListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
	}
}