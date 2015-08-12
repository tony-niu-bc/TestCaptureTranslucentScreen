package com.example.todolist;

import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

public class TestSearchActivity extends FragmentActivity 
								implements LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter sca = null;
	private ToDolistFragment    todoListFragment = null;

	private void parseIntent()
	{		
		if (Intent.ACTION_SEARCH.equals(getIntent().getAction()))
		{			
			Log.i("todolist", "TestSearchActivity - parseIntent - " + getIntent().getStringExtra(SearchManager.QUERY));

			Bundle arg = new Bundle();
			arg.putString(SearchManager.QUERY, getIntent().getStringExtra(SearchManager.QUERY));
			
			getSupportLoaderManager().initLoader(0, arg, this);			
		}		

		if (Intent.ACTION_VIEW.equals(getIntent().getAction()))
		{			
			Log.i("todolist", "TestSearchActivity - parseIntent - " + getIntent().getDataString());

			Uri uri = getIntent().getData();
			List<String> strParamArray = uri.getPathSegments();
			
			String strID = "ALL";
			
			if (1 < strParamArray.size())
			{
				strID = strParamArray.get(1);
			}
			
			Bundle arg = new Bundle();
			arg.putString("ID", strID);
			
			getSupportLoaderManager().initLoader(0, arg, this);			
		}				
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.i("todolist", "TestSearchActivity - onCreate");
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.search_activity);
		 
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		
        todoListFragment = (ToDolistFragment)fm.findFragmentById(R.id.SearchFragment);
		
		sca = new SimpleCursorAdapter(this, 
									  android.R.layout.simple_list_item_2,
									  null, 
									  new String[]{MySQLiteOpenHelper.KEY_TASK, MySQLiteOpenHelper.KEY_CREATION_DATE}, 
									  new int[]{android.R.id.text1, android.R.id.text2}, 
									  0);
		
		todoListFragment.setListAdapter(sca);
		
		parseIntent();
	}

	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);
		
		Log.i("todolist", "TestSearchActivity - onNewIntent");
		
		parseIntent();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) 
	{
		Log.i("todolist", "TestSearchActivity - onCreateLoader - " + arg1.getString(SearchManager.QUERY));
		
		String strWhereClause = null;
		
		if ((null != arg1.getString(SearchManager.QUERY)) && 
			(!arg1.getString(SearchManager.QUERY).trim().equals("")))
		{
			strWhereClause = MySQLiteOpenHelper.KEY_TASK + " like \'%" + arg1.getString(SearchManager.QUERY) + "%\'";
		}
		else if ((null != arg1.getString("ID")) && 
				 (!arg1.getString("ID").trim().equals("ALL")))
		{
			strWhereClause = MySQLiteOpenHelper.KEY_ID + " = " + arg1.getString("ID").trim();
		}
		
		return new CursorLoader(this, 
								ToDoContentProvider.CONTENT_URI, 
								null,
								strWhereClause,
								null,
								null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) 
	{
		if (null != sca)
		{
			sca.swapCursor(arg1);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) 
	{
		if (null != sca)
		{
			sca.swapCursor(null);
		}
	}

}
