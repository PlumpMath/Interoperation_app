package ntu.selab.iot.interoperationapp.tile;


import ntu.selab.iot.interoperationapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

public class TripleView extends PartitionView {

	public TripleView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public TripleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.tripleview, this, true);
		content = new ImageView[3];
		content[0] = (ImageView)findViewById(R.id.triple_up_left);
		content[1] = (ImageView)findViewById(R.id.triple_down_left);
		content[2] = (ImageView)findViewById(R.id.triple_right);
	}

}
