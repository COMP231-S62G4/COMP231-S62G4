package comp231.g4.wemeet.servicehelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;


public class AndroidClient {
	private static final String baseURL = "http://192.168.0.109/WeMeetService/WeMeetService.svc/json/";

	public boolean RegisterPhoneNumber(String phoneNumber) {
		return Boolean
				.parseBoolean(GetData(baseURL + "Register/" + phoneNumber)
						.toString());
	}

	public boolean IsRegisteredPhoneNumber(String phoneNumber) {

		return Boolean.parseBoolean(GetData(
				baseURL + "IsRegistered/" + phoneNumber).toString());
	}

	public boolean UnRegisterPhoneNumber(String phoneNumber) {

		return Boolean.parseBoolean(GetData(
				baseURL + "UnRegister/" + phoneNumber).toString());
	}

	boolean UpdateLocation(String phoneNumber, String latitude, String longitude) {
		return Boolean.parseBoolean(GetData(
				baseURL + "UpdateLocation/" + phoneNumber + "/" + latitude
						+ "/" + longitude).toString());
	}

	LatLng GetLocation(String requesterPhoneNumber, String phoneNumber)
			throws JSONException {
		JSONObject object = new JSONObject(GetData(
				baseURL + "GetLocation/" + requesterPhoneNumber + "/"
						+ phoneNumber).toString());

		return new LatLng(Double.parseDouble(object.getString("latitude")),
				Double.parseDouble(object.getString("latitude")));
	}

	boolean SendLocationSharingRequest(String fromPhoneNumber,
			String toPhoneNumber) {
		return Boolean.parseBoolean(GetData(
				baseURL + "SendLocationSharingRequest/" + fromPhoneNumber + "/"
						+ toPhoneNumber).toString());
	}

	boolean AcceptLocationSharingRequest(String fromPhoneNumber,
			String toPhoneNumber) {
		return Boolean.parseBoolean(GetData(
				baseURL + "AcceptLocationSharingRequest/" + fromPhoneNumber
						+ "/" + toPhoneNumber).toString());
	}

	boolean DeclineLocationSharingRequest(String fromPhoneNumber,
			String toPhoneNumber) {
		return Boolean.parseBoolean(GetData(
				baseURL + "DeclineLocationSharingRequest/" + fromPhoneNumber
						+ "/" + toPhoneNumber).toString());

	}

	public JSONArray GetFriendsNearBy(String phoneNumber) {
		JSONArray array = null;
		try {
			array = new JSONArray(GetData(
					baseURL + "GetFriendsNearBy/" + phoneNumber).toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array;
	}

	private Object GetData(String url) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();

			return EntityUtils.toString(entity);

		} catch (ClientProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	void CreateGroup(String phoneNumber, String groupName) {

	}

	void RenameGroup(String phoneNumber, String groupName) {

	}

	void AddGroupMember(String requesterPhoneNumber, String groupName,
			String phoneNumber) {

	}

	void DeleteGroupMember(String requesterPhoneNumber, String groupName,
			String phoneNumber) {

	}

	void DeleteGroup(String phoneNumber, String groupName) {

	}

	List<String> GetGroups(String phoneNumber) {
		ArrayList lst = new ArrayList();
		return lst;
	}

	List<String> GetGroupMembers(String phoneNumber, String groupName) {
		ArrayList lst = new ArrayList();
		return lst;
	}
}
