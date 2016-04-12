package ntu.selab.iot.interoperationapp.tile;

public class TileBean {

	private int[] resId;
	private String title;
	

	public TileBean(String title,int... resId){
		this.title = title;
		this.resId = resId;
	}
	
	public int[] getResId(){
		return resId;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setResId(int[] resId) {
		this.resId = resId;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
