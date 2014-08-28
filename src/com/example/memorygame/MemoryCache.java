package com.example.memorygame;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

public class MemoryCache {
	Activity activity;
	private Map<Integer, Bitmap> cache=Collections.synchronizedMap(new LinkedHashMap<Integer, Bitmap>(10,1.5f,true));
	private long size=0;
	private long limit;

	public MemoryCache(Activity activity){
		this.activity = activity;
		//use 25% of available heap size
		setLimit(Runtime.getRuntime().maxMemory()/4);
	}

	public void setLimit(long new_limit){
		limit=new_limit;
	}

	public Bitmap get(int id){
		try{
			if(!cache.containsKey(id))
				return null;
			return cache.get(id);
		}catch(NullPointerException ex){
			ex.printStackTrace();
			return null;
		}
	}

	public void put(int id, Bitmap bitmap){
		if(!cache.containsKey(id))
		{
			cache.put(id, bitmap);
			size+=getSizeInBytes(bitmap);
			checkSize();
		}

	}

	private void checkSize() {
		if(size>limit)
			Toast.makeText(activity,"Memory usage exceeded the limit!!", Toast.LENGTH_LONG).show();

	}

	public void clear() {
		try{
			cache.clear();
			size=0;
		}catch(NullPointerException ex){
			ex.printStackTrace();
		}
	}

	long getSizeInBytes(Bitmap bitmap) {
		if(bitmap==null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}