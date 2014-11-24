package comp231.g4.wemeet.helpers;

import java.util.ArrayList;
import java.util.List;

import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.model.ContactPhone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SharedLocationDataSource {
	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	public static final String TABLE_SHARED_LOCATION = "SHARED_LOCATION";
	public static final String COL_ID = "ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_PHONENUMBER = "PHONENUMBER";

	public static final String CREATE_SHARED_LOCATION = "CREATE TABLE "
			+ TABLE_SHARED_LOCATION + "(" + COL_ID + " integer primary key autoincrement,"
			+ COL_NAME + " text not null," + COL_PHONENUMBER + " text not null)";

	private String[] allColumns = { COL_ID, COL_NAME, COL_PHONENUMBER };

	public SharedLocationDataSource(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	public boolean addContact(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(COL_NAME, contact.name);
		values.put(COL_PHONENUMBER, contact.numbers.get(0).number);

		long id = database.insert(TABLE_SHARED_LOCATION, null, values);
		return id > 0;
	}

	public boolean deleteContact(int contactId) {
		return database.delete(TABLE_SHARED_LOCATION, COL_ID + " = "
				+ contactId, null) > 0;
	}

	public Contact exists(Contact contact) {
		Contact ret_val = null;

		for (int i = 0; i < contact.numbers.size(); i++) {
			Cursor c = database.rawQuery("select * from "
					+ TABLE_SHARED_LOCATION + " where " + COL_NAME + "='"
					+ contact.name + "' and " + COL_PHONENUMBER + "='"
					+ contact.numbers.get(i).number + "'", null);
			if (c.moveToFirst()) {
				ret_val = new Contact(c.getString(0), c.getString(1));
				ret_val.numbers.add(new ContactPhone(c.getString(2), ""));
				c.close();// closing cursor
				return ret_val;
			}
			c.close();
		}

		return ret_val;
	}

	public List<Contact> getAllContacts() {
		List<Contact> contacts = new ArrayList<Contact>();

		Cursor cursor = database.query(TABLE_SHARED_LOCATION, allColumns,
				null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			contacts.add(cursorToContact(cursor));
			cursor.moveToNext();
		}
		return contacts;
	}

	private Contact cursorToContact(Cursor cursor) {
		Contact contact = new Contact(cursor.getString(0), cursor.getString(1));
		contact.addNumber(cursor.getString(2), "");
		return contact;
	}

	public boolean deleteAll() {
		return database.delete(TABLE_SHARED_LOCATION, null, null) > 0;

	}
}
