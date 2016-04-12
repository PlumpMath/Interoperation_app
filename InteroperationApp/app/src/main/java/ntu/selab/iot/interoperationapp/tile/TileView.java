package ntu.selab.iot.interoperationapp.tile;

import java.util.ArrayList;

import ntu.selab.android.util.ImageReader;
import ntu.selab.iot.interoperationapp.R;
import ntu.selab.iot.interoperationapp.gatewayBar.SensorGallery;
import ntu.selab.iot.interoperationapp.protocol.communication.DataInfo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.util.Pools.SynchronizedPool;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import ntu.selab.iot.interoperationapp.view.GatewayBar;
public class TileView extends RelativeLayout implements OnTouchListener{

	protected TextView tileTitle;
	protected RelativeLayout tileContent,tileCell;
	protected TileSelect tileSelect;
	protected int[] resId;
	private ArrayList<String> objId;
	private int  TileId;
	private static final SynchronizedPool<TileView> sPool = new SynchronizedPool<TileView>(10);
	private DataInfo reference;
    private String uuid,type;

	public TileView(Context context){
		this(context, null);
	}
	
	public TileView(Context context,boolean dynamicAdjust){
		super(context, null);
		initLayoutRes(context);
		if(dynamicAdjust)		
			adjustSize();
	}
	
	public TileView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView, 0, 0);
		
		initLayoutRes(context);
		boolean adjust = a.getBoolean(R.styleable.TileView_adjustTitleSize, true);
		if(adjust)		
		adjustSize();
		
		boolean isTitle = a.getBoolean(R.styleable.TileView_showTitle, true);
		if(!isTitle)		
			tileTitle.setVisibility(View.GONE);
		
		int titleSize = a.getInteger(R.styleable.TileView_tileTitleSize, 0);
		if(titleSize!=0)
			setTitleSize(titleSize);
		a.recycle();
	}
	
	private void initLayoutRes(Context context){
		LayoutInflater.from(context).inflate(R.layout.tile, this, true);
        tileCell = (RelativeLayout)findViewById(R.id.tile_cell);
		tileTitle = (TextView)findViewById(R.id.tile_title);
		tileContent = (RelativeLayout)findViewById(R.id.tile_content);
		tileSelect = (TileSelect)findViewById(R.id.tile_select);
		setOnTouchListener(this);
	}

    public void gone(){
        tileCell.setVisibility(GONE);
    }

    public void visible(){
        tileCell.setVisibility(VISIBLE);
    }

	private void adjustSize(){
		Context context = getContext();
		SensorGallery gallery = new SensorGallery(context);
		gallery.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int galleryHeight = gallery.getMeasuredHeight();
//		Log.d("galleryHeight:",galleryHeight+"");
		
		int statusBarHeight = getStatusBarHeight();
//		Log.d("statusBarHeight:",statusBarHeight+"");
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int displayHeight = display.getHeight();
//		Log.d("displayHeight:",displayHeight+"");
		
		int cellsize = (displayHeight - statusBarHeight - (galleryHeight)*4)/4;
//		Log.d("cellsize:",cellsize+"");
		
		android.view.ViewGroup.LayoutParams change = tileCell.getLayoutParams();
		change.width = cellsize;
		change.height = cellsize;
		tileCell.setLayoutParams(change);
	}
	
	private int getStatusBarHeight() {
		
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
		    result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	public String getTileTitle(){
		return tileTitle.getText().toString();
	}

	
	public void setSelected(boolean selected){
		if(selected)
			tileSelect.setVisibility(VISIBLE);
		else
			tileSelect.setVisibility(GONE);
	}
	
	public RelativeLayout getTileContent(){
		return tileContent;
	}
	
	public void setTitleVisibility(int visibility){
		this.tileTitle.setVisibility(visibility) ;
	}
	
	public int getTitleVisibility(){
		return this.tileTitle.getVisibility() ;
	}
	
	public TextView getTitleContent(){
		return this.tileTitle ;
	}
	
	public void setTitle(String text){
		tileTitle.setText(text);
	}
	
	public void setTitleSize(float size){
		tileTitle.setTextSize(size);
	}
	
	public void setTitleSize(int unit, float size){
		tileTitle.setTextSize(unit, size);
	}
	
	public void setTileId(int Id){
		TileId=Id;
	}
	
	public float getTitleSize(){
		return this.tileTitle.getTextSize() ;
	}
	
	public int getTileId(){
		return TileId;
	}
	public int[] getContent(){
		return this.resId;
	}
	
	public ArrayList<String> getObjId(){
		return objId;
	}
	
	public void setContent(int...resId){
		Context context = getContext();
		this.resId = resId;
		if(resId.length==1){
			ImageView single = new ImageView(context);
			single.setScaleType(ScaleType.CENTER_CROP);
			single.setBackgroundColor(Color.WHITE);
			single.setImageBitmap(ImageReader.readBitmapByResId(context,resId[0]));
			tileContent.addView(single);
			return ;
		}
		
		
		TilePartitionViewBuilder content = new TilePartitionViewBuilder();
		
		if(resId.length==1){
			content.builderSingleView(context);		
		}else if(resId.length==2){
			content.builderDoubleView(context);
		}else if(resId.length==3){
			content.builderTripleView(context);
		}else if(resId.length==4){
			content.builderQuadrupleView(context);
		}
		
		content.getPartitionView().setContent(resId);
		tileContent.addView(content.getPartitionView());

	}
	
	public void setContent(String[] resId){
		TilePartitionViewBuilder content = new TilePartitionViewBuilder();
		Context context = getContext();
		if(resId.length==1){
			content.builderSingleView(context);		
		}else if(resId.length==2){
			content.builderDoubleView(context);
		}else if(resId.length==3){
			content.builderTripleView(context);
		}else if(resId.length==4){
			content.builderQuadrupleView(context);
		}
		
		content.getPartitionView().setContent(resId);
		tileContent.addView(content.getPartitionView());

	}
	
	public void setContent(ArrayList<String> objId){
		TilePartitionViewBuilder content = new TilePartitionViewBuilder();
		Context context = getContext();
		if(objId.size()==1){
			content.builderSingleView(context);		
		}else if(objId.size()==2){
			content.builderDoubleView(context);
		}else if(objId.size()==3){
			content.builderTripleView(context);
		}else if(objId.size()==4){
			content.builderQuadrupleView(context);
		}
		
		content.getPartitionView().setContent(objId);
		tileContent.addView(content.getPartitionView());

	}
	
	public void setContent(PartitionView content){
		tileContent.addView(content);
	}
	
	public void setContent(View content){
		tileContent.addView(content);
	}

	public void removeContent() {
		if ( tileContent.getChildCount() > 0 )
			tileContent.removeViewAt(tileContent.getChildCount()-1) ;
	} // removeContent()
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Context context = getContext();
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.tilezoomout);   	 
     	v.startAnimation(anim);
		return false;
	}
	public static TileView obtain(Context context){
        TileView instance = sPool.acquire();
        return (instance != null) ? instance : new TileView(context);
    }
	
	public void recycle(){
//		ViewGroup parentViewGroup = (ViewGroup) this.getParent();
//        if (parentViewGroup != null) {
//            parentViewGroup.removeAllViews();
//        }
		this.setOnClickListener(null);
		removeContent();
		setTitle(null);
		sPool.release(this);
	}

    public void setReference(DataInfo dataInfo){
        this.reference = dataInfo;
    }

    public void update(){
        setTitle(((String)reference.getExpressions()[0].getValue())+((String)reference.getExpressions()[0].getUnit()));
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
