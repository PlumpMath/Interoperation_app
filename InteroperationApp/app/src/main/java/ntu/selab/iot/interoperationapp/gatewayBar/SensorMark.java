package ntu.selab.iot.interoperationapp.gatewayBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

public class SensorMark extends TextView {

	private int color;
	public SensorMark(Context context) {
		super(context);
		color = Color.parseColor("#000000");
		// TODO Auto-generated constructor stub
	}
	
	public SensorMark(Context context, AttributeSet attrs) {
		super(context, attrs);
		color = Color.parseColor("#000000");
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
        paint.setStrokeWidth(10.0F);
        canvas.drawRoundRect(rectangle, 0,0, paint);
        
    }

}
