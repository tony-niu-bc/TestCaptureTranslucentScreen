package com.example.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper 
{
	public static final String TABLE_NAME = "todotable";
	public static final String KEY_ID = "_id";
	public static final String KEY_TASK = "task";
	public static final String KEY_CREATION_DATE = "creation_date";
	
	public static final String DATABASE_NAME = "todolist.db";
	public static final int    DATABASE_VERSION = 1;
	
	private static final String SQL_CREATE_TABLE = "create table " + TABLE_NAME + 
												   "( " + 
												   		 KEY_ID + " integer primary key autoincrement, " + 
												   		 KEY_TASK + " text not null, " + 
												   		 KEY_CREATION_DATE + " long" + 
												   	" );";
	
	private static final String SQL_DROP_TABLE = "drop table if exists " + TABLE_NAME;
	
	public MySQLiteOpenHelper(Context       context, 
							  String        name,
							  CursorFactory factory, 
							  int           version) 
	{
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		Log.i("todolist", "Upgrade db version from " + oldVersion + " to " + newVersion);
		
		db.execSQL(SQL_DROP_TABLE);
		
		onCreate(db);
	}

}
