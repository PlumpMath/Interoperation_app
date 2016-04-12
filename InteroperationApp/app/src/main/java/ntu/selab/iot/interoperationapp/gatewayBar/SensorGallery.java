package ntu.selab.iot.interoperationapp.gatewayBar;

import java.util.ArrayList;

import ntu.selab.iot.interoperationapp.FakeSuiteBean;
import ntu.selab.iot.interoperationapp.R;
import ntu.selab.iot.interoperationapp.tile.ContentLayout;
import ntu.selab.iot.interoperationapp.tile.TileBuilder;
import ntu.selab.iot.interoperationapp.tile.TileView;
import ntu.selab.iot.interoperationapp.tile.TileViewBuilder;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class  SensorGallery extends LinearLayout {

	private GalleryTitle title;
	private HorizontalScrollView gallery;
	private LinearLayout staticContent,tabArea,content;

	private GalleryTab tab;
	private int galleryHeight;
	private OnLayoutChange onLayoutChange;
	private TileAdapter galleryTileAdapter;
	public SensorGallery(Context context) {
		this(context, null);
	}
	
	public SensorGallery(Context context, AttributeSet attrs){
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SensorGallery, 0, 0);
		LayoutInflater.from(context).inflate(R.layout.clothegallery, this, true);
		title = (GalleryTitle)findViewById(R.id.gallery_title);
		content = (LinearLayout)findViewById(R.id.content_view);
		staticContent = (LinearLayout)findViewById(R.id.static_content_view);
		gallery = (HorizontalScrollView)findViewById(R.id.gallery_view);
		tab = (GalleryTab)findViewById(R.id.gallery_tab);
		tabArea = (LinearLayout)findViewById(R.id.tab_area);
		
		String clothesGalleryTitle = a.getString(R.styleable.SensorGallery_galleryTitle);
		if(clothesGalleryTitle!=null)
			setTitle(clothesGalleryTitle,false);
		
		boolean showTab = a.getBoolean(R.styleable.SensorGallery_galleryShowTab, false);
		if(showTab)
			setTab(showTab);
		
		boolean adjust = a.getBoolean(R.styleable.SensorGallery_galleryAdjust, false);
		if(adjust)
			adjust();
			
		a.recycle();
	}
	
	public void setTileAdapter(TileAdapter tileAdapter){
		if(tileAdapter!=null){
			this.galleryTileAdapter = tileAdapter;
			setOnScrollToEndListener(new OnScrollToEndListener(){
	
				@Override
				public void onScrollToEnd(View view) {
					// TODO Auto-generated method stub
					galleryTileAdapter.nextView();
				}
				
			});
		}else{
			this.galleryTileAdapter=null;
			setOnScrollToEndListener(null);
		}
	}
	
	public void adjust(){
		int tabAreaPeddingTop = tabArea.getPaddingTop();
		tabAreaPeddingTop += adjustSize();
		tabArea.setPadding(0, tabAreaPeddingTop, 0, 0);
	}
	
	private int adjustSize(){
		Context context = getContext();
		SensorGallery gallery = new SensorGallery(context);
		gallery.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int galleryHeight = gallery.getMeasuredHeight();
		
		int statusBarHeight = getStatusBarHeight();

		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int displayHeight = display.getHeight();
		
		int cellsize = (displayHeight - statusBarHeight - (galleryHeight)*4)/4;
		
		return displayHeight - ((cellsize+galleryHeight)*4) - statusBarHeight;
	}
	
	private int getStatusBarHeight() {
		
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
		    result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	public SensorGallery(Context context, AttributeSet attrs, String titleText, boolean titled, boolean tabed, ArrayList<FakeSuiteBean> sb){
		this(context, attrs);
		setTitle(titleText,titled);
		setTab(tabed);
		ArrayList<TileView> galleryList = buildGalleryList(context,sb);
		for(int i=0;i<galleryList.size();i++)
			addContent(galleryList.get(i));
	}
	
	public void setGallery(String titleText,boolean titled,boolean tabed,ArrayList<FakeSuiteBean> sb){
		setTitle(titleText,titled);
		setTab(tabed);
		ArrayList<TileView> galleryList = buildGalleryList(getContext(),sb);
		for(int i=0;i<galleryList.size();i++)
			addContent(galleryList.get(i));
	}
	
	private ArrayList<TileView> buildGalleryList(Context context,ArrayList<FakeSuiteBean> sb){
		TileBuilder content = new TileViewBuilder();
		for(int viewCount=0 ; viewCount<sb.size(); viewCount++){
			if(sb.get(viewCount).resId.length==1){
				content.buildTileOfSingleView(context, sb.get(viewCount));
			}else if(sb.get(viewCount).resId.length==2){
				content.buildTileOfDoubleView(context, sb.get(viewCount));
			}else if(sb.get(viewCount).resId.length==3){
				content.buildTileOfTripleView(context, sb.get(viewCount));
			}else if(sb.get(viewCount).resId.length==4){
				content.buildTileOfQuadrupleView(context, sb.get(viewCount));
			}			
		}
		return content.getTileViewList();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    galleryHeight = getMeasuredHeight();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		galleryHeight = h;
	    // bounds will now contain none zero values
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		galleryHeight = getMeasuredHeight();
	}
	
	public void setTabIcon(View view, boolean visible){
		tab.setImage(view);
		if(visible){
			tab.setVisibility(VISIBLE);
		}else{
			tab.setVisibility(GONE);
		}
	}
	public void setTab(boolean tabed){
		if(tabed)
		tab.setVisibility(VISIBLE);
		else
		tab.setVisibility(GONE);
	}	
	
	public int getRealHeight(){
		return galleryHeight;
	}
	
	public void setTitle(String text, boolean visible){
		title.setText(text);
		if(visible){
			title.setVisibility(VISIBLE);
		}else{
			title.setVisibility(GONE);
		}
	}
	
	public String getTitle() {
		return title.getText().toString();
	}
	
	
	public void addContent(View view){
		ViewGroup parentViewGroup = (ViewGroup) view.getParent();
		if (parentViewGroup != null) {
            parentViewGroup.removeAllViews();
        }
		content.addView(view);
	}
	
	public void addContent(View view,int index){
		content.addView(view, index);
	}
	
	public void removeContent(View view){
		((LinearLayout)view.getParent()).removeView(view);
	}

    public void hideSpecificView(int i){
        TileView v = (TileView)content.getChildAt(i);
        v.gone();
    }

	
	public void recycleAllTileView(){
		int childcount = content.getChildCount();
		for (int i=0; i < childcount; i++){
			TileView v = (TileView)content.getChildAt(i);
			v.recycle();
		}
	}

    public int getSize(){
        return content.getChildCount();
    }

    public TileView getTileViewByIndex(int i){
        TileView v = (TileView)content.getChildAt(i);
        return v;
    }




	public int getContentIndex(View view){
		int index = ((LinearLayout)view.getParent()).indexOfChild(view);
		return index;
	}
	
	public void addStaticContent(View view){
		gallery.setVisibility(View.GONE);
		staticContent.setVisibility(View.VISIBLE);
		staticContent.addView(view);
	}
	
	public LinearLayout getContent(){
		return content;
	}
	
	public void setOnTileTouchListener(OnTouchListener l){
		for(int i=0 ; i<content.getChildCount() ; i++)
			content.getChildAt(i).setOnTouchListener(l);
	}
	
	public void setOnTitleClickListener(OnClickListener l){
		title.setOnClickListener(l);
	}
	
	public void setOnScrollToEndListener(OnScrollToEndListener l){
		gallery.setOnTouchListener(l);
	}

	public void setOnTabClickListener(OnClickListener l){
		tab.setOnClickListener(l);
	}
	
	
	// 2013.01.09 Added by Keith Hung (kamael@selab.csie.ncu.edu.tw)
	
	public boolean fullScroll(int direction) {
		return gallery.fullScroll(direction);
	}
	
	public void smoothScrollTo(int x, int y){
		gallery.smoothScrollTo(x, y);
	}
	
	public void scrollTo(int x, int y){
		gallery.scrollTo(x, y);
	}
	

	public void childClickable(boolean isLock){
		for(int i=0 ; i<content.getChildCount(); i++){
			content.getChildAt(i).setClickable(isLock);
		}
	}
	
	protected void onLayout (boolean changed, int l, int t, int r, int b){
		super.onLayout(changed, l, t, r, b);
		if(onLayoutChange!=null)
			onLayoutChange.handler();
//		Log.d("galleryLayoutChange:","galleryLayoutChange");
	}
	
	public void setOnLayoutChange(OnLayoutChange l){
		onLayoutChange = l;
	}

}
