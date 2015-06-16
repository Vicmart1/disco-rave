package DiscoRave;

import jaco.mp3.player.MP3Player;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BGMusic {
    static MP3Player player = new MP3Player();
	public List<File> songs = new ArrayList<File>();
	public int[] BPM;
	public int currentBPM;
	public int index = 0;
	public int levels = 0;
	
	public BGMusic(String sourceFolder){
		File folder = new File(sourceFolder);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles)
			if(file.getName().substring(file.getName().length()-3, file.getName().length()).equalsIgnoreCase("mp3")==true)
				songs.add(file);
		BPM = new int[songs.size()];
		
		System.gc();
		FileReader input = null;
		try {
			input = new FileReader(sourceFolder + "BPMvalues.txt");
		} catch (FileNotFoundException e) { e.printStackTrace();}
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		String[] values = new String[2];
		try {
			while ( (myLine = bufRead.readLine()) != null){ 
				values = myLine.split(",");
				int idx = Integer.parseInt(values[0]);
				int tempBPM = Integer.parseInt(values[1]);
				BPM[idx] = tempBPM;
			}
		} catch (IOException|java.lang.NullPointerException e) {}
		
		index = (int)(Math.random()*songs.size());
		currentBPM = BPM[index];
		player.addToPlayList(songs.get(index));
		player.setRepeat(true);
		player.play();
	}
	
	public void nextSong(){
		index++;
		if(index>=songs.size())
			index = 0;
		levels = 0;
		
		currentBPM = BPM[index];
		player.stop();
		player = null;
		player = new MP3Player();
		player.addToPlayList(songs.get(index));
		player.setRepeat(true);
		player.play();
	}
	
	public void playSong(int song){
		index = Math.max(0, Math.min(song, songs.size()-1));
		levels = 0; 
		
		currentBPM = BPM[index];
		player.stop();
		player = null;
		player = new MP3Player();
		player.addToPlayList(songs.get(index));
		player.setRepeat(true);
		player.play();
	}
}
