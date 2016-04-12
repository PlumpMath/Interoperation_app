package ntu.selab.iot.interoperationapp.gatewayBar;

import java.util.List;

import android.content.Context;
import android.view.View.OnClickListener;
import ntu.selab.iot.interoperationapp.tile.TileBean;
import ntu.selab.iot.interoperationapp.tile.TileView;

public class TileAdapter {
	private int initSize=4;
	private int count=0;
	private SensorGallery sensorGallery;
	private List<TileBean> value;
	private OnClickListener onClickListner;
	private Context context;
	public TileAdapter(List<TileBean> value,OnClickListener onClickListner, SensorGallery sensorGallery,Context context){
		this.value = value;
		this.sensorGallery = sensorGallery;
		this.onClickListner = onClickListner;
		this.context = context;
	}
	
	public void init(){
		for(int i=0 ; i<value.size()&&i<initSize ; i++){
			TileBean item = value.get(i);
			TileView tile = new TileView(context);
			tile.setContent(item.getResId());
			tile.setTitle(item.getTitle());
			tile.setOnClickListener(onClickListner);
			sensorGallery.addContent(tile);
			count++;
		}
	}
	public void nextView(){
		int fetchSize = initSize;
		for(int i=count ; i<value.size() ; i++){
			TileBean item = value.get(i);
			TileView tile = new TileView(context);
			tile.setContent(item.getResId());
			tile.setTitle(item.getTitle());
			tile.setOnClickListener(onClickListner);
			sensorGallery.addContent(tile);
			count++;
			fetchSize--;
			if(fetchSize==0)
				break;
		}
	}
}
