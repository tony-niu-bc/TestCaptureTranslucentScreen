package com.example.todolist;

import android.R.integer;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class MyAppWidget extends AppWidgetProvider 
{

	@Override
	public void onUpdate(Context          context, 
						 AppWidgetManager appWidgetManager,
						 int[]            appWidgetIds) 
	{
		final int N = appWidgetIds.length;
		
		for (int i = 0; i < N; ++i)
		{
			int appWidgetId = appWidgetIds[i];
			
			RemoteViews rv = new RemoteViews(context.getPackageName(), 
											 R.layout.my_app_widget_layout);
			
			rv.setTextViewText(R.id.appWidgetContent, "I am the content of AppWidget");
			
			appWidgetManager.updateAppWidget(appWidgetId, rv);
		}
	}

}
