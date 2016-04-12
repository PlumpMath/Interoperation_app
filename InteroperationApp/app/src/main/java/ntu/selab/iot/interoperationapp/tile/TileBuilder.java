package ntu.selab.iot.interoperationapp.tile;

import java.util.ArrayList;

import ntu.selab.iot.interoperationapp.FakeSuiteBean;
import android.content.Context;


public interface TileBuilder {
	void buildTileOfSingleView(Context context,FakeSuiteBean sb);
	void buildTileOfDoubleView(Context context,FakeSuiteBean sb);
	void buildTileOfTripleView(Context context,FakeSuiteBean sb);
	void buildTileOfQuadrupleView(Context context,FakeSuiteBean sb);
	ArrayList<TileView> getTileViewList();
}
