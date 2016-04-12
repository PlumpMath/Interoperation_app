package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import android.util.Log;

import ntu.selab.iot.interoperationapp.utils.Tuple;

public class FU_ANalUnitParser extends NalUnitParser {
	public static final String TAG= "FU_ANalUnitParser";
	private long currentTimeStamp;
	private byte naluHeader;
	private int naluSize=0;//Header
	public int seq=0;
	private PriorityQueue<Tuple<Byte[], Long, Integer>> buffer;
	
	@Override
	public Iterator<Tuple<Byte[], Long, Integer>> parse(int naluType, Tuple<Byte[], Long, Integer> naluInfo) {
		if (naluType==28) {
			int start_bit = (naluInfo._1()[1] & 0x80)>>7;
			int end_bit = (naluInfo._1()[1]  & 0x40)>>6;
			if(start_bit == 1 && end_bit == 0){
				Log.d(TAG,"v-isFirstFragment");
				naluHeader = (byte) ((naluInfo._1()[0]  & 0xE0)|(naluInfo._1()[1]  & 0x1F));
				naluSize=1;//nalu header size
				currentTimeStamp=naluInfo._2();
				initBuffer();
				int payloadSize = naluInfo._1().length-2;
				Byte[] fragment = new Byte[payloadSize];
				System.arraycopy(naluInfo._1(),2,fragment,0,payloadSize);	
				buffer.add(new Tuple<Byte[], Long, Integer>(fragment, naluInfo._2(), naluInfo._3()) );
				naluSize+=payloadSize;
				return null;
			}else if(start_bit == 0 && end_bit == 1){
				if(buffer !=null){
					Log.d(TAG,"v-isEndFragment");
					//Add fragment
					int payloadSize = naluInfo._1().length-2;
					Byte[] fragment = new Byte[payloadSize];
					System.arraycopy(naluInfo._1(),2,fragment,0,payloadSize);	
					buffer.add(new Tuple<Byte[], Long, Integer>(fragment, naluInfo._2(), naluInfo._3()) );
                    if (buffer.isEmpty()) {
                        return null;
                    }
					naluSize+=payloadSize;
					Byte[] nalu=null;
					int composiedSize=0;
					while(!buffer.isEmpty()){	
						Tuple<Byte[], Long, Integer> t =buffer.remove();
						if(seq==0){
							seq = t._3();
							nalu=new Byte[naluSize];
							nalu[0]=naluHeader;
							composiedSize=1;
						}else{
							if((seq+1)!=t._3()){
								seq=0;
								buffer = null;
								currentTimeStamp = 0;
								naluSize=0;
								return null;
							}
							seq=t._3();
						}	
						System.arraycopy(t._1(),0,nalu,composiedSize,t._1().length);
						composiedSize+=t._1().length;
					}
					ArrayList<Tuple<Byte[], Long, Integer>> naluBuffer = new ArrayList<Tuple<Byte[], Long, Integer>>();
					naluBuffer.add(new Tuple<Byte[], Long, Integer>(nalu,currentTimeStamp,seq));
					seq=0;
					return naluBuffer.iterator();
				}else{
					return null;
				}
			}else if(start_bit == 0 && end_bit == 0){
				if(buffer != null){
					Log.d(TAG,"v-isInternalFragment");
					if(naluInfo._2()==currentTimeStamp){
						int payloadSize = naluInfo._1().length-2;
						Byte[] fragment = new Byte[payloadSize];
						System.arraycopy(naluInfo._1(),2,fragment,0,payloadSize);	
						buffer.add(new Tuple<Byte[], Long, Integer>(fragment, naluInfo._2(), naluInfo._3()) );
						naluSize+=payloadSize;
					}else{
						buffer=null;
						currentTimeStamp=0;
						naluSize=0;
					}
				}
				return null;
			}else{
				//For degud
				Log.e(TAG,"NALU header is wrong");
				return null;
			}
	
		} else {
			return super.parse(naluType, naluInfo);
		}
	}
	

	
	public void initBuffer(){
		buffer = new PriorityQueue<Tuple<Byte[], Long, Integer>>();
	}
	
	public void setnaluHeader(byte header){
		naluHeader = header;
	}
	
	public void setNaluSize(int size){
		naluSize = size;
	}
	
	public int getNaluSize(){
		return naluSize;
	}
	
	public void setTimeStamp(long timeStamp){
		currentTimeStamp = timeStamp;
	}
	
	public PriorityQueue<Tuple<Byte[], Long, Integer>> getBuffer(){
		return buffer;
	}
}
