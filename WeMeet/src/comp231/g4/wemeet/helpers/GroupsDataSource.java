package comp231.g4.wemeet.helpers;

import java.util.ArrayList;
import java.util.List;
import comp231.g4.wemeet.model.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GroupsDataSource {
	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	public static final String TABLE_GROUPS = "GROUPS";
	public static final String TABLE_GROUP_MEMBERS = "GROUP_MEMBERS";
	public static final String COL_ID = "ID";
	public static final String COL_GROUP_ID = "GROUP_ID";
	public static final String COL_NAME = "NAME";
	public static final String COL_PHONENUMBER = "PHONENUMBER";

	public static final String CREATE_GROUPS = "CREATE TABLE " + TABLE_GROUPS
			+ "(" + COL_ID + " integer primary key autoincrement," + COL_NAME
			+ " text not null)";

	public static final String CREATE_GROUP_MEMBERS = "CREATE TABLE "
			+ TABLE_GROUP_MEMBERS + "(" + COL_GROUP_ID + " integer," + COL_NAME
			+ " text not null," + COL_PHONENUMBER + " text not null)";

	private String[] allColumns = { COL_ID, COL_NAME };

	public GroupsDataSource(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	public boolean addGroup(String name) {
		/*
		 * int groupId = 1;
		 * 
		 * Cursor c = database.rawQuery("select MAX("+COL_ID+") from " +
		 * TABLE_GROUPS, null); if (c.moveToFirst()) { groupId = c.getInt(0) +
		 * 1; } c.close();// closing cursor
		 */

		ContentValues values = new ContentValues();
		// values.put(COL_ID, groupId);
		values.put(COL_NAME, name.trim());

		long id = database.insert(TABLE_GROUPS, null, values);
		return id > 0;
	}

	public int getGroupId(String name) {
		int groupId = -1;

		Cursor c = database.rawQuery("select " + COL_ID + " from "
				+ TABLE_GROUPS + " where " + COL_NAME + "='" + name + "';",
				null);
		if (c.moveToFirst()) {
			groupId = c.getInt(0);
		}
		c.close();// closing cursor

		return groupId;
	}

	public List<GroupMember> getGroupMemebers(int groupId) {
		List<GroupMember> groupMemebers = new ArrayList<GroupMember>();

		Cursor c = database.rawQuery("select * from " + TABLE_GROUP_MEMBERS
				+ " where " + COL_GROUP_ID + "=" + groupId + ";", null);

		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				groupMemebers.add(new GroupMember(groupId, c.getString(1), c
						.getString(2)));
				c.moveToNext();
			}
		}
		c.close();// closing cursor

		return groupMemebers;
	}

	public boolean deleteGroup(String name) {
		return database.delete(TABLE_GROUPS, COL_NAME + " = '" + name + "'",
				null) > 0;
	}

	public boolean exists(String name) {
		Cursor c = database.rawQuery("select * from " + TABLE_GROUPS
				+ " where " + COL_NAME + "='" + name.trim() + "'", null);
		if (c.moveToFirst()) {
			c.close();// closing cursor
			return true;
		}
		c.close();

		return false;
	}

	public List<Group> getAllGroups() {
		List<Group> groups = new ArrayList<Group>();

		Cursor cursor = database.query(TABLE_GROUPS, allColumns, null, null,
				null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			groups.add(new Group(cursor.getInt(0), cursor.getString(1)));
			cursor.moveToNext();
		}
		return groups;
	}

	public boolean addGroupMember(int groupId, String name, String number) {
		Cursor c = database.rawQuery("SELECT * from " + TABLE_GROUP_MEMBERS
				+ " where " + COL_PHONENUMBER + "='" + number + "' and "
				+ COL_GROUP_ID + "=" + groupId, null);
		if (c.moveToFirst()) {
			c.close();
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(COL_GROUP_ID, groupId);
		values.put(COL_NAME, name);
		values.put(COL_PHONENUMBER, number);

		long id = database.insert(TABLE_GROUP_MEMBERS, null, values);

		return id > 0;
	}

	/*
	 * commented as its harmful method never call it public boolean deleteAll()
	 * { return database.delete(TABLE_GROUPS, null, null) > 0; }
	 */

	public boolean removeGroupMemeber(int groupId, GroupMember member) {

		long id = database
				.delete(TABLE_GROUP_MEMBERS, COL_PHONENUMBER + "='"
						+ member.number + "' and " + COL_GROUP_ID + "="
						+ groupId, null);

		return id > 0;
	}
}
