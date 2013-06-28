package com.example.sqliteplaytime;

import java.sql.Date;
import java.util.ArrayList;


import java.util.List;

import com.example.sqliteplaytime.R.string;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.GetCallback;
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
		
		
		Log.i("TRACE", "setting array adapter");
		final ListView tweet_list = (ListView) findViewById(R.id.tweet_list);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(_context,android.R.layout.simple_list_item_1, all());
		tweet_list.setAdapter(arrayAdapter);
		Log.i("TRACE", "done with adapter");
		
		tweet_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub

				Log.i("TRACE", String.valueOf(id));
				Log.i("TRACE", parent.getItemAtPosition(pos).toString());
				Intent myIntent = new Intent(getBaseContext(), FormActivity.class);
				myIntent.putExtra("page_title", parent.getItemAtPosition(pos).toString());
				Log.i("TRACE", "z");
				startActivity(myIntent);
			}
		});
		
		((Button) findViewById(R.id.add_page)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getBaseContext(), FormActivity.class);
				startActivity(myIntent);
				
			}
		});
		
		((Button) findViewById(R.id.sort_by_alpha_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(_context,android.R.layout.simple_list_item_1, alpha());
				tweet_list.setAdapter(arrayAdapter);
				
			}
		});
		
((Button) findViewById(R.id.sort_by_category_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(_context,android.R.layout.simple_list_item_1, all());
				tweet_list.setAdapter(arrayAdapter);
				
			}
		});

		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.i("TRACE", "back from activity");
    	if (resultCode == RESULT_OK && requestCode == 0) {
    		Log.i("TRACE", "RESULT OK");
    	}
    }
	
	public void sync() {
		Log.i("TRACE", "starting sync!");
		String title;
		String body;
		String updated_at;
		String remote_id;
		String queryString;
		String category;
		// remote missing, local new
		Log.i("TRACE", "pushing new to remote");
		String[] fields = {"id","title", "body", "category", "remote_id"};
		final Cursor c = _db.query("pages", fields, "dirty=? and remote_id=''", new String[] { String.valueOf(true) }, null, null, null);
		Log.i("TRACE", "q");
		if(c.getCount() > 0){
			Log.i("TRACE", "w");
			final ParseObject npo = new ParseObject("Page");
			while(c.moveToNext()){
				Log.i("TRACE", "e");
				npo.put("title", c.getString(c.getColumnIndex("title")));
				npo.put("body", c.getString(c.getColumnIndex("body")));
				npo.put("category", c.getString(c.getColumnIndex("category")));
				try {
					npo.save();
//					Log.i("TRACE", e.getMessage());
					// mark locally as clean and save remote_id. ensure newest updated_at
					remote_id = String.valueOf(npo.getObjectId());
					String id = c.getString(c.getColumnIndex("id"));
					queryString = "update pages set remote_id='"+remote_id+"', updated_at='"+npo.getUpdatedAt()+"', dirty='false'"
							+ " where id='"+id+"'";
					Log.i("TRACE", queryString);
					_db.execSQL(queryString);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
//					@Override
//					public void done(ParseException e) {
						
//					}
				//});
			}
		}
		
		
		
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
						
		
						
						// remote new, local missing
						
						query = "insert into pages (title, body, category, updated_at, remote_id, dirty) values"
								+ "('"+title+"','"+body+"', '"+category+"' ,'"+p.getUpdatedAt()+"','"+remote_id+"', 'false')";
						
						try {
							Log.i("TRACE", "remote new, local missing");
							Log.i("TRACE", query);
							_db.execSQL(query);
						} catch (Exception e2) {
							// TODO: handle exception
							
							// remote changed, local stale
							Log.i("TRACE", "remote changed, local stale");
							query = "update pages set title='"+title+"', body='"+body+"', updated_at='"+p.getUpdatedAt()+"', category='"+category+"', dirty='false' "
									+ " where remote_id='"+remote_id+"' and updated_at < '"+p.getUpdatedAt()+"'";
							Log.i("TRACE", query);
							_db.execSQL(query);
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
		
		Log.i("TRACE", "pushing updates to remote");
		final Cursor c2 = _db.query("pages", fields, "dirty=? and remote_id!=''", new String[] { String.valueOf(true) }, null, null, null);
		Log.i("TRACE", "q");
		if(c2.getCount() > 0){
			Log.i("TRACE", "w");
			final ParseObject npo = new ParseObject("Page");
			while(c2.moveToNext()){
				Log.i("TRACE", "e");
				
				
				remote_id =  c2.getString(c2.getColumnIndex("remote_id"));
				
				final ParseQuery<ParseObject> upo = ParseQuery.getQuery("Page");
				upo.getInBackground(remote_id, new GetCallback<ParseObject>() {
					
					@Override
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							object.put("title", c2.getString(c2.getColumnIndex("title")));
							object.put("body", c2.getString(c2.getColumnIndex("body")));
							object.put("category", c2.getString(c2.getColumnIndex("category")));
							Log.i("TRACE", "parse object updated");
							object.saveInBackground();
					    } else {
					      // something went wrong
					    }
						
					}
				});
				
				try {
					npo.save();
//					Log.i("TRACE", e.getMessage());
					// mark locally as clean and save remote_id. ensure newest updated_at
					remote_id = String.valueOf(npo.getObjectId());
					String id = c2.getString(c.getColumnIndex("id"));
					queryString = "update pages set remote_id='"+remote_id+"', updated_at='"+npo.getUpdatedAt()+"', dirty='false'"
							+ " where id='"+id+"'";
					Log.i("TRACE", queryString);
					_db.execSQL(queryString);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
//					@Override
//					public void done(ParseException e) {
						
//					}
				//});
			}
		}
		
	}
	
	public List<String> all() {
		List<String> pages = new ArrayList<String>();
		String[] fields = {"title"};
		Cursor c = _db.query("pages", fields, null, null, null, null, "category desc");
		
		if(c.getCount() > 0){
			while(c.moveToNext()){
				pages.add(c.getString(c.getColumnIndex("title")));
			}
		}
		
		return pages;
	}
	
	public List<String> alpha() {
		List<String> pages = new ArrayList<String>();
		String[] fields = {"title"};
		Cursor c = _db.query("pages", fields, null, null, null, null, "title asc");
		
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
