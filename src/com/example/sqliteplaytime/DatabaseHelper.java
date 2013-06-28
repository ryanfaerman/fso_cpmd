package com.example.sqliteplaytime;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "pagesDB";
	private static final int DATABASE_VERSION = 18;
	private static final String TAG = "TRACE";
	
	public static final String CREATE_TABLE = "create table if not exists pages (id integer primary key autoincrement, remote_id varchar(255), title varchar(255), body text, dirty boolean default true, updated_at integer, category varchar(255))";
	public static final String CREATE_INDEX = "create unique index if not exists remote_id_idx ON pages(remote_id);";
	
	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "creating tables");
		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_INDEX);


		Log.i(TAG, "done");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "dropping tables");
		db.execSQL("drop table if exists pages");
		Log.i(TAG, "drop complete");
		
		onCreate(db);
	}
	
	

}
