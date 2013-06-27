package com.example.sqliteplaytime;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "tweetDB";
	private static final int DATABASE_VERSION = 4;
	private static final String TAG = "TRACE";
	
	public static final String CREATE_USER_TABLE = "create table if not exists users (" +
		  "id INTEGER PRIMARY KEY,"+
		  "name varchar(255),"+
		  "created_at datetime default current_timestamp,"+
		  "profile_image_url varchar(255),"+
		  "location varchar(255),"+
		  "followers_count int default 0,"+
		  "verified boolean default false"+
		");";
	
	public static final String CREATE_TWEET_TABLE = "create table if not exists tweets ("+
		  "id INTEGER PRIMARY KEY,"+
		  "user_id integer,"+
		  "created_at datetime default current_timestamp,"+
		  "body varchar(255),"+
		  "retweeted boolean default false"+
		");";
	
	public static final String CREATE_MENTIONS_TABLE = "create table if not exists mentions ("+
		  "tweet_id integer,"+
		  "user_id integer"+
		");";
	
	DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "creating tables");
		db.execSQL(CREATE_USER_TABLE);
		db.execSQL(CREATE_TWEET_TABLE);
		db.execSQL(CREATE_MENTIONS_TABLE);
		
		
		ContentValues user_values_0 = new ContentValues();
		user_values_0.put("id", 119476949);
		user_values_0.put("name", "OAuth Dancer");
		user_values_0.put("created_at", "Wed Mar 03 19:37:35 +0000 2010");
		user_values_0.put("profile_image_url", "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg");
		user_values_0.put("location", "San Francisco CA");
		user_values_0.put("followers_count", 28);
		user_values_0.put("verified", false);
		db.insert("users", null, user_values_0);
		Log.i(TAG, "added users");

		ContentValues user_values_1 = new ContentValues();
		user_values_1.put("id", 8285392);
		user_values_1.put("name", "Raffi Krikorian");
		user_values_1.put("created_at", "Sun Aug 19 14:24:06 +0000 2007");
		user_values_1.put("profile_image_url", "http://a0.twimg.com/profile_images/1270234259/raffi-headshot-casual_normal.png");
		user_values_1.put("location", "San Francisco California");
		user_values_1.put("followers_count", 18752);
		user_values_1.put("verified", false);
		db.insert("users", null, user_values_1);
		Log.i(TAG, "added users");

		ContentValues user_values_2 = new ContentValues();
		user_values_2.put("id", 819797);
		user_values_2.put("name", "Taylor Singletary");
		user_values_2.put("created_at", "Wed Mar 07 22:23:19 +0000 2007");
		user_values_2.put("profile_image_url", "http://a0.twimg.com/profile_images/2546730059/f6a8zq58mg1hn0ha8vie_normal.jpeg");
		user_values_2.put("location", "San Francisco CA");
		user_values_2.put("followers_count", 7126);
		user_values_2.put("verified", "false");
		db.insert("users", null, user_values_2);
		Log.i(TAG, "added users");

		ContentValues tweet_values_0 = new ContentValues();
		tweet_values_0.put("id", "240558470661799936");
		tweet_values_0.put("user_id", 119476949);
		tweet_values_0.put("created_at", "Tue Aug 28 21:16:23 +0000 2012");
		tweet_values_0.put("body", "just another test");
		tweet_values_0.put("retweeted", "false");
		db.insert("tweets", null, tweet_values_0);
		Log.i(TAG, "added tweets");

		ContentValues tweet_values_1 = new ContentValues();
		tweet_values_1.put("id", "240556426106372096");
		tweet_values_1.put("user_id", 8285392);
		tweet_values_1.put("created_at", "Tue Aug 28 21:08:15 +0000 2012");
		tweet_values_1.put("body", "lecturing at the analyzing big data with twitter class at @cal with @othman  http://t.co/bfj7zkDJ");
		tweet_values_1.put("retweeted", "false");
		db.insert("tweets", null, tweet_values_1);
		Log.i(TAG, "added tweets");

		ContentValues tweet_values_2 = new ContentValues();
		tweet_values_2.put("id", "240539141056638977");
		tweet_values_2.put("user_id", 819797);
		tweet_values_2.put("created_at", "Tue Aug 28 19:59:34 +0000 2012");
		tweet_values_2.put("body", "You'd be right more often if you thought you were wrong.");
		tweet_values_2.put("retweeted", "true");
		db.insert("tweets", null, tweet_values_2);
		Log.i(TAG, "added tweets");

		ContentValues mention_values_0 = new ContentValues();
		mention_values_0.put("tweet_id", "240556426106372096");
		mention_values_0.put("user_id", 119476949);
		db.insert("mentions", null, mention_values_0);
		Log.i(TAG, "added mentions");

		ContentValues mention_values_1 = new ContentValues();
		mention_values_1.put("tweet_id", "240556426106372096");
		mention_values_1.put("user_id", 819797);
		db.insert("mentions", null, mention_values_1);
		Log.i(TAG, "added mentions");


		Log.i(TAG, "done");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "dropping tables");
		db.execSQL("drop table if exists users");
		db.execSQL("drop table if exists tweets");
		db.execSQL("drop table if exists mentions");
		Log.i(TAG, "drop complete");
		
		onCreate(db);
	}
	
	

}
