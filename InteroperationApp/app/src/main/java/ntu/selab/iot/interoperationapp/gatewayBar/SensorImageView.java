package ntu.selab.iot.interoperationapp.gatewayBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SensorImageView extends ImageView {

	private int color;
	public SensorImageView(Context context) {
		super(context);
		color = Color.parseColor("#FFD800");
		// TODO Auto-generated constructor stub
	}
	
	public SensorImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		color = Color.parseColor("#FFD800");
		// TODO Auto-generated constructor stub
	}
	
	@Override  
    protected void onDraw(Canvas canvas) {  	
        super.onDraw(canvas); 
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rec = canvas.getClipBounds();
        RectF rectangle = new RectF(rec.left,rec.top,rec.right,rec.bottom);
        
        if(isSelected()){ 
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.5F);
        canvas.drawRect(rectangle, paint); 
		}
    } 

}
