package ntu.selab.iot.interoperationapp.tile;

public class FocusTile {
	private TileView focus;
	private int[] focusResId;
	private String focusTitle;
	private int[] maskResId;
	private String maskTitle;
	
	public FocusTile() {
	}
	public void setFocus(TileView tileView) {
		this.focus = tileView;
	}
	public TileView getFocus() {
		return this.focus;
	}
	public void setFocusResId(int[] resId) {
		this.focusResId = resId;
	}
	public int[] getFocusResId() {
		return this.focusResId;
	}
	public void setFocusTitle(String title) {
		this.focusTitle = title;
	}
	public String getFocusTitle() {
		return this.focusTitle;
	}
	public void setMaskResId(int...resId) {
		this.maskResId = resId;
	}
	public int[] getMaskResId() {
		return this.maskResId;
	}
	public void setMaskTitle(String title) {
		this.maskTitle = title;
	}
	public String getMaskTitle() {
		return this.maskTitle;
	}
}