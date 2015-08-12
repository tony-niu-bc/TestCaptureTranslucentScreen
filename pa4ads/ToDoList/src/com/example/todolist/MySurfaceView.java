package com.example.todolist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MySurfaceView extends SurfaceView 
						   implements SurfaceHolder.Callback
{
	private SurfaceHolder sh = null;
	private SurfaceThread st = null;
	private Paint textPaint = null;
	private Paint bgPaint = null;
	private RectF groundRect = null;

	private boolean bIsRun = true;
	
	public void controlRun(boolean _bIsRun)
	{
		Log.i("todolist", "MySurfaceView - controlRun - _bIsRun: " + _bIsRun);
		
		bIsRun = _bIsRun;
		
		try {
			st.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class SurfaceThread extends Thread
	{		
		@Override
		public void run() 
		{
			int i = 0;
			while (bIsRun) 
			{
				Log.i("todolist", "MySurfaceView - SurfaceThread - run: " + i);
				
				Canvas canvas = sh.lockCanvas();
				
				int canvasWidth = canvas.getWidth();
				int canvasHeight = canvas.getHeight();
								
				canvas.drawRect(groundRect, bgPaint);
				canvas.drawText(""+i, canvasWidth / 2, canvasHeight / 2, textPaint);
				
				sh.unlockCanvasAndPost(canvas);
				
				++i;
				
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (i > 10)
				{
					break;
				}
			}
		}
		
	}
	
	public MySurfaceView(Context context) 
	{
		super(context);
		init();
	}

	public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init()
	{
		Log.i("todolist", "MySurfaceView - init");
		
		sh = getHolder();
		sh.addCallback(this);
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.parseColor("#FF00FF00"));
		textPaint.setFakeBoldText(true);
		textPaint.setSubpixelText(true);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(20);
		
		bgPaint = new Paint();
	}
	
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		Log.i("todolist", "MySurfaceView - onWindowVisibilityChanged - visibility: " + visibility);
				
		super.onWindowVisibilityChanged(visibility);
	}

	@Override
	protected void onDetachedFromWindow() {
		Log.i("todolist", "MySurfaceView - onDetachedFromWindow");
				
		super.onDetachedFromWindow();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{		
		Log.i("todolist", "MySurfaceView - surfaceCreated");
		
		st = new SurfaceThread();
		st.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, 
							   int format, 
							   int width,
							   int height) 
	{
		Log.i("todolist", "MySurfaceView - surfaceChanged - format: " + format + " width: " + width + " height: " + height);
		LinearGradient lg = new LinearGradient(width / 2, 0, 0, height, Color.BLUE, Color.LTGRAY, TileMode.CLAMP);
		bgPaint.setShader(lg);
		
		groundRect = new RectF(0, 0, width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		Log.i("todolist", "MySurfaceView - surfaceDestroyed");
		
		if (null != st)
		{
			controlRun(false);
			
			st = null;
		}
	}

}
