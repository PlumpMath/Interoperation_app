package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.ArrayList;
import java.util.Iterator;

import ntu.selab.iot.interoperationapp.utils.Tuple;

public class STAP_ANalUnitParser extends NalUnitParser {

	@Override
	public Iterator<Tuple<Byte[], Long, Integer>> parse(int naluType, Tuple<Byte[], Long, Integer> naluInfo) {
		if (naluType == 24) {
			ArrayList<Tuple<Byte[], Long, Integer>> buffer = new ArrayList<Tuple<Byte[], Long, Integer>>();
			int offset=1;
			while(offset<=naluInfo._1().length){
				int size = (int) ((naluInfo._1()[offset++]&0xFF)<<8|(naluInfo._1()[offset++]&0xFF));
				Byte[] fragment = new Byte[size];
				System.arraycopy(naluInfo._1(),offset,fragment,0,size);	
				buffer.add(new Tuple(fragment,naluInfo._2(),naluInfo._3()));
				offset+=size;
			}
			return buffer.iterator();
		} else {
			return super.parse(naluType, naluInfo);
		}
	}
	
}
