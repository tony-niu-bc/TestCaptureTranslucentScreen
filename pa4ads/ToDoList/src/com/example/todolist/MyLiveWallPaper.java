package com.example.todolist;

import android.R.integer;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.text.TextPaint;
import android.view.SurfaceHolder;

public class MyLiveWallPaper extends WallpaperService 
{

	@Override
	public Engine onCreateEngine() 
	{
		return new MyLiveWallPaperServiceEngine();
	}
	
	public class MyLiveWallPaperServiceEngine extends WallpaperService.Engine
	{

		private final Handler Handler = new Handler();
		
		private Paint textPaint = null;
		private Paint fillPaint = null;
		
		private int iCounter = 4;
		
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) 
		{
			textPaint = new Paint();
			textPaint.setAntiAlias(true);
			textPaint.setColor(Color.RED);
			textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			textPaint.setFakeBoldText(true);
			textPaint.setTextSize(20);

			fillPaint = new Paint();
			fillPaint.setAntiAlias(true);
			fillPaint.setColor(Color.LTGRAY);
			
			drawFrame();
		}
		
		private void drawFrame()
		{
			SurfaceHolder sh = getSurfaceHolder();
			
			Canvas canvas = sh.lockCanvas();
			
			if (null != canvas)
			{
				canvas.drawColor(Color.LTGRAY);
				
				if (iCounter > 3)
				{					
					canvas.drawPaint(fillPaint);
					
					iCounter = 0;
				}				

				switch(iCounter)
				{
				case 0:
					canvas.drawText("I", 50, 50, textPaint);
					break;
	
				case 1:
					canvas.drawText("am", 80, 80, textPaint);
					break;
	
				case 2:
					canvas.drawText("live", 110, 110, textPaint);
					break;
					
				case 3:
					canvas.drawText("Wallpaper", 130, 130, textPaint);
					break;
				}
				
				sh.unlockCanvasAndPost(canvas);
				
				++iCounter;				
			}
			
			Handler.removeCallbacks(drawSurface);
			Handler.postDelayed(drawSurface, 2000);
		}
		
		private final Runnable drawSurface = new Runnable() {
			
			@Override
			public void run() 
			{
				drawFrame();
			}
		};
		
	}

}
