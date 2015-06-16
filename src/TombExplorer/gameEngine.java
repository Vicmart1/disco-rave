package TombExplorer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Arc2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class gameEngine extends JPanel {
	static int boxHeight = 600;
	static int boxWidth = 1000;
	static int updateRate = 60;
	static int numStars = 100;
	static Character player;
	static int view = 1000;
	static int FOV = 20;
	static double convertConstant = (view/2)/FOV;
	static Vector2[] rays = new Vector2[view];
	static boolean[] raySide = new boolean[view];
	static boolean[] keys = new boolean[150];
	//static Panel[][] level.panels = new Panel[boxWidth][boxHeight];
	//static dObject[] stars = new dObject[numStars];
	static int size = 10;
	static int speed = 4;
	static boolean map = false;
	static Image wall;
	static Level level = new Level(256, 256,size);
	
	public gameEngine() {
		Thread mainThread = new Thread() {
			@SuppressWarnings("static-access")
			public void run() {
				while (true) { 
					try{
						if(keys[38]==true){//&&level.panels[(int)((player.pos.x+speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x+speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size) + 10][(int)((player.pos.y+speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y+speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size) + 10]==0&&level.panels[(int)((player.pos.x+speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x+speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size)][(int)((player.pos.y+speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y+speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size)+10]==0&&level.panels[(int)((player.pos.x+speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x+speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size) + 10][(int)((player.pos.y+speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y+speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size) ]==0&&level.panels[(int)((player.pos.x+speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x+speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size)][(int)((player.pos.y+speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y+speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size)]==0){
							player.pos.y+=speed*rays[view/2].normalize().y;
							player.pos.x+=speed*rays[view/2].normalize().x;
						
						}
						if(keys[40]==true){//&&level.panels[(int)((player.pos.x-speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x-speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size) + 10][(int)((player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size)]==0&&level.panels[(int)((player.pos.x - speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x-speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size)][(int)((player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size)]==0&&level.panels[(int)((player.pos.x - speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x-speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size)][(int)((player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size)+10]==0){
							player.pos.y-=speed*rays[view/2].normalize().y;
							player.pos.x-=speed*rays[view/2].normalize().x;
						}/**else if(keys[40]==true&&level.panels[(int)((player.pos.x-speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x-speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size) + 10][(int)((player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size)]==0&&level.panels[(int)((player.pos.x - speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x-speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size)][(int)((player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size)]==0&&level.panels[(int)((player.pos.x - speed*Math.cos((player.angle + 90)*(Math.PI/180))) - (player.pos.x-speed*Math.cos((player.angle + 90)*(Math.PI/180)))%size)][(int)((player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180))) - (player.pos.y-speed*Math.sin((player.angle + 90)*(Math.PI/180)))%size)+10]==0){
								
						}**/
						if(keys[37]==true){
							player.angle-=32;
						}else if(keys[39]==true){
							player.angle+=32;
						}
					}catch(java.lang.NullPointerException e){}
					
					int offset = 7;
					int temp;
					Vector2 lastPanel = new Vector2(0,0);
					Vector2 currentPanel = new Vector2(0,0);
					
					for(int i=-view/2 + (int)player.angle;i<view/2 + (int)player.angle;i++){
						Vector2 A = new Vector2(0,0);
						Vector2 B = new Vector2(0,0);
						Vector2 C = new Vector2(0,0);
						Vector2 D = new Vector2(0,0);
						int Xa, Ya = 0;
						
						double distY = player.pos.y%size;
						double distX = distY/Math.tan(Math.toRadians(i/convertConstant));
						A = player.pos.add(new Vector2(distX, distY));
						
						if(((i/convertConstant)%360>180&&(i/convertConstant)%360<360)||((i/convertConstant)%360<0&&(i/convertConstant)%360>-180)){
							A.y = (int)(player.pos.y/size)*size + size;
							Ya = size;
						}else if(((i/convertConstant)%360>0&&(i/convertConstant)%360<180)||((i/convertConstant)%360<-180&&(i/convertConstant)%360>-360)){
							A.y = (int)(player.pos.y/size)*size - 1;
							Ya = -size;
						}
						
						Xa = (int)(size/Math.tan(Math.toRadians(i/convertConstant)));
						A.x = player.pos.x + (player.pos.y - A.y)/Math.tan(Math.toRadians(i/convertConstant));
						
						C.x = (int)((A.x + Xa)/size);
						C.y = (int)((A.y + Ya)/size);
						while(level.panels[(int)C.x][(int)C.y].exist==false){
							C.x = (int)((C.x + Xa)/size);
							C.y = (int)((C.y + Ya)/size);
						}
					
						if(((i/convertConstant)%360>90&&(i/convertConstant)%360<270)||((i/convertConstant)%360<-90&&(i/convertConstant)%360>-270)){
							B.x = (int)(player.pos.x/size)*size + size;
							Xa = size;
						}else if(((i/convertConstant)%360>270||(i/convertConstant)%360<90)||((i/convertConstant)%360>-90||(i/convertConstant)%360<-270)){
							B.x = (int)(player.pos.x/size)*size - 1;
							Xa = -size;
						}
						
						Ya = (int)(size/Math.tan(Math.toRadians(i/convertConstant)));
						B.y = player.pos.y + (player.pos.x - B.x)/Math.tan(Math.toRadians(i/convertConstant));
						
						D.y = (int)((B.y + Ya)/size);
						D.x = (int)((B.x + Xa)/size);
						while(D.x>-1&&D.y>-1&&level.panels[(int)D.x][(int)D.y].exist==false){
							D.x = (int)((D.x + Xa)/size);
							D.y = (int)((D.y + Ya)/size);
						}
					
						if(Math.abs(C.sub(player.pos).magnitude())<Math.abs(D.sub(player.pos).magnitude())){
							rays[i + (view/2) - (int)player.angle].x = C.sub(player.pos).x;
							rays[i + (view/2) - (int)player.angle].y = C.sub(player.pos).y;
						}else{
							rays[i + (view/2) - (int)player.angle].x = D.sub(player.pos).x;
							rays[i + (view/2) - (int)player.angle].y = D.sub(player.pos).y;
						}
							/**temp = 0;
						while(level.panels[((int)(player.pos.x+temp*Math.cos(Math.toRadians(i/convertConstant)))+offset+2)/size][((int)(player.pos.y+temp*Math.sin(Math.toRadians(i/convertConstant)))+offset)/size].exist==false){
							temp++;
						}
						lastPanel.x = ((int)(player.pos.x+(temp-1)*Math.cos(Math.toRadians(i/convertConstant)))+offset+2)/size;
						lastPanel.y = ((int)(player.pos.y+(temp-1)*Math.sin(Math.toRadians(i/convertConstant)))+offset)/size;
						currentPanel.x = ((int)(player.pos.x+temp*Math.cos(Math.toRadians(i/convertConstant)))+offset+2)/size;
						currentPanel.y = ((int)(player.pos.y+temp*Math.sin(Math.toRadians(i/convertConstant)))+offset)/size;
						if(lastPanel.x<currentPanel.x||lastPanel.x>=currentPanel.x+1)
							raySide[i + (view/2) - (int)player.angle] = true;
						else
							raySide[i + (view/2) - (int)player.angle] = false;
							
						//level.panels[(int)(((player.pos.x+temp*Math.cos(Math.toRadians(i/scaleFactor)))+offset+2) - (((player.pos.x+temp*Math.cos(Math.toRadians(i/scaleFactor)))+offset+2)%size))][(int)(((player.pos.y+temp*Math.sin(Math.toRadians(i/scaleFactor)))+offset) - (((player.pos.y+temp*Math.sin(Math.toRadians(i/scaleFactor)))+offset)%size))]=2;
						rays[i + (view/2) - (int)player.angle].x = temp*Math.cos(Math.toRadians(i/convertConstant));
						rays[i + (view/2) - (int)player.angle].y = temp*Math.sin(Math.toRadians(i/convertConstant));
						**/
					}
					repaint();
					try {
						Thread.sleep(1000/updateRate);  // milliseconds
					} catch (InterruptedException ex) { }
				}
			}
		};
		mainThread.start();  
	}

	
	
	public static void main(String[] args){
		try {
			wall = ImageIO.read(new File(new File("").getCanonicalPath() + "/Assets-TE/wallTile.png"));
		} catch (IOException e1) { }
		
		for(int i=0;i<view;i++){
			rays[i] = new Vector2(0,0);
			raySide[i] = false;
		}

		for(int i=0;i<level.size.x;i++){
			for(int j=0;j<level.size.x;j++){
				level.panels[i][j] = new Panel(size,true);
			}
		}
		
		try {
			level.filePath = new File("").getCanonicalPath() + "/Levels/";
		} catch (IOException e1) { }
		level.width = size;
		
		level.importFromFile(level.filePath + "Official/level"+1+".txt",true);
		
		player = new Character((int)level.origin.x,(int)level.origin.y,0);
		player.size = 20;

		JFrame frame = new JFrame("Tomb Explorer [Title Pending]");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(boxWidth-30,boxHeight);
		frame.setContentPane(new gameEngine());
		frame.setVisible(true);
		frame.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e) {
				keys[(int)e.getKeyCode()]=true;
				if((int)e.getKeyCode()==77){if(map==false){map=true;}else{map=false;}}
				//System.out.println((e.getKeyCode()));
			}

			public void keyReleased(KeyEvent e) {
				keys[(int)e.getKeyCode()]=false;
			}

			public void keyTyped(KeyEvent arg0) {}
		});
	}
	
	public void paintComponent(Graphics g) {
		//fps.StartCounter();
		super.paintComponent(g);  
		g.setColor(Color.black);
		g.fillRect(0, 0, boxWidth, boxHeight);
		g.setColor(new Color((float)0.05,(float)0.05,(float)0.05,(float)1.0));
		g.fillRect(0, boxHeight/2, boxWidth, boxHeight);
		
		g.setColor(Color.WHITE);
		/**for(int s=0;s<numStars;s++){
			g.fillOval((int)(stars[s].x - (player.angle*1%1980)-boxWidth*2+20), (int)stars[s].y, (int)stars[s].diameter, (int)stars[s].diameter);
			g.fillOval((int)(stars[s].x - (player.angle*1%1980)-boxWidth), (int)stars[s].y, (int)stars[s].diameter, (int)stars[s].diameter);
			g.fillOval((int)(stars[s].x - (player.angle*1%1980)), (int)stars[s].y, (int)stars[s].diameter, (int)stars[s].diameter);
			g.fillOval((int)(stars[s].x - (player.angle*1%1980)+boxWidth), (int)stars[s].y, (int)stars[s].diameter, (int)stars[s].diameter);
			g.fillOval((int)(stars[s].x - (player.angle*1%1980)+boxWidth*2-20), (int)stars[s].y, (int)stars[s].diameter, (int)stars[s].diameter);
		}**/
		
		int xSize = (int)(boxWidth*2.00);
		int ySize = 400;

		int xOval = boxWidth/2 - xSize/2;
		int yOval = boxHeight - ySize/2;		
		
		for(double i=1;i<1.15;i+=0.002){
			xSize/=i;
			ySize/=i;
			
			xOval = boxWidth/2 - xSize/2;
			yOval = boxHeight - ySize/2 - 20;
			g.setColor(new Color((float)1.0,(float)1.0,(float)1.0,(float)0.009));
			g.fillOval(xOval, yOval, xSize, ySize);
		}
		
		int xpos = 400;
		int ypos = 50;
		double xsize = 0.5;
		double ysize = 0.5;
		double dist = 0;
		boolean side = false;
		for(int i=-view/2 + (int)player.angle;i<view/2 + (int)player.angle;i++){
			int de = i+(view/2)-(int)player.angle;
			//if(de<0)
				//de=view+de;
			
			dist = rays[de].dotProduct(rays[view/2])/(rays[de].magnitude()*rays[view/2].magnitude())*rays[de].magnitude();
			float color = (float)Math.min(1,Math.max(0,((Math.abs(dist))/150)));
			Graphics2D g2d = (Graphics2D) g.create();
			
			//if(raySide[de])
	        	g2d.drawImage(wall, 
	        		de,
	        		(int)(boxHeight/2 - (10000/dist)), 
	        		de+1, 
	        		(int)(boxHeight/2 + (10000/dist)), 
	        		(int)((rays[de].y + player.pos.y))%(14), 
	        		0, 
	        		(int)((rays[de].y + player.pos.y))%(14)+1, 
	        		wall.getHeight(null), 
	        		null);
	        /**else
	        	g2d.drawImage(wall, 
		        		de,
		        		(int)(boxHeight/2 - (10000/dist)), 
		        		de+1, 
		        		(int)(boxHeight/2 + (10000/dist)), 
		        		(int)((rays[de].x + player.pos.x))%(14), 
		        		0, 
		        		(int)((rays[de].x + player.pos.x))%(14)+1, 
		        		wall.getHeight(null), 
		        		null);
		    **/
			g.setColor(new Color(0,0,0, color));
			g.fillRect(de, (int)(boxHeight/2 - (10000/dist)), 1, (int)(20300/dist));
			
			g.setColor(Color.WHITE);
			//g.fillRect(i+(view/2)-(int)player.angle, (int)(boxHeight/2 - (10000/dist)), 1, (int)(20000/(dist)));
		}
		
		
		
		if(map==true){
			for(int i=-view/2 + (int)player.angle;i<view/2 + (int)player.angle;i++){
				g.setColor(new Color((float)1.0, (float) 1.0, (float)0.2, (float)0.1));
				g.drawLine(
						(int)((player.pos.x + player.size/2)*xsize + xpos), 
						(int)((player.pos.y + player.size/2)*ysize + ypos), 
						(int)((player.pos.x + player.size/2 + rays[i+(view/2)-(int)player.angle].x) * xsize + xpos), 
						(int)((player.pos.y + player.size/2 + rays[i+(view/2)-(int)player.angle].y) * ysize + ypos)
						);
			}
			//while(level.panels[(int)(player.pos.x+temp*Math.cos(Math.toRadians(i/convertConstant)))+offset+2][(int)(player.pos.y+temp*Math.sin(Math.toRadians(i/convertConstant)))+offset]==0){
			
			g.fillOval((int)(player.pos.x*xsize) + xpos,(int)(player.pos.y*ysize) + ypos,(int)(player.size*xsize),(int)(player.size*ysize));
		
			g.setColor(new Color((float)1.0, (float) 0.0, (float)0.0, (float)0.5));
			for(int i=0;i<level.size.x;i++){
				for(int j=0;j<level.size.y;j++){
					if(level.panels[i][j].exist==true)
						g.drawRect((int)((i*xsize*size)+xpos), (int)((j*ysize*size)+ypos), (int)(size*xsize), (int)(size*ysize));
				}
			}
		}
		
		//fps.StopAndPost();
	}
	
	public static double random(double Min, double Max){
		return Min + (Math.random() * ((Max - Min) + 1));
	}
	
}
