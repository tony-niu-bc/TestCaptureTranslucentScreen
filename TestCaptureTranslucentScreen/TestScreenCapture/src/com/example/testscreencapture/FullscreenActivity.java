package com.example.testscreencapture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.testscreencapture.util.SystemUiHider;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity 
{
	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		// 1.Disappear the title of window
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_fullscreen);
		
		// 2.Set up an instance of SystemUiHider to control the system UI for this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, getWindow().getDecorView(), HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.hide();
	}

	public void screenCapture(View v) 
	{
	    // 1.Capture the bitmap of current activity
		View viewRoot = v.getRootView();
		viewRoot.setDrawingCacheEnabled(true);
		viewRoot.buildDrawingCache();
	    Bitmap bmp = viewRoot.getDrawingCache();
		
	    // 2.If have got to success, then save the bitmap
		if (null != bmp)
		{
		    try 
		    {  
		        if (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).exists())
		        {
		        	String strFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + 
										 File.separator + "CurrentScreen.png";
			        FileOutputStream fos = new FileOutputStream(strFilePath);
		            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);  
		            fos.flush();  
		            fos.close();    
		              
		            Toast.makeText(this, 
		            			   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + 
								   File.pathSeparator + "CurrentScreen.png" + "文件创建成功！", 
		            			   Toast.LENGTH_LONG)
		            	 .show();  
		        }
		        else 
		        {
		        	Toast.makeText(this, 
		        				   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "目录不存在！", 
		        				   Toast.LENGTH_LONG)
		        		 .show(); 
				}
		    }
		    catch (Exception e) 
		    {  
		        e.printStackTrace();  
		    }  			
		}
        else 
        {
        	Toast.makeText(this, 
        				   "得到指定视图的绘制缓冲位图失败！", 
        				   Toast.LENGTH_LONG)
        		 .show(); 
		}
	}
}
