package comp231.g4.wemeet.helpers;

import java.util.ArrayList;

import comp231.g4.wemeet.model.Contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.content.CursorLoader;

public class ContactFetcher {
	private Context context;

	public ContactFetcher(Context c) {
		this.context = c;
	}

	public ArrayList<Contact> fetchAll() {
		ArrayList<Contact> listContacts = new ArrayList<Contact>();
		CursorLoader cursorLoader = new CursorLoader(context,
				ContactsContract.Contacts.CONTENT_URI, // uri
				null, // the columns to retrieve (all)
				null, // the selection criteria (none)
				null, // the selection args (none)
				null // the sort order (default)
		);
		// This should probably be run from an AsyncTask
		Cursor c = cursorLoader.loadInBackground();
		if (c.moveToFirst()) {
			do {
				Contact contact = loadContactData(c);
				if (contact.numbers.size() != 0
						&& contact.name.trim().length() != 0) {
					listContacts.add(contact);
				}
			} while (c.moveToNext());
		}
		c.close();
		return listContacts;
	}

	public ArrayList<Contact> searchContactsByName(String query) {
		ArrayList<Contact> listContacts = new ArrayList<Contact>();

		final String SELECTION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY
				+ " LIKE ?"
				: Contacts.DISPLAY_NAME + " LIKE ?";

		// Defines the array to hold values that replace the ?
		String[] mSelectionArgs = { "%"+query+"%" };

		CursorLoader cursorLoader = new CursorLoader(context,
				ContactsContract.Contacts.CONTENT_URI, // uri
				null, // the columns to retrieve (all)
				SELECTION, // the selection criteria
							// (none)
				mSelectionArgs, // the selection args (none)
				null // the sort order (default)
		);
		// This should probably be run from an AsyncTask
		Cursor c = cursorLoader.loadInBackground();
		if (c.moveToFirst()) {
			do {
				Contact contact = loadContactData(c);
				if (contact.numbers.size() != 0
						&& contact.name.trim().length() != 0) {
					listContacts.add(contact);
				}
			} while (c.moveToNext());
		}
		c.close();
		return listContacts;
	}

	private Contact loadContactData(Cursor c) {
		// Get Contact ID
		int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
		String contactId = c.getString(idIndex);
		
		// Get Contact Name
		int nameIndex = c
				.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		
		String contactDisplayName = c.getString(nameIndex);
		Contact contact = new Contact(contactId, contactDisplayName);
		fetchContactNumbers(c, contact);
		return contact;
	}

	public void fetchContactNumbers(Cursor cursor, Contact contact) {
		// Get numbers
		final String[] numberProjection = new String[] { Phone.NUMBER,
				Phone.TYPE, };
		Cursor phone = new CursorLoader(context, Phone.CONTENT_URI,
				numberProjection, Phone.CONTACT_ID + "= ?",
				new String[] { String.valueOf(contact.id) }, null)
				.loadInBackground();

		if (phone.moveToFirst()) {
			final int contactNumberColumnIndex = phone
					.getColumnIndex(Phone.NUMBER);
			final int contactTypeColumnIndex = phone.getColumnIndex(Phone.TYPE);

			while (!phone.isAfterLast()) {
				final String number = phone.getString(contactNumberColumnIndex);
				final int type = phone.getInt(contactTypeColumnIndex);
				String customLabel = "Custom";
				CharSequence phoneType = ContactsContract.CommonDataKinds.Phone
						.getTypeLabel(context.getResources(), type, customLabel);
				contact.addNumber(number, phoneType.toString());
				phone.moveToNext();
			}

		}
		phone.close();
	}

	public void fetchContactEmails(Cursor cursor, Contact contact) {
		// Get email
		final String[] emailProjection = new String[] { Email.DATA, Email.TYPE };

		Cursor email = new CursorLoader(context, Email.CONTENT_URI,
				emailProjection, Email.CONTACT_ID + "= ?",
				new String[] { String.valueOf(contact.id) }, null)
				.loadInBackground();

//		if (email.moveToFirst()) {
//			final int contactEmailColumnIndex = email
//					.getColumnIndex(Email.DATA);
//			final int contactTypeColumnIndex = email.getColumnIndex(Email.TYPE);
//
//			while (!email.isAfterLast()) {
//				final String address = email.getString(contactEmailColumnIndex);
//				final int type = email.getInt(contactTypeColumnIndex);
//				String customLabel = "Custom";
//				CharSequence emailType = ContactsContract.CommonDataKinds.Email
//						.getTypeLabel(context.getResources(), type, customLabel);
//				contact.addEmail(address, emailType.toString());
//				email.moveToNext();
//			}
//
//		}

		email.close();
	}

	public Contact GetContactDetails(String phoneNumber) {
		Contact contact = null;
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
	    
	    ContentResolver contentResolver = context.getContentResolver();
	    Cursor contactLookup = contentResolver.query(uri, new String[] { ContactsContract.PhoneLookup._ID,
	            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

		//Cursor cursor = new CursorLoader(context, Phone.CONTENT_URI, new String[]{ Phone.CONTACT_ID }, Phone.NUMBER +" = ?", new String[]{phoneNumber}, null).loadInBackground();
		
		if (contactLookup.moveToFirst()) {
			
				String id = contactLookup.getString(contactLookup.getColumnIndex(PhoneLookup._ID));
				String contactName = contactLookup.getString(contactLookup.getColumnIndex(PhoneLookup.DISPLAY_NAME));
				
				contact = new Contact(id, contactName);
				contact.addNumber(phoneNumber, "");
				
				contactLookup.close();
		}
		return contact;
	}
}