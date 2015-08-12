package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyDlgFragment extends DialogFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, 
							 ViewGroup container,
							 Bundle savedInstanceState) 
	{
		Log.i("todolist", "MyDlgFragment - onCreateView");
		
		View view = inflater.inflate(R.layout.test_dialog_view, container, false);
		
		TextView tv = (TextView)view.findViewById(R.id.dlgTV);
		tv.setText("Test Dialog Text!");
		
		return view;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		Log.i("todolist", "MyDlgFragment - onCreateDialog");
		
		Dialog dlg = super.onCreateDialog(savedInstanceState);
		dlg.setTitle("I am a MyDlgFragment");
		return dlg;
		
//		DatePickerDialog dpd = new DatePickerDialog(getActivity(), 
//													new DatePickerDialog.OnDateSetListener() {			
//			@Override
//			public void onDateSet(DatePicker view, 
//								  int        year, 
//								  int        monthOfYear,
//								  int        dayOfMonth) 
//			{
//				Toast toast = Toast.makeText(getActivity().getApplicationContext(), "You selected date below", Toast.LENGTH_LONG);
//				toast.setGravity(Gravity.BOTTOM, 0, 0);
//				
//				LinearLayout ll = new LinearLayout(getActivity().getApplicationContext());
//				ll.setOrientation(LinearLayout.VERTICAL);
//				
//				TextView tv = new TextView(getActivity().getApplicationContext());
//				tv.setText("Selected: " + year + "-" + monthOfYear + "-" + dayOfMonth);
//				
//				CompassView cv = new CompassView(getActivity().getApplicationContext());
//				
//				ll.addView(cv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
//						 									 LinearLayout.LayoutParams.WRAP_CONTENT));
//				
//				ll.addView(tv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
//						 									 LinearLayout.LayoutParams.WRAP_CONTENT));
//
//				toast.setView(ll);
//				toast.show();
//				
//				//Log.i("todolist", "Selected: " + year + "-" + monthOfYear + "-" + dayOfMonth);
//			}
//			
//		}, 2015, 07, 27);
		
//		return dpd;
	}

}
