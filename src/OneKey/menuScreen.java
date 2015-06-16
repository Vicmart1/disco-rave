package OneKey;

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
	static Button[] buttons = new Button[4];

	static Camera cam = new Camera(1000,500);
	static int size = 35;
	static Level level = new Level(128,128, size);
	static double timer = 0.5;
	static boolean switchColors = false;
	static BGMusic BG;
	static List<Image> frames = new ArrayList<Image>();
	static int index = 1; 
	static int frameTime = 0;
	
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
		cam.vel.x = 2.5;
		cam.vel.y = 2.5;
		
		for(int b=0;b<buttons.length;b++){
			String btnName = "";
			buttons[b] = new Button((boxWidth/2)-300,150 + 125*b,600,100,btnName);
			switch(b){
				case 0:
					btnName = "Play Local Levels";
					buttons[b].showOption = false;
					break;
				case 1:
					btnName = "Play Community Creations";
					buttons[b].showOption = false;
					buttons[b].active = false;
					break;
				case 2:
					btnName = "Options/Credits";
					buttons[b].showOption = false;
					break;
				case 3:
					btnName = "Quit";
					buttons[b].showOption = false;
					break;
			}
			buttons[b].name = btnName;
		}
		
		BG = new BGMusic(new File("").getCanonicalPath() + "/Assets/");
		
		level.resetPanels();
		level.calculateDisco();
		
		File folder = new File(new File("").getCanonicalPath() + "/Assets/Logos/");
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		for (File file : listOfFiles)
			if(file.getName().substring(file.getName().length()-3, file.getName().length()).equalsIgnoreCase("png")==true)
				frames.add(ImageIO.read(file));
		
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
				
				for(int b=0;b<buttons.length;b++)
					if(buttons[b].hover==true){
						if(buttons[b].value==false)
							buttons[b].value=true;
						else if(buttons[b].value==true)
							buttons[b].value=false;

						if(b==1){
						}else if(b==2){
						}else if(b==3){
							
						}
					}
			}

			public void mouseReleased(MouseEvent arg0) {  }
		});
		frame.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseMoved(MouseEvent e) {
				for(int b=0;b<buttons.length;b++){
					buttons[b].hover = false;
					if(buttons[b].active==true&&e.getXOnScreen()>buttons[b].pos.x&&e.getXOnScreen()<buttons[b].pos.x+buttons[b].size.x&&e.getYOnScreen()>buttons[b].pos.y&&e.getYOnScreen()<buttons[b].pos.y+buttons[b].size.y)
						buttons[b].hover = true;
				}	
			}	

			@Override
			public void mouseDragged(MouseEvent arg0) { }
		});
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		//g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for(int x=(int)cam.pos.x/size - 2;x<(int)(cam.pos.x+getWidth()+50)/size;x++){
			for(int y=(int)cam.pos.y/size - 2;y<(int)(cam.pos.y+getHeight() + 35)/size;y++){
				if(switchColors==true)
					level.panels[x][y].color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
				g.setColor(new Color((float)(timer*level.panels[x][y].color.getRed()/255),(float)(timer*level.panels[x][y].color.getGreen()/255),(float)(timer*level.panels[x][y].color.getBlue()/255),(float)1.0));
				g.fillRect((int)(size*x-cam.pos.x),(int)(size*y-cam.pos.y),size, size);
			}
		}
		switchColors = false;
		
		g.setColor(new Color((float)0.0,(float)0.0,(float)0.0,(float)0.9));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(new Font("Times New Roman", 20, 40));
		for(int b=0;b<buttons.length;b++){
			if(buttons[b].hover==true)
				g.setColor(Color.yellow);
			else if(buttons[b].active==true)
				g.setColor(Color.white);
			else
				g.setColor(Color.GRAY);
			
			g.drawRect((int)buttons[b].pos.x, (int)buttons[b].pos.y, (int)buttons[b].size.x, (int)buttons[b].size.y);
			g.drawString(buttons[b].name, (int)buttons[b].pos.x+20, (int)buttons[b].pos.y+40);
			if(buttons[b].showOption==true)
				if(buttons[b].value==false)
					g.drawString("No", (int)(buttons[b].pos.x+buttons[b].size.x)-95, (int)buttons[b].pos.y+85);
				else
					g.drawString("Yes", (int)(buttons[b].pos.x+buttons[b].size.x)-95, (int)buttons[b].pos.y+85);
		}
		
		g.drawImage(frames.get(index), 100, 25, 600, 100, 0, 0, frames.get(index).getWidth(null), frames.get(index).getHeight(null), null);
		
	}
}
