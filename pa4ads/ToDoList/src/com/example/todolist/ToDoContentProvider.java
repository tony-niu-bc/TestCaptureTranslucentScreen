package com.example.todolist;

import java.util.HashMap;
import java.util.List;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ToDoContentProvider extends ContentProvider 
{
	private static final String PACKAGE_NAME = "com.example.todolist";
	private static final String PATH_NAME = "todoitems";
	public  static final Uri    CONTENT_URI = Uri.parse("content://" + PACKAGE_NAME + "/" + PATH_NAME);
	
	private MySQLiteOpenHelper soh;
	
	private static final UriMatcher uriMatcher;
	private static final int        ALLROWS = 1;
	private static final int        SINGLE_ROW = 2;
	private static final int        SEARCH = 3;
	
	private static final HashMap<String, String> SEARCH_SUGGEST_PROJECTION_MAP;
	
	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		uriMatcher.addURI(PACKAGE_NAME, PATH_NAME, ALLROWS);
		uriMatcher.addURI(PACKAGE_NAME, PATH_NAME + "/#", SINGLE_ROW);
		
		uriMatcher.addURI(PACKAGE_NAME, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
		uriMatcher.addURI(PACKAGE_NAME, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);

		uriMatcher.addURI(PACKAGE_NAME, SearchManager.SUGGEST_URI_PATH_SHORTCUT, SEARCH);
		uriMatcher.addURI(PACKAGE_NAME, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", SEARCH);
		
		SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();
		SEARCH_SUGGEST_PROJECTION_MAP.put("_id", 
										  MySQLiteOpenHelper.KEY_ID + " AS " + "_id");
		SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, 
										  MySQLiteOpenHelper.KEY_TASK + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
		SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, 
				MySQLiteOpenHelper.KEY_ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	}
	
	@Override
	public boolean 
	onCreate() 
	{
		soh = new MySQLiteOpenHelper(getContext(),
									 MySQLiteOpenHelper.DATABASE_NAME, 
									 null,
									 MySQLiteOpenHelper.DATABASE_VERSION);
		return true;
	}

	@Override
	public String 
	getType(Uri uri) 
	{
		Log.i("todolist", "ToDoContentProvider - getType - " + uri.toString());
		
		switch(uriMatcher.match(uri))
		{
		case ALLROWS:
			return "vnd.android.cursor.dir/vnd.wzhnsc.todos";
		case SINGLE_ROW:
			return "vnd.android.cursor.item/vnd.wzhnsc.todos";
		case SEARCH:
			return SearchManager.SUGGEST_MIME_TYPE;
		}
		
		return null;
	}

	@Override
	public Cursor 
	query(Uri      uri, 
	      String[] projection, 
		  String   selection,
		  String[] selectionArgs, 
		  String   sortOrder) 
	{
		Log.i("todolist", "ToDoContentProvider - query - " + uri.toString());
		
		SQLiteDatabase db = soh.getWritableDatabase();
		
		SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
		sqb.setTables(soh.TABLE_NAME);
		
		String strLimit = "";
		
		switch(uriMatcher.match(uri))
		{
		case SINGLE_ROW:
			
			String rowID = uri.getPathSegments().get(1);
			sqb.appendWhere(soh.KEY_ID + "=" + rowID);
			
			break;
			
		case SEARCH:
			sqb.setProjectionMap(SEARCH_SUGGEST_PROJECTION_MAP);
						
			String[] strUriArray = uri.toString().split("\\?");
			
			if ((0 < strUriArray.length) && (!strUriArray[0].trim().equals("")))
			{
				List<String> listUri = Uri.parse(strUriArray[0].trim()).getPathSegments();
				
				if (1 < listUri.size())
				{
					String strQuery = listUri.get(1);
					
					if ((null != strQuery) && 
						(!strQuery.trim().equals("")))
					{
						sqb.appendWhere(soh.KEY_TASK + " like \'%" + strQuery.trim() + "%\'");
					}
				}				
			}
			
			if ((1 < strUriArray.length) && (!strUriArray[1].trim().equals("")))
			{
				if (strUriArray[1].trim().contains("\\&"))
				{
					String[] strParamArray = strUriArray[1].trim().split("\\&");
					
					for (int i = 0; i < strParamArray.length; ++i)
					{
						String[] strKeyValueArray = strParamArray[i].split("=");
						
						if (strKeyValueArray[0].toLowerCase().equals("limit"))
						{
							strLimit = strKeyValueArray[1];
							break;
						}
					}
				}
				else
				{
					String[] strKeyValueArray = strUriArray[1].trim().split("=");
					
					if (strKeyValueArray[0].toLowerCase().equals("limit"))
					{
						strLimit = strKeyValueArray[1];
					}
					
				}
			}
			
			break;
		}
		
		Cursor cursor = null;
		
		if (strLimit.equals(""))
		{
			cursor = sqb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		}
		else
		{
			cursor = sqb.query(db, projection, selection, selectionArgs, null, null, sortOrder, strLimit);
		}
		
		return cursor;
	}

	
	@Override
	public Uri 
	insert(Uri           uri, 
		   ContentValues values) 
	{
		SQLiteDatabase db = soh.getWritableDatabase();
		
		long id = db.insert(soh.TABLE_NAME, null, values);
		
		if (-1 < id)
		{
			Uri uriInsertedRow = ContentUris.withAppendedId(CONTENT_URI, id);
			
			getContext().getContentResolver().notifyChange(uriInsertedRow, null);
			
			return uriInsertedRow;
		}
		
		return null;
	}

	@Override
	public int 
	delete(Uri      uri, 
		   String   selection, 
		   String[] selectionArgs) 
	{
		SQLiteDatabase db = soh.getWritableDatabase();

		switch(uriMatcher.match(uri))
		{
		case SINGLE_ROW:
			
			String rowID = uri.getPathSegments().get(1);
			selection = soh.KEY_ID + "=" + rowID + 
					    ((!TextUtils.isEmpty(selection)) ? " AND (" + selection + ")" : "" );
			
			break;
		}
		
		if (null == selection)
		{
			selection = "1";
		}
		
		int delCount = db.delete(soh.TABLE_NAME, selection, selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);
		
		return delCount;
	}

	@Override
	public int 
	update(Uri           uri, 
		   ContentValues values, 
		   String        selection,
		   String[]      selectionArgs) 
	{
		SQLiteDatabase db = soh.getWritableDatabase();

		switch(uriMatcher.match(uri))
		{
		case SINGLE_ROW:
			
			String rowID = uri.getPathSegments().get(1);
			selection = soh.KEY_ID + "=" + rowID + 
					    ((!TextUtils.isEmpty(selection)) ? " AND (" + selection + ")" : "" );
			
			break;
		}

		int updateCount = db.update(soh.TABLE_NAME, values, selection, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return updateCount;
	}

}
