package ntu.selab.iot.interoperationapp.tile;

import android.content.Context;
import android.view.View;

public interface PartitionViewBuilder {
	void builderSingleView(Context context);
	void builderDoubleView(Context context);
	void builderTripleView(Context context);
	void builderQuadrupleView(Context context);
	PartitionView getPartitionView();
}
