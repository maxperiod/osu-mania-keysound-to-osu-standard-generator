package osukeysoundgreentiming;

import java.util.List;


public class Note {
	public int column = -1;
	public int time;
	public int sliderEndTime;
	public int noteType;
	public int keysound = -1;
	
	
	public Note(String line, List<String> keysounds){
		String[] components = line.split(",");
		
		int rawColumn = Integer.parseInt(components[0]);
		time = Integer.parseInt(components[2]);
		int rawNoteType = Integer.parseInt(components[3]);
		//int rawNoteLength = Integer.parseInt(components[4]);
		
		switch(rawColumn){
		case 36:
			column = 1;
			break;
		case 109:
			column = 2;
		}
		
		noteType = rawNoteType;
					
		//length = Integer.parseInt(components[4]);
		
		String[] keysoundComponents = components[5].split(":");
		
		if (noteType == 1 || noteType == 5){
			if (keysoundComponents.length == 5) 
				keysound = keysounds.indexOf(keysoundComponents[4]);
		}
		else if (noteType == 128){
			if (keysoundComponents.length == 6) 
				keysound = keysounds.indexOf(keysoundComponents[5]);
			sliderEndTime = Integer.parseInt(keysoundComponents[0]);
		}
		
		
	}

	@Override
	public String toString() {
		return "Note [column=" + column + ", time=" + time + ", noteType="
				+ noteType + ", keysound=" + keysound + "]";
	}
	
	
}
