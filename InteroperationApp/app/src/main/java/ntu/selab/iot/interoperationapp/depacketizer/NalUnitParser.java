package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.ArrayList;
import java.util.Iterator;

import ntu.selab.iot.interoperationapp.utils.Tuple;

public abstract class NalUnitParser {
	private NalUnitParser parser;
	
	public Iterator<Tuple<Byte[], Long, Integer>> parse(
			int naluType, Tuple<Byte[], Long, Integer> naluInfo) {
		if (parser != null) {
			return parser.parse(naluType, naluInfo);
		} else {
			return new ArrayList<Tuple<Byte[], Long, Integer>>().iterator();
		}
	}
	
	public void setNext(NalUnitParser parser) {
		this.parser = parser;
	}
}
