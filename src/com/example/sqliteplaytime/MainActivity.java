package com.example.sqliteplaytime;

import java.sql.Date;
import java.util.ArrayList;


import java.util.List;

import com.example.sqliteplaytime.R.string;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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
		
		Parse.initialize(this, "umROMo87Wx1C9sUdX3UM2TV512CemMd0m6jFOzdZ", "LSQLLijsgCeNLFDvI00UF3h6dZraiSNJmRNLp9hU");
		ParseAnalytics.trackAppOpened(getIntent());
		
		sync();
		
		
		Log.i("TRACE", String.valueOf(all()));
		
		ListView tweet_list = (ListView) findViewById(R.id.tweet_list);
		Log.i("TRACE", "a");
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(_context,android.R.layout.simple_list_item_1, all());
		Log.i("TRACE", "B");
		tweet_list.setAdapter(arrayAdapter);
		Log.i("TRACE", "c");
		
		tweet_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub
				Log.i("TRACE", parent.getItemAtPosition(pos).toString());
			}
		});
		
		RadioGroup radioGrp = (RadioGroup)findViewById(R.id.radioGrp);
		radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup arg0, int id) {
				switch (id) {
				case R.id.radio_retweeted:
					Log.i("TRACE", "ID");
					
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
	
	public void sync() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Page");
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> pageList, ParseException e) {
				if(e==null) {
					ContentValues parseValue = new ContentValues();
					String title;
					String body;
					String updated_at;
					String remote_id;
					String query;
					String category;
					for(ParseObject p : pageList) {
						parseValue.clear();
						Log.i("TRACE", String.valueOf(p.get("title")));
						title = "";
						body = "";
						updated_at = "";
						remote_id = "";
						category = "";
								
						Log.i("TRACE", "k");
						title = String.valueOf(p.get("title"));
						body = String.valueOf(p.get("body"));
						updated_at = String.valueOf(p.get("updatedAt"));
						remote_id = String.valueOf(p.getObjectId());
						category = String.valueOf(p.get("category"));
						
						Log.i("TRACE", "j");
						parseValue.put("title", String.valueOf(p.get("title")));
						parseValue.put("body", String.valueOf(p.get("body")));
						parseValue.put("updated_at", String.valueOf(p.get("updatedAt")));
						parseValue.put("remote_id", String.valueOf(p.getObjectId()));
						Log.i("TRACE", "m");
						
						// remote new, local missing
						query = "insert into pages (title, body, category, updated_at, remote_id, dirty) values"
								+ "('"+title+"','"+body+"', '"+category+"' ,'"+p.getUpdatedAt()+"','"+remote_id+"', 'false')";
						
						try {
							Log.i("TRACE", query);
							_db.execSQL(query);
						} catch (Exception e2) {
							// TODO: handle exception
							
							// remote changed, local stale
							query = "update pages set title='"+title+"', body='"+body+"', updated_at='"+p.getUpdatedAt()+"', category='"+category+"', dirty='false' "
									+ " where remote_id='"+remote_id+"' and updated_at < '"+p.getUpdatedAt()+"'";
							Log.i("TRACE", query);
							_db.execSQL(query);
						}
						
						
						// remote missing or stale, local new
						String[] fields = {"id","title", "body", "category"};
						final Cursor c = _db.query("pages", fields, "dirty=?", new String[] { String.valueOf(true) }, null, null, null);
						Log.i("TRACE", "q");
						if(c.getCount() > 0){
							Log.i("TRACE", "w");
							final ParseObject npo = new ParseObject("page");
							while(c.moveToNext()){
								Log.i("TRACE", "e");
								npo.put("title", c.getString(c.getColumnIndex("title")));
								npo.put("body", c.getString(c.getColumnIndex("body")));
								npo.put("category", c.getString(c.getColumnIndex("category")));
								npo.saveInBackground(new SaveCallback() {
									
									@Override
									public void done(ParseException e) {
										Log.i("TRACE", e.getMessage());
										// mark locally as clean and save remote_id. ensure newest updated_at
										String remote_id = String.valueOf(npo.getObjectId());
										String id = c.getString(c.getColumnIndex("category"));
										String query = "update pages set remote_id='"+remote_id+"', updated_at='"+npo.getUpdatedAt()+"', dirty='false'"
												+ " where id='"+id+"'";
										Log.i("TRACE", query);
										_db.execSQL(query);
									}
								});
							}
						}
					}
					
					ListView tweet_list = (ListView) findViewById(R.id.tweet_list);
					Log.i("TRACE", "a");
					ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(_context,android.R.layout.simple_list_item_1, all());
					Log.i("TRACE", "B");
					tweet_list.setAdapter(arrayAdapter);
					Log.i("TRACE", "c");
				} else {
					Log.i("TRACE", "Error: " + e.getMessage());
				}
				
			}
		});
		
	}
	
	public List<String> all() {
		List<String> pages = new ArrayList<String>();
		String[] fields = {"title"};
		Cursor c = _db.query("pages", fields, null, null, null, null, null);
		
		if(c.getCount() > 0){
			while(c.moveToNext()){
				pages.add(c.getString(c.getColumnIndex("title")));
			}
		}
		
		return pages;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
