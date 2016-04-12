package ntu.selab.iot.interoperationapp.tile;


import ntu.selab.iot.interoperationapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

public class SingleView extends PartitionView {

	public SingleView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public SingleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.singleview, this, true);
		content = new ImageView[1];
		content[0] = (ImageView)findViewById(R.id.single);
	}

}
