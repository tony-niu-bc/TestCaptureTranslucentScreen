package com.example.todolist;

import java.io.File;
import java.security.PrivilegedActionException;
import java.util.regex.Pattern;

import com.example.todolist.TestTabOneFragment.OnFragmentInteractionListener;

import android.R.anim;
import android.R.integer;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService.Engine;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;

public class TestCtrlActivity extends AppCompatActivity 
							  implements OnFragmentInteractionListener
{
	private SharedPreferences sp;
	
	private Spinner  spinner = null;
	private TextView tv      = null;
	
	private MarginLayoutParams mlpView  = null;
	private View               moveView = null;
	
	private ImageView iv2 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);

		supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
		
		setContentView(R.layout.test_ctrl_view);

		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		
		tv = (TextView)findViewById(R.id.testLinkify);
		
		Pattern p = Pattern.compile("://\\S*");
		Linkify.addLinks(tv, p, "mm");
		
		spinner = (Spinner)findViewById(R.id.spinner1);
		
		ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, 
																		R.array.test_spinner_array_value, 
																		android.R.layout.simple_spinner_item);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner.setAdapter(aa);
		
		sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		Log.i("todolist", sp.getString("TEST_SPINNER", "1"));
		
		String strValue = sp.getString("TEST_SPINNER", "1");
		
		String[] strArray = getResources().getStringArray(R.array.test_spinner_array_value);
		
		int iSelectedItem = 0;
		for (String strTmp : strArray)
		{
			if (strValue.equals(strTmp))
			{
				break;
			}
			
			++iSelectedItem;
		}
		
		Log.i("todolist", "iSelectedItem = " + iSelectedItem);
		
		spinner.setSelection(iSelectedItem);
		
		SearchManager sm = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
		SearchableInfo si = sm.getSearchableInfo(getComponentName());
		
		SearchView sv = (SearchView)findViewById(R.id.searchView);
		sv.setSearchableInfo(si);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();	
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		moveView = spinner;
		mlpView = (MarginLayoutParams)spinner.getLayoutParams();
		mlpView.width = displaymetrics.widthPixels / 3;
		
		Log.i("todolist", 
			  "TestCtrlActivity - onCreate - SelfWidth: " + displaymetrics.widthPixels + 
			  " mlpView.width: " + mlpView.width);
		
		moveView.setLayoutParams(mlpView);
		
		//为了让超链接 TextView 露出，先隐藏状态栏
		if (null != getSupportActionBar())
		{
			getSupportActionBar().hide();
		}

		iv2 = (ImageView)findViewById(R.id.testScaleDrawable);
		iv2.setImageResource(R.drawable.my_scale_drawable);
		ScaleDrawable sd = (ScaleDrawable)iv2.getDrawable();
		sd.setLevel(1);
		
		new MyAsyncTask().execute(30);
	}
	
	private class MyAsyncTask extends AsyncTask<Integer, Integer, Integer>
	{
		@Override
		protected Integer doInBackground(Integer... params) 
		{
			int leftMargin = mlpView.leftMargin;
			
			Log.i("todolist", "MyAsyncTask - doInBackground - leftMargin: " + leftMargin);
			
			for (int i = 0; i < 6; ++i)
			{
//				if (5 > i)
//				{
					leftMargin += params[0];
//				}
//				else
//				{
//					leftMargin -= params[0];
//				}
				
				publishProgress(leftMargin);
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return leftMargin;
		}

		@Override
		protected void onPostExecute(Integer result) 
		{
			Log.i("todolist", "MyAsyncTask - onPostExecute - result: " + result);

			mlpView.leftMargin = result;
			moveView.setLayoutParams(mlpView);
			
//			Animation animation = AnimationUtils.loadAnimation(TestCtrlActivity.this, R.anim.scale_show);
//			tv.startAnimation(animation);
			
			ImageView iv = (ImageView)findViewById(R.id.testAnimationList);
			iv.setBackgroundResource(R.drawable.test_animation_list);
			AnimationDrawable ad = (AnimationDrawable)iv.getBackground();
			ad.start();
		}

		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			Log.i("todolist", "MyAsyncTask - onProgressUpdate - values[0]: " + values[0]);
			
			mlpView.leftMargin = values[0];
			moveView.setLayoutParams(mlpView);
			
			if (10000 > mlpView.leftMargin * 56)
			{
				iv2.setImageLevel(mlpView.leftMargin * 56);
			}
			else 
			{			
				iv2.setImageLevel(10000);
			}
		}
	}
	
	public void searchContent(View v)
	{
		onSearchRequested();
	}

	public void launchService(View v)
	{
//		Intent intent = new Intent(this, TestService.class);
//		
		Log.i("todolist", 
			  "TestCtrlActivity - launchService - TID: " + Thread.currentThread().getId() + 
			  " PID: " + Process.myPid() + 
			  " TID: " + Process.myTid() + 
			  " UID: " + Process.myUid());
//		
//		startService(intent);
		MyIntentService.startActionBaz(this, "Baz1", "Baz2");
		MyIntentService.startActionFoo(this, "Foo1", "Foo2");
	}

	public void finishService(View v)
	{
//		Intent intent = new Intent(this, TestService.class);
//		
//		stopService(intent);
	}
	
	private TestService testService = null;
	private ServiceConnection conn = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName name,
									   IBinder       service) 
		{
			Log.i("todolist", "TestCtrlActivity - onServiceConnected - ComponentName: " + name);
//			testService = ((TestService.TestBinder)service).getService();
			ITestService ts = ITestService.Stub.asInterface(service);
			
//			Log.i("todolist", 
//				  "TestCtrlActivity - onServiceConnected - getCurrentValue: " + testService.getCurrentValue());
			
			ToDoItem tdi = new ToDoItem("TestAIDL");
			
			try 
			{
				ts.setToDoItem(tdi);
			} 
			catch (RemoteException e) 
			{
				e.printStackTrace();
			}
			
			try 
			{
				ToDoItem tdiBak = ts.getToDoItem();
				
				Log.i("todolist", 
					  "TestCtrlActivity - onServiceConnected - tdiBak.getTask(): " + tdiBak.getTask());
			} 
			catch (RemoteException e) 
			{
				e.printStackTrace();
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) 
		{
			Log.i("todolist", "TestCtrlActivity - onServiceDisconnected - ComponentName: " + name);
			testService = null;
		}

	};
	
	boolean bIsConnService = false;
	public void conneService(View v)
	{
		Intent intent = new Intent(this, TestService.class);
		
		Log.i("todolist", 
			  "TestCtrlActivity - conneService - TID: " + Thread.currentThread().getId() + 
			  " PID: " + Process.myPid() + 
			  " TID: " + Process.myTid() + 
			  " UID: " + Process.myUid() + 
			  " ITestService.class.getName(): " + ITestService.class.getName());
		
		bIsConnService = 
		bindService(intent, 
					conn, 
				    Context.BIND_AUTO_CREATE);
//		bindService(new Intent(ITestService.class.getName()), 
//					conn, 
//					Context.BIND_AUTO_CREATE);
	}

	public void disconService(View v)
	{
		if (bIsConnService)
			unbindService(conn);
	}

	public void foregroundService(View v)
	{
		if (bIsConnService)
			testService.setForegroundSerive();
	}

	public void backgroundService(View v)
	{
		if (bIsConnService)
			testService.setBackgroundSerive();
	}

	public void setAlarm(View v)
	{
		Intent intent = new Intent(this, CompassActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 1, intent, 0);
		
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000, pi);
	}
	
//	private ActionBar actionBar = getSupportActionBar();
	public void showActionBar(View v)
	{
		Log.i("todolist", "TestCtrlActivity - showActionBar - actionBar: " + getSupportActionBar());
		
		if (null != getSupportActionBar())
		{
			getSupportActionBar().show();
		}
	}

	public void hideActionBar(View v)
	{
		Log.i("todolist", "TestCtrlActivity - hideActionBar - actionBar: " + getSupportActionBar());
		
		if (null != getSupportActionBar())
		{
			getSupportActionBar().hide();
		}
	}

	public static class MyTabListener<T extends Fragment> implements ActionBar.TabListener
	{
		private Fragment          fragment = null;
		
		private Context			  context;
		private Class<T>          fragmentClass;
		private int               iContainerResID;
		
		private int               iDifference;

		public MyTabListener(Context  context,
							 int      iContainerResID, 
							 Class<T> fragmentClass, 
							 int      iDifference) 
		{			
			this.context         = context;
			this.fragmentClass   = fragmentClass;
			this.iContainerResID = iContainerResID;
			this.iDifference = iDifference;
		}

		@Override
		public void onTabReselected(android.support.v7.app.ActionBar.Tab arg0,
									FragmentTransaction 				 arg1) 
		{
			Log.i("todolist", "TestCtrlActivity - onTabReselected - fragment: " + fragment);
			
			if (null != fragment)	
				arg1.attach(fragment);
		}

		@Override
		public void onTabSelected(android.support.v7.app.ActionBar.Tab arg0,
								  FragmentTransaction				   arg1) 
		{
			if (null == fragment)
			{
				String fragmentName = fragmentClass.getName();
				fragment = Fragment.instantiate(context, fragmentName);
				
				if (1 == iDifference)
				{
					Bundle args = new Bundle();
					args.putString("param1", "Tab1");
					args.putString("param2", "111");
					fragment.setArguments(args);
				}
				else 
				{
					Bundle args = new Bundle();
					args.putString("param1", "Tab2");
					args.putString("param2", "222");
					fragment.setArguments(args);
				}
				
				String strFragmentName = fragmentName + iDifference;
				
				Log.i("todolist", "TestCtrlActivity - onTabSelected - strFragmentName: " + strFragmentName);
				
				arg1.add(iContainerResID, fragment, strFragmentName);
			}
			else
			{
				arg1.attach(fragment);
			}
		}

		@Override
		public void onTabUnselected(android.support.v7.app.ActionBar.Tab arg0,
									FragmentTransaction 				 arg1) 
		{
			Log.i("todolist", "TestCtrlActivity - onTabUnselected - fragment: " + fragment);
			
			if (null != fragment)	
				arg1.detach(fragment);
		}
		
	}
	
	MyTabListener<TestTabOneFragment> tab1Listener = null;
	MyTabListener<TestTabOneFragment> tab2Listener = null;
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) 
	{
		super.onPostCreate(savedInstanceState);
		
		if (null != getSupportActionBar())
		{
			getSupportActionBar().setSubtitle("I\'m subtitle");
			getSupportActionBar().setDisplayShowHomeEnabled(true);
			getSupportActionBar().setIcon(R.drawable.notif_icon);
//			getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_launcher));
//			getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
//			getSupportActionBar().setHomeButtonEnabled(true);
//			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			
//			getSupportActionBar().setDisplayShowTitleEnabled(false);			
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			android.support.v7.app.ActionBar.Tab tab1 = getSupportActionBar().newTab();
			tab1.setText("Tab1")
				.setContentDescription("Tab1 only for test")
				.setTabListener(tab1Listener);
			getSupportActionBar().addTab(tab1);
			
			android.support.v7.app.ActionBar.Tab tab2 = getSupportActionBar().newTab();
			tab2.setText("Tab2")
				.setContentDescription("Tab2 only for test")
				.setTabListener(tab2Listener);
			getSupportActionBar().addTab(tab2);
			
			Log.i("todolist", 
				  "TestCtrlActivity - onPostCreate - tab1Listener.fragment: " + tab1Listener.fragment + 
				  " tab2Listener.fragment: " + tab2Listener.fragment);

		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Log.i("todolist", "TestCtrlActivity - onOptionsItemSelected - item.getItemId(): " + item.getItemId());
		
		switch(item.getItemId())
		{
		case android.R.id.home:
			
			Toast.makeText(this, "The icon of activity was clicked!", Toast.LENGTH_SHORT).show();
			
			break;
				
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		
		SharedPreferences sp = getPreferences(AppCompatActivity.MODE_PRIVATE);
		int iWhichSelected = sp.getInt("TabSelectedIndex", 0);
		
		Log.i("todolist", "TestCtrlActivity - onResume - iWhichSelected: " + iWhichSelected);
		
		getSupportActionBar().setSelectedNavigationItem(iWhichSelected);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		int iWhichSelected = getSupportActionBar().getSelectedTab().getPosition();
		
		Log.i("todolist", "TestCtrlActivity - onSaveInstanceState - iWhichSelected: " + iWhichSelected);
		
		SharedPreferences.Editor editor = getPreferences(AppCompatActivity.MODE_PRIVATE).edit();
		editor.putInt("TabSelectedIndex", iWhichSelected);
		editor.commit();
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		if (null != tab1Listener.fragment)
		{
			ft.detach(tab1Listener.fragment);
		}

		if (null != tab2Listener.fragment)
		{
			ft.detach(tab2Listener.fragment);
		}
		
		ft.commit();
		
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) 
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		String strFragmentName = TestTabOneFragment.class.getName() + 1;
		
		tab1Listener.fragment = getSupportFragmentManager().findFragmentByTag(strFragmentName);

		Log.i("todolist", "TestCtrlActivity - onRestoreInstanceState - tab1Listener.fragment: " + tab1Listener.fragment);

		strFragmentName = TestTabOneFragment.class.getName() + 2;
		
		tab2Listener.fragment = getSupportFragmentManager().findFragmentByTag(strFragmentName);
		
		Log.i("todolist", "TestCtrlActivity - onRestoreInstanceState - tab2Listener.fragment: " + tab2Listener.fragment);
	}

	@Override
	protected void onStart() {				
		Log.i("todolist", 
			  "TestCtrlActivity - onStart - tab1Listener: " + tab1Listener + 
			  " tab2Listener: " + tab2Listener);
		
		if (null == tab1Listener)
			tab1Listener = new MyTabListener<TestTabOneFragment>(this, 
																 R.id.FragmentContainer, 
																 TestTabOneFragment.class,
																 1);
		
		if (null == tab2Listener)
			tab2Listener = new MyTabListener<TestTabOneFragment>(this, 
																 R.id.FragmentContainer, 
																 TestTabOneFragment.class,
																 2);
		
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		
		Log.i("todolist", "TestCtrlActivity - onCreateOptionsMenu - getFilesDir():" + getFilesDir());
		
		MenuItem mi = menu.add(0, Menu.FIRST, 0, "test menu 1");
		
		// 添加操作View
//		MenuItemCompat.setActionView(mi, R.layout.actionbar_view);
//		MenuItemCompat.setShowAsAction(mi, 
//									   MenuItemCompat.SHOW_AS_ACTION_IF_ROOM | 
//									   MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//		mi.setIcon(R.drawable.notif_icon);
//		
//		SearchManager smr = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
//		SearchableInfo si = smr.getSearchableInfo(getComponentName());
//		
//		View view = MenuItemCompat.getActionView(mi);
//		SearchView sv = (SearchView)view.findViewById(R.id.searchTabView);
//		sv.setSearchableInfo(si);
		
		// 添加 ActionProvider
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		
		Uri uri = Uri.fromFile(new File(getFilesDir(), "NiuShengchao.jpg"));
		intent.putExtra(Intent.EXTRA_STREAM, uri.toString());
		
		ShareActionProvider sap = new ShareActionProvider(this);
		sap.setShareIntent(intent);
		
		MenuItemCompat.setActionProvider(mi, sap);
		MenuItemCompat.setShowAsAction(mi, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		
		SubMenu sm = menu.addSubMenu(0, Menu.FIRST + 1, 0, "test submenu 0");
		MenuItemCompat.setShowAsAction(sm.getItem(), MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		sm.getItem().setIcon(R.drawable.ic_launcher);
		
		sm.add(1, Menu.FIRST + 2, 0, "test submenu 1");
		sm.add(1, Menu.FIRST + 3, 0, "test submenu 2").setChecked(true);
		sm.add(1, Menu.FIRST + 4, 0, "test submenu 3");
		
		sm.setGroupCheckable(1, true, true);
		
		return true;
	}

	private static final int CUSTOME_DIALOG = 0;
	private static final int ALERT_DIALOG = 1;

	private int iSel = CUSTOME_DIALOG;
	public void showDlg(View v)
	{
		// Android 3.0 以后不推荐此方法了
//		if (CUSTOME_DIALOG == iSel){
//			iSel = ALERT_DIALOG;			
//		}
//		else {
//			iSel = CUSTOME_DIALOG;
//		}
//		
//		showDialog(iSel);
		
		// Android 3.0 以后推荐的方法
		android.support.v4.app.DialogFragment mdf = (android.support.v4.app.DialogFragment)new MyDlgFragment();
		mdf.show(getSupportFragmentManager(), "my_dlg_fragment");
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		switch(id)
		{
		case CUSTOME_DIALOG:
			// 自定义视图的对话框 - 大小因内容而定
			Dialog dialog = new Dialog(this);
			dialog.setTitle("Test Dialog");
			dialog.setContentView(R.layout.test_dialog_view);
			
			TextView tv = (TextView)dialog.findViewById(R.id.dlgTV);
			tv.setText("Test Dialog Text!");
			return dialog;
					
		case ALERT_DIALOG:
			//
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setTitle("TestAlertDialog");
			ad.setMessage("Test AlertDialog Content");
			ad.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					Toast.makeText(TestCtrlActivity.this, "which = " + which, Toast.LENGTH_LONG).show();
				}
			});
			ad.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(TestCtrlActivity.this, "which = " + which, Toast.LENGTH_LONG).show();				
				}
			});
			ad.setCancelable(false);
			ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) 
				{
					//Toast.makeText(TestCtrlActivity.this, "Are you cancel?", Toast.LENGTH_LONG).show();
					Log.i("todolist", "TestCtrlActivity - DialogInterface.OnCancelListener");
				}
			});
			
			return ad.create();			
		}
		
		return null;
	}
	
	private TextToSpeech tts = null;
	private boolean      bIsTTSable = false;

	public void useTTS(View v)
	{
		Intent intent = new Intent(android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(intent, 0x101); // 0x101 随便选用的只为保证唯一性
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) 
	{
		Log.i("todolist", "TestCtrlActivity - onActivityResult - arg0: " + arg0 + " arg1: " + arg1);
		
		if (0x101 == arg0)
		{
			if (android.speech.tts.TextToSpeech.Engine.CHECK_VOICE_DATA_PASS == arg1)
			{
				tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
					
					@Override
					public void onInit(int status) 
					{
						if (TextToSpeech.SUCCESS == status)
						{
							bIsTTSable = true;
							
							tts.speak("Hello, tony!", TextToSpeech.QUEUE_ADD, null);//, "testTTS");//四个参数的在 Android 5.0 开发支持
						}
					}
				});
			}
		}
		else 
		{
			Intent intent = new Intent(android.speech.tts.TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() 
	{
		if (null != tts)
		{
			tts.stop();
			tts.shutdown();
		}
		
		super.onDestroy();
	}

}
