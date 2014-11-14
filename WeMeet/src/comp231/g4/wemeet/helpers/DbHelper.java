package comp231.g4.wemeet.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "WeMeedDb";
	
	public static final String TABLE_REGISTERED_CONTACTS = "REGISTERED_CONTACTS";
	public static final String RC_ID = "ID";
	public static final String RC_NAME = "NAME";
	public static final String RC_PHONENUMBER = "PHONENUMBER";
	private static final String CREATE_REGISTERED_CONTACTS = "CREATE TABLE "+ TABLE_REGISTERED_CONTACTS+
			"("+ RC_ID +" integer primary key," +
				RC_NAME + " text not null," +
				RC_PHONENUMBER + " text not null)";
	private Context _context;
	
	public DbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		_context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_REGISTERED_CONTACTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void DeleteAccount() {
		RegisteredContactsDataSource dsRegisteredContacts = new RegisteredContactsDataSource(_context);
		dsRegisteredContacts.deleteAll();
		
	}
}
