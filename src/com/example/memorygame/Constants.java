package com.example.memorygame;

import java.util.ArrayList;

class Constants 
{
	public static String FLICKR_API_KEY = "9bc22b76e3cb045ff03c48d1bebcfed5";
	public static String IMAGE_TAG = "India";
	public static int imageDownloadCount = 0;
	public static int taskFinishCount = 0;
	public static MemoryCache memoryCache;
	public static ArrayList<Integer> revealedImagePosList;

	public static void resetStaticVariables()
	{
		imageDownloadCount = 0;
		taskFinishCount = 0;
		memoryCache.clear();
		revealedImagePosList = null;
	}


}