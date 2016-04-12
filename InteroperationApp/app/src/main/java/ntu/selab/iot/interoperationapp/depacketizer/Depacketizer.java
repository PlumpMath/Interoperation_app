package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import android.util.Log;

import ntu.selab.iot.interoperationapp.protocol.rtp.RtpPacket;
import ntu.selab.iot.interoperationapp.utils.Tuple;

public class Depacketizer {
	public static final String TAG = "Depacketizer";
	private static final int bufferSize = 50;
	private NalUnitParser nalUnitParsers;
	private PriorityQueue<Tuple<Byte[], Long, Integer>> buffer;
	int i = 0;
//	private Tuple<Byte[], Long, Integer> nalu;

	public Depacketizer() {
		buffer = new PriorityQueue<Tuple<Byte[], Long, Integer>>();
		// nonInterleavedModeBuild();
	}

	public void nonInterleavedModeBuild() {
		NalUnitParserChainBuilder singleNalUnitExtractorChainBuilder = new SingleNalUnitExtractorChainBuilder();
		NalUnitParserChainBuilder fragmentationUnitCompositorChainBuilder = new FragmentationUnitCompositorChainBuilder();
		NalUnitParserChainBuilder aggregationPacketDepacketizerChainBuilder = new AggregationPacketDepacketizerChainBuilder();
		singleNalUnitExtractorChainBuilder
				.setNext(fragmentationUnitCompositorChainBuilder);
		fragmentationUnitCompositorChainBuilder
				.setNext(aggregationPacketDepacketizerChainBuilder);
		nalUnitParsers = singleNalUnitExtractorChainBuilder
				.nonInterleavedModeBuild();
	}

	public void interleavedModeBuild() {
		NalUnitParserChainBuilder fragmentationUnitCompositorChainBuilder = new FragmentationUnitCompositorChainBuilder();
		NalUnitParserChainBuilder aggregationPacketDepacketizerChainBuilder = new AggregationPacketDepacketizerChainBuilder();
		fragmentationUnitCompositorChainBuilder
				.setNext(aggregationPacketDepacketizerChainBuilder);
		nalUnitParsers = fragmentationUnitCompositorChainBuilder
				.interleavedModeBuild();
	}

	public void depacketize(RtpPacket pck) {
		if (isForbidden(pck)) {
			Log.e(TAG, "isForbidden");
			return;
		}
//		if(!isImportant(pck)){
//			Log.e(TAG, "isNotImportant");
//			return;
//		}
		Tuple<Byte[], Long, Integer> naluInfo = new Tuple<Byte[], Long, Integer>(
				pck.getData(), pck.timestamp, pck.seqnum);
		Iterator<Tuple<Byte[], Long, Integer>> iterator = nalUnitParsers.parse(
				recognize(pck), naluInfo);
		i++;
		if (iterator != null) {
			while (iterator.hasNext()) {
                Tuple<Byte[],Long,Integer> t = iterator.next();
                if(t._1()!=null){
				    buffer.add(t);
                }else{
                    Log.e(TAG, "nalu is null");
                }
//				nalu=iterator.next();
			}
		}
	}

	public int recognize(RtpPacket pck) {
		byte[] data = (byte[]) pck.data;
		int nalUnitType = data[0] & 0x1F;
		Log.d(TAG, "f-nalUnitType: " + nalUnitType);
		return nalUnitType;
	}

	public Queue<Tuple<Byte[], Long, Integer>> getBuffer() {
		return buffer;
	}

	private boolean isForbidden(RtpPacket pck) {
		int forbidden_zero_bit = (pck.data[0] & 0x80) >> 7;
		if (forbidden_zero_bit == 1) {
			return true;
		} else {
			return false;
		}
	}
	
//	private boolean isImportant(RtpPacket pck){
//		int nri = (pck.data[0] & 0x60)>>5;
//		if(nri == 3){
//			return true;
//		}else{
//			return false;
//		}
//	}

	public boolean bufferIsEnough() {
		if (buffer.size() > bufferSize) {
			return true;
		}
		return false;
	}

	public Tuple<Byte[], Long, Integer> getNalu() {
		return buffer.remove();
	}

    public void flush(){
        buffer.clear();
    }
}
