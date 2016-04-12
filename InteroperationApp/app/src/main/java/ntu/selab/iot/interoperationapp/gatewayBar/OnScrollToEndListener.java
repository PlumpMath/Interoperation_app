package ntu.selab.iot.interoperationapp.gatewayBar;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;

public abstract class OnScrollToEndListener implements OnTouchListener {

	@Override
	public boolean onTouch(View horizontalScrollView, MotionEvent event) {
		// TODO Auto-generated method stub
		View view = (View) ((HorizontalScrollView)horizontalScrollView).getChildAt(((HorizontalScrollView)horizontalScrollView).getChildCount() - 1);
		int subViewWidth = view.getRight();
		int x = horizontalScrollView.getScrollX();
		if(subViewWidth - x - horizontalScrollView.getWidth() == 0)
			onScrollToEnd(view);
		return false;
	}
	
	abstract public void onScrollToEnd(View view);

}
