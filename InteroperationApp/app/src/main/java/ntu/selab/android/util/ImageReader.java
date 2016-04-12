package ntu.selab.android.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

/**
 * Author: Keith Hung (kamael@selab.csie.ncu.edu.tw)
 * Date: 2013.01.28
 * Last Update: 2014.07.16
 * Usage: Reducing memory consumption when loading an image
 * */

public class ImageReader {
	
	public static Bitmap readBitmapByResId(Context context, int resId) {
		BitmapFactory.Options opts = initBitmapFactoryOptions();
		
		InputStream is = context.getResources().openRawResource(resId);
		
		return BitmapFactory.decodeStream(is, null , opts);
	}
	
	public static Bitmap readBitmapByPath(String path) {
		Bitmap bitmap = null;
		
		BitmapFactory.Options opts = initBitmapFactoryOptions();
		
		if (path != null) {
			bitmap = BitmapFactory.decodeFile(path, opts);
		}
		
		return bitmap;
	}
	
	public static Bitmap readBitmapByUri(Context context, Uri uri) {
		Bitmap bitmap = null;
		
		BitmapFactory.Options opts = initBitmapFactoryOptions();
		
		if (uri != null) {
			try {
				InputStream is = context.getContentResolver().openInputStream(uri);
				bitmap = BitmapFactory.decodeStream(is, null, opts);
				
			} catch (FileNotFoundException e) {
				Log.e("IMAGE_READER", "File not found in: " + uri);
				
				return null;
			}
		}
		
		return bitmap;
	}

	private static BitmapFactory.Options initBitmapFactoryOptions() {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		
		return opts;
	}
}
