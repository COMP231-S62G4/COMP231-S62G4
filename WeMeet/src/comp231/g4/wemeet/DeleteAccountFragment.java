package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.List;

import comp231.g4.wemeet.helpers.DbHelper;
import comp231.g4.wemeet.servicehelper.AndroidClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DeleteAccountFragment extends Fragment implements OnClickListener {
	private Button btnDelete;
	private Dialog dialogLoading;
	private AlertDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_delete_account, null);
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		// initializing components
		InitializeComponents();
	}

	private void InitializeComponents() {
		btnDelete = (Button) getActivity().findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(this);

		dialogLoading = new Dialog(getActivity());
		dialogLoading.setTitle(R.string.title_deleting_Account);
		dialogLoading.setContentView(R.layout.dialog_loading);

		TextView tvMessage = (TextView) dialogLoading
				.findViewById(R.id.tvMessage);
		tvMessage.setText(R.string.msg_deleting_account);

		dialogLoading.setCancelable(false);

		dialog = new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_info)
				.setCancelable(false).create();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDelete:
			dialogLoading.show();

			try {
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						final List<Boolean> list = new ArrayList<Boolean>();

						AndroidClient client = new AndroidClient();
						try {
							boolean done = client
									.UnRegisterPhoneNumber(PreferenceManager
											.getDefaultSharedPreferences(
													getActivity()
															.getApplicationContext())
											.getString(
													MainActivity.KEY_PHONE_NUMBER,
													""));

							list.add(Boolean.valueOf(done));

							if (done) {
								DbHelper helper = new DbHelper(getActivity());
								helper.DeleteAccount();
							}
						} catch (Exception e) {

						} finally {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// dismissing dialog
									dialogLoading.dismiss();

									if (list.size() > 0
											&& list.get(0).booleanValue()) {
										dialog.setTitle("Account Deleted");
										dialog.setMessage("Your account deleted successfully.");
									} else {
										dialog.setTitle("ERROR");
										dialog.setMessage("Unable to delete your account.");
										dialog.setIcon(android.R.drawable.ic_dialog_alert);
									}

									dialog.setButton(
											AlertDialog.BUTTON_POSITIVE,
											"Ok",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {

													dialog.dismiss();

													// closing
													// application
													getActivity()
															.finishAffinity();

												}
											});

									dialog.show();

								}
							});
						}

					}
				});

				t.start();

			} catch (Exception e) {
				Log.e("WeMeet_Exception", e.getMessage());
			}

			break;
		}
	}

}
