package com.example.memorygame;

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

	public ImageGridViewAdapter(Activity activity, boolean areImagesHidden)
	{
		this.activity = activity;
		this.areImagesHidden = areImagesHidden;
	}

	@Override
	public int getCount() {
		return 9;
	}

	@Override
	public Object getItem(int position) {
		return Constants.memoryCache.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(activity.getBaseContext());
		if(areImagesHidden && !Constants.revealedImagePosList.contains(position))
			imageView.setImageResource(R.drawable.no_photo);
		else 
			imageView.setImageBitmap(Constants.memoryCache.get(position));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		//set Size of each image
		int imageSizeInDP = 80;
		Resources resource = activity.getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imageSizeInDP, resource.getDisplayMetrics());
		imageView.setLayoutParams(new GridView.LayoutParams(px, px));
		return imageView;
	}
}