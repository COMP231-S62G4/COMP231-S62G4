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

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;


public class AndroidClient {
	//private static final String baseURL = "http://192.168.0.104/WeMeetService/WeMeetService.svc/json/";
	private static final String baseURL = "http://10.24.70.16/WeMeetService/WeMeetService.svc/json/";

	public boolean RegisterPhoneNumber(String phoneNumber) throws Exception{
		return Boolean
				.parseBoolean(GetData(baseURL + "Register/" + phoneNumber)
						.toString());
	}

	public boolean IsRegisteredPhoneNumber(String phoneNumber) throws Exception{

		return Boolean.parseBoolean(GetData(
				baseURL + "IsRegistered/" + phoneNumber).toString());
	}

	public boolean UnRegisterPhoneNumber(String phoneNumber) throws Exception{

		return Boolean.parseBoolean(GetData(
				baseURL + "UnRegister/" + phoneNumber).toString());
	}

	public boolean UpdateLocation(String phoneNumber, String latitude, String longitude)throws Exception {
		return Boolean.parseBoolean(GetData(
				baseURL + "UpdateLocation/" + phoneNumber + "/" + latitude
						+ "/" + longitude).toString());
	}
	
	public List<String> GetLocationRequests(String phoneNumber) throws JSONException, Exception{
		ArrayList<String> phoneNumbers = new ArrayList<String>();
		
		JSONArray array = new JSONArray(GetData(
				baseURL + "GetLocationRequests/" +
						phoneNumber).toString());
		for(int i=0;i<array.length();i++)
			phoneNumbers.add(array.getString(i));
		
		return phoneNumbers;
	}

	public LatLng GetLocation(String requesterPhoneNumber, String phoneNumber)
			throws JSONException, Exception {
		JSONObject object = new JSONObject(GetData(
				baseURL + "GetLocation/" + requesterPhoneNumber + "/"
						+ phoneNumber).toString());

		return new LatLng(Double.parseDouble(object.getString("Latitude")),
				Double.parseDouble(object.getString("Longitude")));
	}

	public boolean IsRequestSentTo(String fromPhoneNumber,
			String toPhoneNumber) throws Exception{
		return Boolean.parseBoolean(GetData(
				baseURL + "IsRequestSentTo/" + fromPhoneNumber + "/"
						+ toPhoneNumber).toString());
	}
	
	public boolean SendLocationSharingRequest(String fromPhoneNumber,
			String toPhoneNumber) throws Exception{
		return Boolean.parseBoolean(GetData(
				baseURL + "SendLocationSharingRequest/" + fromPhoneNumber + "/"
						+ toPhoneNumber).toString());
	}

	public boolean AcceptLocationSharingRequest(String fromPhoneNumber,
			String toPhoneNumber) throws Exception{
		return Boolean.parseBoolean(GetData(
				baseURL + "AcceptLocationSharingRequest/" + fromPhoneNumber
						+ "/" + toPhoneNumber).toString());
	}

	public boolean DeleteLocationSharingRequest(String fromPhoneNumber,
			String toPhoneNumber) throws Exception{
		return Boolean.parseBoolean(GetData(
				baseURL + "DeleteLocationSharingRequest/" + fromPhoneNumber
						+ "/" + toPhoneNumber).toString());

	}

	public JSONArray GetFriendsNearBy(String phoneNumber)  throws Exception{
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

	private Object GetData(String url) throws Exception {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();

			return EntityUtils.toString(entity);

		} catch (ClientProtocolException e) {
			Log.e("WeMeet_Exception", e.getMessage());
			throw e;
		} catch (IOException e) {
			Log.e("WeMeet_Exception", e.getMessage());
			throw e;
		} catch (ParseException e) {
			Log.e("WeMeet_Exception", e.getMessage());
			throw e;
		} catch (Exception e) {
			Log.e("WeMeet_Exception", e.getMessage());
			throw e;
		}
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
