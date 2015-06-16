package DiscoRave;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.Robot;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel; 
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import jaco.mp3.player.MP3Player;
import jaco.mp3.player.c;


@SuppressWarnings("serial")
public class bumperCars extends JPanel {
	static int boxWidth = 1280;
	static int boxHeight = 720;
	static int updateRate = 60;
	static boolean[] keys = new boolean[200];
	
	static int size = 35;
	static int enemyNum = 733;
	static Level level = new Level(256, 256,size, enemyNum);
	static Camera cam = new Camera(0,0);
	static Button[] buttons = new Button[5];
	static boolean isLoaded = false;
	
	static double timer = 0.75;
	static boolean switchColors = false;
	static BGMusic BG;
	static double friction = 1.001;
	
	static byte[]imageByte=new byte[0];  
	static Image cursorImage=Toolkit.getDefaultToolkit().createImage(imageByte);  
	static Cursor myCursor=Toolkit.getDefaultToolkit().createCustomCursor(cursorImage,new Point(0,0),"cursor");
	
	static JFrame frame = new JFrame("Disco Cars [Alpha Build] by Vicmart Studios");
	
	public bumperCars() {
		Thread playerThread = new Thread() {
			public void run() {
				while (true) {
				while(level.pause==false){
				try{
					calculateCollision(level.player);
					
					if(level.player.tailDist%20==0){
						for(int t=level.player.tailMax-1;t>0;t--){
							level.player.tail[t] = level.player.tail[t-1];
							level.player.tailVel[t] = level.player.tailVel[t-1];
						}
						level.player.tail[0] = level.player.pos;
						level.player.tailVel[0] = level.player.vel;
						level.player.tailDist = 0;
					}
					level.player.tailDist++;		
					

					for(int x=-1;x<2;x++)
						for(int y=-1;y<2;y++)
							if(level.panels[((int)level.player.pos.x)/size + x][((int)level.player.pos.y)/size + y].check==true){
								level.player.timeLeft = 1;
								level.panels[((int)level.player.pos.x)/size + x][((int)level.player.pos.y)/size + y].passed=true;
								level.lastCheckpoint.reset((((int)level.player.pos.x)/size + x)*size,(((int)level.player.pos.y)/size + y)*size);
							}
					
					level.player.tailCurrent = (int)(level.player.timeLeft*20);
					
					for(int e=0;e<enemyNum;e++){
						calculateCollision(level.enemy[e]);
					}
					
					try {
						Thread.sleep(1);  // milliseconds
					} catch (InterruptedException ex) { }
				}catch(java.lang.ArrayIndexOutOfBoundsException ee){}
				}
				try {
					Thread.sleep(1);  // milliseconds
				} catch (InterruptedException ex) { }
				}
			}
		};
		playerThread.start();  
		
		Thread mainThread = new Thread() {
			public void run() {
				while (true) { 
					while(level.pause==false){
						cam.pos = cam.pos.add(level.player.pos.sub(cam.pos.add(new Vector2(boxWidth/2,boxHeight/2))).div(8));
						timer-=((double)BG.currentBPM/7200);
						
						if(timer<0.5){
							timer+=0.5;
							switchColors = true;
							for(int e=0;e<enemyNum;e++){
								level.enemy[e].doAction(0, e, level, cam, boxWidth, boxHeight);
							}
						}
						repaint();
						try {
							Thread.sleep(1000/updateRate);  // milliseconds
						} catch (InterruptedException ex) { }
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
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException{
		isLoaded = true;
		
		try{
			level.index = Integer.parseInt(args[0]);
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			String num = JOptionPane.showInputDialog("Created by Vicmart Coding Studios \nCopyright 2014, All Rights Reserved\n \nEnter level number:","");
			level.index = Integer.parseInt(num);
		}catch(java.lang.NumberFormatException e){
			level.index = -1;
			level.name = args[0];
			//level.filePath = new File("").getCanonicalPath() + "/Levels/level" + args[0] + ".txt"; 
		}
	
		for(int lx=0;lx<level.size.x;lx++)
			for(int ly=0;ly<level.size.y;ly++)
				level.panels[lx][ly] = new Panel(size,true);
		
		level.filePath = new File("").getCanonicalPath() + "/Levels/";
		level.width = size;
		
		if(level.index!=-1)
			level.importFromFile(level.filePath + "Official/level"+level.index+".txt",true);
		else
			level.importFromFile(level.filePath + "level"+level.name+".txt",true);
		
		level.player = new Character((int)level.origin.x,(int)level.origin.y,(int)(59 * ((double)size/20)), new File("").getCanonicalPath() + "/Assets/BumperCars/");
		level.player.player = true;
		level.player.marker.x = boxWidth/2;
		level.player.marker.y = boxHeight/2;
		level.player.vel = new Vector2(0,0.01);
		level.player.ropeMax = -1;
		//level.player.mass = 1;
		//level.player.size = 150;
		
		for(int e=0;e<enemyNum;e++){
			level.enemy[e] = new Character((int)(level.origin.x + ((e/12 + 1) * (70 * ((double)size/20)))*Math.cos((e%12)*((Math.PI*2)/enemyNum))),(int)(level.origin.y + ((e/12 + 1) * (70 * ((double)size/20)))*Math.sin((e%12)*((Math.PI*2)/enemyNum))),(int)(59 * ((double)size/20)), new File("").getCanonicalPath() + "/Assets/BumperCars/");
			level.enemy[e].player = true;
			level.enemy[e].marker.x = boxWidth/2;
			level.enemy[e].marker.y = boxHeight/2;
			level.enemy[e].vel = new Vector2(Math.cos(e*((Math.PI*2)/enemyNum))/32,Math.sin(e*((Math.PI*2)/enemyNum))/32);
			level.enemy[e].ropeMax = -1;
			level.enemy[e].changeColor(new Color[]
					{
						new Color((float)Math.random(),(float)Math.random(),(float)Math.random(),(float)1),
						new Color((float)Math.random(),(float)Math.random(),(float)Math.random(),(float)1),
						new Color((float)Math.random(),(float)Math.random(),(float)Math.random(),(float)1)
					});
		}
		
		for(int b=0;b<buttons.length;b++){
			String btnName = "";
			buttons[b] = new Button((boxWidth/2)-300,50 + 125*b,600,100,btnName);
			switch(b){
				case 0:
					btnName = "Turn off music";
					break;
				case 1:
					btnName = "Next Song";
					buttons[b].showOption = false;
					break;
				case 2:
					btnName = "Edit Map";
					buttons[b].showOption = false;
					break;
				case 3:
					btnName = "Help";
					buttons[b].showOption = false;
					break;
				case 4:
					btnName = "Return to Menu";
					buttons[b].showOption = false;
					break;
			}
			buttons[b].name = btnName;
		}

		BG = new BGMusic(new File("").getCanonicalPath() + "/Assets/");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setCursor(myCursor);
		frame.setSize(boxWidth,boxHeight);
		frame.setContentPane(new bumperCars());
		frame.setVisible(true);
		frame.setResizable(false);
		frame.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==27)
					if(level.pause==false)
					{
						level.pause = true;
						frame.setCursor(Cursor.DEFAULT_CURSOR);
					}else{
						level.pause = false;
						frame.setCursor(myCursor);
					}
			}

			public void keyReleased(KeyEvent e) {
				keys[(int)e.getKeyCode()]=false;
			}

			public void keyTyped(KeyEvent arg0) {}
		});
		frame.addMouseListener(new MouseListener()
		{	
			public void mouseClicked(MouseEvent arg0) {	}
			
			public void mouseEntered(MouseEvent arg0) { }
			
			public void mouseExited(MouseEvent e) { }
			
			public void mousePressed(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1&&level.pause==false){
					if(level.player.tension==false&&level.player.ropeLength!=-1){
						level.player.tension = true;
					}else{
						level.player.release();
					}
				}else if(level.pause==true){
					for(int b=0;b<buttons.length;b++)
						if(buttons[b].hover==true){
							if(buttons[b].value==false){
								if(b==0){
									BG.player.pause();
								}
								buttons[b].value=true;
							}else if(buttons[b].value==true){
								if(b==0){
									BG.player.play();
								}
								buttons[b].value=false;
							}

							if(b==1&&buttons[0].value==false){
								BG.nextSong();
								//String n = JOptionPane.showInputDialog("Enter new BPM:");
								//BG.currentBPM = Integer.parseInt(n);
							}else if(b==2){
								String[] arg = new String[1];
								if(level.index!=-1)
									arg[0] = level.index + "";
								else
									arg[0] = level.name + "";
									
								if(levelEditor.isLoaded==false){
									levelEditor.isLoaded = true;
									try {
										levelEditor.main(arg);
										//frame.dispose();
									} catch (IOException e1) { }
								}else{
									levelEditor.level.index = level.index;
									levelEditor.level.name = level.name;
									levelEditor.level.resetLevel(false);
									levelEditor.frame.setCursor(levelEditor.myCursor);
									levelEditor.frame.show();
								}
								BG.player.stop();
								frame.hide();
							}else if(b==4){
								String[] args = null;
								BG.player.stop();
								if(menuScreen.isLoaded==false){
									menuScreen.isLoaded = true;
									try {
										menuScreen.main(args); 
									} catch (IOException e1) { }
								}else{
									menuScreen.menuFrame = 2;
									menuScreen.frame.show();
									menuScreen.BG.player.play();
								}
								frame.hide();
							}
						}
				}
			}

			public void mouseReleased(MouseEvent arg0) { 
				if(level.pause==false){
					level.player.release();
				}
			}
		});
		frame.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseMoved(MouseEvent e) {
				if(level.pause==false){
					if(level.player.marker.x + ((double)(e.getXOnScreen()) - boxWidth/2)>0&&level.player.marker.x + ((double)(e.getXOnScreen()) - boxWidth/2)<boxWidth - 10)
						level.player.marker.x += ((double)(e.getXOnScreen()) - boxWidth/2);
					if(level.player.marker.y + ((double)(e.getYOnScreen()) - boxHeight/2)>0&&level.player.marker.y + ((double)(e.getYOnScreen()) - boxHeight/2)<boxHeight - 30)
						level.player.marker.y += ((double)(e.getYOnScreen()) - boxHeight/2);
					try {
						Robot robot = new Robot();
						robot.mouseMove(boxWidth/2, boxHeight/2);
					} catch (AWTException ex) { }
				}else{
					for(int b=0;b<buttons.length;b++){
						buttons[b].hover = false;
						if(e.getX()>buttons[b].pos.x&&e.getX()<buttons[b].pos.x+buttons[b].size.x&&e.getY()>buttons[b].pos.y&&e.getY()<buttons[b].pos.y+buttons[b].size.y+20)
							buttons[b].hover = true;
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if(level.pause==false){
					if(level.player.marker.x + ((double)(e.getXOnScreen()) - boxWidth/2)>0&&level.player.marker.x + ((double)(e.getXOnScreen()) - boxWidth/2)<boxWidth - 10)
						level.player.marker.x += ((double)(e.getXOnScreen()) - boxWidth/2);
					if(level.player.marker.y + ((double)(e.getYOnScreen()) - boxHeight/2)>0&&level.player.marker.y + ((double)(e.getYOnScreen()) - boxHeight/2)<boxHeight - 30)
						level.player.marker.y += ((double)(e.getYOnScreen()) - boxHeight/2);
					try {
						Robot robot = new Robot();
						robot.mouseMove(boxWidth/2, boxHeight/2);
					} catch (AWTException ex) { }
				}
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);  
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if(level.switching==false){			
			
			
			
			Level lvl = level;
			for(int x=(int)cam.pos.x/size - 2;x<(int)(cam.pos.x+getWidth()+50)/size;x++){
				for(int y=(int)cam.pos.y/size - 2;y<(int)(cam.pos.y+getHeight() + 35)/size;y++){
					double dist = Math.max(0.5,Math.min(1/(level.player.pos.sub(new Vector2(x*size,y*size)).magnitude()/100)*level.player.timeLeft,1.0));
					if(x>=0&&y>=0&&x<lvl.size.x-1&&y<lvl.size.y-1){
						if(lvl.panels[x][y].exist==true){	
							if(lvl.panels[x][y].disco==false)	
								g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
							else if(lvl.panels[x][y].disco==true){
								if(switchColors==true)
									lvl.panels[x][y].color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
								g.setColor(new Color((float)(timer*dist*lvl.panels[x][y].color.getRed()/255),(float)(timer*dist*lvl.panels[x][y].color.getGreen()/255),(float)(timer*dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
							}
							if(lvl.panels[x][y].disco==false){
								if(x>0&&x<level.size.x-1&&y<level.size.y-1&&lvl.panels[x+1][y].exist==false&&lvl.panels[x-1][y].exist==false&&lvl.panels[x][y+1].exist==false){
									Color tempColor = new Color(51,51,51);
									g.setColor(new Color((float)(dist*tempColor.getRed()/255),(float)(dist*tempColor.getGreen()/255),(float)(dist*tempColor.getBlue()/255),(float)1.0));
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
									g.fillOval((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), size, size/2);
								}else if(x<level.size.x-1&&y>0&&y<level.size.y-1&&lvl.panels[x+1][y].exist==false&&lvl.panels[x][y-1].exist==false&&lvl.panels[x][y+1].exist==false){
									Color tempColor = new Color(51,51,51);
									g.setColor(new Color((float)(dist*tempColor.getRed()/255),(float)(dist*tempColor.getGreen()/255),(float)(dist*tempColor.getBlue()/255),(float)1.0));
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
									g.fillOval((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), size/2, size);
								}else if(x>0&&x<level.size.x-1&&y>0&&lvl.panels[x+1][y].exist==false&&lvl.panels[x][y-1].exist==false&&lvl.panels[x-1][y].exist==false){
									Color tempColor = new Color(51,51,51);
									g.setColor(new Color((float)(dist*tempColor.getRed()/255),(float)(dist*tempColor.getGreen()/255),(float)(dist*tempColor.getBlue()/255),(float)1.0));
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
									g.fillOval((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y + (double)size/2), size,size/2 + 1);
								}else if(x>0&&y<level.size.y-1&&y>0&&lvl.panels[x][y+1].exist==false&&lvl.panels[x][y-1].exist==false&&lvl.panels[x-1][y].exist==false){
									Color tempColor = new Color(51,51,51);
									g.setColor(new Color((float)(dist*tempColor.getRed()/255),(float)(dist*tempColor.getGreen()/255),(float)(dist*tempColor.getBlue()/255),(float)1.0));
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
									g.fillOval((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.fillRect((int)(size*x-cam.pos.x + (double)size/2),(int)(size*y-cam.pos.y), size/2 + 1,size);
								}else if(x<level.size.x-1&&y<level.size.y-1&&lvl.panels[x+1][y].exist==false&&lvl.panels[x][y+1].exist==false){
									Color tempColor = new Color(51,51,51);
									g.setColor(new Color((float)(dist*tempColor.getRed()/255),(float)(dist*tempColor.getGreen()/255),(float)(dist*tempColor.getBlue()/255),(float)1.0));
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
									g.fillArc((int)(size*x-cam.pos.x-size),(int)(size*y-cam.pos.y-size), lvl.panels[x][y].size*2, lvl.panels[x][y].size*2, 270,90);
								}else if(x>0&&y<level.size.y-1&&lvl.panels[x-1][y].exist==false&&lvl.panels[x][y+1].exist==false){
									Color tempColor = new Color(51,51,51);
									g.setColor(new Color((float)(dist*tempColor.getRed()/255),(float)(dist*tempColor.getGreen()/255),(float)(dist*tempColor.getBlue()/255),(float)1.0));
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
									g.fillArc((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y-size), lvl.panels[x][y].size*2, lvl.panels[x][y].size*2, 180,90);
								}else if(x<level.size.x-1&&y>0&&lvl.panels[x+1][y].exist==false&&lvl.panels[x][y-1].exist==false){
									Color tempColor = new Color(51,51,51);
									g.setColor(new Color((float)(dist*tempColor.getRed()/255),(float)(dist*tempColor.getGreen()/255),(float)(dist*tempColor.getBlue()/255),(float)1.0));
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
									g.fillArc((int)(size*x-cam.pos.x-size),(int)(size*y-cam.pos.y), lvl.panels[x][y].size*2, lvl.panels[x][y].size*2, 0,90);
								}else if(x>0&&y>0&&lvl.panels[x-1][y].exist==false&&lvl.panels[x][y-1].exist==false){
									Color tempColor = new Color(51,51,51);
									g.setColor(new Color((float)(dist*tempColor.getRed()/255),(float)(dist*tempColor.getGreen()/255),(float)(dist*tempColor.getBlue()/255),(float)1.0));
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
									g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
									g.fillArc((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size*2, lvl.panels[x][y].size*2, 90,90);
								}else
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
							}else if(x%2==0&&y%2==0)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size*2, lvl.panels[x][y].size*2);
							else if(y%2==1&&x%2==0&&lvl.panels[x][y-1].disco==false)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size*2, lvl.panels[x][y].size);
							else if(y%2==0&&x%2==1&&lvl.panels[x-1][y].disco==false)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size*2);
							else if(x%2==1&&y%2==1&&lvl.panels[x-1][y].disco==false&&lvl.panels[x][y-1].disco==false)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
						}else{
							Color tempColor = new Color(51,51,51);
							g.setColor(new Color((float)(dist*tempColor.getRed()/255),(float)(dist*tempColor.getGreen()/255),(float)(dist*tempColor.getBlue()/255),(float)1.0));
							if(x%2==0&&y%2==0)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size*2, lvl.panels[x][y].size*2);
							else if(x%2==0&&y%2==1&&lvl.panels[x][y-1].exist==true)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size*2, lvl.panels[x][y].size);
							else if(x%2==1&&y%2==0&&lvl.panels[x-1][y].exist==true)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size*2);
							else if(x%2==1&&y%2==1&&lvl.panels[x-1][y].exist==true&&lvl.panels[x][y-1].exist==true)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
						}
						if(y>0&&x>0&&lvl.panels[x][y].check==true){
							for(int e=4;e>1;e--){
								if(e%2==0)
									if(level.panels[x][y].passed==false){
										dist*=0.5;
										g.setColor(new Color((float)(dist*Color.GRAY.getRed()/255),(float)(dist*Color.GRAY.getGreen()/255),(float)(dist*Color.GRAY.getBlue()/255),(float)1.0));
									}else
										g.setColor(new Color((float)(dist*Color.WHITE.getRed()/255),(float)(dist*Color.WHITE.getGreen()/255),(float)(dist*Color.WHITE.getBlue()/255),(float)1.0));
								else
									g.setColor(Color.black);
								g.fillOval((int)(size*x - ((e*8) * ((double)size/20)) - cam.pos.x), (int)(size*y - ((e*8) * ((double)size/20)) - cam.pos.y), (int)((e*16) * ((double)size/20)), (int)((e*16) * ((double)size/20)));
							}
						}
					}else if(x<0||y<0){
							if(switchColors==true&&lvl.panels[(int)lvl.size.x-Math.abs(x)-1][(int)lvl.size.y-Math.abs(y)-1].disco==true)
								lvl.panels[(int)lvl.size.x-Math.abs(x)-1][(int)lvl.size.y-Math.abs(y)-1].color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
							g.setColor(new Color((float)(timer*dist*lvl.panels[(int)lvl.size.x-Math.abs(x)-1][(int)lvl.size.y-Math.abs(y)-1].color.getRed()/255),(float)(timer*dist*lvl.panels[(int)lvl.size.x-Math.abs(x)-1][(int)lvl.size.y-Math.abs(y)-1].color.getGreen()/255),(float)(timer*dist*lvl.panels[(int)lvl.size.x-Math.abs(x)-1][(int)lvl.size.y-Math.abs(y)-1].color.getBlue()/255),(float)1.0));
						if(x%2==0&&y%2==0)
							g.fillRect((int)((x*size)-cam.pos.x),(int)((y*size)-cam.pos.y), size*2, size*2);
					}else{
						if(switchColors==true&&lvl.panels[(int)Math.abs(x - lvl.size.x)][(int)Math.abs(y - lvl.size.y)].disco==true)
							lvl.panels[(int)Math.abs(x - lvl.size.x)][(int)Math.abs(y - lvl.size.y)].color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
						g.setColor(new Color((float)(timer*dist*lvl.panels[(int)Math.abs(x - lvl.size.x)][(int)Math.abs(y - lvl.size.y)].color.getRed()/255),(float)(timer*dist*lvl.panels[(int)Math.abs(x - lvl.size.x)][(int)Math.abs(y - lvl.size.y)].color.getGreen()/255),(float)(timer*dist*lvl.panels[(int)Math.abs(x - lvl.size.x)][(int)Math.abs(y - lvl.size.y)].color.getBlue()/255),(float)1.0));
					if(x%2==0&&y%2==0)
						g.fillRect((int)((x*size)-cam.pos.x),(int)((y*size)-cam.pos.y), size*2, size*2);
				}
				}
			}
			switchColors = false;
			
			//g.setColor(new Color((float)(level.player.color.getRed()/255),(float)(level.player.color.getGreen()/255),(float)(level.player.color.getBlue()/255), (float)1.0));
			//g.fillOval((int)(level.player.pos.x - level.player.size/2 - cam.pos.x),(int)(level.player.pos.y - level.player.size/2 - cam.pos.y), level.player.size, level.player.size);
			
			//g.setColor(Color.GREEN);
			//g.drawLine((int)(level.player.pos.x - cam.pos.x-(level.player.vel.perpendicular().normalize().x*level.player.size/2)), (int)(level.player.pos.y - cam.pos.y-(level.player.vel.perpendicular().normalize().y*level.player.size/2)), (int)(level.player.pos.x - cam.pos.x+(level.player.vel.perpendicular().normalize().x*level.player.size/2)), (int)(level.player.pos.y - cam.pos.y+(level.player.vel.perpendicular().normalize().y*level.player.size/2)));
			//g.setColor(Color.RED);
			//g.drawLine((int)(level.player.pos.x - cam.pos.x-(level.player.vel.normalize().x*level.player.size/2)), (int)(level.player.pos.y - cam.pos.y-(level.player.vel.normalize().y*level.player.size/2)), (int)(level.player.pos.x - cam.pos.x+(level.player.vel.normalize().x*level.player.size/2)), (int)(level.player.pos.y - cam.pos.y+(level.player.vel.normalize().y*level.player.size/2)));
			
			
				g.setColor(new Color((float)(level.player.color.getRed()/255),(float)(level.player.color.getGreen()/255),(float)(level.player.color.getBlue()/255), (float)Math.max(0,Math.min(1, (float)(0.45-(1/(40.0*((double)level.player.tailCurrent/level.player.tailMax))))))));
				int[] xPoints = new int[4];
				int[] yPoints = new int[4];
				
				xPoints[0] = (int)(level.player.pos.x     - cam.pos.x - (level.player.vel.perpendicular().normalize().x       *level.player.size/3));
				yPoints[0] = (int)(level.player.pos.y     - cam.pos.y - (level.player.vel.perpendicular().normalize().y       *level.player.size/3));
				xPoints[1] = (int)(level.player.tail[0].x - cam.pos.x - (level.player.tailVel[0].perpendicular().normalize().x*level.player.size/3));
				yPoints[1] = (int)(level.player.tail[0].y - cam.pos.y - (level.player.tailVel[0].perpendicular().normalize().y*level.player.size/3));
				xPoints[2] = (int)(level.player.tail[0].x - cam.pos.x + (level.player.tailVel[0].perpendicular().normalize().x*level.player.size/3));
				yPoints[2] = (int)(level.player.tail[0].y - cam.pos.y + (level.player.tailVel[0].perpendicular().normalize().y*level.player.size/3));
				xPoints[3] = (int)(level.player.pos.x     - cam.pos.x + (level.player.vel.perpendicular().normalize().x       *level.player.size/3));
				yPoints[3] = (int)(level.player.pos.y     - cam.pos.y + (level.player.vel.perpendicular().normalize().y       *level.player.size/3));
				if(xPoints[0]>10&&xPoints[1]>10&&xPoints[2]>10&&xPoints[3]>10)
					g.fillPolygon(new Polygon(xPoints, yPoints, 4));
				
				for(int v=0;v<level.player.tailCurrent-1;v++){
					g.setColor(new Color((float)(level.player.color.getRed()/255),(float)(level.player.color.getGreen()/255),(float)(level.player.color.getBlue()/255), (float)(0.45-(v/(40.0*((double)level.player.tailCurrent/level.player.tailMax))))));
					int newsize = (int)((level.player.size/2) - (((double)level.player.tailMax/level.player.tailCurrent)*v));
					xPoints[0] = (int)(level.player.tail[v].x - cam.pos.x - (level.player.tailVel[v].perpendicular().normalize().x*2*newsize/3));
					yPoints[0] = (int)(level.player.tail[v].y - cam.pos.y - (level.player.tailVel[v].perpendicular().normalize().y*2*newsize/3));
					xPoints[3] = (int)(level.player.tail[v].x - cam.pos.x + (level.player.tailVel[v].perpendicular().normalize().x*2*newsize/3));
					yPoints[3] = (int)(level.player.tail[v].y - cam.pos.y + (level.player.tailVel[v].perpendicular().normalize().y*2*newsize/3));
				
					v++;
					newsize    = (int)((level.player.size/2)  - (((double)level.player.tailMax/level.player.tailCurrent)*v));
					xPoints[1] = (int)(level.player.tail[v].x - cam.pos.x - (level.player.tailVel[v].perpendicular().normalize().x*2*newsize/3));
					yPoints[1] = (int)(level.player.tail[v].y - cam.pos.y - (level.player.tailVel[v].perpendicular().normalize().y*2*newsize/3));
					xPoints[2] = (int)(level.player.tail[v].x - cam.pos.x + (level.player.tailVel[v].perpendicular().normalize().x*2*newsize/3));
					yPoints[2] = (int)(level.player.tail[v].y - cam.pos.y + (level.player.tailVel[v].perpendicular().normalize().y*2*newsize/3));
					v--;
					
					if(xPoints[0]>10&&xPoints[1]>10&&xPoints[2]>10&&xPoints[3]>10)
						g.fillPolygon(new Polygon(xPoints, yPoints, 4));
				}
			
			
			AffineTransform at = new AffineTransform();
			if(level.pause==false)
				if(level.player.tension==true)
					if(level.player.markedHook.x<=level.player.pos.x)
						level.player.angle += (Math.atan(-(level.player.markedHook.x-level.player.pos.x)/(level.player.markedHook.y-level.player.pos.y))-level.player.angle)/8;
					else if(level.player.markedHook.x>level.player.pos.x) 
						level.player.angle += (Math.atan((level.player.markedHook.x-level.player.pos.x)/(level.player.markedHook.y-level.player.pos.y))-level.player.angle)/8;
				
			g.setColor(Color.gray);
			if(level.player.tension==true)
				for(int t=-Math.max(1, Math.min(5, (int)(2000/level.player.ropeLength)));t<Math.max(1, Math.min(5, (int)(2000/level.player.ropeLength)));t++)
					g.drawLine((int)(level.player.pos.x - cam.pos.x + t*level.player.pos.sub(level.player.markedHook).normalize().perpendicular().x/1.5),(int)(level.player.pos.y - cam.pos.y + t*level.player.pos.sub(level.player.markedHook).normalize().perpendicular().y/1.5), (int)(level.player.markedHook.sub(cam.pos).x + t*level.player.pos.sub(level.player.markedHook).normalize().perpendicular().x/1.5), (int)(level.player.markedHook.sub(cam.pos).y + t*level.player.pos.sub(level.player.markedHook).normalize().perpendicular().y/1.5));
			
			for(int e = 0;e<enemyNum;e++)
				if(level.enemy[e].tension==true)
					for(int t=-Math.max(1, Math.min(5, (int)(2000/level.enemy[e].ropeLength)));t<Math.max(1, Math.min(5, (int)(2000/level.enemy[e].ropeLength)));t++)
						g.drawLine((int)(level.enemy[e].pos.x - cam.pos.x + t*level.enemy[e].pos.sub(level.enemy[e].markedHook).normalize().perpendicular().x/1.5),(int)(level.enemy[e].pos.y - cam.pos.y + t*level.enemy[e].pos.sub(level.enemy[e].markedHook).normalize().perpendicular().y/1.5), (int)(level.enemy[e].markedHook.sub(cam.pos).x + t*level.enemy[e].pos.sub(level.enemy[e].markedHook).normalize().perpendicular().x/1.5), (int)(level.enemy[e].markedHook.sub(cam.pos).y + t*level.enemy[e].pos.sub(level.enemy[e].markedHook).normalize().perpendicular().y/1.5));
				
			
	        at.setToRotation(level.player.angle, (int)(level.player.pos.x - cam.pos.x) , (int)(level.player.pos.y - cam.pos.y));
	        Graphics2D g2d = (Graphics2D) g.create();
	        g2d.setTransform(at);
			g2d.drawImage(level.player.currentFrame, (int)(level.player.pos.x - level.player.size/2 - cam.pos.x),(int)(level.player.pos.y - level.player.size/2 - cam.pos.y), (int)(level.player.pos.x - level.player.size/2 - cam.pos.x) + level.player.size, (int)(level.player.pos.y - level.player.size/2 - cam.pos.y) + level.player.size, 0, 0, (int)(level.player.currentFrame.getWidth(null)), level.player.currentFrame.getHeight(null), null);
				
			findTargetDistance(level.player);
		
			for(int e=0;e<enemyNum;e++){
				at = new AffineTransform();
				if(level.pause==false)
					if(level.enemy[e].tension==true)
						if(level.enemy[e].markedHook.x<=level.enemy[e].pos.x)
							level.enemy[e].angle += (Math.atan(-(level.enemy[e].markedHook.x-level.enemy[e].pos.x)/(level.enemy[e].markedHook.y-level.enemy[e].pos.y))-level.enemy[e].angle)/8;
						else if(level.enemy[e].markedHook.x>level.enemy[e].pos.x) 
							level.enemy[e].angle += (Math.atan((level.enemy[e].markedHook.x-level.enemy[e].pos.x)/(level.enemy[e].markedHook.y-level.enemy[e].pos.y))-level.enemy[e].angle)/8;
				
				if(level.enemy[e].pos.x - cam.pos.x>-level.player.size&&level.enemy[e].pos.x - cam.pos.x<boxWidth+level.player.size&&level.enemy[e].pos.y - cam.pos.y>-level.player.size&&level.enemy[e].pos.y - cam.pos.y<boxHeight+level.player.size){
					at.setToRotation(level.enemy[e].angle, (int)(level.enemy[e].pos.x - cam.pos.x) , (int)(level.enemy[e].pos.y - cam.pos.y));
					g2d.setTransform(at);
					g2d.drawImage(level.enemy[e].currentFrame, (int)(level.enemy[e].pos.x - level.enemy[e].size/2 - cam.pos.x),(int)(level.enemy[e].pos.y - level.enemy[e].size/2 - cam.pos.y), (int)(level.enemy[e].pos.x - level.enemy[e].size/2 - cam.pos.x) + level.enemy[e].size, (int)(level.enemy[e].pos.y - level.enemy[e].size/2 - cam.pos.y) + level.enemy[e].size, 0, 0, (int)(level.enemy[e].currentFrame.getWidth(null)), level.enemy[e].currentFrame.getHeight(null), null);
				}
				findTargetDistance(level.enemy[e]);
			}
			
			if((level.player.ropeLength<level.player.ropeMax||level.player.ropeMax==-1)&&level.player.ropeLength>0)
				g.setColor(Color.RED);
			else
				g.setColor(Color.orange);
		
			g.drawLine((int)level.player.marker.x, (int)level.player.marker.y-25, (int)level.player.marker.x, (int)level.player.marker.y+25);
			g.drawLine((int)level.player.marker.x-25, (int)level.player.marker.y, (int)level.player.marker.x+25, (int)level.player.marker.y);
			g.drawOval((int)level.player.marker.x - 15, (int)level.player.marker.y - 15, 30, 30);
		
			g.setColor(new Color((float)1.0,(float)0.0,(float)0.0,(float)Math.max(0,Math.min(level.player.health/700,0.5))));	
			g.fillRect(0, 0, getWidth(), getHeight());
		}else{
			g.setFont(new Font("Times New Roman", 20, 40));
			g.setColor(Color.WHITE);
			g.drawString("Loading...",getWidth()-220,getHeight() - 10);
		}
		if(level.pause==true){
			g.setColor(new Color((float)0.0,(float)0.0,(float)0.0,(float)0.7));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setFont(new Font("Times New Roman", 20, 40));
			g.setColor(Color.WHITE);
			g.drawString("Paused",getWidth()-170,getHeight() - 10);
			for(int b=0;b<buttons.length;b++){
				if(buttons[b].hover==true){
					for(int r=0;r<7;r++){
						g.setColor(Button.highlight(r));
						for(int r2=0;r2<2;r2++)
							g.drawRect((int)buttons[b].pos.x-r*2+r2, (int)buttons[b].pos.y-r*2+r2, (int)buttons[b].size.x+((r*2)-r2)*2, (int)buttons[b].size.y+((r*2)-r2)*2);
					}
					g.setColor(Color.yellow);
				}else
					g.setColor(Color.white);
				g.drawRect((int)buttons[b].pos.x, (int)buttons[b].pos.y, (int)buttons[b].size.x, (int)buttons[b].size.y);
				g.drawString(buttons[b].name, (int)buttons[b].pos.x+20, (int)buttons[b].pos.y+40);
				if(buttons[b].showOption==true)
					if(buttons[b].value==false)
						g.drawString("No", (int)(buttons[b].pos.x+buttons[b].size.x)-95, (int)buttons[b].pos.y+85);
					else
						g.drawString("Yes", (int)(buttons[b].pos.x+buttons[b].size.x)-95, (int)buttons[b].pos.y+85);
			}
		}
	}
	
	public double findTargetDistance(Character chr){
		Vector2 dir;
		if(chr.tension==false)
			dir = chr.marker.add(cam.pos).sub(chr.pos).normalize();
		else
			dir = chr.markedHook.sub(chr.pos).normalize();
		
		Vector2 res = chr.pos;
		double cnt = 0;
		
		try{
			while(level.panels[(int)res.x/size][(int)res.y/size].exist==false){
				res = res.add(dir);
				cnt++;
			}
		
			if(level.panels[(int)res.x/size][(int)res.y/size].color==Color.red){
				chr.ropeLength = -1;
				return -1;
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){}
		
		
		if(chr.tension==false)
			chr.markedHook = res;
		
		chr.markedPanel.reset((int)res.x/size,(int)res.y/size);
		
		chr.ropeLength = cnt;
		return 0;
	}

	public void calculateCollision(Character ch){
		if(ch.tension==false){
			ch.vel.y*=0.999/friction;
			ch.vel.x*=0.999/friction;
		}
		
		if(ch.tension==true)
			ch.reelIn(cam);

		int iterations = 16;
		
		for(double tht=0;tht<Math.PI*2;tht+=Math.PI/iterations){
			int xloc = (int)(ch.pos.add(ch.vel).x+(Math.cos(tht)*(ch.size/2)))/size;
			int yloc = (int)(ch.pos.add(ch.vel).y+(Math.sin(tht)*(ch.size/2)))/size;

			if(level.panels[xloc][yloc].exist==true){
				if(level.panels[xloc-1][yloc].exist==false&&level.panels[xloc+1][yloc].exist==false&&level.panels[xloc][yloc+1].exist==false){
					if(Math.abs(ch.pos.add(ch.vel).sub(new Vector2((xloc+0.5)*size,(yloc+0.5)*size)).magnitude())<size+(ch.size/2))
						if(ch.pos.y>(yloc+0.5)*size)
							ch.vel = ch.vel.reflectAlong(new Vector2((xloc+0.5)*size,(yloc+0.5)*size).sub(ch.pos)).neg().x(1/friction);
						else
							ch.vel.x*=-1/friction;
				
				}else if(level.panels[xloc-1][yloc].exist==false&&level.panels[xloc+1][yloc].exist==false&&level.panels[xloc][yloc-1].exist==false){
					if(Math.abs(ch.pos.add(ch.vel).sub(new Vector2((xloc+0.5)*size,(yloc+0.5)*size)).magnitude())<size+(ch.size/2))
						if(ch.pos.y<(yloc+0.5)*size)
							ch.vel = ch.vel.reflectAlong(new Vector2((xloc+0.5)*size,(yloc+0.5)*size).sub(ch.pos)).neg().x(1/friction);
						else
							ch.vel.x*=-1/friction;
				
				}else if(level.panels[xloc-1][yloc].exist==false&&level.panels[xloc][yloc+1].exist==false&&level.panels[xloc][yloc-1].exist==false){
					if(Math.abs(ch.pos.add(ch.vel).sub(new Vector2((xloc+0.5)*size,(yloc+0.5)*size)).magnitude())<size+(ch.size/2))
						if(ch.pos.x<(xloc+0.5)*size)
							ch.vel = ch.vel.reflectAlong(new Vector2((xloc+0.5)*size,(yloc+0.5)*size).sub(ch.pos)).neg().x(1/friction);
						else
							ch.vel.y*=-1/friction;
				
				}else if(level.panels[xloc+1][yloc].exist==false&&level.panels[xloc][yloc+1].exist==false&&level.panels[xloc][yloc-1].exist==false){
					if(Math.abs(ch.pos.add(ch.vel).sub(new Vector2((xloc+0.5)*size,(yloc+0.5)*size)).magnitude())<size+(ch.size/2))
						if(ch.pos.x>(xloc+0.5)*size)
							ch.vel = ch.vel.reflectAlong(new Vector2((xloc+0.5)*size,(yloc+0.5)*size).sub(ch.pos)).neg().x(1/friction);
						else
							ch.vel.y*=-1/friction;
				
				}else if(level.panels[xloc+1][yloc].exist==false&&level.panels[xloc][yloc+1].exist==false){
					if(Math.abs(ch.pos.add(ch.vel).sub(new Vector2(xloc*size,yloc*size)).magnitude())<size+(ch.size/2)){
						ch.vel = ch.vel.reflectAlong(new Vector2(xloc*size,yloc*size).sub(ch.pos)).neg().x(1/friction);
						ch.pos = new Vector2(xloc*size,yloc*size).add(ch.pos.sub(new Vector2(xloc*size,yloc*size)).normalize().x(size+(ch.size/2)));
					}
					
				}else if(level.panels[xloc-1][yloc].exist==false&&level.panels[xloc][yloc+1].exist==false){
					if(ch.pos.add(ch.vel).sub(new Vector2((xloc+1)*size,yloc*size)).magnitude()<size+(ch.size/2)){
						ch.vel = ch.vel.reflectAlong(new Vector2((xloc+1)*size,yloc*size).sub(ch.pos)).neg().x(1/friction);
						ch.pos = new Vector2((xloc+1)*size,yloc*size).add(ch.pos.sub(new Vector2((xloc+1)*size,yloc*size)).normalize().x(size+(ch.size/2)));
					}
				
				}else if(level.panels[xloc-1][yloc].exist==false&&level.panels[xloc][yloc-1].exist==false){
					if(ch.pos.add(ch.vel).sub(new Vector2((xloc+1)*size,(yloc+1)*size)).magnitude()<size+(ch.size/2)){
						ch.vel = ch.vel.reflectAlong(new Vector2((xloc+1)*size,(yloc+1)*size).sub(ch.pos)).neg().x(1/friction);
						ch.pos = new Vector2((xloc+1)*size,(yloc+1)*size).add(ch.pos.sub(new Vector2((xloc+1)*size,(yloc+1)*size)).normalize().x(size+(ch.size/2)));
					}
						
				}else if(level.panels[xloc+1][yloc].exist==false&&level.panels[xloc][yloc-1].exist==false){
					if(ch.pos.add(ch.vel).sub(new Vector2(xloc*size,(yloc+1)*size)).magnitude()<size+(ch.size/2)){
						ch.vel = ch.vel.reflectAlong(new Vector2(xloc*size,(yloc+1)*size).sub(ch.pos)).neg().x(1/friction);
						ch.pos = new Vector2(xloc*size,(yloc+1)*size).add(ch.pos.sub(new Vector2(xloc*size,(yloc+1)*size)).normalize().x(size+(ch.size/2)));
					}
					
				}else{
					if(level.panels[xloc-1][yloc].exist==false||level.panels[xloc+1][yloc].exist==false){
						ch.vel.x *= -1/friction;
						if(ch.pos.x<xloc*size)
							ch.pos.x = (xloc*size) - (ch.size/2);
						else
							ch.pos.x = ((xloc+1)*size) + (ch.size/2);
							
					}else if(level.panels[xloc][yloc-1].exist==false||level.panels[xloc][yloc+1].exist==false){
						ch.vel.y *= -1/friction;
						if(ch.pos.y<yloc*size)
							ch.pos.y = (yloc*size) - (ch.size/2);
						else
							ch.pos.y = ((yloc+1)*size) + (ch.size/2);
						
					}
				}
				if(ch.endOfRope==true)
					ch.release();
				break;
			}
		}
		

		for(int e1=0;e1<enemyNum;e1++){
			if(ch!=level.enemy[e1]&&level.enemy[e1].pos.x>ch.pos.x-(ch.size*2)&&level.enemy[e1].pos.x<ch.pos.x+(ch.size*2)&&level.enemy[e1].pos.y>ch.pos.y-(ch.size*2)&&level.enemy[e1].pos.y<ch.pos.y+(ch.size*2)){
				if(ch.pos.add(ch.vel).sub(level.enemy[e1].pos.add(level.enemy[e1].vel)).magnitude()<(level.enemy[e1].size/2)+(ch.size/2)){
					/**double mag1 = ch.vel.magnitude();
					double mag2 = level.enemy[e1].vel.magnitude();
					Vector2 impact = ch.pos.sub(level.enemy[e1].pos);
				
					if(ch.vel.dotProduct(ch.pos.sub(level.enemy[e1].pos))>0)
						ch.vel = ch.vel.reflectAlong(impact).normalize().x((4*mag2/5) + (mag1/5));
					else
						ch.vel = ch.vel.reflectAlong(impact).normalize().x((4*mag2/5) + (mag1/5)).neg();
					if(level.enemy[e1].vel.dotProduct(ch.pos.sub(level.enemy[e1].pos))<0)
						level.enemy[e1].vel = level.enemy[e1].vel.reflectAlong(impact).normalize().x((4*mag1/5) + (mag2/5));
					else
						level.enemy[e1].vel = level.enemy[e1].vel.reflectAlong(impact).normalize().x((4*mag1/5) + (mag2/5)).neg();
					 **/
				
					ch.recalVel(level.enemy[e1]);
					ch.vel = ch.vel.x(1/friction);
					level.enemy[e1].vel = level.enemy[e1].vel.x(1/friction);
				
					ch.pos = level.enemy[e1].pos.add(ch.pos.sub(level.enemy[e1].pos).normalize().x((level.enemy[e1].size/2)+(ch.size/2)));
				
					level.enemy[e1].release();
					ch.release();
				}
			}
		}
		
		ch.pos = ch.pos.add(ch.vel); 
		
		if(ch.health>0)
			ch.health-=Math.min(ch.health/64,0.5);
			
			
		if(ch.health>480||ch.timeLeft<-0.05)
			level.resetPlayer();
	}
}
