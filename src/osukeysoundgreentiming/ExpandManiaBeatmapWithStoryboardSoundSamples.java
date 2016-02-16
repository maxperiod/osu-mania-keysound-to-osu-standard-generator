package osukeysoundgreentiming;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ExpandManiaBeatmapWithStoryboardSoundSamples {
	

	
	private static final String inputFile = "E:\\osu!\\Songs\\_import crazy go go\\Super Station  Impact Line - Crazy Go Go (Max Period) [Lv.13].osu";
	private static final String outputFile = "E:\\osu!\\Songs\\_import crazy go go\\test.osu";
	
	public static void expandAndMoveStoryboardHitsoundsToMap(Beatmap beatmap, int numColumns){
		
	
	
	
		//final int NUM_COLUMNS = 18;
		
		final int MANIA_Y_VALUE = 192;
		
		
		
		Map<Integer, List<Note>> notesAtTimes = ManiaColumnFunctions.getManiaNotesAtTimes(beatmap);//new TreeMap<Integer, List<Note>>();
		
		int numKeys = beatmap.difficultyFields.get("CircleSize").intValue();
		
		
		Set<Integer> keyset;
				
		
		keyset = notesAtTimes.keySet();
		
		for (int key: keyset){
			for (int i = numKeys; i < numColumns; i ++) 
				notesAtTimes.get(key).add(null);
		}

		
		for (int i = 0; i < beatmap.storyboardSoundSamples.size(); i ++){
			StoryboardSoundSample sample = beatmap.storyboardSoundSamples.get(i);
			
			if (!notesAtTimes.containsKey(sample.time)){
				List<Note> notesAtTime = new ArrayList<Note>();		
				for (int j = 0; j < numColumns; j ++) notesAtTime.add(null);
				notesAtTimes.put(sample.time, notesAtTime);
			}
			
			for (int j = numKeys; j < numColumns; j ++){
				if (notesAtTimes.get(sample.time).get(j) == null){
					Note note = new Note();
					note.x = ManiaColumnFunctions.columnToX(j, numColumns);
					note.y = MANIA_Y_VALUE;
					note.flags = 1;
					note.time = sample.time;
					note.hitsound = 0;
					note.addition = "0:0:0:0:" + sample.audioFile.replace("\"", "");
					notesAtTimes.get(sample.time).set(j, note);
					break;
				}
			}
			//beatmap.storyboardSoundSamples.
		}
		
		beatmap.notes.clear();
		beatmap.storyboardSoundSamples.clear();
		//beatmap.difficultyFields.remove("CircleSize");
		beatmap.difficultyFields.put("CircleSize", (float)numColumns);
		
		keyset = notesAtTimes.keySet();
		for (int key: keyset){
			//System.out.println(key + ": " + notesAtTimes.get(key).size());
			
			List<Note> notesAtTime = notesAtTimes.get(key);
			
			for (int i = 0; i < numColumns; i ++){
				Note note = notesAtTime.get(i);
				
				if (note != null){
					Note noteWithNewX = new Note();
					noteWithNewX.x = ManiaColumnFunctions.columnToX(i, numColumns);
					noteWithNewX.y = note.y;
					noteWithNewX.time = note.time;
					noteWithNewX.flags = note.flags;
					noteWithNewX.hitsound = note.hitsound;
					noteWithNewX.addition = note.addition;
					
					
					beatmap.notes.add(noteWithNewX);
				}
			}
				
		}

		String difficultyName = beatmap.metadataFields.get("Version"); 
		beatmap.metadataFields.put("Version", "Expanded from " + difficultyName);
	}
	
	public static void main(String[] args) throws IOException{	
		BeatmapLoader beatmapLoader = new BeatmapLoader();
		
		Beatmap beatmap = new Beatmap();
		beatmapLoader.loadBeatmap(beatmap, inputFile);
		
		expandAndMoveStoryboardHitsoundsToMap(beatmap, 18);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		bw.write(beatmap.toString());
		bw.close();
	}
}
