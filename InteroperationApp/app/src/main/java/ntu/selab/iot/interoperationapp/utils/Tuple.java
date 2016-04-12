package ntu.selab.iot.interoperationapp.utils;

public class Tuple <X, Y, Z> implements Comparable{
	private X x;
	private Y y;
	private Z z;
	
	public Tuple(X x, Y y, Z z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public X _1() {
		return x;
	}

	public Y _2() {
		return y;
	}

	public Z _3() {
		return z;
	}

	@Override
    public int compareTo(Object o) {
            Tuple t=(Tuple)o;
            if((Integer)this.z> (Integer)t.z){
                    return 1;
            }else if((Integer)this.z< (Integer)t.z){
                    return -1;
            }
            return 0;
    }
	
	public void setX_1(X x){
		this.x=x;
	}
	
	public void setY_1(Y y){
		this.y=y;
	}
	
	public void setZ_3(Z z){
		this.z=z;
	}

	
}
