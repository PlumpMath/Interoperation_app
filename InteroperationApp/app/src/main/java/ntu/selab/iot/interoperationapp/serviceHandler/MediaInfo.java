package ntu.selab.iot.interoperationapp.serviceHandler;

public class MediaInfo {
	
	private byte[] sps;
	private byte[] pps;
	private String mediaType;
	private String control;
	
	public void setPPS(byte[] pps){
		this.pps=pps;
	}
	
	public byte[] getPPS(){
		return pps;
	}
	
	public void setSPS(byte[] sps){
		this.sps=sps;
	}
	
	public byte[] getSPS(){
		return sps;
	}
	
	public void setMediaType(String mediaType){
		this.mediaType=mediaType;
	}
	
	public String getMediaType(){
		return mediaType;
	}
	
	public void setMediaControl(String mediaControl){
		this.control=mediaControl;
	}
	
	public String getMediaControl(){
		return control;
	}
}
