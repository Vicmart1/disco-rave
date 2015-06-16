package TombExplorer;

import java.awt.Color;
import java.awt.Polygon;

public class Panel {
	public int size;
	public Color color = Color.white;
	public boolean exist = true;
	public boolean check = false;
	public boolean passed = false;
	public boolean disco = false;
	public boolean ending = false;
	public boolean endPoint = false;
	public boolean startPoint = false;
	public Panel(int s, boolean e){
		size = s;
		exist = e;
	}
}
