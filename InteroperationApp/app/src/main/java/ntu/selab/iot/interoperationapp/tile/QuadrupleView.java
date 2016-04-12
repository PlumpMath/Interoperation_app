package ntu.selab.iot.interoperationapp.tile;


import ntu.selab.iot.interoperationapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

public class QuadrupleView extends PartitionView {

	public QuadrupleView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public QuadrupleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.quadrupleview, this, true);
		content = new ImageView[4];
		content[0] = (ImageView)findViewById(R.id.quadruple_up_left);
		content[1] = (ImageView)findViewById(R.id.quadruple_up_right);
		content[2] = (ImageView)findViewById(R.id.quadruple_down_left);
		content[3] = (ImageView)findViewById(R.id.quadruple_down_right);
	}

}
