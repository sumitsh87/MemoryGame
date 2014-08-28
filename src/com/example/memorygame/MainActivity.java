package com.example.memorygame;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

public class MainActivity extends Activity {
	GridViewFragment gridViewFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialiseElements();
		
		//Using Fragment inorder to avoid async-task's restart when orientation is changed
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		gridViewFragment = (GridViewFragment) fragmentManager.findFragmentByTag("gridViewFragment");
		if (gridViewFragment == null) {
			GridViewFragment gridViewFragment = new GridViewFragment();
			fragmentTransaction.add(R.id.gridView, gridViewFragment, "gridViewFragment").commit();
		}

	}
	void initialiseElements()
	{
		if(Constants.memoryCache==null)
			Constants.memoryCache=new MemoryCache(this);
		
		if(Constants.revealedImagePosList==null)
			Constants.revealedImagePosList=new ArrayList<Integer>();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}