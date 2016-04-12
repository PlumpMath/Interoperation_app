package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.ArrayList;
import java.util.Iterator;

import ntu.selab.iot.interoperationapp.utils.Tuple;

public class MTAP24NalUnitParser extends NalUnitParser {

	@Override
	public Iterator<Tuple<Byte[], Long, Integer>> parse(int naluType, Tuple<Byte[], Long, Integer> naluInfo) {
		if (naluType == 27) {
			ArrayList<Tuple<Byte[], Long, Integer>> buffer = new ArrayList<Tuple<Byte[], Long, Integer>>();
			int offset=1;
			int donBase = (int) ((naluInfo._1()[offset++]&0xFF)<<8|(naluInfo._1()[offset++]&0xFF));
			while(offset<=naluInfo._1().length){
				int size = (int) ((naluInfo._1()[offset++]&0xFF)<<8|(naluInfo._1()[offset++]&0xFF));
				int dond = (int)(naluInfo._1()[offset++]&0xFF);
				int tsOffset = (int)(naluInfo._1()[offset++]&0xFF)<<8|(naluInfo._1()[offset++]&0xFF);
				Byte[] fragment = new Byte[size];
				System.arraycopy(naluInfo._1(),offset,fragment,0,size);	
				buffer.add(new Tuple(fragment,naluInfo._2()+tsOffset,(donBase+dond)%65536));
				offset+=size;
			}
			return buffer.iterator();
		} else {
			return super.parse(naluType,naluInfo);
		}
	}

}
