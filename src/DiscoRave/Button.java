package DiscoRave;

import java.awt.Color;

public class Button {
	public Vector2 pos;
	public Vector2 size;
	public String name;
	public boolean value = false;
	public boolean hover = false;
	public boolean showOption = true;
	public boolean active = true;
	public Button(int x, int y, int sx, int sy, String name){
		pos = new Vector2(x,y);
		size = new Vector2(sx,sy);
		this.name = name;
	}
	public static Color highlight(int r){
		switch(r){
		case 0:
			return (Color.RED);
		case 1:
			return(Color.ORANGE);
		case 2:
			return(Color.YELLOW);
		case 3:
			return(Color.GREEN);
		case 4:
			return(Color.CYAN);
		case 5:
			return(Color.BLUE);
		case 6:
			return(Color.magenta);
		}
		return null;
	}
}
