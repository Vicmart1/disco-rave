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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import jaco.mp3.player.MP3Player;
import jaco.mp3.player.c;


@SuppressWarnings("serial")
public class gameEngine extends JPanel {
	static int boxWidth = 1280;
	static int boxHeight = 720;
	static int updateRate = 30;
	static boolean[] keys = new boolean[200];
	
	static int size = 35;
	static Level level = new Level(256, 256,size);
	static Camera cam = new Camera(0,0);
	static Button[] buttons = new Button[4];
	static boolean isLoaded = false;
	
	static double timer = 0.75;
	static double onGround = 0;
	static boolean switchColors = false;
	static BGMusic BG;
	static double friction = 1.000;
	
	static byte[]imageByte=new byte[0];  
	static Image cursorImage=Toolkit.getDefaultToolkit().createImage(imageByte);  
	static Cursor myCursor=Toolkit.getDefaultToolkit().createCustomCursor(cursorImage,new Point(0,0),"cursor");
	
	static JFrame frame = new JFrame("Disco Rave by Vicmart Studios");
	
	static AllNotifications msgs = new AllNotifications();
	static int numChecks = 0;
	static String[] notifs = {"Welcome to DiscoRave!",
							"Use your mouse to move the marker.", 
							"Click to activate the player's grappling hook.", 
							"Hold the mouse button to swing!",
							"Press Esc to Pause.",
							"You can use hooks to move in all directions.",
							"Try walking up the stairs",
							"These checkpoints save your progress.",
							"Over time your light weakens, use checkpoints to restore it.",
							"Avoid touching red panels, which are deadly.",
							"Blue panels are elastic. Experiment with them!",
							"You can even use the hooks to climb walls.",
							"Don't break a bone on your way down!",
							"Time to get back up!",
							"You've mastered the tutorial!",
							"Enjoy the rest of the game,",
							"and don't forget to make your own levels!"};
	
	public gameEngine() {
		ActionListener taskPerformer = new ActionListener() {
    		public void actionPerformed(ActionEvent evt) {
				//while (true) {
				if(level.pause==false){
					try{
					if(level.player.tension==false){
						level.player.vel.y+=level.player.accel.y;
						level.player.vel.x*=0.999;
					}else{
						level.player.vel.y+=level.player.accel.y*2;
						level.player.vel.x*=0.9975;
					}
					
					if(Math.abs(level.player.vel.x)<1)
						if(keys[37]==true||keys[65]==true)
							level.player.vel.x -= 0.01;
						else if(keys[39]==true||keys[68]==true)
							level.player.vel.x += 0.01;
					
					/**if((keys[38]==true||keys[32]==true)&&level.player.jumps<1&&level.player.tension==false){
						level.player.vel.y = -0.5;
						level.player.jumps++;
					}**/
					
					if(level.player.tension==true&&Math.abs(level.player.markedHook.sub(level.player.pos.add(level.player.vel)).magnitude())>level.player.ropeLength+0.1)
						level.player.vel = level.player.vel.add(level.player.markedHook.sub(level.player.pos).normalize().div(20));
					
					if(level.player.tension==true)
						level.player.reelIn(cam);

					int iterations = 64;
					
					for(double tht=Math.PI;tht<Math.PI*2;tht+=Math.PI/iterations){
						Panel test1 = level.panels[(int)(level.player.pos.x+(Math.cos(tht)*level.player.size/2))/size][(int)(level.player.pos.add(level.player.vel).y+Math.abs(Math.sin(tht))*(level.player.size/2))/size];
						Panel test2 = level.panels[(int)(level.player.pos.x+(Math.cos(tht)*level.player.size/2))/size][(int)(level.player.pos.add(level.player.vel).y-Math.abs(Math.sin(tht))*(level.player.size/2))/size];

						if(test1.exist==true||test2.exist==true){
							if((test1.color==Color.BLUE&&test1.exist==true)||(test2.color==Color.BLUE&&test2.exist==true)){
								if(level.player.tension==true&&level.player.bounce==true){
									level.player.vel.y *= -1.5;
									level.player.tension = false;
									level.player.bounce = false;
									level.player.endOfRope = false;
								}else
									level.player.vel.y *= -0.5;
							}else
								level.player.vel.y *= -0.17;
							if(level.player.vel.y<0){
								level.player.jumps = 0;
							}
							if(level.player.endOfRope==false&&Math.abs(level.player.vel.x-Math.cos(tht)/10)<0.1&&level.player.endOfRope==false){
								level.player.vel.x-=Math.cos(tht)/10;
								level.player.angle+=(-(tht-(3*Math.PI/2))*10-level.player.angle)/16;
							}
							
							if((test1.color==Color.red&&test1.exist==true)||(test2.color==Color.red&&test2.exist==true)){
								if(level.player.health<1)
									level.player.health+=50;
								level.player.health += 13;
								level.player.tension = false;
							}
							if(test1.exist==true&&test2.exist==false)
								onGround = 1.0;
						}
					}
					
					for(double tht=Math.PI;tht<Math.PI*2;tht+=Math.PI/iterations){
						Panel test1 = level.panels[(int)(level.player.pos.add(level.player.vel).x+Math.sin(tht)*level.player.size/2)/size][(int)(level.player.pos.y+(Math.cos(tht)*level.player.size/2))/size];
						Panel test2 = level.panels[(int)(level.player.pos.add(level.player.vel).x-Math.sin(tht)*level.player.size/2)/size][(int)(level.player.pos.y+(Math.cos(tht)*level.player.size/2))/size];
						if(test1.exist==true||test2.exist==true){
							if((test1.color==Color.BLUE&&test1.exist==true)||(test2.color==Color.BLUE&&test2.exist==true)){
								if(level.player.tension==true&&level.player.bounce==true){
									level.player.vel.x *= -1.5;
									level.player.tension = false;
									level.player.bounce = false;
									level.player.endOfRope = false;
								}else
									level.player.vel.x *= -0.5;
							}else
								level.player.vel.x *= -0.25;
							
							if(level.player.endOfRope==false&&Math.abs(level.player.vel.y-Math.cos(tht)/10)<0.1&&level.player.endOfRope==false)
								level.player.vel.y-=Math.cos(tht)/10;

						
							if((test1.color==Color.red&&test1.exist==true)||(test2.color==Color.red&&test2.exist==true)){
								if(level.player.health<1)
									level.player.health+=50;
								level.player.health += 13;
								level.player.tension = false;
							}
						}
					}
					if(level.player.vel.x<-1.2)
						level.player.vel.x=-1.2;
					else if(level.player.vel.x>1.2)
						level.player.vel.x=1.2;
					
					level.player.pos = level.player.pos.add(level.player.vel); 
					
					//if(buttons[0].value==false){
						if(level.player.tailDist%20==0){
							for(int t=level.player.tailMax-1;t>0;t--){
								level.player.tail[t] = level.player.tail[t-1];
								level.player.tailVel[t] = level.player.tailVel[t-1];
							}
							if(level.player.endOfRope==false){
								level.player.tail[0] = level.player.pos;
								level.player.tailVel[0] = level.player.vel;
							}else{
								level.player.tail[0] = level.player.pos;
								level.player.tailVel[0] = new Vector2(0,0);
							}
							level.player.tailDist = 0;
						}
						level.player.tailDist++;		
					//}
					//if(level.player.ropeLength>level.player.ropeMax)
						//level.player.tension=false;
					
					if(level.player.health>480||level.player.timeLeft<-0.05)
						level.resetPlayer();

					if(level.player.health>0)
						level.player.health-=Math.min(level.player.health/16,10);
					
					for(int x=-1;x<2;x++)
						for(int y=-1;y<2;y++)
							if(level.panels[((int)level.player.pos.x)/size + x][((int)level.player.pos.y)/size + y].check==true){
								if(level.panels[((int)level.player.pos.x)/size + x][((int)level.player.pos.y)/size + y].passed==false)
									numChecks++;
								level.player.timeLeft = 1;
								level.panels[((int)level.player.pos.x)/size + x][((int)level.player.pos.y)/size + y].passed=true;
								level.lastCheckpoint.reset((((int)level.player.pos.x)/size + x)*size,(((int)level.player.pos.y)/size + y)*size);
							}
					
					if(Math.abs(level.player.pos.sub(level.ending).magnitude()) < 100){
						if(level.index!=-1)
							level.nextLevel(size,true);
						else{
							callMenu();
						}
						BG.levels++;
						if(BG.levels>4)
							BG.playSong((int)(Math.random()*BG.songs.size()));
					}
					
					if(onGround>0){
						if((level.player.tension==true||Math.abs(level.player.vel.x)>0.15)&&level.player.endOfRope==false){
							level.player.dancing = false;
							level.player.slide();
							level.player.sliding = true;
						}else{
							level.player.dancing = true;
							level.player.sliding = false;
						}
						onGround-=0.01;
					}else if(level.player.tension==true&&level.player.endOfRope==false){
						level.player.dancing = false;
						level.player.hang();
					}else
						level.player.dancing = true;
					
					//System.out.println(Math.abs(level.player.vel.x));
					
					level.player.timeLeft-=0.00005;
					level.player.tailCurrent = (int)(level.player.timeLeft*20);
					if(level.player.ropeTimer<1)
						level.player.ropeTimer+=0.002;
					}catch(java.lang.ArrayIndexOutOfBoundsException exx){
						callMenu();
					}
					/**try {
						Thread.sleep(1);  // milliseconds
					} catch (InterruptedException ex) { }**/
				}
				/**try {
					Thread.sleep(1);  // milliseconds
				} catch (InterruptedException ex) { }**/
				//}
			}
		};
		new Timer(1, taskPerformer).start();
		//playerThread.start();  
		
		ActionListener taskPerformermain = new ActionListener() {
    		public void actionPerformed(ActionEvent evt) {
				//while (true) { 
					if(level.pause==false){
						cam.pos = cam.pos.add(level.player.pos.sub(cam.pos.add(new Vector2(boxWidth/2,boxHeight/2))).div(8));
						timer-=((double)BG.currentBPM/7200);
						
						if(timer<0.83&&level.player.frameTime==0)
							level.player.nextDanceFrame();
						if(timer<0.67&&level.player.frameTime==1)
							level.player.nextDanceFrame();
						
						if(timer<0.5){
							timer+=0.5;
							switchColors = true;
							if(level.player.dancing==true)
								level.player.nextDanceFrame();
						}
						
						if(level.index==0){
							msgs.activateMessage(notifs[0]);
							msgs.activateMessage(notifs[1]);
							msgs.activateMessage(notifs[2]);
							switch(numChecks){
								case 1:
									msgs.activateMessage(notifs[3]);
									msgs.activateMessage(notifs[4]);
									break;
								case 2:
									msgs.activateMessage(notifs[5]);
									msgs.activateMessage(notifs[6]);
									break;
								case 3:
									msgs.activateMessage(notifs[7]);
									msgs.activateMessage(notifs[8]);
									break;
								case 4:
									msgs.activateMessage(notifs[9]);
									break;
								case 5:
									msgs.activateMessage(notifs[10]);
									break;
								case 6:
									msgs.activateMessage(notifs[11]);
									break;
								case 7:
									msgs.activateMessage(notifs[12]);
									break;
								case 8:
									msgs.activateMessage(notifs[13]);
									break;
								case 9:
									msgs.activateMessage(notifs[14]);
									msgs.activateMessage(notifs[15]);
									msgs.activateMessage(notifs[16]);
									break;
							}
							msgs.iterate(boxWidth);
						}else{
							if(level.index!=-1)
								msgs.activateMessage(notifs.length, "Level " + level.index);
							else
								msgs.activateMessage(notifs.length, "Custom Level " + level.name);
							msgs.activateMessage(notifs.length + 1, "Author: " + level.author);
							msgs.iterate(boxWidth);
						}
						
						//repaint();
						/**try {
							Thread.sleep(1000/updateRate);  // milliseconds
						} catch (InterruptedException ex) { }**/
					}
					repaint();
					/**try {
						Thread.sleep(1000/updateRate);  // milliseconds
					} catch (InterruptedException ex) { }**/
				//}
			}
		};
		//mainThread.start();  
		new Timer(1000/updateRate, taskPerformermain).start();
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
		
		level.player = new Character((int)level.origin.x,(int)level.origin.y,(int)(38 * ((double)size/20)), new File("").getCanonicalPath() + "/Assets/");
		level.player.player = true;
		level.player.marker.x = boxWidth/2;
		level.player.marker.y = boxHeight/2;
		
		for(int b=0;b<buttons.length;b++){
			String btnName = "";
			buttons[b] = new Button((boxWidth/2)-300,100 + 125*b,600,100,btnName);
			switch(b){
				case 0:
					btnName = "Turn off music";
					break;
				case 1:
					btnName = "Touch Screen Mode";
					break;
				case 2:
					btnName = "Edit Map";
					buttons[b].showOption = false;
					break;
				case 3:
					btnName = "Return to Menu";
					buttons[b].showOption = false;
					break;
			}
			buttons[b].name = btnName;
		}

		BG = new BGMusic(new File("").getCanonicalPath() + "/Assets/");
		
		for(int n=0;n<notifs.length;n++){
			msgs.all.add(new Notification(1,notifs[n],boxWidth, boxHeight));
		}
		
		if(level.index!=-1)
			msgs.all.add(new Notification(1,"Level "+level.index,boxWidth, boxHeight));
		else
			msgs.all.add(new Notification(1,"Custom Level "+level.name,boxWidth, boxHeight));
		msgs.all.add(new Notification(1,"Author: "+level.author,boxWidth, boxHeight));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setCursor(myCursor);
		frame.setSize(boxWidth,boxHeight);
		frame.setContentPane(new gameEngine());
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
					if(level.player.tension==false&&level.player.ropeTimer>1){
						level.player.tension = true;
						level.player.ropeTimer = 0;
					}else{
						level.player.tension=false;
						if(level.player.endOfRope==true){
							level.player.vel.reset();
							level.player.endOfRope = false;
						}
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

							//if(b==1&&buttons[0].value==false){
								//BG.nextSong();
							if(b==2){
								callEditor();
							}else if(b==3){
								callMenu();
							}
						}
				}
				/**	
				if(e.getButton()==MouseEvent.BUTTON3){
					level.player.tension=false;
					if(level.player.endOfRope==true){
						level.player.vel.reset();
						level.player.endOfRope = false;
					}
				} 
				**/	
			}

			public void mouseReleased(MouseEvent arg0) { 
				if(level.pause==false){
					level.player.tension=false;
					if(level.player.endOfRope==true){
						level.player.vel.reset();
						level.player.endOfRope = false;
					}
				}
			}
		});
		frame.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseMoved(MouseEvent e) {
				if(level.pause==false){
					if(buttons[1].value==false){
						if(level.player.marker.x + ((double)(e.getXOnScreen()) - boxWidth/2)>0&&level.player.marker.x + ((double)(e.getXOnScreen()) - boxWidth/2)<boxWidth - 10)
							level.player.marker.x += ((double)(e.getXOnScreen()) - boxWidth/2);
						if(level.player.marker.y + ((double)(e.getYOnScreen()) - boxHeight/2)>0&&level.player.marker.y + ((double)(e.getYOnScreen()) - boxHeight/2)<boxHeight - 30)
							level.player.marker.y += ((double)(e.getYOnScreen()) - boxHeight/2);
						try {
							Robot robot = new Robot();
							robot.mouseMove(boxWidth/2, boxHeight/2);
						} catch (AWTException ex) { }
					}else{
						level.player.marker.x = e.getXOnScreen();
						level.player.marker.y = e.getYOnScreen();
					}
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
					if(buttons[1].value==false){
						if(level.player.marker.x + ((double)(e.getXOnScreen()) - boxWidth/2)>0&&level.player.marker.x + ((double)(e.getXOnScreen()) - boxWidth/2)<boxWidth - 10)
							level.player.marker.x += ((double)(e.getXOnScreen()) - boxWidth/2);
						if(level.player.marker.y + ((double)(e.getYOnScreen()) - boxHeight/2)>0&&level.player.marker.y + ((double)(e.getYOnScreen()) - boxHeight/2)<boxHeight - 30)
							level.player.marker.y += ((double)(e.getYOnScreen()) - boxHeight/2);
						try {
							Robot robot = new Robot();
							robot.mouseMove(boxWidth/2, boxHeight/2);
						} catch (AWTException ex) { }
					}else{
						level.player.marker.x = e.getXOnScreen();
						level.player.marker.y = e.getYOnScreen();
					}
				}
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);  
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(level.switching==false){			
			for(int e=8;e>1;e--){
				if(e%2==0)
					g.setColor(Color.white);
				else
					g.setColor(Color.black);
				g.fillOval((int)(level.ending.x - ((e*8) * ((double)size/20)) - cam.pos.x), (int)(level.ending.y - ((e*8) * ((double)size/20)) - cam.pos.y), (int)((e*16) * ((double)size/20)), (int)((e*16) * ((double)size/20)));
			}
			
			Level lvl = level;
			for(int x=(int)cam.pos.x/size - 2;x<(int)(cam.pos.x+getWidth()+50)/size;x++){
				for(int y=(int)cam.pos.y/size - 2;y<(int)(cam.pos.y+getHeight() + 35)/size;y++){
					double dist = Math.max(0,Math.min(1/(level.player.pos.sub(new Vector2(x*size,y*size)).magnitude()/100)*level.player.timeLeft,1.0));
					if(x>=0&&y>=0&&x<lvl.size.x-1&&y<lvl.size.y-1){
						if(lvl.panels[x][y].exist==true){	
							if(lvl.panels[x][y].disco==false)	
								if(level.player.timeLeft<0.25){
									double dist2 = Math.max(0,Math.min(1/(level.player.pos.sub(new Vector2(x*size,y*size)).magnitude()/100),1.0));
									g.setColor(new Color((float)(dist2*lvl.panels[x][y].color.getRed()/255),(float)(dist2*lvl.panels[x][y].color.getGreen()/255),(float)(dist2*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
								}else
									g.setColor(new Color((float)(dist*lvl.panels[x][y].color.getRed()/255),(float)(dist*lvl.panels[x][y].color.getGreen()/255),(float)(dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
							else if(lvl.panels[x][y].disco==true){
								if(switchColors==true)
									lvl.panels[x][y].color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
								g.setColor(new Color((float)(timer*dist*lvl.panels[x][y].color.getRed()/255),(float)(timer*dist*lvl.panels[x][y].color.getGreen()/255),(float)(timer*dist*lvl.panels[x][y].color.getBlue()/255),(float)1.0));
							}
							if(lvl.panels[x][y].disco==false)
								if(level.player.timeLeft<0.25)
									g.drawRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
								else
									g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
							else if(x%2==0&&y%2==0)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size*2, lvl.panels[x][y].size*2);
							else if(y%2==1&&x%2==0&&lvl.panels[x][y-1].disco==false)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size*2, lvl.panels[x][y].size);
							else if(y%2==0&&x%2==1&&lvl.panels[x-1][y].disco==false)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size*2);
							else if(x%2==1&&y%2==1&&lvl.panels[x-1][y].disco==false&&lvl.panels[x][y-1].disco==false)
								g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
						}else if(lvl.panels[x][y].ending==false){
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
			
			//if(level.player.endOfRope==false){
				g.setColor(new Color((float)(level.player.color.getRed()/255),(float)(level.player.color.getGreen()/255),(float)(level.player.color.getBlue()/255), (float)Math.max(0,Math.min(1, (float)((0.45-(1/(40.0*((double)level.player.tailCurrent/level.player.tailMax))))*level.player.ropeTimer)))));
				int[] xPoints = new int[4];
				int[] yPoints = new int[4];
				
				//xPoints[0] = (int)(level.player.pos.x - cam.pos.x - (level.player.vel.perpendicular().normalize().x*level.player.size/2));
				//yPoints[0] = (int)(level.player.pos.y - cam.pos.y - (level.player.vel.perpendicular().normalize().y*level.player.size/2));
				
				xPoints[0] = (int)(level.player.pos.x - cam.pos.x - (level.player.vel.perpendicular().normalize().x*level.player.size/4));
				yPoints[0] = (int)(level.player.pos.y - 10 -cam.pos.y - (level.player.vel.perpendicular().normalize().y*level.player.size/4));
				xPoints[1] = (int)(level.player.tail[0].x - cam.pos.x - (level.player.tailVel[0].perpendicular().normalize().x*level.player.size/4));
				yPoints[1] = (int)(level.player.tail[0].y - 10 - cam.pos.y - (level.player.tailVel[0].perpendicular().normalize().y*level.player.size/4));
				xPoints[2] = (int)(level.player.tail[0].x - cam.pos.x + (level.player.tailVel[0].perpendicular().normalize().x*level.player.size/4));
				yPoints[2] = (int)(level.player.tail[0].y - 10 - cam.pos.y + (level.player.tailVel[0].perpendicular().normalize().y*level.player.size/4));
				xPoints[3] = (int)(level.player.pos.x - cam.pos.x + (level.player.vel.perpendicular().normalize().x*level.player.size/4));
				yPoints[3] = (int)(level.player.pos.y - 10 -cam.pos.y + (level.player.vel.perpendicular().normalize().y*level.player.size/4));
				if(xPoints[0]>10&&xPoints[1]>10&&xPoints[2]>10&&xPoints[3]>10)
					g.fillPolygon(new Polygon(xPoints, yPoints, 4));
				
				for(int v=0;v<level.player.tailCurrent-1;v++){
					g.setColor(new Color((float)(level.player.color.getRed()/255),(float)(level.player.color.getGreen()/255),(float)(level.player.color.getBlue()/255), (float)((0.45-(v/(40.0*((double)level.player.tailCurrent/level.player.tailMax))))*level.player.ropeTimer)));
					int newsize = (int)((level.player.size/2)-(((double)level.player.tailMax/level.player.tailCurrent)*v));
					xPoints[0] = (int)(level.player.tail[v].x - cam.pos.x - (level.player.tailVel[v].perpendicular().normalize().x*newsize/2));
					yPoints[0] = (int)(level.player.tail[v].y - 10 -cam.pos.y - (level.player.tailVel[v].perpendicular().normalize().y*newsize/2));
					xPoints[3] = (int)(level.player.tail[v].x - cam.pos.x + (level.player.tailVel[v].perpendicular().normalize().x*newsize/2));
					yPoints[3] = (int)(level.player.tail[v].y - 10 - cam.pos.y + (level.player.tailVel[v].perpendicular().normalize().y*newsize/2));
				
					v++;
					newsize = (int)((level.player.size/2)-(((double)level.player.tailMax/level.player.tailCurrent)*v));
					xPoints[1] = (int)(level.player.tail[v].x - cam.pos.x - (level.player.tailVel[v].perpendicular().normalize().x*newsize/2));
					yPoints[1] = (int)(level.player.tail[v].y - 10 - cam.pos.y - (level.player.tailVel[v].perpendicular().normalize().y*newsize/2));
					xPoints[2] = (int)(level.player.tail[v].x - cam.pos.x + (level.player.tailVel[v].perpendicular().normalize().x*newsize/2));
					yPoints[2] = (int)(level.player.tail[v].y - 10 - cam.pos.y + (level.player.tailVel[v].perpendicular().normalize().y*newsize/2));
					v--;
					
					if(xPoints[0]>10&&xPoints[1]>10&&xPoints[2]>10&&xPoints[3]>10)
						g.fillPolygon(new Polygon(xPoints, yPoints, 4));
					
				//g.fillOval((int)(level.player.tail[v].x - newsize/2 - cam.pos.x),(int)(level.player.tail[v].y - newsize/2 - cam.pos.y), newsize, newsize);
				}
			//}
			
			AffineTransform at = new AffineTransform();
			if(level.pause==false)
				if(level.player.tension==true&&onGround<=0)
					if(level.player.endOfRope==true)
						level.player.angle += Math.max(0,(Math.atan(-(level.player.markedHook.x-level.player.pos.x)/(level.player.markedHook.y-level.player.pos.y))-level.player.angle)/16);
					else
						level.player.angle += ((Math.atan(-(level.player.markedHook.x-level.player.pos.x)/(level.player.markedHook.y-level.player.pos.y))-level.player.angle)/16);
				else if(level.player.sliding==true)
					if(level.player.vel.x<0)
						level.player.angle += ((Math.PI/16) - level.player.angle)/16;
					else
						level.player.angle += ((-Math.PI/16) - level.player.angle)/16;
				else
					level.player.angle -= level.player.angle/16;
			
	        at.setToRotation(level.player.angle, (int)(level.player.pos.x - cam.pos.x) , (int)(level.player.pos.y - cam.pos.y));
	        Graphics2D g2d = (Graphics2D) g.create();
	        g2d.setTransform(at);
			g2d.drawImage(level.player.currentFrame, (int)(level.player.pos.x - level.player.size/2 - cam.pos.x - 1),(int)(level.player.pos.y - level.player.size/2 - cam.pos.y - 1), (int)(level.player.pos.x - level.player.size/2 - cam.pos.x) + level.player.size + 2, (int)(level.player.pos.y - level.player.size/2 - cam.pos.y) + level.player.size + 2, -4, 0, (int)(level.player.currentFrame.getWidth(null)*1.3), level.player.currentFrame.getHeight(null), null);
				
			findTargetDistance(level.player);
		
			if(level.player.ropeLength<level.player.ropeMax&&level.player.ropeLength>0&&level.player.ropeTimer>1)
				g.setColor(Color.RED);
			else
				g.setColor(Color.orange);
		
			g.drawLine((int)level.player.marker.x, (int)level.player.marker.y-25, (int)level.player.marker.x, (int)level.player.marker.y+25);
			g.drawLine((int)level.player.marker.x-25, (int)level.player.marker.y, (int)level.player.marker.x+25, (int)level.player.marker.y);
			g.drawOval((int)level.player.marker.x - 15, (int)level.player.marker.y - 15, 30, 30);
		
			g.setColor(Color.gray);
			if(level.player.tension==true)
					g.drawLine((int)(level.player.pos.x - cam.pos.x),(int)(level.player.pos.y - cam.pos.y), (int)level.player.markedHook.sub(cam.pos).x, (int)level.player.markedHook.sub(cam.pos).y);

			g.setColor(new Color((float)1.0,(float)0.0,(float)0.0,(float)Math.max(0,Math.min(level.player.health/700,0.5))));	
			g.fillRect(0, 0, getWidth(), getHeight());
		
			int countMsg =-1;
			for(int n=0;n<msgs.all.size();n++){
				if(msgs.all.get(n).active==true){
					countMsg++;
					g.setColor(new Color((float)0.0,(float)0.0,(float)0.0,(float)0.85));
					g.fillRect((int)msgs.all.get(n).pos.x, (int)(msgs.all.get(n).pos.y - ((msgs.activeMessages-countMsg)*msgs.all.get(n).size.y)), (int)msgs.all.get(n).size.x, (int)msgs.all.get(n).size.y);
					g.fillArc((int)(msgs.all.get(n).pos.x-msgs.all.get(n).size.y/2), (int)((msgs.all.get(n).pos.y - 0.5) - ((msgs.activeMessages-countMsg)*msgs.all.get(n).size.y)), (int)msgs.all.get(n).size.y, (int)msgs.all.get(n).size.y, 90, 180);
					g.setFont(new Font("Times New Roman", 20, 40));
					g.setColor(Color.WHITE);
					g.drawString(msgs.all.get(n).text,(int)msgs.all.get(n).pos.x+20, (int)(msgs.all.get(n).pos.y+35 - ((msgs.activeMessages-countMsg)*msgs.all.get(n).size.y)));
				}
			}	
				
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
		
			if(level.panels[(int)res.x/size][(int)res.y/size].color==Color.red){//||level.panels[(int)res.x/size][(int)(res.y/size)-1].exist==false){
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
	
	public static void resetEngine(){
		level.resetLevel(true);
		msgs.all.remove(notifs.length+1);
		msgs.all.remove(notifs.length);
		
		if(level.index!=-1)
			msgs.all.add(new Notification(1,"Level "+level.index,boxWidth, boxHeight));
		else
			msgs.all.add(new Notification(1,"Custom Level "+level.name,boxWidth, boxHeight));
		msgs.all.add(new Notification(1,"Author: "+level.author,boxWidth, boxHeight));
		
		numChecks = 0;
		msgs.activeMessages  = -1;
		for(int n=0;n<notifs.length + 2;n++){
			msgs.all.get(n).reset(boxWidth);
		}
		frame.setCursor(gameEngine.myCursor);
		frame.show();
		BG.player.play();
	}
	
	public static void callMenu(){
		String[] args = null;
		BG.player.stop();
		if(menuScreen.isLoaded==false){
			menuScreen.isLoaded = true;
			try {
				menuScreen.main(args); 
			} catch (IOException e1) { }
		}else{
			menuScreen.menuFrame = 2;
			try {
				menuScreen.refreshLevels();
			} catch (IOException e1) { e1.printStackTrace();}
			menuScreen.frame.show();
			menuScreen.BG.player.play();
		}
		level.pause=true;
		frame.hide();
	}
	
	public static void callEditor(){
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
			levelEditor.resetEditor();
			levelEditor.frame.show();
		}
		BG.player.stop();
		frame.hide();
	}
}

