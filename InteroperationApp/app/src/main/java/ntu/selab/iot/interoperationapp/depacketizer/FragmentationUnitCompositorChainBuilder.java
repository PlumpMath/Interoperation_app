package ntu.selab.iot.interoperationapp.depacketizer;

public class FragmentationUnitCompositorChainBuilder extends
NalUnitParserChainBuilder {
	@Override
	public NalUnitParser nonInterleavedModeBuild() {
		NalUnitParser parser1 = new FU_ANalUnitParser();
		parser1.setNext(super.nonInterleavedModeBuild());
		return parser1;
	}
	
	@Override
	public NalUnitParser interleavedModeBuild(){
		NalUnitParser parser1 = new FU_BNalUnitParser();
		parser1.setNext(super.interleavedModeBuild());
		return parser1;
	}
}
