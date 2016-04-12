package ntu.selab.iot.interoperationapp.protocol.communication;

import java.util.ArrayList;

public class DataInfo {
	private String type = null;
	private ArrayList<ExpressionInfo> expressions = null;
//    private String command;
	
	public DataInfo(){
		expressions = new ArrayList<ExpressionInfo>(0);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void addExpression(ExpressionInfo expressionInfo){
		expressions.add(expressionInfo);
	}
	public ExpressionInfo[] getExpressions(){
		return expressions.toArray(new ExpressionInfo[0]);
	}
	public void cleanExpressions(){
		expressions = new ArrayList<ExpressionInfo>(0);
	}

//    public String getCommand() {
//        return command;
//    }
//
//    public void setCommand(String command) {
//        this.command = command;
//    }
}
