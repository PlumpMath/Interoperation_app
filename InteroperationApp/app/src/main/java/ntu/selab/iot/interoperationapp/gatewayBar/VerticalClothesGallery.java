package ntu.selab.iot.interoperationapp.gatewayBar;

import ntu.selab.iot.interoperationapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class VerticalClothesGallery extends LinearLayout{
	private ScrollView gallery;
	private LinearLayout content;
	private int			height ;
	private	int 		width ;
	private OnLayoutChange onLayoutChange;
	public VerticalClothesGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.reportgallery, this, true);
		content = (LinearLayout)findViewById(R.id.verticalcontent_view);
		gallery = (ScrollView)findViewById(R.id.verticalgallery_view);
	}
	
	public void addContent(View view){
		content.addView(view);
	}
	
	public void addContent(View view,int index){
		content.addView(view, index);
	}
	
	public void removeContent(View view){
		((LinearLayout)view.getParent()).removeView(view);
	}
	
	public LinearLayout getContent(){
		return content;
	}
	
	public void removeAllContents(){
		content.removeAllViews();
	}
	public ScrollView getGallery(){
		return gallery;
	}
	
	public int getContentIndex(View view){
		int index = ((LinearLayout)view.getParent()).indexOfChild(view);
		return index;
	}
	
	public void inVisibility (){
		gallery.setVisibility(View.GONE) ;
	}
	
	public void Visibility (){
		gallery.setVisibility(View.VISIBLE) ;
	}
	
	
	public void setOnTileTouchListener(OnTouchListener l){
		for(int i=0 ; i<content.getChildCount() ; i++)
			content.getChildAt(i).setOnTouchListener(l);
	}
	
	public void setOnScrollToEndListener(OnScrollToEndListener l){
		gallery.setOnTouchListener(l);
	}
	public boolean fullScroll(int direction) {
		return gallery.fullScroll(direction);
	}
	
	public void smoothScrollTo(int x, int y){
		gallery.smoothScrollTo(x, y);
	}
	
	public void scrollTo(int x, int y){
		gallery.scrollTo(x, y);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    height = getMeasuredHeight();
	    width = getMeasuredHeight() ;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		height = h;
		width = w ;
	    // bounds will now contain none zero values
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		height = getMeasuredHeight();
	    width = getMeasuredHeight() ;
	}
	
	public int getRealHeight(){
		measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED) ;
		return getMeasuredHeight();
	}
	
	public int getRealWidth(){
		measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED) ;
		return getMeasuredWidth();
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
	
	public void setSmoothScrollingEnabled(boolean smoothScrollingEnabled){
		this.gallery.setSmoothScrollingEnabled(smoothScrollingEnabled) ;
	}
}
