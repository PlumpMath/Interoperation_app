package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.Iterator;

import ntu.selab.iot.interoperationapp.utils.Tuple;
import junit.framework.TestCase;

public class SingleNalUnitParserTest extends TestCase {

	private Tuple<Byte[], Long, Integer> notFrobiddenSingleNaluPacket;
	static NalUnitParser parser1 ;
	static NalUnitParser parser2 ;
	static NalUnitParser parser3 ;
	
	public SingleNalUnitParserTest(String name) {
		super(name);
	}

	protected static void setUpBeforeClass() throws Exception {
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
		notFrobiddenSingleNaluPacket = new Tuple<Byte[], Long, Integer>(new Byte[]{(byte)0x77, (byte)0xFF, (byte)0xCF}, Long.valueOf(2^32-1), Integer.valueOf(65535));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParse() {
		Iterator<Tuple<Byte[], Long, Integer>> iterator = parser1.parse(23, notFrobiddenSingleNaluPacket);
		assertNotNull(iterator);
		Tuple<Byte[], Long, Integer> t = null;
		int numOfnalu = 0;
		while(iterator.hasNext()){
			numOfnalu++;
			t= iterator.next(); 
		}
		assertNotNull(t);
		assertEquals(1,numOfnalu);
		assertEquals(3,t._1().length);
		assertEquals(Long.valueOf(2^32-1),(Long)t._2());
		assertEquals(Integer.valueOf(65535),t._3());
		assertEquals((byte)0xFF,(byte)t._1()[1]);
		assertEquals((byte)0xCF,(byte)t._1()[2]);
		
		
	}

}
