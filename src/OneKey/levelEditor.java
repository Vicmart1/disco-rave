package OneKey;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class levelEditor extends JPanel {
	static int boxWidth = 1280;
	static int boxHeight = 720;
	static int updateRate = 60;
	static boolean[] keys = new boolean[200];
	
	static Camera cam = new Camera(0,0);
	static Vector2 camOffset = new Vector2(0,0);
	static Vector2 threshold = new Vector2(100,100);
	static Vector2 start = new Vector2(-1,-1);
	static Vector2 startf = new Vector2(-1,-1);
	static Vector2 end = new Vector2(0,0);
	
	static int size = 20;
	static Level level = new Level(256,256,size);
	static int type = 1;
	static int speed = size;
	static boolean isLoaded = false;
	
	static boolean delete = false;
	static boolean brush = false;
	static int brushSize = 5;
	
	static String source;
	static String sourceOld;
	static List<String> saveFile = new ArrayList<>();
	static String saved = "";
	
	static Button[] buttons = new Button[3];
	static boolean mouseDrag = false;
	static Vector2 mouse = new Vector2(0,0);
	
	static byte[]imageByte=new byte[0];  
	static Image cursorImage=Toolkit.getDefaultToolkit().createImage(imageByte);  
	static Cursor myCursor=Toolkit.getDefaultToolkit().createCustomCursor(cursorImage,new Point(0,0),"cursor");
	
	static JFrame frame = new JFrame("Disco Rave [Level Editor] by Vicmart Studios");
	
	public levelEditor() {
		Thread mainThread = new Thread() {
			public void run() {
				while (true) { 
					Vector2 mk = level.player.marker;
					/**
					if(mk.x>boxWidth-threshold.x&&cam.pos.x+((mk.x-(boxWidth-threshold.x))/speed)<level.size.x*size-1000)
						cam.pos.x+=(mk.x-(boxWidth-threshold.x))/speed;
					else if(mk.x<threshold.x&&cam.pos.x-((threshold.x-mk.x)/speed)>-200)
						cam.pos.x-=(threshold.x-mk.x)/speed;
					if(mk.y>boxHeight-threshold.y&&cam.pos.y+((mk.y-(boxHeight-threshold.y))/speed)<level.size.y*size-500)
						cam.pos.y+=(mk.y-(boxHeight-threshold.y))/speed;
					else if(mk.y<threshold.y&&cam.pos.y-((threshold.y-mk.y)/speed)>-200)
						cam.pos.y-=(threshold.y-mk.y)/speed;
					**/
					
					if(keys[68]==true&&cam.pos.x+speed<level.size.x*size-1000){
						cam.pos.x+=speed;
						if(mouseDrag==true)
							drawCircle(mouse.x, mouse.y);
					}else if(keys[65]==true&&cam.pos.x-speed>-200){
						cam.pos.x-=speed;
						if(mouseDrag==true)
							drawCircle(mouse.x, mouse.y);
					}if(keys[83]==true&&cam.pos.y+speed<level.size.y*size-500&&keys[17]==false){
						cam.pos.y+=speed;
						if(mouseDrag==true)
							drawCircle(mouse.x, mouse.y);
					}else if(keys[87]==true&&cam.pos.y-((threshold.y-mk.y)/speed)>-200){
						cam.pos.y-=speed;
						if(mouseDrag==true)
							drawCircle(mouse.x, mouse.y);
					}
					
					if(start.equals(new Vector2(-1,-1))==true&&startf.equals(new Vector2(-1,-1))==false){
						int sx = (int)cam.pos.x;
						int ex = (int)cam.pos.x;
						int sy = (int)cam.pos.y;
						int ey = (int)cam.pos.y;
						if(startf.x<end.x){
							sx += (int)startf.x;
							ex += (int)end.x;
						}else{
							sx += (int)end.x;
							ex += (int)startf.x;
						}
						if(startf.y<end.y){
							sy += (int)startf.y;
							ey += (int)end.y;
						}else{
							sy += (int)end.y;
							ey += (int)startf.y;
						}
						try{
						for(int x=sx/size;x<ex/size;x++)
							for(int y=sy/size;y<ey/size;y++)
								if(delete==true)
									level.panels[x][y].exist = false;
								else if(type==1){
									level.panels[x][y].exist = true;
									level.panels[x][y].color = Color.white;
								}else if(type==2){
									level.panels[x][y].exist = true;
									level.panels[x][y].color = Color.red;
								}else if(type==3){
									level.panels[x][y].exist = true;
									level.panels[x][y].color = Color.blue;
								}
						if(delete==false)
							saveFile.add(sx/size+","+sy/size+","+ex/size+","+ey/size+","+type);
						else
							saveFile.add(sx/size+","+sy/size+","+ex/size+","+ey/size+",0");
						startf = new Vector2(-1,-1);
						delete = false;
						}catch(java.lang.ArrayIndexOutOfBoundsException ee){}
					}
					//System.out.println(source);
					repaint();
					try {
						Thread.sleep(1000/updateRate);  // milliseconds
					} catch (InterruptedException ex) { }
				}
			}
		};
		mainThread.start();  
	}
	
	public static void toFile(){
		FileReader input = null;
		try {
			input = new FileReader(source);
		} catch (FileNotFoundException e) { }
		int cnt = 0;
		String[] trans = null;
		try{
			BufferedReader bufRead = new BufferedReader(input);
			String myLine = null;
			try {
				while ( (myLine = bufRead.readLine()) != null){    
					cnt++;
				}
			} catch (IOException e) { }
		
			try {
				input = new FileReader(source);
			} catch (FileNotFoundException e) { }
			bufRead = new BufferedReader(input);
			myLine = null;
		
			trans = new String[cnt];
			cnt = 0;
			try {
				while ( (myLine = bufRead.readLine()) != null){    
					//if(Integer.parseInt(myLine.split(",")[4])!=6&&Integer.parseInt(myLine.split(",")[4])!=7){
						trans[cnt] = myLine;
						cnt++;
					//}
				}
			} catch (IOException e) { }
		}catch(java.lang.NullPointerException e){}
		PrintWriter printWriter = null;
        try {
            try {
				printWriter = new PrintWriter(source, "UTF-8");
			} catch (IOException e) { }
            for(int c = 0;c<cnt;c++)
            	printWriter.println(trans[c]);
            for(String s:saveFile)
            	printWriter.println(s);
            printWriter.println((int)level.origin.x + "," + (int)level.origin.y + "," + (int)level.ending.x + "," + (int)level.ending.y + ",4");
        } finally {
            printWriter.close();
        }
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        saved = "Saved at " + timeStamp;
        
        saveFile.clear();
	}
	
	public static void main(String[] args) throws IOException{
		isLoaded = true;
		
		try{
			level.index = Integer.parseInt(args[0]);
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			String num = JOptionPane.showInputDialog("Created by Vicmart Coding Studios \nCopyright 2014, All Rights Reserved\n \nEnter level number:\n(If no level exists for that number, a new one will be created)","");
			level.index = Integer.parseInt(num);
		}
		
		level.width = size;
		for(int lx=0;lx<level.size.x;lx++)
			for(int ly=0;ly<level.size.y;ly++)
				level.panels[lx][ly] = new Panel(size,true);
		
		saveFile.add(level.size.x + "," + level.size.y + ",0,0,-1");
		
		level.player = new Character(250,150,50,new File("").getCanonicalPath() + "/Assets/");
		level.player.player = true;
		
		level.filePath = new File("").getCanonicalPath() + "/Levels/";
		try {
			source = new File("").getCanonicalPath() + "/Levels/level"+level.index+".txt";
			//sourceOld = new File("").getCanonicalPath() + "/src/OneKey/level"+num+".txt";
		} catch (IOException e1) { }
		
		try{
			level.importFromFile(source,false);
		}catch(java.lang.NullPointerException e){}
		
		for(int b=0;b<buttons.length;b++){
			String btnName = "";
			buttons[b] = new Button((boxWidth/2)-300,150 + 150*b,600,100,btnName);
			switch(b){
				case 0:
					btnName = "Delete Map";
					buttons[b].showOption = false;
					break;
				case 1:
					btnName = "Load New Map";
					buttons[b].showOption = false;
					break;
				case 2:
					btnName = "Test Map";
					buttons[b].showOption = false;
					break;
			}
			buttons[b].name = btnName;
		}
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setCursor(myCursor);
		frame.setSize(boxWidth,boxHeight);
		frame.setContentPane(new levelEditor());
		frame.setVisible(true);
		frame.setResizable(false);
		frame.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e) {
				if(level.pause==false){
					keys[(int)e.getKeyCode()]=true;
					if(e.getKeyCode()==66)
						if(brush==false)
							brush=true;
						else
							brush=false;
					//System.out.println(e.getKeyCode());
					if(e.getKeyCode()==49)
						brushSize--;
					else if(e.getKeyCode()==50)
						brushSize++;
					brushSize = Math.max(1,Math.min(brushSize,9));
				
					if(keys[17]==true&&keys[83]==true)
						toFile();
				}
				if(e.getKeyCode()==27){
					if(level.pause==false)
					{
						level.pause = true;
						frame.setCursor(Cursor.DEFAULT_CURSOR);
					}else{
						level.pause = false;
						frame.setCursor(myCursor);
					}
				}
			}

			public void keyReleased(KeyEvent e) {
				keys[(int)e.getKeyCode()]=false;
			}

			public void keyTyped(KeyEvent arg0) {}
		});
		frame.addMouseListener(new MouseListener(){
			
			public void mouseClicked(MouseEvent e) {
				if(type<4&&level.pause==false){
					startf = start;
					start = new Vector2(-1,-1);
					int sx = (int)cam.pos.x;
					int sy = (int)cam.pos.y;
					sx += (int)e.getX()-4;
					sy += (int)e.getY()-25;
					
					try{
					int x = sx/size;
					int y = sy/size;
						if(delete==true)
							level.panels[x][y].exist = false;
						else if(type==1){
							level.panels[x][y].exist = true;
							level.panels[x][y].color = Color.white;
						}else if(type==2){
							level.panels[x][y].exist = true;
							level.panels[x][y].color = Color.red;
						}else if(type==3){
							level.panels[x][y].exist = true;
							level.panels[x][y].color = Color.blue;
						}		
					if(delete==false)	
						saveFile.add((x)+","+(y)+","+(x+1)+","+(y+1)+","+type);
					else
						saveFile.add((x)+","+(y)+","+(x+1)+","+(y+1)+",0");
					startf = new Vector2(-1,-1);
					delete = false;
					}catch(java.lang.ArrayIndexOutOfBoundsException ee){}
				}
			}

			public void mouseEntered(MouseEvent arg0) { }

			public void mouseExited(MouseEvent e) { }

			public void mousePressed(MouseEvent e) { 
				if(level.pause==false){
					if(e.getButton()==MouseEvent.BUTTON3&&type<4)
						delete = true;
					else
						delete = false;
				}
				if(level.pause==true){
					for(int b=0;b<buttons.length;b++)
						if(buttons[b].hover==true)
							if(b==0){
								Object[] options = {"Yes", "No"};
								int n = JOptionPane.showOptionDialog(frame,
										"Are you sure you want to delete Level " + level.index + "?" , "Warning",
										JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE,
										null,
										options,
										options[1]);
								if(n==0){
									System.out.println(source);
									try{
										System.gc();
										File levelFile = new File(source);
										if(levelFile.delete()==true){
											JOptionPane.showMessageDialog(frame, "Level " + level.index + " deleted!");
											level.resetLevel(false);
											level.origin.reset();
											level.ending.reset();
											saved = ""; 
										}else{
											JOptionPane.showMessageDialog(frame, "Delete operation failed. Level " + level.index + " does not exist.");
										}
									}catch(Exception f){f.printStackTrace();}
								}
							}else if(b==1){
								toFile();
								String lvl = JOptionPane.showInputDialog(frame, "Enter level number:");
								level.index = Integer.parseInt(lvl);
								cam.pos.reset();
								saved = "";
								try {
									source = new File("").getCanonicalPath() + "/Levels/level"+level.index+".txt";
								} catch (IOException e1) { }
								level.resetLevel(false);
							}else if(b==2){
								String[] arg = new String[1];
								arg[0] = level.index + "";
								toFile();
								if(gameEngine.isLoaded==false){
									gameEngine.isLoaded = true;
									try {
										gameEngine.main(arg);
									} catch (IOException e1) { }
								}else{
									gameEngine.level.index = level.index;
									gameEngine.level.resetLevel(true);
									gameEngine.frame.setCursor(gameEngine.myCursor);
									gameEngine.frame.show();
									gameEngine.BG.player.play();
								}
								frame.hide();
							}
				}
			}

			public void mouseReleased(MouseEvent e) {
				if(level.pause==false){
					mouseDrag = false;
					if(type==4&&e.getButton()==MouseEvent.BUTTON1)
						level.origin = new Vector2(e.getX()-4+cam.pos.x,e.getY()-25+cam.pos.y);
					else if(type==4&&e.getButton()==MouseEvent.BUTTON3)
						level.ending = new Vector2(size*(int)((e.getX()-4+cam.pos.x)/size),size*(int)((e.getY()-25+cam.pos.y)/size));
					else if(type==5&&e.getButton()==MouseEvent.BUTTON1){
						level.panels[(int)(e.getX()-4+cam.pos.x)/size][(int)(e.getY()-25+cam.pos.y)/size].check = true;
						saveFile.add((int)(e.getX()-4+cam.pos.x)/size+","+(int)(e.getY()-25+cam.pos.y)/size+",0,0,"+type);	
					}else if(type==5&&e.getButton()==MouseEvent.BUTTON3){
						for(int x=0;x<2;x++)
							for(int y=0;y<2;y++)
								if(level.panels[(int)(e.getX()-4+cam.pos.x)/size + x][(int)(e.getY()-25+cam.pos.y)/size + y].check==true){
									saveFile.add(((int)(e.getX()-4+cam.pos.x)/size + x)+","+((int)(e.getY()-25+cam.pos.y)/size + y)+",0,0,6");
									level.panels[(int)(e.getX()-4+cam.pos.x)/size + x][(int)(e.getY()-25+cam.pos.y)/size + y].check = false;
								}
					}
					if(e.getButton()==MouseEvent.BUTTON3)
						delete = true;
				
					if(type<4){
						startf = start;
						start = new Vector2(-1,-1);
					}else{
						start = new Vector2(-1,-1);
						startf = new Vector2(-1,-1);
					}
				}
			}
			
		});
		frame.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(level.pause==false){
					type -= (int)e.getWheelRotation();
					if(type>5)
						type = 1;
					else if(type<1)
						type = 5;
				}
			}
			
		});
		frame.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseMoved(MouseEvent e) {
				if(startf.equals(new Vector2(-1,-1))==true&&level.pause==false){
					level.player.marker.x = e.getX()-4;
					level.player.marker.y = e.getY()-25;
				}else if(level.pause==true){
					for(int b=0;b<buttons.length;b++){
						buttons[b].hover = false;
						if(e.getXOnScreen()>buttons[b].pos.x&&e.getXOnScreen()<buttons[b].pos.x+buttons[b].size.x&&e.getYOnScreen()>buttons[b].pos.y&&e.getYOnScreen()<buttons[b].pos.y+buttons[b].size.y)
							buttons[b].hover = true;
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if(level.pause==false){
					if(brush==true&&type<4){
						mouseDrag = true;
						mouse.x = e.getX();
						mouse.y = e.getY();
						drawCircle(e.getX(),e.getY());
					}else{
						if(start.equals(new Vector2(-1,-1))==true&&e.getX()-4+cam.pos.x>0&&e.getY()-25+cam.pos.y>0&&e.getX()-4+cam.pos.x<=level.size.x*size&&e.getY()-25+cam.pos.y<=level.size.y*size)
							start = new Vector2(e.getX() - 4, e.getY() - 25);
						if(e.getX()-4+cam.pos.x>0&&e.getY()-25+cam.pos.y>0&&e.getX()-4+cam.pos.x<=level.size.x*size&&e.getY()-25+cam.pos.y<=level.size.y*size)
							end = new Vector2(e.getX() - 4, e.getY() - 25);
					}
				}
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);  
		g.setColor(Color.black);
		g.fillRect(0, 0, boxWidth, boxHeight);
		
		Level lvl = level;
		for(int x=(int)Math.max(0,(cam.pos.x/size)+0);x<(int)Math.min(lvl.size.x,(cam.pos.x+boxWidth+50)/size);x++){
			for(int y=(int)Math.max(0,(cam.pos.y/size)+0);y<(int)Math.min(lvl.size.y,(cam.pos.y+boxHeight+50)/size);y++){
				if(lvl.panels[x][y].exist==true){
					g.setColor(lvl.panels[x][y].color);
					g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
				}
				if(y>0&&lvl.panels[x][y-1].check==true){
					g.setColor(Color.cyan);
					g.fillOval((int)(size*x-cam.pos.x-30),(int)(size*(y-1)-cam.pos.y-30), 60,60);
				}
					
				g.setColor(Color.GRAY);
				g.drawRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y), lvl.panels[x][y].size, lvl.panels[x][y].size);
			}
		}
		
		g.setColor(Color.ORANGE);
		g.fillOval((int)(level.origin.x-30-cam.pos.x), (int)(level.origin.y-30-cam.pos.y), 60, 60);
		g.setColor(Color.magenta);
		g.fillOval((int)(level.ending.x-30-cam.pos.x), (int)(level.ending.y-30-cam.pos.y), 60, 60);
		g.setColor(Color.cyan);

		g.setColor(new Color((float)0,(float)1,(float)0,(float)0.5));
		//g.drawRect(size*((int)(level.player.marker.x+cam.pos.x%size)/size), size*((int)(level.player.marker.y+cam.pos.y%size)/size), size, size);
		if(start.equals(new Vector2(-1,-1))==false&&type<4){
			int sx = (int)cam.pos.x%size;
			int ex = (int)cam.pos.x%size;
			int sy = (int)cam.pos.y%size;
			int ey = (int)cam.pos.y%size;
			if(startf.x<end.x){
				sx += (int)start.x;
				ex += (int)end.x;
			}else{
				sx += (int)end.x;
				ex += (int)start.x;
			}
			if(startf.y<end.y){
				sy += (int)start.y;
				ey += (int)end.y;
			}else{
				sy += (int)end.y;
				ey += (int)start.y;
			}
			g.fillRect(size*((int)sx/size), size*((int)sy/size), size*((int)(ex-sx+(5*size/9))/size), size*((int)(ey-sy+(4*size/9))/size));
				
			//g.fillRect(size*((int)(start.x+(cam.pos.x%size))/size), size*((int)(start.y+(cam.pos.y%size))/size), size*((int)(end.x-start.x+(cam.pos.x%size))/size), size*((int)(end.y-start.y+(cam.pos.y%size))/size));
		
		}	
			
		g.setColor(Color.RED);
		g.drawLine((int)level.player.marker.x, (int)level.player.marker.y-25, (int)level.player.marker.x, (int)level.player.marker.y+25);
		g.drawLine((int)level.player.marker.x-25, (int)level.player.marker.y, (int)level.player.marker.x+25, (int)level.player.marker.y);
		g.drawOval((int)level.player.marker.x - 15, (int)level.player.marker.y - 15, 30, 30);	
		
		g.setColor(new Color((float)0.0,(float)0.0,(float)0.0,(float)0.4));
		g.setFont(new Font("Times New Roman", 20, 40));
		switch(type){
		case 1:
			g.fillRect(-5,15,450,90);
			g.setColor(Color.WHITE);
			g.drawString("Action: Add White Panel", 10,50);
			break;
		case 2:
			g.fillRect(-5,15,430,90);
			g.setColor(Color.WHITE);
			g.drawString("Action: Add Red Panel", 10,50);
			break;
		case 3:
			g.fillRect(-5,15,440,90);
			g.setColor(Color.WHITE);
			g.drawString("Action: Add Blue Panel", 10,50);
			break;
		case 4:
			g.fillRect(-5,15,550,90);
			g.setColor(Color.WHITE);
			g.drawString("Action: Modify Start/End Points", 10,50);
			break;
		case 5:
			g.fillRect(-5,15,500,90);
			g.setColor(Color.WHITE);
			g.drawString("Action: Modify Checkpoints", 10,50);
			break;
		}
		if(brush==true&&type<4){
			g.drawString("Tool: Brush " + " Size: " + brushSize, 10,90);
			g.setColor(new Color((float)0,(float)1,(float)0,(float)0.5));
			g.fillOval((int)level.player.marker.x - (size*brushSize), (int)level.player.marker.y - (size*brushSize), (size*brushSize)*2, (size*brushSize)*2);	
		}else
			g.drawString("Tool: Rectangle", 10,90);
			
		if(saved.equals("")==false){
			g.setColor(new Color((float)0.0,(float)0.0,(float)0.0,(float)0.4));
			g.fillRect(-5,getHeight()-45,310,40);
			g.setColor(Color.WHITE);
			g.drawString(saved,10,getHeight() - 10);
		}

		g.setColor(new Color((float)0.0,(float)0.0,(float)0.0,(float)0.4));
		g.fillRect(getWidth()-290,15,400,80);
		g.setColor(Color.WHITE);
		if(type==4){
			g.drawString("Left: Origin", getWidth()-280,50);
			g.drawString("Right: Endpoint", getWidth()-280,90);
		}else{
			g.drawString("Left: Add", getWidth()-280,50);
			g.drawString("Right: Delete", getWidth()-280,90);
		}
		
		g.setColor(new Color((float)0.0,(float)0.0,(float)0.0,(float)0.4));
		g.fillRect(getWidth()-225,getHeight()-45,300,40);
		g.setColor(Color.WHITE);
		g.drawString("<"+(int)cam.pos.x+","+(int)cam.pos.y+">",getWidth()-220,getHeight() - 10);
		
		if(level.pause==true){
			g.setColor(new Color((float)0.0,(float)0.0,(float)0.0,(float)0.7));
			g.fillRect(0, 0, boxWidth, boxHeight);
			g.setFont(new Font("Times New Roman", 20, 40));
			g.setColor(Color.WHITE);
			g.drawString("Paused",getWidth()-170,getHeight() - 10);
			for(int b=0;b<buttons.length;b++){
				if(buttons[b].hover==true)
					g.setColor(Color.yellow);
				else
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
	
	public static void drawCircle(double mx, double my){
		level.player.marker.x = mx-4;
		level.player.marker.y = my-25;
		for(int t=0;t<360;t+=5){
			for(int r=0;r<brushSize*size;r+=size){
				int sx = (int)cam.pos.x;
				int sy = (int)cam.pos.y;
				sx += (int)mx-4;
				sy += (int)my-25;
				sx += r*Math.cos(Math.toRadians(t));
				sy += r*Math.sin(Math.toRadians(t));
				try{
				int x = sx/size;
				int y = sy/size;
					if(delete==true)
						level.panels[x][y].exist = false;
					else if(type==1){
						level.panels[x][y].exist = true;
						level.panels[x][y].color = Color.white;
					}else if(type==2){
						level.panels[x][y].exist = true;
						level.panels[x][y].color = Color.red;
					}else if(type==3){
						level.panels[x][y].exist = true;
						level.panels[x][y].color = Color.blue;
					}		
				}catch(java.lang.ArrayIndexOutOfBoundsException ee){}
			}
		}
		if(delete==false)	
			saveFile.add(((int)cam.pos.x + mx-4)+","+((int)cam.pos.y + my-25)+",0,"+brushSize+","+type+",1");
		else
			saveFile.add(((int)cam.pos.x + mx-4)+","+((int)cam.pos.y + my-25)+",0,"+brushSize+",0,1");
	}
	
	/**
	public static void fileCleaner(String src){
		FileReader input = null;
		try {
			input = new FileReader(src);
		} catch (FileNotFoundException e) { }
		List<String> trans = null;
		Panel[][] panels = new Panel[1000][1000];
		int cnt = 0;
		try{
			BufferedReader bufRead = new BufferedReader(input);
			String myLine = null;
			String[] values = null;
			try {
				while ( (myLine = bufRead.readLine()) != null){    
					values = myLine.split(",");
					if(Integer.parseInt(values[4])==-1){
						trans.add(0, myLine);
					}else{
						for(int x = Integer.parseInt(values[0]); x<Integer.parseInt(values[2]); x++)
							for(int y = Integer.parseInt(values[1]); y<Integer.parseInt(values[3]); y++){
								int val = Integer.parseInt(values[4]);
								if(val==0)
									panels[x][y].exist = false;
								else if(val==1){
									panels[x][y].exist = true;
									panels[x][y].color = Color.white;	
								}else if(val==2){
									panels[x][y].exist = true;
									panels[x][y].color = Color.red;
								}else if(val==3){
									panels[x][y].exist = true;
									panels[x][y].color = Color.blue;
								}
							}
						if(Integer.parseInt(values[4])==4){
							level.origin.x = Integer.parseInt(values[0])*(size/20);
							level.origin.y = Integer.parseInt(values[1])*(size/20);
							level.ending.x = Integer.parseInt(values[2])*(size/20);
							level.ending.y = Integer.parseInt(values[3])*(size/20);
						}else if(Integer.parseInt(values[4])==5){
							panels[Integer.parseInt(values[0])][Integer.parseInt(values[1])].check = true;
						}else if(Integer.parseInt(values[4])==6){
							panels[Integer.parseInt(values[0])][Integer.parseInt(values[1])].check = false;
						}						
					}
					cnt++;
				}
			} catch (IOException e) { }
		}catch(java.lang.NullPointerException e){}
		
		PrintWriter printWriter = null;
        try {
            try {
				printWriter = new PrintWriter(source, "UTF-8");
			} catch (IOException e) { }
            for(Panel[] pa:panels)
            	for(Panel p:pa)
            		printWriter.println(p.x);
            	printWriter.println(trans.get(c));
            for(String s:saveFile)
            	printWriter.println(s);
            printWriter.println((int)level.origin.x + "," + (int)level.origin.y + "," + (int)level.ending.x + "," + (int)level.ending.y + ",4");
        } finally {
            printWriter.close();
        }
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        saved = "Saved at " + timeStamp;
	}
	**/
}
