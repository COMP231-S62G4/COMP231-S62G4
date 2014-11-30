package comp231.g4.wemeet.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "WeMeedDb";
	
	private Context _context;
	
	public DbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		_context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(RegisteredContactsDataSource.CREATE_REGISTERED_CONTACTS);
		db.execSQL(InvitationDataSource.CREATE_INVITATIONS);
		db.execSQL(GroupsDataSource.CREATE_GROUPS);
		db.execSQL(GroupsDataSource.CREATE_GROUP_MEMBERS);
		db.execSQL(SharedLocationDataSource.CREATE_SHARED_LOCATION);
		db.execSQL(NearbyContactsDataSource.CREATE_NEARBY_CONTACTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void DeleteAccount() {
		//removing all local data
		RegisteredContactsDataSource dsRegisteredContacts = new RegisteredContactsDataSource(_context);
		dsRegisteredContacts.deleteAll();
		
		InvitationDataSource dsInvitations = new InvitationDataSource(_context);
		dsInvitations.deleteAll();
		
		GroupsDataSource dsGroups = new GroupsDataSource(_context);
		dsGroups.deleteAll();
		
		SharedLocationDataSource dsSharedLocations = new SharedLocationDataSource(_context);
		dsSharedLocations.deleteAll();
		
		NearbyContactsDataSource dsNearby = new NearbyContactsDataSource(_context);
		dsNearby.deleteAll();
	}
}
