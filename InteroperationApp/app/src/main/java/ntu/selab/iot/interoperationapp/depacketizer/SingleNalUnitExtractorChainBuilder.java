package ntu.selab.iot.interoperationapp.depacketizer;

public class SingleNalUnitExtractorChainBuilder extends
NalUnitParserChainBuilder {
	
	@Override
	public NalUnitParser nonInterleavedModeBuild() {
		NalUnitParser parser1 = new SingleNalUnitParser();
		parser1.setNext(super.nonInterleavedModeBuild());
		
		return parser1;
	}
}
