package DiscoRave;

import java.util.ArrayList;
import java.util.List;

public class AllNotifications {
	public List<Notification> all = new ArrayList<Notification>();
	public int activeMessages = -1;
	
	public AllNotifications(){ 
		
	}
	public void iterate(int boxwidth){
		for(int n=0;n<all.size();n++){
			if(all.get(n).active==true){
				if(all.get(n).dir==0)
					all.get(n).appear(boxwidth);
				else if(all.get(n).dir==1)
					all.get(n).disappear(boxwidth);
				else{
					activeMessages--;
					all.get(n).active=false;
				}
			}
		}
	}
	public void activateMessage(int index, String[] texts){
		try{
			if(all.get(index).active==false&&all.get(index).text.equals(texts[index])==true){
				activeMessages++;
				all.get(index).active = true;
			}
		}catch(java.lang.IndexOutOfBoundsException e){}
	}
	public void activateMessage(String textCompare){
		for(Notification not:all){
			if(not.text.equals(textCompare)&&not.active==false){
				activeMessages++;
				not.active=true;
			}
		}
	}
	public void activateMessage(int index, String name){
		try{
			if(all.get(index).active==false&&all.get(index).text.equals(name)==true){
				activeMessages++;
				all.get(index).active = true;
			}
		}catch(java.lang.IndexOutOfBoundsException e){}
	}
}
