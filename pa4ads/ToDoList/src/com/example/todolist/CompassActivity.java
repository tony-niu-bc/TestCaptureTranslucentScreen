package com.example.todolist;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class CompassActivity extends Activity {

	@Override
	protected void onDestroy() 
	{
		Log.i("todolist", "CompassActivity - onDestroy");
				
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.i("todolist", "CompassActivity - onCreate");
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.compass_view);
		
		Intent i = getIntent();
		
		if ( null != i )
		{
			Uri uri = i.getData();
			
			if ( null != uri )
			{
				Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG)
				     .show();
			}
		}
				
		CompassView cv = (CompassView)findViewById(R.id.compassView);
		cv.setBearing(45);
		cv.setPitch(45);
		cv.setRoll(45);
	}

}
