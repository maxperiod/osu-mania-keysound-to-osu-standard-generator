package osukeysoundgreentiming;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ConvertManiaKeysoundsToStd {
	private static String inputKeysoundOsuFile = "E:\\osu!\\Songs\\zl o2jam\\Dr.Flowershirts - zl (Max Period) [keysound sliders].osu"; 
	private static String outputFolderPath = "E:\\osu!\\Songs\\zl o2jam - Copy";

	private static boolean COPY_KEYSOUND_FILES = true; 
	
	private static int STARTING_HITSOUND_SET_NUMBER_OFFSET = 1;
	
	public static void main(String[] args) throws IOException {
		 
		final int MAX_HITSOUNDS_PER_NOTE = 2;
		
		File sourceOsuFile = new File(inputKeysoundOsuFile);
		File outputFolder = new File(outputFolderPath);
		
		System.out.println(sourceOsuFile.getAbsolutePath());
		
		File folder = new File(sourceOsuFile.getAbsolutePath().substring(0, sourceOsuFile.getAbsolutePath().lastIndexOf('\\')));			
		
		if (!folder.isDirectory())
			System.out.println(sourceOsuFile.getAbsolutePath() + "is not a directory");
		else if (!outputFolder.isDirectory())
			System.out.println(outputFolder.getAbsolutePath() + "is not a directory");
		else {
							
			List<String> keysounds = new ArrayList<String>();
			//List<RawHitObject> rawHitObjects = new ArrayList<RawHitObject>();
			
			//List<ProcessedNote> notes = new ArrayList<ProcessedNote>();
			//List<Pair<Integer, String>> storyboardHitsounds = new ArrayList<Pair<Integer, String>>();
			Map<Integer, IntegerPair> keysoundCombinationAtTime = new TreeMap<Integer, IntegerPair>();
			Set<IntegerPair> uniqueKeysoundCombinations = new TreeSet<IntegerPair>();
			//List<IntegerPair> uniqueKeysoundCombinationsNumbered = new ArrayList<IntegerPair>();
			Set<Integer> sliderStartTimestamps = new TreeSet<Integer>();
			Set<Integer> sliderEndTimestamps = new TreeSet<Integer>();
			//Map<Integer, Integer> sliderStartTimestamps = new TreeMap<Integer, Integer>();
			
			String[] files = folder.list();
			for (String file: files)
				if (file.endsWith(".ogg") || file.endsWith(".wav"))
					keysounds.add(file);
				//System.out.println(file);
				
			//BufferedReader br = new BufferedReader(new FileReader(sourceOsuFile));
			BeatmapLoader loader = new BeatmapLoader();
			
			Beatmap beatmap = new Beatmap();						
			
			loader.loadBeatmap(beatmap, inputKeysoundOsuFile); 
			
			ConstrictAndMoveExcessNotesToStoryboard.constrictAndMoveExcessNotesToStoryboard(beatmap, MAX_HITSOUNDS_PER_NOTE);
			
			Map<Integer, List<Note>> notesAtTime = ManiaColumnFunctions.getManiaNotesAtTimes(beatmap);
			
	
			
			Set<Integer> keyset;
			
			keyset = notesAtTime.keySet();
			for (Integer key: keyset){
				
				
				Note column1 = notesAtTime.get(key).get(0); 
				Note column2 = notesAtTime.get(key).get(1);
				
				String noteKeysound = null;
				String additionKeysound = null;
				if (column1 != null){
					int keysoundSubColumn = column1.flags == 128 ? 5 : 4;
					String[] keysoundValues = column1.addition.split(":");
					noteKeysound = keysoundValues[keysoundSubColumn];
					
					if (column1.flags == 128){
						sliderStartTimestamps.add(key);
						sliderEndTimestamps.add(key + Integer.parseInt(keysoundValues[0]));
					}
				}
				if (column2 != null){
					int keysoundSubColumn = column2.flags == 128 ? 5 : 4;
					String[] keysoundValues = column2.addition.split(":");
					additionKeysound = keysoundValues[keysoundSubColumn];
					
					if (column2.flags == 128 && (column1 == null || column1.flags != 128)){
						sliderStartTimestamps.add(key);
						sliderEndTimestamps.add(key + Integer.parseInt(keysoundValues[0]));
					}
				}
				
				IntegerPair keysoundPair = new IntegerPair();
				keysoundPair.first = keysounds.indexOf(noteKeysound);
				keysoundPair.second = keysounds.indexOf(additionKeysound);
				keysoundPair.rearrangeValuesSmallerFirst();
				
				keysoundCombinationAtTime.put(key, keysoundPair); 
				uniqueKeysoundCombinations.add(keysoundPair);
			}
			
			for (IntegerPair pair: uniqueKeysoundCombinations) System.out.println(pair.first + ", " + pair.second);
			
		}
		

			
			
		
	}

}
