package OneKey;

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
}
