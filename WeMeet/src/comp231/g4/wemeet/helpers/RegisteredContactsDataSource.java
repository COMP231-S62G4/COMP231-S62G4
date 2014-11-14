package comp231.g4.wemeet.helpers;

import java.util.ArrayList;
import java.util.List;

import comp231.g4.wemeet.model.Contact;
import comp231.g4.wemeet.model.ContactPhone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RegisteredContactsDataSource {
	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	private String[] allColumns = { DbHelper.RC_ID, DbHelper.RC_NAME,
			DbHelper.RC_PHONENUMBER };

	public RegisteredContactsDataSource(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public boolean addContact(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(DbHelper.RC_ID, contact.id);
		values.put(DbHelper.RC_NAME, contact.name);
		values.put(DbHelper.RC_PHONENUMBER, contact.numbers.get(0).number);

		long id = database.insert(DbHelper.TABLE_REGISTERED_CONTACTS, null,
				values);
		return id > 0;
	}

	public boolean deleteContact(Contact contact) {
		return database.delete(DbHelper.TABLE_REGISTERED_CONTACTS,
				DbHelper.RC_ID + " = " + contact.id, null) > 0;
	}

	public Contact exists(Contact contact) {
		Contact ret_val = null;

		for (int i = 0; i < contact.numbers.size(); i++) {
			Cursor c = database.rawQuery("select * from "
					+ DbHelper.TABLE_REGISTERED_CONTACTS + " where "
					+ DbHelper.RC_NAME + "='" + contact.name + "' and "
					+ DbHelper.RC_PHONENUMBER + "='"
					+ contact.numbers.get(i).number + "'", null);
			if (c.moveToFirst()) {
				ret_val = new Contact(c.getString(0), c.getString(1));
				ret_val.numbers.add(new ContactPhone(c.getString(2), ""));
				c.close();//closing cursor
				return ret_val;
			}
			c.close();
		}

		return ret_val;
	}

	public List<Contact> getAllContacts(Contact contact) {
		List<Contact> contacts = new ArrayList<Contact>();

		Cursor cursor = database.query(DbHelper.TABLE_REGISTERED_CONTACTS,
				allColumns, null, null, null, null, null);
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
		return database.delete(DbHelper.TABLE_REGISTERED_CONTACTS, null, null) > 0;

	}
}
