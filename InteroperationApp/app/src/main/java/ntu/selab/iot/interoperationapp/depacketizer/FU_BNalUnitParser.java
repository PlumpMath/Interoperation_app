package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

import ntu.selab.iot.interoperationapp.utils.Tuple;

public class FU_BNalUnitParser extends NalUnitParser {
	FU_ANalUnitParser fu_aNalUnitParser = new FU_ANalUnitParser();

	public static final String TAG = "FU_ANalUnitParser";
	private static int currentTimeStamp;
//	private byte naluHeader;
	int naluSize = 0;// Header
	private int don;

	@Override
	public Iterator<Tuple<Byte[], Long, Integer>> parse(int naluType,
			Tuple<Byte[], Long, Integer> naluInfo) {
		if (naluType == 29) {
			int start_bit = (naluInfo._1()[1] & 0x80) >> 7;
			int end_bit = (naluInfo._1()[1] & 0x40) >> 6;
			don = ((int) ((naluInfo._1()[2] & 0xFF) << 8 | (naluInfo._1()[3] & 0xFF)));
			if (start_bit == 1 && end_bit == 0) {
				fu_aNalUnitParser
						.setnaluHeader((byte) ((naluInfo._1()[0] & 0xE0) | (naluInfo
								._1()[1] & 0x1F)));
				fu_aNalUnitParser.setTimeStamp(naluInfo._2());
				fu_aNalUnitParser.initBuffer();
				int payloadSize = naluInfo._1().length - 4;
				Byte[] fragment = new Byte[payloadSize];
				System.arraycopy(naluInfo._1(), 4, fragment, 0, payloadSize);
				fu_aNalUnitParser.getBuffer().add(
						new Tuple<Byte[], Long, Integer>(fragment, naluInfo
								._2(), naluInfo._3()));
				fu_aNalUnitParser.setNaluSize(1 + payloadSize);// nalu header
																// size + first
																// fragment
																// payload size
			}
		} else if (naluType == 28) {
			int start_bit = (naluInfo._1()[1] & 0x80) >> 7;
			int end_bit = (naluInfo._1()[1] & 0x40) >> 6;
			if (start_bit == 1 && end_bit == 0) {
				Log.e(TAG,
						"In the interleaved mode, first fragment should be FU-B type");
			} else {
				Iterator<Tuple<Byte[], Long, Integer>> iterator = fu_aNalUnitParser
						.parse(naluType, naluInfo);
				if (iterator != null) {
					Tuple<Byte[], Long, Integer> t = null;
					while (iterator.hasNext()) {// In this case, it should be
												// just one iteration.
						t = iterator.next();
					}
					t.setZ_3(don);
					ArrayList<Tuple<Byte[], Long, Integer>> naluBuffer = new ArrayList<Tuple<Byte[], Long, Integer>>();
					naluBuffer.add(t);
					return naluBuffer.iterator();
				}
			}
		} else {
			return super.parse(naluType, naluInfo);
		}
		return null;
	}

}
