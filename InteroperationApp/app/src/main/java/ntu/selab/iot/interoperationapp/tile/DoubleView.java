package ntu.selab.iot.interoperationapp.tile;


import ntu.selab.iot.interoperationapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

public class DoubleView extends PartitionView {

	
	public DoubleView(Context context){
		this(context, null);
	}
	
	public DoubleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.doubleview, this, true);
		content = new ImageView[2];
		content[0] = (ImageView)findViewById(R.id.double_up);
		content[1] = (ImageView)findViewById(R.id.double_down);
	}
	
}
