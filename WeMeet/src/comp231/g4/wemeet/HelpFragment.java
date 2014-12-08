package comp231.g4.wemeet;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class HelpFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_help, null);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle("Help");
		getActivity().getActionBar().setIcon(android.R.drawable.ic_menu_help);
		
		WebView wv = (WebView) getActivity().findViewById(R.id.wvHelp);  
        wv.loadUrl("file:///android_asset/home.html");
	}
}
