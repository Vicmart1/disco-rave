package DiscoRave;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class menuScreen extends JPanel{

	static JFrame frame = new JFrame("Disco Rave by Vicmart Studios");
	static boolean[] keys = new boolean[200];
	static int boxWidth = 1280;
	static int boxHeight = 720;
	static int updateRate = 60;

	static Camera cam = new Camera(1000,500);
	static int size = 50;
	static Level level = new Level(128,128, size);
	static double timer = 0.5;
	static boolean switchColors = false;
	static BGMusic BG;
	static List<Image> frames = new ArrayList<Image>();
	static int index = 1; 
	static int frameTime = 0;
	static boolean isLoaded = false;
	
	static int menuFrame = 1;
	static Button[] currentButtons;
	static Button[] mainMenu = new Button[4];
	static Button[] lvlSelect;
	static Button[] help;
	
	static int[] scroll = new int[5]; 
	static boolean scrollDown = false;
	static boolean scrollUp = false;
	static int levels = 0;
	static boolean play = true;
	
	static int threshold = 100;
	static int officialLevels = 0;
	
	static String[] helpText = {"Controls [In-Game]:",
								"Use the mouse button to shoot a hook",
								"Press [Esc] to pause",
								"",
								"Controls [Level Editor]:",
								"Press [B] to switch between brush and drag mode",
								"In drag mode, drag the mouse to create rectangles",
								"In brush mode, drag the mouse to select areas with the brush",
								"Click on a single panel to edit it individually",
								"Press [Esc] to pause",
								"Press [1] or [2] to change the size of the brush",
								"Press [Ctrl]-[S] to save the level",
								"Use the scroll wheel to switch types of panels",
								"([Q] and [E] if you do not have a scroll wheel)",
								"Use [W],[A],[S],[D] to move the camera",
								"",
								"DiscoRave Version 1.0 Credits",
								"Programmer: Vicmart",
								"Artist: Vicmart",
								"",
								"Open-source libraries used:",
								"JACo Cross-Platform Java MP3 Library v0.9.3 - http://jacomp3player.sourceforge.net/",
								"",
								"Music used: [Artist - Song (order)]",
								"Solisio - 70's Disco!-Sample (1)",
								"EpicRPGRemixes - White Classic Orchestra (2)",
								"DiscJohnny - Back To Disco (3)",
								"OfficialNovacore - Smoothie (4)",
								"jmerk800 - Menu Funk (5)",
								"Hex72 - Ninja At The Disco(Hex72 Mix) (6)",
								"Mattron - Techno Disco! (7)",
								"durn - Sidechain Your Heart {durn} (8)",
								"Fionnhodgson - Ninja At The Disco (9)",
								"D3MON-SI4YER - DS Dance The Night Away (10)",
								"DJ-Galax - [DJ-G] Weekend Fever (11)",
								"wolf-tech - (FW) Disco (12)",
								"",
								"�2014 Vicmart Coding Studios. All Rights Reserved."};
	
	public menuScreen(){
		Thread main = new Thread(){
			public void run(){
				while(true){
					if(cam.pos.y/size>level.size.y-40||cam.pos.y/size<10)
						cam.vel.y*=-1;

					if(cam.pos.x/size>level.size.x-40||cam.pos.x/size<10)
						cam.vel.x*=-1;
					
					cam.pos = cam.pos.add(cam.vel);
					timer-=((double)BG.currentBPM/7200);
					
					if(timer<0.75&&frameTime==0){
						index++;
						frameTime++;
					}
					if(timer<0.67&&frameTime==1){
						index++;
						frameTime++;
					}
					
					if(timer<0.5){
						timer+=0.5;
						switchColors = true;
						frameTime = 0;
					}
					
					if(index>4)
						index = 1;
					
					if(menuFrame==2){
						if(scrollDown==true&&currentButtons[currentButtons.length-4].pos.y+scroll[menuFrame]>500)
							scroll[menuFrame]-=4;
						
						if(scrollUp==true&&scroll[menuFrame]<0)
							scroll[menuFrame]+=4;
					}else if(menuFrame==3){
						if(scrollDown==true&&scroll[menuFrame]>-650)
							scroll[menuFrame]-=4;
						
						if(scrollUp==true&&scroll[menuFrame]<0)
							scroll[menuFrame]+=4;
					}
					repaint();
					
					try {
						Thread.sleep(1000/updateRate);
					} catch (InterruptedException e) { }
				}
			}
		};
		main.start();
	}
	
	public static void main(String[] args) throws IOException{
		isLoaded = true;
		
		cam.vel.x = 2.5;
		cam.vel.y = 2.5;
		
		for(int b=0;b<mainMenu.length;b++){
			String btnName = "";
			mainMenu[b] = new Button((boxWidth/2)-300,150 + 125*b,600,100,btnName);
			switch(b){
				case 0:
					btnName = "Play Local Levels";
					mainMenu[b].showOption = false;
					break;
				case 1:
					btnName = "Disco Bumper Cars [Coming Soon]";
					mainMenu[b].showOption = false;
					mainMenu[b].active = false;
					break;
				case 2:
					btnName = "Controls/Credits";
					mainMenu[b].showOption = false;
					break;
				case 3:
					btnName = "Quit";
					mainMenu[b].showOption = false;
					break;
			}
			mainMenu[b].name = btnName;
		}
		
		refreshLevels();
		
		currentButtons = new Button[mainMenu.length];
		currentButtons = mainMenu;
		
		BG = new BGMusic(new File("").getCanonicalPath() + "/Assets/");
		
		level.resetPanels();
		level.calculateDisco();
		
		File folder = new File(new File("").getCanonicalPath() + "/Assets/Logos/");
		File[] listOfFiles = folder.listFiles();
		
		Arrays.sort(listOfFiles);
		for (File file : listOfFiles)
			if(file.getName().substring(file.getName().length()-3, file.getName().length()).equalsIgnoreCase("png")==true)
				frames.add(ImageIO.read(file));
		
		help = new Button[1];
		help[0] = new Button(boxWidth-300, 25, 125, 55, "Back");
		help[0].showOption = false;
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(boxWidth,boxHeight);
		frame.setContentPane(new menuScreen());
		frame.setVisible(true);
		frame.setResizable(false);
		frame.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e) {
				keys[(int)e.getKeyCode()]=false;
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
				for(int b=0;b<currentButtons.length;b++)
					if(currentButtons[b].hover==true){
						if(currentButtons[b].value==false)
							currentButtons[b].value=true;
						else if(currentButtons[b].value==true)
							currentButtons[b].value=false;
						if(menuFrame==2&&b<currentButtons.length-3){
							String[] arg = new String[1];
							if(b<officialLevels)
								arg[0] = b + "";
							else
								arg[0] = currentButtons[b].name + "";
							if(currentButtons[currentButtons.length-2].value==false){
								frame.setTitle(frame.getTitle() + " - Loading...");
								if(gameEngine.isLoaded==false){
									gameEngine.isLoaded = true;
									try {
										gameEngine.main(arg);
									} catch (IOException e1) { }
								}else{
									if(b<officialLevels)
										gameEngine.level.index = b;
									else
										gameEngine.level.index = -1;
									gameEngine.level.name = currentButtons[b].name;
									gameEngine.resetEngine();
									BG.player.stop();
								}
							}else{
								frame.setTitle(frame.getTitle() + " - Loading...");
								if(levelEditor.isLoaded==false){
									levelEditor.isLoaded = true;
									try {
										levelEditor.main(arg);
									} catch (IOException e1) { }
								}else{
									if(b<officialLevels)
										levelEditor.level.index = b;
									else
										levelEditor.level.index = -1;
									
									levelEditor.level.name = currentButtons[b].name;
									levelEditor.level.resetLevel(false);
									levelEditor.frame.setCursor(levelEditor.myCursor);
									levelEditor.frame.show();
									levelEditor.level.origin.reset();
									levelEditor.level.ending.reset();
									BG.player.stop();
								}
							}
							frame.setTitle(frame.getTitle().substring(0, frame.getTitle().length() - 13));
							frame.hide();
						}
						if(menuFrame==1){ 
							if(b==0){
								currentButtons[b].hover=false;
								menuFrame = 2;
								currentButtons = new Button[lvlSelect.length];
								currentButtons = lvlSelect;
							}else if(b==2){
								currentButtons[b].hover=false;
								menuFrame = 3;
								currentButtons = new Button[help.length];
								currentButtons = help;
							}else if(b==3)
								System.exit(1);
							
						}else if(menuFrame==2){
							if(b==currentButtons.length-1){ 
								currentButtons[b].hover=false;
								menuFrame = 1;
								currentButtons = new Button[mainMenu.length];
								currentButtons = mainMenu;
							}else if(b==currentButtons.length-2){ 
								if(currentButtons[b].value==true){
									currentButtons[b].name = "Edit Level";
									play = false;
								}else{
									currentButtons[b].name = "Play Level";
									play = true;
								}
							}else if(b==currentButtons.length-3){
								String lvl = JOptionPane.showInputDialog(frame, "Enter new level number/name:");
								String aut = JOptionPane.showInputDialog(frame, "Enter your name:");
								if(lvl.equals("")==false){
									String[] arg = new String[2];
									arg[0] = lvl;
									arg[1] = aut;
									boolean officialLvl = true;
									try{
										int lvlNum= Integer.parseInt(arg[0]);
									}catch (java.lang.NumberFormatException ee){
										officialLvl = false;
									}
									frame.setTitle(frame.getTitle() + " - Loading...");
									if(levelEditor.isLoaded==false){
										levelEditor.isLoaded = true;
										try {
											levelEditor.main(arg);
										} catch (IOException e1) { }
									}else{
										try{
											if(officialLvl = true)
												levelEditor.level.index = Integer.parseInt(arg[0]);
											else
												levelEditor.level.index = -1;
										}catch(java.lang.NumberFormatException ee){
											levelEditor.level.index = -1;
										}
										levelEditor.level.author = aut;
										levelEditor.level.name = lvl;
										levelEditor.resetEditor();
										levelEditor.frame.show();
									}
									frame.setTitle(frame.getTitle().substring(0, frame.getTitle().length() - 13));
									frame.hide();
									BG.player.stop();
								}
							}
						}else if(menuFrame==3){
							if(b==0){
								currentButtons[b].hover=false;
								menuFrame = 1;
								currentButtons = new Button[mainMenu.length];
								currentButtons = mainMenu;
							}
						}	
					}
			}

			public void mouseReleased(MouseEvent arg0) {  }
		});
		frame.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseMoved(MouseEvent e) {
				if(menuFrame!=3){
					for(int b=0;b<currentButtons.length;b++){
						currentButtons[b].hover = false;
						if(b<currentButtons.length-3&&currentButtons[b].pos.y+scroll[menuFrame]>threshold&&currentButtons[b].pos.y+scroll[menuFrame]<boxHeight-threshold-100&&currentButtons[b].active==true&&e.getX()>currentButtons[b].pos.x&&e.getX()<currentButtons[b].pos.x+currentButtons[b].size.x&&e.getY()>currentButtons[b].pos.y+scroll[menuFrame]&&e.getY()<currentButtons[b].pos.y+currentButtons[b].size.y+20+scroll[menuFrame])
							currentButtons[b].hover = true;
						else if(b>=currentButtons.length-3&&currentButtons[b].active==true&&e.getX()>currentButtons[b].pos.x&&e.getX()<currentButtons[b].pos.x+currentButtons[b].size.x&&e.getY()>currentButtons[b].pos.y&&e.getY()<currentButtons[b].pos.y+currentButtons[b].size.y+20)
							currentButtons[b].hover = true;
					}	
					if(e.getY()>boxHeight-threshold&&currentButtons[currentButtons.length-3].hover==false)
						scrollDown=true;
					else
						scrollDown=false;
					
					if(e.getY()<threshold&&currentButtons[currentButtons.length-1].hover==false&&currentButtons[currentButtons.length-2].hover==false)
						scrollUp=true;
					else
						scrollUp=false;
				}else{
					for(int b=0;b<currentButtons.length;b++){
						currentButtons[b].hover = false;
						if(currentButtons[b].active==true&&e.getX()>currentButtons[b].pos.x&&e.getX()<currentButtons[b].pos.x+currentButtons[b].size.x&&e.getY()>currentButtons[b].pos.y&&e.getY()<currentButtons[b].pos.y+currentButtons[b].size.y+20)
							currentButtons[b].hover = true;
					}	
					if(e.getY()>boxHeight-threshold)
						scrollDown=true;
					else
						scrollDown=false;
					
					if(e.getY()<threshold)
						scrollUp=true;
					else
						scrollUp=false;
				}
			}	

			@Override
			public void mouseDragged(MouseEvent arg0) { }
		});
		frame.addMouseWheelListener(new MouseWheelListener()
		{
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(menuFrame==2){
					if(e.getWheelRotation()<0&&currentButtons[currentButtons.length-4].pos.y+scroll[menuFrame]>500)
						scroll[menuFrame]+=e.getWheelRotation()*20;
					
					if(e.getWheelRotation()>0&&scroll[menuFrame]<0)
						scroll[menuFrame]+=e.getWheelRotation()*20;
				}else if(menuFrame==3){
					if(e.getWheelRotation()<0&&scroll[menuFrame]>-650)
						scroll[menuFrame]+=e.getWheelRotation()*20;
					
					if(e.getWheelRotation()>0&&scroll[menuFrame]<-10)
						scroll[menuFrame]+=e.getWheelRotation()*20;
				}
			}
		});
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		//g.setColor(Color.black);
		//g.fillRect(0, 0, getWidth(), getHeight());
		for(int x=(int)cam.pos.x/size - 2;x<(int)(cam.pos.x+getWidth()+50)/size;x++){
			for(int y=(int)cam.pos.y/size - 2;y<(int)(cam.pos.y+getHeight() + 50)/size;y++){
				if(switchColors==true)
					level.panels[x][y].color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
				g.setColor(new Color((float)(timer*level.panels[x][y].color.getRed()/255),(float)(timer*level.panels[x][y].color.getGreen()/255),(float)(timer*level.panels[x][y].color.getBlue()/255),(float)1.0));
				g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y),size, size);
			}
		}
		switchColors = false;
		
		g.setColor(new Color((float)0.0,(float)0.0,(float)0.0,(float)0.7));
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setFont(new Font("Times New Roman", 20, 40));
		for(int b=0;b<currentButtons.length;b++){
			if((currentButtons[b].pos.y + scroll[menuFrame]>75&&currentButtons[b].pos.y + scroll[menuFrame]<boxHeight-100)||(b>currentButtons.length-4)){
				if(currentButtons[b].hover==true){
					for(int r=0;r<7;r++){
						g.setColor(Button.highlight(r));
						for(int r2=0;r2<2;r2++)
							if(b<currentButtons.length-3)
								g.drawRect((int)currentButtons[b].pos.x-r*2+r2, (int)currentButtons[b].pos.y-r*2+r2+scroll[menuFrame], (int)currentButtons[b].size.x+((r*2)-r2)*2, (int)currentButtons[b].size.y+((r*2)-r2)*2);
							else
								g.drawRect((int)currentButtons[b].pos.x-r*2+r2, (int)currentButtons[b].pos.y-r*2+r2, (int)currentButtons[b].size.x+((r*2)-r2)*2, (int)currentButtons[b].size.y+((r*2)-r2)*2);
					}
					g.setColor(Color.yellow);
				}else if(currentButtons[b].active==true){
					if(b<currentButtons.length-3)
						g.setColor(new Color((float)(Color.WHITE.getRed()/255),(float)(Color.WHITE.getGreen()/255),(float)(Color.WHITE.getBlue()/255),(float)(Math.max(0, Math.min(Math.min(-((currentButtons[b].pos.y+40+scroll[menuFrame])-600)/100,((currentButtons[b].pos.y+40+scroll[menuFrame])-100)/100),1)))));
					else
						g.setColor(Color.WHITE);
				}else
					g.setColor(Color.GRAY);
				if(b<currentButtons.length-3){
					g.drawRect((int)currentButtons[b].pos.x, (int)currentButtons[b].pos.y+scroll[menuFrame], (int)currentButtons[b].size.x, (int)currentButtons[b].size.y);
					g.drawString(currentButtons[b].name, (int)currentButtons[b].pos.x+20, (int)currentButtons[b].pos.y+40+scroll[menuFrame]);
				}else{
					g.drawRect((int)currentButtons[b].pos.x, (int)currentButtons[b].pos.y, (int)currentButtons[b].size.x, (int)currentButtons[b].size.y);
					g.drawString(currentButtons[b].name, (int)currentButtons[b].pos.x+20, (int)currentButtons[b].pos.y+40);
				}
				if(currentButtons[b].showOption==true)
					if(currentButtons[b].value==false)
						g.drawString("No", (int)(currentButtons[b].pos.x+currentButtons[b].size.x)-95, (int)currentButtons[b].pos.y+85+scroll[menuFrame]);
					else
						g.drawString("Yes", (int)(currentButtons[b].pos.x+currentButtons[b].size.x)-95, (int)currentButtons[b].pos.y+85+scroll[menuFrame]);
			}
		}
		
		if(menuFrame==2){
			g.setColor(new Color((float)(Color.WHITE.getRed()/255),(float)(Color.WHITE.getGreen()/255),(float)(Color.WHITE.getBlue()/255),(float)(Math.max(0, Math.min(Math.min(-((currentButtons[0].pos.y+40+scroll[menuFrame])-600)/100,((currentButtons[0].pos.y+40+scroll[menuFrame])-100)/100),1)))));
			g.drawString("Official Levels",75,(int) (currentButtons[0].pos.y - 25 + scroll[menuFrame]));
			g.setColor(new Color((float)(Color.WHITE.getRed()/255),(float)(Color.WHITE.getGreen()/255),(float)(Color.WHITE.getBlue()/255),(float)(Math.max(0, Math.min(Math.min(-((currentButtons[officialLevels-1].pos.y+40+scroll[menuFrame])-600)/100,((currentButtons[officialLevels-1].pos.y+40+scroll[menuFrame])-100)/100),1)))));
			g.drawString("Custom Levels",75,(int) (currentButtons[officialLevels-1].pos.y + 125 + scroll[menuFrame]));
		}
		
		if(menuFrame==3){
			g.setFont(new Font("Times New Roman", 20, 30));
			g.setColor(Color.WHITE);
			for(int h=0;h<helpText.length;h++){
				if(160 + h*30 + scroll[menuFrame]>75&&160 + h*30 + scroll[menuFrame]<boxHeight-100){
					g.setColor(new Color((float)(Color.WHITE.getRed()/255),(float)(Color.WHITE.getGreen()/255),(float)(Color.WHITE.getBlue()/255),(float)(Math.max(0, Math.min(Math.min(-(((double)160 + h*30 + scroll[menuFrame])-700)/100,(((double)160 + h*30 + scroll[menuFrame])-100)/80),1)))));
					g.drawString(helpText[h], 100, 160 + h*30 + scroll[menuFrame]);
				}
			}
		}
		
		g.drawImage(frames.get(index), 100, 25, 600, 100, 0, 0, frames.get(index).getWidth(null), frames.get(index).getHeight(null), null);
		
	}
	
	public static void refreshLevels() throws IOException{
		String[] fileNames = null;
		
		fileNames = Level.findLevels(new File("").getCanonicalPath() + "/Levels/Official/");
		officialLevels = fileNames.length;
		
		ArrayList<String> temp = new ArrayList<String>();
		temp.addAll(Arrays.asList(fileNames));
		temp.addAll(Arrays.asList(Level.findLevels(new File("").getCanonicalPath() + "/Levels/")));
		String [] concatedArgs = temp.toArray(new String[fileNames.length+Level.findLevels(new File("").getCanonicalPath() + "/Levels/").length]);
		
		fileNames = new String[concatedArgs.length];
		fileNames = concatedArgs;
		lvlSelect = new Button[fileNames.length+3];
		levels = fileNames.length;
		for(int b=0;b<fileNames.length;b++){
			if(b<officialLevels){
				lvlSelect[b] = new Button((boxWidth/2)-600+(b%6)*200, 175 + 75*(b/6), 170, 55, "Level "+ b);
			}else{
				lvlSelect[b] = new Button((boxWidth/2)-600+((b-(officialLevels%6))%3)*400, 250 + 75*((officialLevels-1)/6) + 75 + 75*((b-officialLevels)/3), 340, 55, fileNames[b].substring(0, Math.min(fileNames[b].length(),18)));
			}
			lvlSelect[b].showOption = false;
		}
		
		lvlSelect[fileNames.length + 2] = new Button(boxWidth-300, 25, 125, 55, "Back");
		lvlSelect[fileNames.length + 2].showOption = false;
		
		lvlSelect[fileNames.length + 1] = new Button(boxWidth-525, 25, 200, 55, "Play level");
		lvlSelect[fileNames.length + 1].showOption = false;
		if(play==false)
			lvlSelect[fileNames.length + 1].name = "Edit Level";
			
		lvlSelect[fileNames.length] = new Button(boxWidth-525, boxHeight-110, 340, 55, "Create New Level");
		lvlSelect[fileNames.length].showOption = false;
		
		currentButtons = new Button[lvlSelect.length];
		currentButtons = lvlSelect;
	}
}
