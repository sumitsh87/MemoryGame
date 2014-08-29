package com.example.memorygame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GridViewFragment extends Fragment {
	Activity mainActivity;
	HttpResponse response;
	JSONObject responseJSON;
	JSONArray imageItemsList;
	String imageLink;
	GridView gridView;
	ImageView singleImageView;
	Timer timer;
	ViewGroup root;
	ImageGridViewAdapter imageGridViewAdapter;
	int currentRandomNumber;
	Random rand;
	boolean allotedTimeUp=false;
	int imagesRevealedCount = 0;
	boolean areImagesHidden = false;
	TextView movesCountView, timerValueView;
	int movesCount = 0;
	int successCount = 0;
	ArrayList<Integer> list;
	int timeCountDown;
	boolean isDownloadTaskRunning = false;
	ProgressDialog progressDialog;
	MemoryCache memoryCache;
	ArrayList<Integer> revealedImagePosList;

	public static Fragment newInstance(Context context) {
		GridViewFragment gridViewFragment = new GridViewFragment();
		return gridViewFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		initialiseElements();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainActivity =getActivity();
 
		//If Fragment is not being re-used, then only initilialise these UI Elements 
		if(root==null)
		{
			root = (ViewGroup) inflater.inflate(R.layout.grid_view_fragment, null);
			movesCountView = (TextView)root.findViewById(R.id.movesCount);
			timerValueView = (TextView)root.findViewById(R.id.timerValue);
			gridView = (GridView)root.findViewById(R.id.gridView);
			gridView.setOnItemClickListener(itemClickListener);

			//Generate a Random Number List by shuffling
			genRandomNumberList();

			//Download image list from FLickr Asynchronously
			Thread thread = new Thread()
			{
				@Override
				public void run()
				{
					downloadImageLinks();
					downloadStartHandler.sendEmptyMessage(1);
				}
			};
			thread.start();
		}
		else 
			gridView.setAdapter(imageGridViewAdapter);

		return root;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(isDownloadTaskRunning)
			showProgressBar();
	}
	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
	}


	public void downloadTaskFinished() {
		if(progressDialog!=null)
			progressDialog.dismiss();
		isDownloadTaskRunning = false;
		areImagesHidden=false;
		//create a new adapter to show all the images in a grid
		imageGridViewAdapter = new ImageGridViewAdapter(mainActivity, areImagesHidden,memoryCache, revealedImagePosList);
		gridView.setAdapter(imageGridViewAdapter);
		Toast.makeText(mainActivity,"Memorize the images, you have 15 seconds", Toast.LENGTH_LONG).show();
		checkCallTimeout();

	}

	void downloadImageLinks()
	{
		HttpGet httpGet = new HttpGet("https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1&tags="+Constants.IMAGE_TAG);
		HttpClient httpclient = new DefaultHttpClient();

		try {
			response = httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();

		try {
			responseJSON = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			imageItemsList = responseJSON.getJSONArray("items");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkCallTimeout()
	{
		final Handler handler = new Handler();
		timer = new Timer();
		timeCountDown=15;

		TimerTask doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {

						timeCountDown--;
						timerValueView.setText(String.valueOf(timeCountDown));
						if(timeCountDown == 0)
						{
							allotedTimeUp = true;
							Toast.makeText(mainActivity,"Time is up!! Now identify the image location", Toast.LENGTH_LONG).show();
							timer.cancel();
							revealNewImage();
							
							//Lets hide all the images
							imageGridViewAdapter.areImagesHidden = true;
							imageGridViewAdapter.notifyDataSetChanged();
							gridView.setAdapter(imageGridViewAdapter);
						}

					}
				});
			}
		};
		timer.scheduleAtFixedRate(doAsynchronousTask, 1000, 1000);   // every 15 hits the url to maintain session
	}

	public void revealNewImage()
	{
		Log.v("test", "changing the single image, count = "+successCount);
		currentRandomNumber = list.get(successCount++);

		Log.v("test", "current random number:  "+currentRandomNumber);
		singleImageView = (ImageView)root.findViewById(R.id.imageOption);
		singleImageView.setImageBitmap(memoryCache.get(currentRandomNumber));
		singleImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		singleImageView.setVisibility(View.VISIBLE);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			if(allotedTimeUp)
			{
				movesCount++;
				movesCountView.setText(String.valueOf(movesCount));

				if (currentRandomNumber == position && successCount <Constants.gridSize)
				{
					ImageView imageView = (ImageView) view;		
					imageView.setImageBitmap(memoryCache.get(position));
					//add the position to the list to save  in case there is a configuration change
					revealedImagePosList.add(currentRandomNumber);	
					Log.v("test", "position clicked : "+position);
					revealNewImage();
				}
				else if (successCount==Constants.gridSize)
				{
					showSuccessAlert();
				}
		
			}
		}

	};

	void showSuccessAlert()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainActivity);
		alertDialogBuilder.setTitle("Congrats");
		// set dialog message
		alertDialogBuilder 
		.setMessage("You have successfully finished the level.")
		.setCancelable(false)
		.setPositiveButton("Play Again",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				resetElements();
				Intent intent = new Intent(mainActivity, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);

			}
		})
		.setNegativeButton("Exit",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				resetElements();
				mainActivity.finish();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		// show it

		alertDialog.show();
	}

	void genRandomNumberList()
	{
		//prepare for random values
		list = new ArrayList<Integer>();
		for(int i=0;i<Constants.gridSize;i++)
		{
			list.add(i);  
		}
		Collections.shuffle(list);	

	}


	Handler downloadStartHandler = new Handler() {
		public void handleMessage(Message msg) 
		{
			areImagesHidden =false;
			imageGridViewAdapter = new ImageGridViewAdapter(mainActivity, areImagesHidden,memoryCache, revealedImagePosList);
			isDownloadTaskRunning = true;
			showProgressBar();
			for (int index=0; index<Constants.gridSize; index++)
			{
				try {
					JSONObject mediaObj = (JSONObject) imageItemsList.getJSONObject(index).get("media");
					imageLink = mediaObj.get("m").toString();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.v("image", "image Link : "+ imageLink);
				ImageDownloadAsyncTask imageDownloadTask = new ImageDownloadAsyncTask (GridViewFragment.this, mainActivity , imageLink, imageGridViewAdapter, memoryCache );
				imageDownloadTask.execute(ImageDownloadAsyncTask.THREAD_POOL_EXECUTOR, "ImageDownloader");
			}

		}
	};

	void showProgressBar()
	{
		progressDialog = new ProgressDialog(mainActivity);
		progressDialog.setMessage("Downloading images from Flickr");
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	@Override
	public void onDetach() {
	
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		super.onDetach();
	}

	void initialiseElements()
	{
			memoryCache=new MemoryCache(mainActivity);
			//revealedImagePosList - stores position of images already revealed/identified. 
			//Useful for restoring state after orientation change
			revealedImagePosList=new ArrayList<Integer>();
	}
	void resetElements()
	{
		memoryCache=null;
		revealedImagePosList=null;
		Constants.imageDownloadCount = 0;
		Constants.taskFinishCount = 0;
		
	}

}
