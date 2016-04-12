package ntu.selab.iot.interoperationapp.tile;

import java.io.InputStream;
import java.util.ArrayList;

import ntu.selab.iot.interoperationapp.ImageDownloader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public abstract class PartitionView extends LinearLayout {

//	private final ImageDownloader imageDownloader = ImageDownloader.getNewInstance();
	private final String picSType ="-S.JPG";
	private final String picRType ="-R.PNG";
	private final String picLType ="-L.JPG";
	private final String picUrl = "http://140.115.113.164:8080/wardrobe/wardrobedata/pic/";
	protected ImageView[] content;
	public PartitionView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public PartitionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setContent(int[] resId){
		for(int i =0 ; i<resId.length ; i++){
//			content[i].setImageResource(resId[i]);
			Bitmap bitmap = readBitMap(getContext(),resId[i]);
			content[i].setImageBitmap(bitmap); 
		}
	}
	
	public void setContent(String[] resId){
	    ImageDownloader imageDownloader = ImageDownloader.getNewInstance();
		for(int i =0 ; i<resId.length ; i++)
			imageDownloader.download(picUrl+resId[i]+picRType, content[i]);
	}
	
	public void setContent(ArrayList<String> resId){
		ImageDownloader imageDownloader = ImageDownloader.getNewInstance();
		for(int i =0 ; i<resId.size() ; i++)
			imageDownloader.download(picUrl+resId.get(i)+picRType, content[i]);
	}
	
	private static Bitmap readBitMap(Context context, int resId){  
        BitmapFactory.Options opt = new BitmapFactory.Options();  
        opt.inPreferredConfig = Bitmap.Config.RGB_565;   
        opt.inPurgeable = true;  
        opt.inInputShareable = true;      
        InputStream is = context.getResources().openRawResource(resId);
        try {
    		BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(opt,true);
    	} catch (IllegalArgumentException e) {
    		e.printStackTrace();
    	} catch (SecurityException e) {
    		e.printStackTrace();
    	} catch (IllegalAccessException e) {
    		e.printStackTrace();
    	} catch (NoSuchFieldException e) {
    		e.printStackTrace();
    	}
        return BitmapFactory.decodeStream(is,null,opt);
        
	}
	
		public void setScaleType(ScaleType scaletype) {
		//by BB
		for(int i=0;i<content.length;i++)
		{content[i].setScaleType(scaletype);}
	}

}
