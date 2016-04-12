package ntu.selab.iot.interoperationapp.depacketizer;

public class NalUnitParserChainBuilder {
	private NalUnitParserChainBuilder builder;
	
	// FIXME This is a little weird.
	public NalUnitParser nonInterleavedModeBuild() {
		if (builder != null) {
			return builder.nonInterleavedModeBuild();
		} else {
			return null;
		}
	}
	
	public NalUnitParser interleavedModeBuild(){
		if (builder != null) {
			return builder.interleavedModeBuild();
		} else {
			return null;
		}
	}
	
	public void setNext(NalUnitParserChainBuilder builder) {
		this.builder = builder;
	}
}
