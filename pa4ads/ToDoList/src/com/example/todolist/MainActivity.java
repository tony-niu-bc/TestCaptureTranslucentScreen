package com.example.todolist;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.app.DownloadManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends FragmentActivity 
						  implements NewItemFragment.OnNewItemAddedListener, 
						             LoaderManager.LoaderCallbacks<Cursor>
{

	private ToDoItemAdapter aa;
	private ArrayList<ToDoItem>       todoItems;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
//        View myView = getWindow().getDecorView();
//        myView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);    
        
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        
        ToDolistFragment todoListFragment = (ToDolistFragment)fm.findFragmentById(R.id.ToDolistFragment);
                
        todoItems = new ArrayList<ToDoItem>();
        
        aa = new ToDoItemAdapter(this, 
                                 R.layout.todolist_item,
                                 todoItems);
        
        todoListFragment.setListAdapter(aa);
        
        Log.i("todolist", "Called onCreate!");
        
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    // DownloadManager 下载项引用标识
//    private long lDownloadItem = 0;
//    
//    // 接收下载完成广播的广播接收器
//    private BroadcastReceiver re = null;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	Intent intent;
    	
        int id = item.getItemId();
        switch (id) 
        {
        case R.id.action_settings:
        	
        	String[] strFileList = this.fileList();
        	String   strMerg = "fileList(): ";
        	for (String strFileName : strFileList)
        	{
        		strMerg += strFileName + " ";
        	}
        	
        	if (strFileList.length > 0)
        	{
        		Log.i("todolist", strMerg);
        	}
        	
        	File file = this.getDir("MyDir", Context.MODE_PRIVATE);
        	Log.i("todolist", "getDir(): " + file.getAbsolutePath() + "+" + file.getName());
        	
        	file = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        	Log.i("todolist", "getExternalFilesDir(): " + file.getAbsolutePath() + "+" + file.getName());
        	
        	file = Environment.getDataDirectory();
        	Log.i("todolist", "Environment.getDataDirectory(): " + file.getAbsolutePath() + "+" + file.getName());
        	
        	file = Environment.getExternalStorageDirectory();
        	Log.i("todolist", "Environment.getExternalStorageDirectory(): " + file.getAbsolutePath() + "+" + file.getName());
        	
        	Class c = (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) ? 
        		      MyPreferenceActivity.class : 
        		      FragmentPreferenceActivity.class;
        	
        	Log.i("todolist", c.getName());
        	
        	Intent i = new Intent(this, c);
        	
        	startActivity(i);
        	
//        	// 查看电池状
//        	IntentFilter batIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        	Intent battery = registerReceiver(null, batIntentFilter);
//        	int status = battery.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//        	
//        	Toast.makeText(this, "status: " + status, Toast.LENGTH_LONG).show();
//        	
//        	final DownloadManager dm = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
//        	
//        	Uri uri = Uri.parse("http://www.baidu.com/img/bd_logo1.png");
//        	
//        	DownloadManager.Request r = new DownloadManager.Request(uri);
//        	r.setVisibleInDownloadsUi(true);
//        	
//        	lDownloadItem = dm.enqueue(r);
//        	
//        	IntentFilter ifr = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//        	
//        	re = new BroadcastReceiver(){
//
//				@Override
//				public void onReceive(Context context, Intent intent) 
//				{
//					if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
//					{
//						long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//						
//						try
//						{
//							if (id == lDownloadItem)
//							{
//								DownloadManager.Query query = new DownloadManager.Query();
//								query.setFilterById(id);
//								
//								Cursor c = dm.query(query);
//								
//								if (c.moveToFirst())
//								{
//									int ifileName = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
//									int ifileUrl  = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
//									
//									String strFileName = c.getString(ifileName);
//									String strFileUrl  = c.getString(ifileUrl);
//								
//									Toast.makeText(context, 
//												   "Name: " + strFileName + "\n" + "Url: " + strFileUrl, 
//												   Toast.LENGTH_LONG)
//									     .show();
//								}
//							}
//						}
//						catch (FileNotFoundException e)
//						{
//							Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
//						}
//						
//						unregisterReceiver(re);
//						re = null;
//					}
//				}
//        		
//        	};
//        	
//        	registerReceiver(re, ifr);
        	
        	break;

        case R.id.action_test_ctrl:

        	intent = new Intent(this, TestCtrlActivity.class);
    		startActivity(intent);
        	
        	break;
        	
        case R.id.compass_show:

        	intent = new Intent(this, CompassActivity.class);
    		startActivity(intent);
        	
        	break;

        case R.id.contact_picker:
        	
        	intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts/"));
        	startActivityForResult(intent, 1);
        	
        	break;
        	
        default:
            return super.onOptionsItemSelected(item);        	
        }

        return true;
    }

	@Override
	public void onNewItemAdded(String newItem) 
	{
		ContentResolver cr = getContentResolver();
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteOpenHelper.KEY_TASK, newItem);
		values.put(MySQLiteOpenHelper.KEY_CREATION_DATE, java.lang.System.currentTimeMillis());
		
		cr.insert(ToDoContentProvider.CONTENT_URI, values);
		
		Log.i("todolist", "Called onNewItemAdded!");
		
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) 
	{
		super.onActivityResult(arg0, arg1, arg2);
				
		if ( null != arg2 )
		{
			Uri contactData = arg2.getData();
			
			if ( null != contactData )
			{
				Cursor c = getContentResolver().query(contactData, null, null, null, null);
				
				c.moveToFirst();
				
				String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
				
				c.close();
				
				Toast.makeText(this, "arg0: " + arg0 + " , arg1" + arg1 + " , name: " + name, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onStop() 
	{
//		if ( null != re )
//		{
//			unregisterReceiver(re);
//		}
		
		super.onStop();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) 
	{
		Log.i("todolist", "Called onCreateLoader!");
		
		CursorLoader loader = new CursorLoader(this, ToDoContentProvider.CONTENT_URI, null, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) 
	{
		Log.i("todolist", "Called onLoadFinished!");
		
		int iTaskIndex = arg1.getColumnIndexOrThrow(MySQLiteOpenHelper.KEY_TASK);
		int iDateIndex = arg1.getColumnIndexOrThrow(MySQLiteOpenHelper.KEY_CREATION_DATE);
		
		todoItems.clear();
		
		while (arg1.moveToNext())
		{
			String strTask = arg1.getString(iTaskIndex);
			Date   dateCreation = new Date(arg1.getLong(iDateIndex));
			
			ToDoItem tdi = new ToDoItem(strTask, dateCreation);
			
	        todoItems.add(0, tdi);
		}
		
        aa.notifyDataSetChanged();

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) 
	{
		Log.i("todolist", "Called onLoaderReset!");
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		
		Log.i("todolist", "Called onResume!");
		
		getSupportLoaderManager().restartLoader(0, null, this);
	}
}
