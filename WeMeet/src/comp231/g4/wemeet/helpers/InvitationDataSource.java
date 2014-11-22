package comp231.g4.wemeet.helpers;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class InvitationDataSource {
	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	public static final String TABLE_INVITATIONS = "INVITATIONS";
	public static final String COL_NAME = "NAME";
	public static final String COL_PHONENUMBER = "PHONENUMBER";

	public static final String CREATE_INVITATIONS = "CREATE TABLE "
			+ TABLE_INVITATIONS + "(" + COL_NAME + " text not null,"
			+ COL_PHONENUMBER + " text primary key)";

	private String[] allColumns = { COL_NAME, COL_PHONENUMBER };

	public InvitationDataSource(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	public boolean addContact(String name, String phoneNumber) {
		ContentValues values = new ContentValues();
		values.put(COL_NAME, name);
		values.put(COL_PHONENUMBER, phoneNumber);

		long id = database.insert(TABLE_INVITATIONS, null, values);
		return id > 0;
	}

	public boolean deleteContact(String phoneNumber) {
		return database.delete(TABLE_INVITATIONS, COL_PHONENUMBER + " = '"
				+ phoneNumber + "'", null) > 0;
	}

	public boolean exists(String name, String phoneNumber) {
		Cursor c = database.rawQuery("select * from " + TABLE_INVITATIONS
				+ " where " + COL_NAME + "='" + name + "' and "
				+ COL_PHONENUMBER + "='" + phoneNumber + "'", null);
		if (c.moveToFirst()) {
			c.close();// closing cursor
			return true;
		}
		c.close();

		return false;
	}

	public HashMap<String, String> getAllContacts() {
		HashMap<String, String> contacts = new HashMap<String, String>();

		Cursor cursor = database.query(TABLE_INVITATIONS, allColumns, null,
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			contacts.put(cursor.getString(1), cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		
		return contacts;
	}

	public boolean deleteAll() {
		return database.delete(TABLE_INVITATIONS, null, null) > 0;

	}
}
