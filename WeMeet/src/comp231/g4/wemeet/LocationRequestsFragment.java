package comp231.g4.wemeet;

import java.util.ArrayList;
import java.util.List;

import comp231.g4.wemeet.helpers.ContactFetcher;
import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.servicehelper.AndroidClient;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LocationRequestsFragment extends Fragment {
	private LinearLayout llSearchbar;
	private ListView lvRequests;
	private final ArrayList<Contact> listContacts = new ArrayList<Contact>();
	private Dialog dialogLoading;
	private AlertDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_contacts, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// initializing components
		InitializewComponents();
	}

	private void InitializewComponents() {
		llSearchbar = (LinearLayout) getActivity().findViewById(
				R.id.llSearchBar);
		llSearchbar.setVisibility(View.GONE);

		lvRequests = (ListView) getActivity().findViewById(R.id.lvContacts);

		dialogLoading = new Dialog(getActivity());
		dialogLoading.setTitle(R.string.title_connecting_server);
		dialogLoading.setContentView(R.layout.dialog_loading);

		dialog = new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_info)
				.setCancelable(false).create();

		TextView tvMessage = (TextView) dialogLoading
				.findViewById(R.id.tvMessage);
		tvMessage.setText(R.string.msg_wait);

		dialogLoading.setCancelable(false);
	}

	@Override
	public void onResume() {
		super.onResume();

		// fetch all location request
		fetchLoationRequest();
	}

	private void fetchLoationRequest() {
		// showing loading dialog
		dialogLoading.show();

		// loading requests on separate thread
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String phoneNumber = PreferenceManager
							.getDefaultSharedPreferences(
									getActivity().getApplicationContext())
							.getString(MainActivity.KEY_PHONE_NUMBER,
									"16472787694");

					AndroidClient client = new AndroidClient();
					List<String> phoneNumbers = client
							.GetLocationRequests(phoneNumber);

					ContactFetcher fetcher = new ContactFetcher(getActivity());

					Looper.prepare();

					for (int i = 0; i < phoneNumbers.size(); i++) {
						listContacts.add(fetcher.GetContactDetails(phoneNumbers
								.get(i)));
					}

				} catch (Exception e) {
					Log.e("WeMeet_Exception", e.getMessage());
				} finally {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {

							if (listContacts.size() > 0) {
								final LocationRequestAdapter adapterContacts = new LocationRequestAdapter(
										getActivity(), listContacts, lvRequests, getActivity());

								final DataSetObserver observer = new DataSetObserver() {
									@Override
									public void onChanged() {
										super.onChanged();

										if (adapterContacts.getCount() == 0) {
											showNoPendingRequestDialog();
										}
									}
								};

								adapterContacts
										.registerDataSetObserver(observer);

								// Sets the adapter for the ListView
								lvRequests.setAdapter(adapterContacts);
							} else {
								showNoPendingRequestDialog();
							}

							// closing dialog
							dialogLoading.dismiss();
						}

						private void showNoPendingRequestDialog() {
							dialog.setMessage("You don't have any pending request at the moment.");
							dialog.setIcon(android.R.drawable.ic_dialog_alert);

							dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

											dialog.dismiss();
										}
									});

							dialog.show();
						}

					});
				}

			}
		});

		t.start();

	}
}
