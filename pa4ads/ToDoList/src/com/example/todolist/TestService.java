package com.example.todolist;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;

public class TestService extends Service 
{

	private int iCurrentValue = 0;
	
	public int getCurrentValue()
	{
		return iCurrentValue;
	}
	
	public void setForegroundSerive()
	{
		Intent intent = new Intent(this, CompassActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 1, intent, 0);
		
		// Android 2.2 可用代码
//		Notification notif = new Notification(R.drawable.notif_icon, "Test Notification", System.currentTimeMillis());
//		notif.setLatestEventInfo(this, "I am title?", "I am content?", pi);
//		notif.flags |= Notification.FLAG_ONGOING_EVENT;
//		notif.contentView = new RemoteViews(this.getPackageName(), R.layout.my_status_window);
//		notif.contentView.setImageViewResource(R.id.status_icon, R.drawable.notif_icon);
//		notif.contentView.setTextViewText(R.id.status_text, "I am custom text");
//		notif.contentView.setProgressBar(R.id.status_progress, 100, 50, false);
//		startForeground(1, notif);
		
		// Android 3.0 以上可用代码
		RemoteViews rv = new RemoteViews(this.getPackageName(), R.layout.my_status_window);
		
		android.support.v4.app.NotificationCompat.Builder nb = new android.support.v4.app.NotificationCompat.Builder(this);
		nb.setSmallIcon(R.drawable.notif_icon)
		  .setTicker("I am Ticker")
		  .setWhen(System.currentTimeMillis())
		  .setContentTitle("I am ContetnTile")
		  .setProgress(100, 50, false)
//		  .setContent(rv)
		  .setOngoing(true)
		  .setContentIntent(pi);
				
//		rv.setImageViewResource(R.id.status_icon, R.drawable.notif_icon);
//		rv.setTextViewText(R.id.status_text, "I am custom text");
//		rv.setProgressBar(R.id.status_progress, 100, 50, false);
		
		startForeground(1, nb.build());
	}
	
	public void setBackgroundSerive()
	{
		stopForeground(true);
	}
	
	public class TestBinder extends Binder
	{
		public TestService getService()
		{
			return TestService.this;
		}
	}
	
	private ToDoItem tdi = new ToDoItem("CreateToDoItemInTestService");
	
	IBinder testServiceStub = new ITestService.Stub() {
		
		@Override
		public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException 
		{
			boolean bResult = false;
			
			try 
			{
				bResult = super.onTransact(code, data, reply, flags);
			} 
			catch (Exception e) 
			{
				Log.i("todolist", "TestService - onTransact - exception: ", e);
			}
			
			return bResult;
		}

		@Override
		public void setToDoItem(ToDoItem tdi) throws RemoteException 
		{
			Log.i("todolist", "TestService - ITestService.Stub() - setToDoItem");
			
			TestService.this.tdi = new ToDoItem(tdi.getTask(), tdi.getDate());
		}
		
		@Override
		public ToDoItem getToDoItem() throws RemoteException 
		{
			Log.i("todolist", "TestService - ITestService.Stub() - getToDoItem");
			return new ToDoItem(tdi.getTask(), tdi.getDate());
		}
				
	};
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		Log.i("todolist", 
				  "TestService - onBind - intent: " + intent + 
				  " ProcessID - TID: " + Thread.currentThread().getId() + 
				  " PID: " + Process.myPid() + 
				  " TID: " + Process.myTid() + 
				  " UID: " + Process.myUid());
		
//		return new TestBinder();
		return testServiceStub;
	}

	@Override
	public void onCreate() 
	{
		Log.i("todolist", 
			  "TestService - onCreate - ProcessID - TID: " + Thread.currentThread().getId() + 
			  " PID: " + Process.myPid() + 
			  " TID: " + Process.myTid() + 
			  " UID: " + Process.myUid());
		
		new Thread(new Runnable(){

			@Override
			public void run() 
			{
				while (3 > iCurrentValue)
				{
					Log.i("todolist", "iCurrentValue = " + iCurrentValue);
					
					++iCurrentValue;
					
					try 
					{
						Thread.sleep(3000);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}				
			}
		}).start();
		
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		Log.i("todolist", 
			  "TestService - onStartCommand -" + " intent = " + intent + " flags =" + flags + " startId =" + startId );

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() 
	{
		Log.i("todolist", "TestService - onDestroy");

		super.onDestroy();
	}

}
