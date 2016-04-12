package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

import ntu.selab.iot.interoperationapp.utils.Tuple;

public class SingleNalUnitParser extends NalUnitParser {
	public final static String TAG ="SingleNalUnitParser";
	@Override
	public Iterator<Tuple<Byte[], Long, Integer>> parse(int naluType, Tuple<Byte[], Long, Integer> naluInfo) {
		if (1<=naluType&&naluType<=23) {
//			Log.d(TAG,"v-inParse");
			ArrayList<Tuple<Byte[], Long, Integer>> buffer = new ArrayList<Tuple<Byte[], Long, Integer>>();
			buffer.add(naluInfo);
			Iterator iterator = buffer.iterator();
			return iterator;
		} else {
			return super.parse(naluType, naluInfo);
		}
	}
	
//	@Override
//	public Iterator<Tuple<Byte[], Long, Integer>> parse(int naluType, Tuple<Byte[], Long, Integer> naluInfo) {
//		Log.e(TAG,"d-ssssinParse");
//		return super.parse(naluType, naluInfo);
//		
//	}
}
