package ntu.selab.iot.interoperationapp.depacketizer;

import java.util.LinkedList;
import java.util.Queue;

import ntu.selab.iot.interoperationapp.utils.Tuple;

public class Rearrangement {

	public static Queue rearrange(Queue q){
		LinkedList<Tuple<Byte[], Integer, Short>> buffer = new LinkedList<Tuple<Byte[], Integer, Short>>();
		while(!q.isEmpty()){
			buffer.add((Tuple<Byte[], Integer, Short>) q.remove());
		}
		return buffer;
	}
}
