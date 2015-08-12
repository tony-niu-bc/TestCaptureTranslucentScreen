package com.example.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> 
{

	private int iResource;
	
	public ToDoItemAdapter(Context        context, 
			               int            resource, 
			               List<ToDoItem> objects) 
	{
		super(context, resource, objects);
		
		iResource = resource;
	}

	@Override
	public View getView(int       position, 
			            View      convertView, 
			            ViewGroup parent) 
	{
		//LinearLayout todoView;
		
		ToDoItem tdi = getItem(position);
		
		String strTask     = tdi.getTask();
		Date   dateCreated = tdi.getDate();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
		String dateString    = sdf.format(dateCreated);
		
		if (null == convertView)
		{
			//todoView = new LinearLayout(getContext());
			
			String strInflater = Context.LAYOUT_INFLATER_SERVICE;
			
			LayoutInflater li = (LayoutInflater)getContext().getSystemService(strInflater);
			convertView = li.inflate(iResource, parent, false);
		}
//		else
//		{
//			todoView = (LinearLayout)convertView;
//		}
		
		TextView tvDate = (TextView)convertView.findViewById(R.id.rowDate);
		TextView tvTask = (TextView)convertView.findViewById(R.id.row);
		
		tvDate.setText(dateString);
		tvTask.setText(strTask);
		
		return convertView;
	}

}
