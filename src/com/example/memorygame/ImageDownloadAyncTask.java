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
	MemoryCache memoryCache;


	public ImageDownloadAsyncTask (GridViewFragment fragment, Context context, String imageLink, ImageGridViewAdapter gridAdapter, MemoryCache memoryCache)
	{
		this.imageLink = imageLink;
		this.mainActivity = (MainActivity) context;
		this.fragment = fragment;
		this.memoryCache = memoryCache;

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
		memoryCache.put(Constants.imageDownloadCount, image);
		Constants.imageDownloadCount++;

		return null;
	}

	@Override
	protected void onPostExecute(List s) {

		Constants.taskFinishCount++;
		if (Constants.taskFinishCount>=9)
			fragment.downloadTaskFinished();
		super.onPostExecute(s);
	}

}