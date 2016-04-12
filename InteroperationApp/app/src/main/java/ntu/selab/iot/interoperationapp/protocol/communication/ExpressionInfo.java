package ntu.selab.iot.interoperationapp.protocol.communication;

public class ExpressionInfo {
	private String unit = null;
	private Object value = null;
	private String className = null;
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public void setValue(Object value){
		this.value = value;
	}
	public Object getValue(){
		return value;
	}
	public String getClassName(){
		return className;
	}
	public void setClassName(String className){
		this.className = className;
	}
}
