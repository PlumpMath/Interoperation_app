package ntu.selab.iot.interoperationapp.depacketizer;

import ntu.selab.iot.interoperationapp.protocol.rtp.RtpPacket;
import ntu.selab.iot.interoperationapp.utils.Tuple;
import junit.framework.TestCase;

public class DepacketizerTest extends TestCase {
	
	private RtpPacket singlePacket1 = new RtpPacket();
	private RtpPacket singlePacket2 = new RtpPacket();
	private RtpPacket firstFragmentPacket = new RtpPacket();
	private RtpPacket internalFragmentPacket = new RtpPacket();
	private RtpPacket internal2FragmentPacket = new RtpPacket();
	private RtpPacket endFragmentPacket = new RtpPacket();
//	private RtpPacket notImportantPak = new RtpPacket();
	private Depacketizer depacketizer;

	public DepacketizerTest(String name) {
		super(name);
	}

	protected static void setUpBeforeClass() throws Exception {
	}

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
		singlePacket1.data=new byte[]{(byte)0x77, (byte)0xFF};
		singlePacket1.seqnum=65530;
		singlePacket1.timestamp=(long)(2^32-3);
		
		singlePacket2.data=new byte[]{(byte)0x77, (byte)0xFF};
		singlePacket2.seqnum=65531;
		singlePacket2.timestamp=(long)(2^32-2);
		
		firstFragmentPacket.data=new byte[]{(byte)0x7C, (byte)0x9E, (byte)0x1F, (byte)0x2F};
		firstFragmentPacket.seqnum=65532;
		firstFragmentPacket.timestamp=(long)(2^32-1);
		
		internalFragmentPacket.data=new byte[]{(byte)0x7C, (byte)0x1E, (byte)0x3F,};
		internalFragmentPacket.seqnum=65533;
		internalFragmentPacket.timestamp=(long)(2^32-1);
		
		internal2FragmentPacket.data=new byte[]{(byte)0x7C, (byte)0x1E, (byte)0x4F,};
		internal2FragmentPacket.seqnum=65534;
		internal2FragmentPacket.timestamp=(long)(2^32-1);
		
		endFragmentPacket.data=new byte[]{(byte)0x7C, (byte)0x5E,(byte)0x5F,(byte)0x6F,(byte)0x7F};
		endFragmentPacket.seqnum=65535;
		endFragmentPacket.timestamp=(long)(2^32-1);
		
//		notImportantPak .data=new byte[]{(byte)0x17, (byte)0xFF};
//		notImportantPak.seqnum=65522;
//		notImportantPak.timestamp=(long)(2^32-5);
		
		depacketizer = new Depacketizer();
		depacketizer.nonInterleavedModeBuild();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

//	public void testNonInterleavedModeBuild() {
//		fail("Not yet implemented");
//	}

//	public void testInterleavedModeBuild() {
//		fail("Not yet implemented");
//	}

	public void testDepacketize() {
		depacketizer.depacketize(singlePacket1);
		depacketizer.depacketize(firstFragmentPacket);
		depacketizer.depacketize(internal2FragmentPacket);
		depacketizer.depacketize(internalFragmentPacket);
		depacketizer.depacketize(singlePacket2);
		depacketizer.depacketize(endFragmentPacket);
//		depacketizer.depacketize(notImportantPak);
		assertEquals(3,depacketizer.getBuffer().size());
		Tuple<Byte[],Long,Integer> t =depacketizer.getBuffer().remove();
		assertEquals(65530,t._3().intValue());
		t =depacketizer.getBuffer().remove();
		assertEquals(65531,t._3().intValue());
		t =depacketizer.getBuffer().remove();
		assertEquals(65535,t._3().intValue());
		assertEquals(8,t._1().length);
		assertEquals((byte)0x7E,(byte)t._1()[0]);
		assertEquals((byte)0x1F,(byte)t._1()[1]);
		assertEquals((byte)0x2F,(byte)t._1()[2]);
		assertEquals((byte)0x3F,(byte)t._1()[3]);
		assertEquals((byte)0x4F,(byte)t._1()[4]);
		assertEquals((byte)0x5F,(byte)t._1()[5]);
		assertEquals((byte)0x6F,(byte)t._1()[6]);
		assertEquals((byte)0x7F,(byte)t._1()[7]);
	}
	

//	public void testRecognize() {
//		
//		
//	}

}
