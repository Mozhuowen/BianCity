package com.putaotown.localio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PushMessHelper extends SQLiteOpenHelper
{
	public static int VERSION = 2;
	private Context mContext;
	private static final String CREATE_TABLE = "create table Pushmess (" 
			+"id integer primary key autoincrement,"
			+"type integer,"
			+"comment text,"
			+"good text,"
			+"system text,"
			+"community text,"
			+"hasread integer,"
			+"userid integer,"
			+"receivetime integer)";
	private static final String DROP_TABLE = "drop table Pushmess";

	public PushMessHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(DROP_TABLE);
		db.execSQL(CREATE_TABLE);
	}
	
}