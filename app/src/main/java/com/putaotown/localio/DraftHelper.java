package com.putaotown.localio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DraftHelper extends SQLiteOpenHelper
{
	public static int VERSION = 1;
	private Context mContext;
	private static final String CREATE_TABLE = "create table Draft (" 
			+ "id integer primary key autoincrement,"
			+ "randomcode text,"
			+ "type integer,"
			+ "title text,"
			+ "cover text,"
			+ "content text,"
			+ "images text,"
			+ "townid integer,"
			+ "freeaddr text,"
			+ "isfinish integer,"
			+ "geoinfo text)";
			

	public DraftHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
}