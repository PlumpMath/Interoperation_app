package ntu.selab.iot.interoperationapp.tile;

import android.content.Context;
import android.view.View;

public class TilePartitionViewBuilder implements PartitionViewBuilder {
	private PartitionView content;
	@Override
	public void builderSingleView(Context context) {
		// TODO Auto-generated method stub
		content = new SingleView(context);
	}

	@Override
	public void builderDoubleView(Context context) {
		// TODO Auto-generated method stub
		content = new DoubleView(context);
	}

	@Override
	public void builderTripleView(Context context) {
		// TODO Auto-generated method stub
		content = new TripleView(context);
	}

	@Override
	public void builderQuadrupleView(Context context) {
		// TODO Auto-generated method stub
		content = new QuadrupleView(context);
	}

	@Override
	public PartitionView getPartitionView() {
		// TODO Auto-generated method stub
		return content;
	}

}
