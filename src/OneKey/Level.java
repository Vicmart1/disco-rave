package OneKey;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Level {
	public Panel[][] panels;
	public Vector2 size = new Vector2(0,0);
	public Vector2 origin = new Vector2(0,0);
	public Vector2 ending = new Vector2(0,0);
	public Vector2 lastCheckpoint = new Vector2(0,0);
	public Character player;
	public int index = 0;
	public int width = 0;
	public int sizeP = 0;
	public String filePath = null;
	public boolean switching = false;
	public boolean pause = false;
	
	public Level(int sizex, int sizey, int sizep){
		panels = new Panel[sizex][sizey];
		size = new Vector2(sizex, sizey);
		sizeP = sizep;
	}
	
	public void resetPanels(){
		for(int lx=0;lx<size.x;lx++)
			for(int ly=0;ly<size.y;ly++)
				panels[lx][ly] = new Panel(sizeP,true);
	}

	@SuppressWarnings("resource")
	public int importFromFile(String source, boolean game){
		FileReader input = null;
		try {
			input = new FileReader(source);
		} catch (FileNotFoundException e) {return -1;}
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		String[] values = new String[6];
		try {
			while ( (myLine = bufRead.readLine()) != null){ 
				myLine += ",0";
				values = myLine.split(",");
				if(Integer.parseInt(values[4])==-1){
					//this.size.x = Double.parseDouble(values[0]);
					//this.size.y = Double.parseDouble(values[1]);
				}else if(Integer.parseInt(values[5])==1){
					for(int t=0;t<360;t+=5){
						for(int r=0;r<Integer.parseInt(values[3])*sizeP;r+=sizeP){
							int sx = (int)(Double.parseDouble(values[0])*((double)sizeP/20));
							int sy = (int)(Double.parseDouble(values[1])*((double)sizeP/20));
							sx += r*Math.cos(Math.toRadians(t));
							sy += r*Math.sin(Math.toRadians(t));
							try{
							int x = sx/sizeP;
							int y = sy/sizeP;
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
							}catch(java.lang.ArrayIndexOutOfBoundsException ee){}
						}
					}
				}else{
					for(int x = Integer.parseInt(values[0]); x<Integer.parseInt(values[2]); x++)
						for(int y = Integer.parseInt(values[1]); y<Integer.parseInt(values[3]); y++){
							if(x<size.x&&y<size.y){
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
						}
					if(Integer.parseInt(values[4])==4){
						origin.x = Integer.parseInt(values[0])*((double)sizeP/20);
						origin.y = Integer.parseInt(values[1])*((double)sizeP/20);
						ending.x = Integer.parseInt(values[2])*((double)sizeP/20);
						ending.y = Integer.parseInt(values[3])*((double)sizeP/20);
					}else if(Integer.parseInt(values[4])==5&&Integer.parseInt(values[0])<size.x&&Integer.parseInt(values[1])<size.y){
						panels[Integer.parseInt(values[0])][Integer.parseInt(values[1])].check = true;
					}else if(Integer.parseInt(values[4])==6&&Integer.parseInt(values[0])<size.x&&Integer.parseInt(values[1])<size.y){
						panels[Integer.parseInt(values[0])][Integer.parseInt(values[1])].check = false;
					}						
				}
				//System.out.println(faceVerticies[0] + " : " + faceVerticies[1] + " : " + faceVerticies[2]);
			}
		} catch (IOException|java.lang.NullPointerException e) {}
		int xpos = (int)(ending.x/sizeP);
		int ypos = (int)(ending.y/sizeP);
		for(int xi = -3;xi<2;xi++)
			for(int yi = -3;yi<2;yi++)
				panels[(int)Math.max(0,Math.min(xpos+xi,size.x-1))][(int)Math.max(0,Math.min(ypos+yi,size.y-1))].ending=true;
		if(game==true)
			calculateDisco();
		return 0;
	}
	public void resetPlayer(){
		if(lastCheckpoint.x==0&&lastCheckpoint.y==0)
			player.pos = origin;
		else
			player.pos = lastCheckpoint;
		player.vel.reset();
		player.resetTail();
		player.tension = false;
		player.health = 0;
		player.timeLeft = 1;
	}
	public void nextLevel(int size1, boolean game){
		switching = true;
		index++;
		for(int lx=0;lx<this.size.x;lx++)
			for(int ly=0;ly<this.size.y;ly++){
				panels[lx][ly].exist = true;
				panels[lx][ly].check = false;
				panels[lx][ly].color = Color.WHITE;
				panels[lx][ly].passed = false;
				panels[lx][ly].ending = false;
				panels[lx][ly].disco = false;
			}
		
		if(importFromFile(filePath + "level"+index+".txt",game)==-1)
			index = 1;
		importFromFile(filePath + "level"+index+".txt",game);
			
		lastCheckpoint.reset();
		player.pos.reset(origin.x,origin.y);
		player.tension = false;
		player.vel.reset();
		player.resetTail();
		player.timeLeft = 1;
		switching = false;
	}
	public void resetLevel(boolean game){
		switching = true;
		for(int lx=0;lx<this.size.x;lx++)
			for(int ly=0;ly<this.size.y;ly++){
				panels[lx][ly].exist = true;
				panels[lx][ly].check = false;
				panels[lx][ly].color = Color.WHITE;
				panels[lx][ly].passed = false;
				panels[lx][ly].ending = false;
				panels[lx][ly].disco = false;
			}
		importFromFile(filePath + "level"+index+".txt",game);
		//origin.reset();
		//ending.reset();
		lastCheckpoint.reset();
		player.pos.reset(origin.x,origin.y);
		player.tension = false;
		player.vel.reset();
		player.resetTail();
		player.timeLeft = 1;
		switching = false;
		pause = false;
	}
	
	public void calculateDisco(){
		for(int x=0;x<this.size.x;x++)
			for(int y=0;y<this.size.y;y++){
				boolean count = false;
				if(x>1&&y>1&&x<size.x-2&&y<size.y-2){
					int ix = -2;
					int iy = -2;
					while(count==false&&iy<=2){
						if(panels[x+ix][y+iy].exist==false||(panels[x+ix][y+iy].color!=Color.WHITE&&panels[x+ix][y+iy].disco==false))
							count = true;
						ix++;
						if(ix>2){
							ix=-2;
							iy++;
						}
					}
				}
				panels[x][y].disco = !count;
				if(panels[x][y].disco==true)
					panels[x][y].color = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
			}
	}
}
