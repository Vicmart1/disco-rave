package OneKey;

public class Vector2 {
	public double x;
	public double y;
	public Vector2(double a, double b){
		x = a;
		y = b;
	}
	public void reset(){
		x = 0;
		y = 0;
	}
	public void reset(double a, double b){
		x = a;
		y = b;
	}
	public Vector2 perpendicular(){
		return new Vector2(y,x);
	}
	public Vector2 normalize(){
		double mag = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
		Vector2 resultant = new Vector2(x/mag, y/mag);
		return resultant;
	}
	public double dotProduct(Vector2 vec){
		return (x*vec.x + y*vec.y);
	}
	public double magnitude(){
		return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
	}
	public double comp(int type){
		switch(type){
			case 0:
				return x;
			case 1:
				return y;
			default:
				return 1;
		}
	}
	public Vector2 x(double a){
		Vector2 resultant = new Vector2(x*a,y*a);
		return resultant;
	}
	public Vector2 div(double a){
		Vector2 resultant = new Vector2(x/a,y/a);
		return resultant;
	}
	public Vector2 add(Vector2 vec){
		Vector2 resultant = new Vector2(x+vec.x,y+vec.y);
		return resultant;
	}
	public Vector2 sub(Vector2 vec){
		Vector2 resultant = new Vector2(x-vec.x,y-vec.y);
		return resultant;
	}
	public Vector2 neg(){
		return new Vector2(-x,-y);
	}
	public Vector2 rot(double thetaDeg, Vector2 cent){
		double theta = Math.toRadians(thetaDeg);
		Vector2 res = new Vector2(0,0);
		res.x = (x-cent.x) * Math.cos(theta) - (y-cent.y) * Math.sin(theta);
		res.y = (x-cent.x) * Math.sin(theta) + (y-cent.y) * Math.cos(theta);
		return res.add(cent);
	}
	public void print(){
		System.out.println("< " + x + "," + y + " >");
	}
	public boolean equals(Vector2 vec){
		if(x==vec.x&&y==vec.y)
			return true;
		return false;
	}
	public double difference(Vector2 vec){
		return new Vector2(x-vec.x,y-vec.y).magnitude();
	}
	public Vector2 roundDown(){
		return new Vector2((int)x, (int)y);
	}
}
