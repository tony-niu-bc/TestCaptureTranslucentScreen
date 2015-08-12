package com.example.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ToDoItem implements Parcelable
{
	private String strTask;
	private Date   dateCreated;
	
	public String getTask()
	{
		return strTask;
	}
	
	public Date getDate()
	{
		return dateCreated;
	}
	
	public ToDoItem(String _task)
	{
		this(_task, new Date(java.lang.System.currentTimeMillis()));
	}
	
	public ToDoItem(String _task, Date _Created)
	{
		strTask     = _task;
		dateCreated = _Created;
	}

	@Override
	public String toString() 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
		
		String strDate = sdf.format(dateCreated);
		
		return "(" + strDate + ")" + strTask;
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(strTask);
		dest.writeLong(dateCreated.getTime());
	}
	
	private ToDoItem(Parcel in)
	{
		strTask = in.readString();
		dateCreated = new Date();
		dateCreated.setTime(in.readLong());
	}
	
	public static final Parcelable.Creator<ToDoItem> CREATOR = new Parcelable.Creator<ToDoItem>()
	{

		@Override
		public ToDoItem createFromParcel(Parcel source) 
		{
			return new ToDoItem(source);
		}

		@Override
		public ToDoItem[] newArray(int size) 
		{
			return new ToDoItem[size];
		}

	};
	
}
