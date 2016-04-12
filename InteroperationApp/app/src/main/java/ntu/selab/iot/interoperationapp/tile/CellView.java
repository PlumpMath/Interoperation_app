package ntu.selab.iot.interoperationapp.tile;

import ntu.selab.iot.interoperationapp.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class CellView extends RelativeLayout {

	protected RelativeLayout cellContent,cellMask;
	
	public CellView(Context context) {
		this(context, null);
	}
	
	public CellView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.cellview, this, true);
		cellContent = (RelativeLayout) findViewById(R.id.cell_content);
		cellMask = (RelativeLayout) findViewById(R.id.cell_mask);
	}
	
	public void addCellContent(View view){
		cellContent.addView(view);
	}
	
	public void addCellMask(View view){
		cellMask.addView(view);
	}

}
