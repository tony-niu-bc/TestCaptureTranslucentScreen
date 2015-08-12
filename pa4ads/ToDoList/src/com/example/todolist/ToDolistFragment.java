package com.example.todolist;

import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
//import android.widget.Toast;

public class ToDolistFragment extends ListFragment 
{

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
//		Toast.makeText(this.getActivity().getBaseContext(), 
//				       "position: " + position + " id: " + id, 
//				       Toast.LENGTH_SHORT)
//		     .show();		
		super.onListItemClick(l, v, position, id);
	}

}
