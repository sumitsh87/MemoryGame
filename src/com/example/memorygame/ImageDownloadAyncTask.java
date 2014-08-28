package com.example.memorygame;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

class ImageDownloadAsyncTask extends AsyncTask<String, Integer, List> {
	private String imageLink;
	private MainActivity mainActivity;
	private Bitmap image;
	private GridViewFragment fragment;


	public ImageDownloadAsyncTask (GridViewFragment fragment, Context context, String imageLink, ImageGridViewAdapter gridAdapter)
	{
		this.imageLink = imageLink;
		this.mainActivity = (MainActivity) context;
		this.fragment = fragment;

	}

	private static final boolean API_LEVEL_11 = android.os.Build.VERSION.SDK_INT > 11;

	public void execute(Executor aExecutor, String Param) {     
		if(API_LEVEL_11)
			executeOnExecutor(aExecutor, Param); 
		else
			super.execute(Param);
	}

	@Override
	protected List doInBackground(String... params) {

		try {
			URL url = new URL(imageLink);
			image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Constants.memoryCache.put(Constants.imageDownloadCount, image);
		Constants.imageDownloadCount++;

		return null;
	}

	@Override
	protected void onPostExecute(List s) {
		//		progressDialog.dismiss();
		//        imageGridViewAdapter = new ImageGridViewAdapter(MainActivity.this);
		//        gridView.setAdapter(imageGridViewAdapter);
		Constants.taskFinishCount++;
		if (Constants.taskFinishCount>=9)
		{
			fragment.downloadTaskFinished();
			Log.v("image", "cached images: "+Constants.memoryCache);

		}
		super.onPostExecute(s);
	}

}