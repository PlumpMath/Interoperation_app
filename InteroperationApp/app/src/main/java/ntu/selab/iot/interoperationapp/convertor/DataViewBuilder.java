package ntu.selab.iot.interoperationapp.convertor;

import java.util.ArrayList;

import ntu.selab.iot.interoperationapp.tile.TileView;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;

import android.content.Context;
import android.widget.RelativeLayout;

public abstract class DataViewBuilder {
    protected Context context;
	protected ArrayList<RelativeLayout> dataViewsOutput = new ArrayList<RelativeLayout>();
//	protected HashMap<String,TileView> dataViewsOutput = new HashMap<>();
	
	public abstract void convertStringDataView(DataInfo dataInfo,String uuid);
//	public abstract void convertIntegerDataView(int integer);
//	public abstract void convertImageDataView(Bitmap image);
//	public abstract void convertContextDataView(Bitmap image);
    public abstract void convertSpecificSensorTileView(final String uuid, final String deviceName, String ip);
	public abstract TileView convertMediaDataView(DataInfo dataInfo,String uuid,String ip,String DeviceName);
	public abstract ArrayList<RelativeLayout> getDataViews_OldUI();
	public abstract ArrayList<RelativeLayout> getDataViews();
	
}
