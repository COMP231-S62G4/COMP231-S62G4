package comp231.g4.wemeet.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import comp231.g4.wemeet.model.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PasswordRecoveryDataSource {
	// Database fields
	private SQLiteDatabase database;
	private DbHelper dbHelper;
	public static final String TABLE_SECURITY_SETUP = "SECURITY_SETUP";
	public static final String COL_QUESTION = "QUESTION";
	public static final String COL_ANSWER = "ANSWER";

	public static final String CREATE_SECURITY_SETUP = "CREATE TABLE "
			+ TABLE_SECURITY_SETUP + "(" + COL_QUESTION + " text not null,"
			+ COL_ANSWER + " text not null)";

	public PasswordRecoveryDataSource(Context context) {
		dbHelper = new DbHelper(context);
	}

	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	public boolean add(String question, String answer) {
		ContentValues values = new ContentValues();
		// values.put(COL_ID, groupId);
		values.put(COL_QUESTION, question.trim());
		values.put(COL_ANSWER, answer.trim());

		long id = database.insert(TABLE_SECURITY_SETUP, null, values);
		return id > 0;
	}

	public HashMap<String, String> getAll() {
		HashMap<String, String> qa = new HashMap<String, String>();
		
		Cursor c = database.rawQuery("select * from " + TABLE_SECURITY_SETUP
				+ ";", null);
		c.moveToFirst();
		while(!c.isAfterLast()) {
			qa.put(c.getString(0), c.getString(1));
			c.moveToNext();
		}
		c.close();// closing cursor

		return qa;
	}

	public String getAnswer(String question) {
		String answer = null;
		Cursor c = database.rawQuery("select "+COL_ANSWER+" from " + TABLE_SECURITY_SETUP
				+ " where " + COL_QUESTION + "='" + question.trim() + "'", null);
		if (c.moveToFirst()) {
			answer = c.getString(0); 
		}
		c.close();

		return answer;
	}

	public boolean deleteAll() {
		open();
		boolean retVal = database.delete(TABLE_SECURITY_SETUP, null, null) > 0;
		close();
		return retVal;
	}
}
