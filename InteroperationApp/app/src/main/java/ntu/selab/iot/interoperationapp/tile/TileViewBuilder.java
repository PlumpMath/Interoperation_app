package ntu.selab.iot.interoperationapp.tile;

import java.util.ArrayList;

import ntu.selab.iot.interoperationapp.FakeSuiteBean;
import android.content.Context;

public class TileViewBuilder implements TileBuilder {
	private ArrayList<TileView> tileViewList = new ArrayList<TileView>();
	private PartitionView partitionView;
	private TileView tile;
	
	public void buildTileOfSingleView(Context context,FakeSuiteBean sb) {
		// TODO Auto-generated method stub
		partitionView = new SingleView(context);
		partitionView.setContent(sb.resId);
		tile = new TileView(context);
		tile.setContent(partitionView);
		tile.setTitle(sb.tilte);
		tileViewList.add(tile);
	}


	public void buildTileOfDoubleView(Context context,FakeSuiteBean sb) {
		// TODO Auto-generated method stub
		partitionView = new DoubleView(context);
		partitionView.setContent(sb.resId);
		tile = new TileView(context);
		tile.setContent(partitionView);
		tile.setTitle(sb.tilte);
		tileViewList.add(tile);
	}


	public void buildTileOfTripleView(Context context,FakeSuiteBean sb) {
		// TODO Auto-generated method stub
		partitionView = new TripleView(context);
		partitionView.setContent(sb.resId);
		tile = new TileView(context);
		tile.setContent(partitionView);
		tile.setTitle(sb.tilte);
		tileViewList.add(tile);
	}


	public void buildTileOfQuadrupleView(Context context,FakeSuiteBean sb) {
		// TODO Auto-generated method stub
		partitionView = new QuadrupleView(context);
		partitionView.setContent(sb.resId);
		tile = new TileView(context);
		tile.setContent(partitionView);
		tile.setTitle(sb.tilte);
		tileViewList.add(tile);
	}


	public ArrayList<TileView> getTileViewList() {
		// TODO Auto-generated method stub
		return tileViewList;
	}

}
