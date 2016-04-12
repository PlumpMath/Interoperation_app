package ntu.selab.iot.interoperationapp.gatewayBar;

import ntu.selab.iot.interoperationapp.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GalleryTab extends LinearLayout {
	
	private int color;
	public GalleryTab(Context context) {
		this(context,null);
	}

	public GalleryTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		ImageView plus = new ImageView(context);
        plus.setImageResource(R.drawable.plus);
        addView(plus);
		color = Color.parseColor("#000000");
		setWillNotDraw(false);
		// TODO Auto-generated constructor stub
	}

	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); 
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rec = canvas.getClipBounds();
        RectF rectangle = new RectF(rec.left,rec.top,rec.right,rec.bottom+15);
        
        
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.0F);
        canvas.drawRoundRect(rectangle, 0,0, paint);
        
    }
	
	public void setImage(View view){
		removeAllViews();
		addView(view);
	}
}
