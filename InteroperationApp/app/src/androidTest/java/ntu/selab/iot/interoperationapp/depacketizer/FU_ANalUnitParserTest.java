package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.Iterator;

import ntu.selab.iot.interoperationapp.utils.Tuple;
import junit.framework.TestCase;

public class FU_ANalUnitParserTest extends TestCase {

	private Tuple<Byte[], Long, Integer> notFrobiddenSingleNaluPacketStart;
	private Tuple<Byte[], Long, Integer> notFrobiddenSingleNaluPacketInterval;
	private Tuple<Byte[], Long, Integer> notFrobiddenSingleNaluPacketEnd;
	static NalUnitParser parser1 ;
	static FU_ANalUnitParser parser2 ;
	static NalUnitParser parser3 ;
	
	public FU_ANalUnitParserTest(String name) {
		super(name);
	}

	protected static void setUpBeforeClass() throws Exception {
//		parser1 = new SingleNalUnitParser();
//		parser2 = new FU_ANalUnitParser();
//		parser3 = new STAP_ANalUnitParser();
//		parser1.setNext(parser2);
//		parser2.setNext(parser3);
	}

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
		parser1 = new SingleNalUnitParser();
		parser2 = new FU_ANalUnitParser();
		parser3 = new STAP_ANalUnitParser();
		parser1.setNext(parser2);
		parser2.setNext(parser3);
		notFrobiddenSingleNaluPacketStart = new Tuple<Byte[], Long, Integer>(new Byte[]{(byte)0x7C, (byte)0x9F, (byte)0x1F}, Long.valueOf(2^32-1), Integer.valueOf(65533));
		notFrobiddenSingleNaluPacketInterval = new Tuple<Byte[], Long, Integer>(new Byte[]{(byte)0x7C, (byte)0x1F, (byte)0x2F,(byte)0x3F}, Long.valueOf(2^32-1), Integer.valueOf(65534));
		notFrobiddenSingleNaluPacketEnd = new Tuple<Byte[], Long, Integer>(new Byte[]{(byte)0x7C, (byte)0x5F,(byte)0x4F,(byte)0x5F, (byte)0x6F}, Long.valueOf(2^32-1), Integer.valueOf(65535));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParse() {
		Iterator<Tuple<Byte[], Long, Integer>> iterator = parser1.parse(28, notFrobiddenSingleNaluPacketStart);
		assertNull(iterator);
		assertEquals(2,parser2.getNaluSize());
		iterator = parser1.parse(28, notFrobiddenSingleNaluPacketInterval);
		assertNull(iterator);
		assertEquals(4,parser2.getNaluSize());
		iterator = parser1.parse(28, notFrobiddenSingleNaluPacketEnd);
		assertEquals(0,parser2.seq);
		assertEquals(7,parser2.getNaluSize());
		assertNotNull(iterator);
		Tuple<Byte[], Long, Integer> t = null;
		int numOfnalu = 0;
		while(iterator.hasNext()){
			numOfnalu++;
			t= iterator.next(); 
		}
		assertNotNull(t);
		assertEquals(1,numOfnalu);
		assertEquals(7,t._1().length);
		assertEquals(Long.valueOf(2^32-1),(Long)t._2());
		assertEquals(Integer.valueOf(65535),t._3());
		assertEquals((byte)0x7F,(byte)t._1()[0]);
		assertEquals((byte)0x1F,(byte)t._1()[1]);
		assertEquals((byte)0x2F,(byte)t._1()[2]);
		assertEquals((byte)0x3F,(byte)t._1()[3]);
		assertEquals((byte)0x4F,(byte)t._1()[4]);
		assertEquals((byte)0x5F,(byte)t._1()[5]);
		assertEquals((byte)0x6F,(byte)t._1()[6]);
		
	}

}
