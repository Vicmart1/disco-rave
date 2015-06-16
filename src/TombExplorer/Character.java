package TombExplorer;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class Character {
	public Vector2 pos = new Vector2(0,0);
	public Vector2 vel = new Vector2(0,0);
	public Vector2 accel = new Vector2(0,0.00098);
	public Vector2 marker = new Vector2(0,0);
	public Vector2 markedPanel = new Vector2(0,0);
	public Vector2 markedHook = new Vector2(0,0);
	public int tailMax = 20;
	public int tailCurrent = tailMax;
	public int tailDist = 0;
	public Vector2[] tail = new Vector2[tailMax];
	public Vector2[] tailVel = new Vector2[tailMax];
	public double ropeLength = 0;
	public double ropeMax = 325;
	public double health = 0;
	public boolean tension = false;
	public boolean player = false;
	public boolean endOfRope = false;
	public boolean bounce = true;
	public double timeLeft = 1;
	public int size;
	public int jumps = 0;
	public Color color = new Color((float)1.0,(float)1.0,(float)0.0,(float)0.2);
	public List<Image> frames = new ArrayList<Image>();
	public Image currentFrame;
	public int frameIndex = 0;
	public int frameDir = 1;
	public int frameTime = 0;
	public boolean dancing = false;
	public boolean sliding = false;
	public double angle = 0;
	public double ropeTimer = 0;
	public int choice = 0;
	public double maxVel = 1.0;
	public int mass = 1;
	
	public Character(int x, int y, int s){
		pos.x = x;
		pos.y = y;
		size = s;
		ropeMax = ropeMax * ((double)size/20);
	}
	
	public Character(int x, int y, int s, String sourceFolder) throws IOException{
		pos.x = x;
		pos.y = y;
		size = s;
		ropeMax = ropeMax * ((double)size/20);
		for(int t=0;t<tailMax;t++){
			tail[t] = pos;
			tailVel[t] = new Vector2(0,0);
		}
		
		File folder = new File(sourceFolder);
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		for (File file : listOfFiles)
			if(file.getName().substring(file.getName().length()-3, file.getName().length()).equalsIgnoreCase("png")==true)
				frames.add(ImageIO.read(file));
				
		currentFrame = frames.get(0);
	}
	
	public void nextDanceFrame(){
		frameTime++;
		frameIndex = Math.max(0, Math.min(frameIndex, 3));
		frameIndex+=frameDir;
		if(frameIndex>=3){
			frameDir = -1;
			frameTime = 0;
		}
		else if(frameIndex<=0){
			frameDir = 1;
			frameTime = 0;
		}
		currentFrame = frames.get(frameIndex); 
	}
	
	public void slide(){
		if(vel.x>0)
			frameIndex = 6;
		else
			frameIndex = 7;
		currentFrame = frames.get(frameIndex); 
	}
	
	public void hang(){
		if(vel.x>0)
			frameIndex = 5;
		else
			frameIndex = 4;
		currentFrame = frames.get(frameIndex); 
	}
	
	public void reelIn(Camera cam){
		if((ropeLength<ropeMax||ropeMax==-1)&&ropeLength>0){
			vel = vel.add(markedHook.sub(pos).normalize().div(250));
			tension = true;
			if(ropeLength<3*size/4){
				endOfRope = true;
			}
			if(ropeLength<size*2){
				bounce = true;
			}
		}else{
			tension = false;
		}
	}
	
	public void resetTail(){
		for(int t=0;t<tailMax;t++){
			tail[t] = pos;
			tailVel[t].reset();
		}
	}
	
	public void release(){
		tension = false;
		endOfRope = false;
	}
	
	public void changeColor(Color[] colors) {
		BufferedImage image = (BufferedImage) currentFrame;
        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster raster = image.getRaster();
        
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                if(pixels[0]==237&&pixels[1]==28&&pixels[2]==36){
                	pixels[0] = colors[0].getRed();
                	pixels[1] = colors[0].getGreen();
                	pixels[2] = colors[0].getBlue();
                }else if(pixels[0]==181&&pixels[1]==230&&pixels[2]==29){
                	pixels[0] = colors[1].getRed();
                	pixels[1] = colors[1].getGreen();
                	pixels[2] = colors[1].getBlue();
                }else if(pixels[0]==163&&pixels[1]==73&&pixels[2]==164){
                	pixels[0] = colors[2].getRed();
                	pixels[1] = colors[2].getGreen();
                	pixels[2] = colors[2].getBlue();
                }
                raster.setPixel(xx, yy, pixels);
            }
        }
    }
	
	public void recalVel(Character cha){
		Vector2 dx = cha.pos.sub(pos);
		double phi;
		if(dx.x==0)
			phi = Math.PI/2;
		else
			phi = Math.atan(dx.y/dx.x);
		
		double v1i = vel.magnitude();
		double v2i = cha.vel.magnitude();
		double ang1 = vel.arcTan();
		double ang2 = cha.vel.arcTan();
		
		double m1 = mass*vel.magnitude();
		double m2 = cha.mass*cha.vel.magnitude();
		
		Vector2 v1r = new Vector2(v1i*Math.cos(ang1-phi),v1i*Math.sin(ang1-phi));
		Vector2 v2r = new Vector2(v2i*Math.cos(ang2-phi),v2i*Math.sin(ang2-phi));
		Vector2 v1fr = new Vector2(((m1-m2)*v1r.x + (m2+m2)*v2r.x)/(m1+m2),v1r.y);
		Vector2 v2fr = new Vector2(((m1+m1)*v1r.x + (m2-m1)*v2r.x)/(m1+m2),v2r.y);
		
		Vector2 v1f = new Vector2(Math.cos(phi)*v1fr.x + Math.cos(phi + Math.PI/2)*v1fr.y,Math.sin(phi)*v1fr.x + Math.sin(phi + Math.PI/2)*v1fr.y);
		Vector2 v2f = new Vector2(Math.cos(phi)*v2fr.x + Math.cos(phi + Math.PI/2)*v2fr.y,Math.sin(phi)*v2fr.x + Math.sin(phi + Math.PI/2)*v2fr.y);
		if(v1f.magnitude()>maxVel)
			v1f = v1f.normalize().x(maxVel);
		
		if(v2f.magnitude()>maxVel)
			v2f = v2f.normalize().x(maxVel);
		
		vel = v1f;
		cha.vel = v2f;
	}
	
	public void doAction(int choice, int enemyNum, Level level, Camera cam, int boxWidth, int boxHeight){
		switch(choice){
			case 0:
				choice = (int)(Math.random()*(enemyNum+1)*10);
				if(choice<enemyNum){
					marker = level.enemy[choice].pos.sub(cam.pos);
					tension=true;
				}else if(choice%(enemyNum+1)==enemyNum){
					marker = level.player.pos.sub(cam.pos);
					tension=true;
				}else if(choice<enemyNum*4){
					marker = new Vector2(Math.random()*boxWidth+cam.pos.x, Math.random()*boxHeight+cam.pos.y);
					tension=true;
				}
				break;
		}
	}
}