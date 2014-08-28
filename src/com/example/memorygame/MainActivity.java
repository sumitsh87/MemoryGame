package com.example.memorygame;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

public class MainActivity extends Activity {
	GridViewFragment gridViewFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		//Check if the fragment is re-used after orientation change
		gridViewFragment = (GridViewFragment) fragmentManager.findFragmentByTag("gridViewFragment");
		if (gridViewFragment == null) {
			GridViewFragment gridViewFragment = new GridViewFragment();
			fragmentTransaction.add(R.id.gridView, gridViewFragment, "gridViewFragment").commit();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
