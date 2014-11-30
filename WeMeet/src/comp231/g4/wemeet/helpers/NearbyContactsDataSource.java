package comp231.g4.wemeet.helpers;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import comp231.g4.wemeet.model.NearbyContact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NearbyContactsDataSource {
	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	public static final String TABLE_NEARBY_CONTACTS = "NEARBY_CONTACTS";
	public static final String COL_PHONENUMBER = "PHONENUMBER";
	public static final String COL_LATITUDE = "LATITUDE";
	public static final String COL_LONGITUDE = "LONGITUDE";
	public static final String COL_DISTANCE = "DISTANCE";
	public static final String COL_LAST_SEEN = "LAST_SEEN";

	public static final String CREATE_NEARBY_CONTACTS = "CREATE TABLE "
			+ TABLE_NEARBY_CONTACTS + "(" + COL_PHONENUMBER + " text primary key,"
			+ COL_LATITUDE + " text not null," 
			+ COL_LONGITUDE + " text not null,"
			+ COL_DISTANCE + " text not null,"
			+ COL_LAST_SEEN + " text not null)";


	public NearbyContactsDataSource(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	public boolean addNearbyContact(NearbyContact contact) {
		ContentValues values = new ContentValues();
		values.put(COL_PHONENUMBER, contact.phoneNumber);
		values.put(COL_LATITUDE, contact.location.latitude);
		values.put(COL_LONGITUDE, contact.location.longitude);
		values.put(COL_DISTANCE, contact.distance);
		values.put(COL_LAST_SEEN, contact.lastSeen);

		long id = database.insert(TABLE_NEARBY_CONTACTS, null, values);
		return id > 0;
	}

	public List<NearbyContact> getNearbyContacts() {
		List<NearbyContact> contacts = new ArrayList<NearbyContact>();

		Cursor cursor = database.query(TABLE_NEARBY_CONTACTS, null,
				null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			NearbyContact contact = new NearbyContact(
					cursor.getString(0),
					new LatLng(Double.parseDouble(cursor.getString(1)),Double.parseDouble(cursor.getString(2))),
					Double.parseDouble(cursor.getString(3)),
					cursor.getString(4)
					);
			contacts.add(contact);
			
			cursor.moveToNext();
		}
		return contacts;
	}

	public boolean deleteAll() {
		open();
		boolean retVal = database.delete(TABLE_NEARBY_CONTACTS, null, null) > 0;
		close();
		return retVal;

	}
}
