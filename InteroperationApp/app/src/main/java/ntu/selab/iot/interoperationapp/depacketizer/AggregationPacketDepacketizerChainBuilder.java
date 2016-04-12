package ntu.selab.iot.interoperationapp.depacketizer;

public class AggregationPacketDepacketizerChainBuilder extends
		NalUnitParserChainBuilder {

	@Override
	public NalUnitParser nonInterleavedModeBuild() {
		NalUnitParser parser1 = new STAP_ANalUnitParser();
//		NalUnitParser parser2 = new STAP_BNalUnitParser();
//		NalUnitParser parser3 = new MTAP16NalUnitParser();
//		NalUnitParser parser4 = new MTAP24NalUnitParser();
		parser1.setNext(super.nonInterleavedModeBuild());
		
		return parser1;
	}
	
	@Override
	public NalUnitParser interleavedModeBuild(){
		NalUnitParser parser1 = new STAP_BNalUnitParser();
		NalUnitParser parser2 = new MTAP16NalUnitParser();
		NalUnitParser parser3 = new MTAP24NalUnitParser();
		parser3.setNext(super.interleavedModeBuild());
		return parser1;
	}

}
