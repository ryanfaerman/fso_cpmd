package com.example.sqliteplaytime;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

public class FormActivity extends Activity {
	Context _context;
	
	String title;
	String body;
	String updated_at;
	String query;
	String id;
	String remote_id;
	String category;
	
	SQLiteOpenHelper _dbHelper;
	SQLiteDatabase _db;
	
	boolean use_update = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);
		_context = this;
		
		_dbHelper = new DatabaseHelper(_context);
		_db = _dbHelper.getWritableDatabase();
		
		Log.i("FORM", "LOADING TEST ACTIVITY");
		
		Bundle extras = getIntent().getExtras();
		Log.i("FORM", "a");
		if (extras != null) {
		    String value = extras.getString("page_title");
		    Log.i("FORM", value);
		   
		    String[] fields = {"id","title", "body", "category", "remote_id"};
			final Cursor c = _db.query("pages", fields, "title=?", new String[] { value }, null, null, null);
			Log.i("FORM", "q");
			
			if(c.getCount() > 0){
				Log.i("FORM", "w");
				use_update = true;
				
				while(c.moveToNext()){
					Log.i("FORM", "e");
					id = c.getString(c.getColumnIndex("id"));
					title = c.getString(c.getColumnIndex("title"));
					body =  c.getString(c.getColumnIndex("body"));
					category = c.getString(c.getColumnIndex("category"));
					remote_id = c.getString(c.getColumnIndex("remote_id"));
				}
			}
		}
		
		((EditText) findViewById(R.id.title_text)).setText(title);
		((EditText) findViewById(R.id.category_text)).setText(category);
		((EditText) findViewById(R.id.body_text)).setText(body);
		
		Log.i("FORM", "remote_id: "+remote_id);
		
		((Button) findViewById(R.id.cancel_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
		((Button) findViewById(R.id.save_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				title = ((EditText) findViewById(R.id.title_text)).getText().toString();
				category = ((EditText) findViewById(R.id.category_text)).getText().toString();
				body = ((EditText) findViewById(R.id.body_text)).getText().toString();
				
				if(use_update) {
					query = "update pages set title='"+title+"', body='"+body+"', category='"+category+"', dirty='true' "
							+ " where id='"+id+"'";
				} else {
					query = "insert into pages (title, body, category, updated_at, dirty) values"
							+ "('"+title+"','"+body+"', '"+category+"' ,'0', 'true')";
				}
				
				Log.i("TRACE", query);
				_db.execSQL(query);
				
				
				finish();
				
			}
		});
		
	}
	
	public void finish() {
		_db.close();
	    super.finish();
	}
}
