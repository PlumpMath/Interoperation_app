package ntu.selab.iot.interoperationapp.tile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class TileSelect extends View {

	private int color;
	public TileSelect(Context context) {
		this(context,null);
	}
	
	public TileSelect(Context context, AttributeSet attrs) {
		super(context, attrs);
		color = Color.parseColor("#FFD800");

	}
	
	@Override  
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); 
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rec = canvas.getClipBounds();
        RectF rectangle = new RectF(rec.left,rec.top,rec.right,rec.bottom);
        
        
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.5F);
        canvas.drawRect(rectangle, paint); 
		
    } 

}
