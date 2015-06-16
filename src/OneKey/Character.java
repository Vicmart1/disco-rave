package OneKey;

import java.awt.Color;
import java.awt.Image;
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
		if(ropeLength<ropeMax&&ropeLength>0){
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
}