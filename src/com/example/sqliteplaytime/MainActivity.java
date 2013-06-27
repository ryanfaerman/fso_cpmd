package com.example.sqliteplaytime;

import java.util.ArrayList;

import java.util.List;

import com.example.sqliteplaytime.R.string;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	SQLiteOpenHelper _dbHelper;
	SQLiteDatabase _db;
	Context _context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_context = this;
		_dbHelper = new DatabaseHelper(_context);
		_db = _dbHelper.getWritableDatabase();
		
		
		Log.i("TRACE", String.valueOf(all()));
		
		ListView tweet_list = (ListView) findViewById(R.id.tweet_list);
		Log.i("TRACE", "a");
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(_context,android.R.layout.simple_list_item_1, all());
		Log.i("TRACE", "B");
		tweet_list.setAdapter(arrayAdapter);
		Log.i("TRACE", "c");
		
		RadioGroup radioGrp = (RadioGroup)findViewById(R.id.radioGrp);
		radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup arg0, int id) {
				switch (id) {
				case R.id.radio_retweeted:
					Log.i("TRACE", "ID");
					ArrayAdapter<String> arrayAdapter_retweeted = new ArrayAdapter<String>(_context,android.R.layout.simple_list_item_1, retweeted());
					((ListView) findViewById(R.id.tweet_list)).setAdapter(arrayAdapter_retweeted);
					break;
				case R.id.radio_all:
					Log.i("TRACE", "ID");
					ArrayAdapter<String> arrayAdapter_all = new ArrayAdapter<String>(_context,android.R.layout.simple_list_item_1, all());
					((ListView) findViewById(R.id.tweet_list)).setAdapter(arrayAdapter_all);
					break;
				
				default:
					Log.i("TRACE", "ERROR RADIO GROUP FUNCTION");
					break;
				}
			}
		});
		
	}
	
	public List<String> all() {
		List<String> tweets = new ArrayList<String>();
		String[] fields = {"body"};
		Cursor c = _db.query("tweets", fields, null, null, null, null, null);
		
		if(c.getCount() > 0){
			while(c.moveToNext()){
				tweets.add(c.getString(c.getColumnIndex("body")));
			}
		}
		
		return tweets;
	}
	
	public List<String> retweeted() {
		List<String> tweets = new ArrayList<String>();
		String[] fields = {"body"};
		Cursor c = _db.query("tweets", fields, "retweeted=?", new String[] { String.valueOf(true) }, null, null, null);
		
		if(c.getCount() > 0){
			while(c.moveToNext()){
				tweets.add(c.getString(c.getColumnIndex("body")));
			}
		}
		
		return tweets;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
