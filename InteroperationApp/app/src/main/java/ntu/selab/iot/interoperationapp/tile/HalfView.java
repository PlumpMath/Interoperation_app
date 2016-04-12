package ntu.selab.iot.interoperationapp.tile;


import ntu.selab.iot.interoperationapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HalfView extends LinearLayout {

	private ImageView[] content;
	public HalfView(Context context){
		this(context, null);
	}
	
	public HalfView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.half, this, true);
		content = new ImageView[2];
		content[0] = (ImageView)findViewById(R.id.half_up);
		content[1] = (ImageView)findViewById(R.id.half_down);
	}
	
	public void setContent(int[] resId){
		for(int i =0 ; i<resId.length ; i++)
			content[i].setImageResource(resId[i]);
	}
	
	
}
