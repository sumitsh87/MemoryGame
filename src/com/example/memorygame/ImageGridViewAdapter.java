package com.example.memorygame;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageGridViewAdapter extends BaseAdapter {

	private Activity activity;
	public boolean areImagesHidden;
	MemoryCache memoryCache;
	ArrayList<Integer> revealedImagePosList;
	
	public ImageGridViewAdapter(Activity activity, boolean areImagesHidden, MemoryCache memoryCache, ArrayList<Integer> revealedImagePosList)
	{
		this.activity = activity;
		this.areImagesHidden = areImagesHidden;
		this.memoryCache = memoryCache;
		this.revealedImagePosList = revealedImagePosList;
	}

	@Override
	public int getCount() {
		return Constants.gridSize;
	}

	@Override
	public Object getItem(int position) {
		return memoryCache.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(activity.getBaseContext());
		//Show blank image, if 15 sec timeout is over (areImagesHidden is true) and image at this position not yet revealed.
		if(areImagesHidden && !revealedImagePosList.contains(position))
			imageView.setImageResource(R.drawable.no_photo);
		else 
			imageView.setImageBitmap(memoryCache.get(position));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		//set dimensions of each image
		Resources resource = activity.getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.imageSizeInDP, resource.getDisplayMetrics());
		imageView.setLayoutParams(new GridView.LayoutParams(px, px));
		return imageView;
	}
}