package osukeysoundgreentiming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ManiaColumnFunctions {
	
	public static int columnToX(int column, int numColumns){
		return (int)(512f / numColumns * column + 512f / (numColumns * 2));
	}
	
	public static int xToColumn(int x, int numColumns){
		for (int i = 0; i < numColumns; i ++){
			if (columnToX(i, numColumns) == x) return i;
			
		}
		return -1;
	}
	
	public static int getKeyCount(Beatmap beatmap){
		return beatmap.difficultyFields.get("CircleSize").intValue();
	}
	
	public static Map<Integer, ArrayList<Note>> getManiaNotesAtTimes(Beatmap beatmap){
		int numKeys = getKeyCount(beatmap);//.difficultyFields.get("CircleSize").intValue();
		Map<Integer, ArrayList<Note>> notesAtTimes = new TreeMap<Integer, ArrayList<Note>>();
		for (Note note: beatmap.notes){
			if (notesAtTimes.containsKey(note.time)){
				notesAtTimes.get(note.time).set(ManiaColumnFunctions.xToColumn(note.x, numKeys), note);
			}
			else {
				ArrayList<Note> notesAtTime = new ArrayList<Note>();		
				for (int i = 0; i < numKeys; i ++) notesAtTime.add(null);
				
				notesAtTime.set(ManiaColumnFunctions.xToColumn(note.x, numKeys), note);
				
				notesAtTimes.put(note.time, notesAtTime);
			}
		}
		return notesAtTimes;
	}
}
