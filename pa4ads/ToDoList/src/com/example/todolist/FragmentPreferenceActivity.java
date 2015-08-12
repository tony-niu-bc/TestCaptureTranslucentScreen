package com.example.todolist;

import java.util.List;

import android.preference.PreferenceActivity;

public class FragmentPreferenceActivity extends PreferenceActivity
{

	@Override
	public void onBuildHeaders(List<Header> target) 
	{
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

}
