package DiscoRave;

public class Notification {
	public int side = 1;
	public String text = "";
	public Vector2 size = new Vector2(0,0);
	public Vector2 pos = new Vector2(0,0);
	public int dir = 0;
	public boolean active = false;
	public Notification(int s, String ss, int bw, int bh){
		side = s;
		text = ss;
		size.x = ss.length()*13 + 200;
		size.y = 50;
		pos.x = bw;
		pos.y = bh - 200;
	}
	public void appear(int bw){
		if(Math.abs((bw - size.x) - pos.x)<0.01)
			dir = 1;
		else
			pos.x += ((bw - size.x) - pos.x)/16;	
	}
	public void disappear(int bw){
		if(bw - pos.x<-size.x)
			dir = 2;
		else
			pos.x -= ((bw - size.x) - pos.x - 1)/16;
	}
	public void reset(int bw){
		pos.x = bw;
		active = false;
		dir = 0;
	}
}
