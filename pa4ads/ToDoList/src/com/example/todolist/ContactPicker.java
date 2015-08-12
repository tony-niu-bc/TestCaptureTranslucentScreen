package com.example.todolist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ContactPicker extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,                
                			 WindowManager.LayoutParams. FLAG_FULLSCREEN);
		
		setContentView(R.layout.contact_picker);
		
		// 测试一：从联系人中读出姓名
		Log.i("todolist", ContactsContract.Contacts.CONTENT_URI.toString());
		
		final Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
													null, 
													null, 
													null, 
													null);
		
		String[] from = new String[]{ Contacts.DISPLAY_NAME_PRIMARY };
		int[]    to   = new int[]{ R.id.itemTextView };
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, 
															  R.layout.similar_word_list,
															  c,
															  from,
															  to, 
															  SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

//		// 测试二：读出支持某数据类型处理的Activity列表
//		final ArrayList<String> lables = new ArrayList<String>();
//		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
//															  		  android.R.layout.simple_list_item_1, 
//															  		  lables);
//
		ListView lv = (ListView)findViewById(R.id.contactListView);
		
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new ListView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, 
									View           view,
									int            position, 
									long           id) 
			{
				// 测试一：传回选择的姓名
				c.moveToPosition(position);
				
				int rowId = c.getInt(c.getColumnIndexOrThrow("_id"));
				
				Uri outURI = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, rowId);
				
				Intent outData = new Intent();
				outData.setData(outURI);
				
				setResult(Activity.RESULT_OK, outData);
				
//				// 测试二：只返回结果
//				setResult(Activity.RESULT_OK);
				
				finish();
			}
			
		});
		
//		Intent intent = new Intent();
//		Uri    uri = Uri.parse("mm://123456");
//		intent.setData(uri);
//		//intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
//		intent.addCategory(Intent.CATEGORY_SELECTED_ALTERNATIVE);
//		
//		List<ResolveInfo> actions = getPackageManager().queryIntentActivities(intent, 
//																			  PackageManager.MATCH_DEFAULT_ONLY);
//		for (ResolveInfo action : actions)
//		{
//			lables.add(getResources().getString(action.labelRes));
//		}
//		
//		adapter.notifyDataSetChanged();
	}

}
