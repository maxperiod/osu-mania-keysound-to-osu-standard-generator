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

public class test {
	
	private static String inputKeysoundOsuFile = "E:\\osu!\\Songs\\zl o2jam\\Dr.Flowershirts - zl (Max Period) [keysound].osu"; 
	private static String outputFolderPath = "E:\\osu!\\Songs\\zl o2jam - Copy";

	private static boolean COPY_KEYSOUND_FILES = true; 
	
	private static int STARTING_HITSOUND_SET_NUMBER_OFFSET = 1;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
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
			
			List<Note> notes = new ArrayList<Note>();
			Map<Integer, IntegerPair> keysoundCombinationAtTime = new TreeMap<Integer, IntegerPair>();
			Set<IntegerPair> uniqueKeysoundCombinations = new TreeSet<IntegerPair>();
			List<IntegerPair> uniqueKeysoundCombinationsNumbered = new ArrayList<IntegerPair>();
			Set<Integer> sliderStartTimestamps = new TreeSet<Integer>();
			
			String[] files = folder.list();
			for (String file: files)
				if (file.endsWith(".ogg") || file.endsWith(".wav"))
					keysounds.add(file);
				//System.out.println(file);
				
			BufferedReader br = new BufferedReader(new FileReader(sourceOsuFile));
			
			String section = "";
			
			//Read osumania keysound map notes
			while(true){
				String line = br.readLine();
				if (line == null) break;
				if (line.matches("\\[.*\\]"))
					section = line;
				
				else if (section.equals("[HitObjects]")){
					if (line.isEmpty()) break;
					//String[] lineEntries = line.split(",");					
					
					Note note = new Note(line, keysounds);
					if (note.column == 1 || note.column == 2)
						notes.add(note);
				}
			}
			br.close();
			
			//Make list of distinctive combination of keysounds
			for (Note note: notes){
				if (keysoundCombinationAtTime.containsKey(note.time)){
					keysoundCombinationAtTime.get(note.time).second = note.keysound;
					keysoundCombinationAtTime.get(note.time).rearrangeValuesSmallerFirst();
				}
				else {
					IntegerPair uniqueKeysoundCombination = new IntegerPair();
					uniqueKeysoundCombination.first = note.keysound;
					
					if (note.noteType == 128) uniqueKeysoundCombination.second = -2;
					else uniqueKeysoundCombination.second = -1;
										
					
					keysoundCombinationAtTime.put(note.time, uniqueKeysoundCombination);
					
				}
			}
			
			//Make list of slider starts
			for (Note note: notes){
				if ((note.column == 1 || note.column == 2) && note.noteType == 128)
					sliderStartTimestamps.add(note.time);
			}
			
			//Associate timestamps with which set of keysound combination
			Set<Integer> keys = keysoundCombinationAtTime.keySet();
			for (int key: keys){
				IntegerPair pair = keysoundCombinationAtTime.get(key);
				
				
				if (pair.second != -1) {
					System.out.println(key + ": " + pair.first + " / " + pair.second);
					uniqueKeysoundCombinations.add(pair);
				}
				
				
			}
			
			
			/*
			for (IntegerPair uniqueKeysoundCombination: uniqueKeysoundCombinations){
				System.out.println(uniqueKeysoundCombination.first + " / " + uniqueKeysoundCombination.second);
			}
			*/			
			

			//Assign set number to each distinct keysound combinations
			for (IntegerPair uniqueKeysoundCombination: uniqueKeysoundCombinations){
				//System.out.println(uniqueKeysoundCombination.first + " / " + uniqueKeysoundCombination.second);
				uniqueKeysoundCombinationsNumbered.add(uniqueKeysoundCombination);
			}
			
			
			Set<Integer> numberedKeysoundCombinationWithSliders = new TreeSet<Integer>();
			
			for (int i = 0; i < uniqueKeysoundCombinationsNumbered.size(); i ++){//IntegerPair keysoundCombination: uniqueKeysoundCombinationsNumbered){
				IntegerPair keysoundCombination = uniqueKeysoundCombinationsNumbered.get(i);
				
				boolean isSlider = false;
				for (int timestamp: sliderStartTimestamps){
					if (keysoundCombination == keysoundCombinationAtTime.get(timestamp)) isSlider = true;
				}
				if (isSlider){
					System.out.println("KS combination " + i + "is slider");
					numberedKeysoundCombinationWithSliders.add(i);
				}
				
			}
			
			
			for (int i = 0; i < uniqueKeysoundCombinationsNumbered.size(); i ++){
				System.out.println(i + ": " + uniqueKeysoundCombinationsNumbered.get(i).first + " / " + uniqueKeysoundCombinationsNumbered.get(i).second);
			}
			//int i = 100;
			//for (IntegerPair uniqueKeysoundCombination: uniqueKeysoundCombinations){
			
			//Copy keysound files for each distinct keysound combination as a new custom hitsound set
			for (int i = 0; i < uniqueKeysoundCombinationsNumbered.size(); i ++){
				//System.out.println(i + ": " + uniqueKeysoundCombinationsNumbered.get(i).first + " / " + uniqueKeysoundCombinationsNumbered.get(i).second);
				//IntegerPair pair = uniqueKeysoundCombinationsNumbered.get(i);
				
				int primaryKeysound = uniqueKeysoundCombinationsNumbered.get(i).first;
				int additionKeysound = uniqueKeysoundCombinationsNumbered.get(i).second;
				
				if (additionKeysound == -1) continue;
				
				//Copy keysounds to basic slot of hitsound set
				StringBuilder sbSrc = new StringBuilder();
				sbSrc.append(folder.getAbsolutePath());
				sbSrc.append('\\');
				sbSrc.append(keysounds.get(primaryKeysound));
				

				
				StringBuilder sbDst = new StringBuilder();
				sbDst.append(outputFolder.getAbsolutePath());
				sbDst.append('\\');
				sbDst.append("normal-hitnormal");
				if (i + STARTING_HITSOUND_SET_NUMBER_OFFSET != 1) sbDst.append(i + STARTING_HITSOUND_SET_NUMBER_OFFSET);
				sbDst.append(".ogg");								
				
				System.out.println(sbSrc + " -> " + sbDst.toString());
				
				if (COPY_KEYSOUND_FILES) Files.copy(Paths.get(sbSrc.toString()), Paths.get(sbDst.toString()));
				
				//Copy addition keysounds to clap slot of hitsound set
				
				if (additionKeysound != -1 && additionKeysound != -2){
					StringBuilder sbSrcAddition = new StringBuilder();
					sbSrcAddition.append(folder.getAbsolutePath());
					sbSrcAddition.append('\\');
					sbSrcAddition.append(keysounds.get(additionKeysound));
					
					StringBuilder sbDstAddition = new StringBuilder();
					sbDstAddition.append(outputFolder.getAbsolutePath());
					sbDstAddition.append('\\');
					sbDstAddition.append("normal-hitclap");
					if (i + STARTING_HITSOUND_SET_NUMBER_OFFSET != 1) sbDstAddition.append(i + STARTING_HITSOUND_SET_NUMBER_OFFSET);
					sbDstAddition.append(".ogg");
					
					if (COPY_KEYSOUND_FILES) Files.copy(Paths.get(sbSrcAddition.toString()), Paths.get(sbDstAddition.toString()));
					else System.out.println(sbSrcAddition + " -> " + sbDstAddition.toString());
				}
								
				//If this numbered distinct kdysound combination has slider
				if (numberedKeysoundCombinationWithSliders.contains(i)){
					StringBuilder sbSrcSliderEnd = new StringBuilder();
					sbSrcSliderEnd.append(folder.getAbsolutePath());
					sbSrcSliderEnd.append('\\');
					sbSrcSliderEnd.append("blank.wav");
					
					StringBuilder sbDstSliderEnd = new StringBuilder();
					sbDstSliderEnd.append(outputFolder.getAbsolutePath());
					sbDstSliderEnd.append('\\');
					sbDstSliderEnd.append("drum-hitnormal");
					if (i + STARTING_HITSOUND_SET_NUMBER_OFFSET != 1) sbDstSliderEnd.append(i + STARTING_HITSOUND_SET_NUMBER_OFFSET);
					sbDstSliderEnd.append(".wav");
					
					if (COPY_KEYSOUND_FILES) Files.copy(Paths.get(sbSrcSliderEnd.toString()), Paths.get(sbDstSliderEnd.toString())); 
					else System.out.println(sbSrcSliderEnd + " -> " + sbDstSliderEnd.toString());
					
					StringBuilder sbDstSliderSlide = new StringBuilder();
					sbDstSliderSlide.append(outputFolder.getAbsolutePath());
					sbDstSliderSlide.append('\\');
					sbDstSliderSlide.append("normal-sliderslide");
					if (i + STARTING_HITSOUND_SET_NUMBER_OFFSET != 1) sbDstSliderSlide.append(i + STARTING_HITSOUND_SET_NUMBER_OFFSET);
					sbDstSliderSlide.append(".wav");
					
					if (COPY_KEYSOUND_FILES) Files.copy(Paths.get(sbSrcSliderEnd.toString()), Paths.get(sbDstSliderSlide.toString()));
					else System.out.println(sbSrcSliderEnd + " -> " + sbDstSliderSlide.toString());
					
					StringBuilder sbDstSliderTick = new StringBuilder();
					sbDstSliderTick.append(outputFolder.getAbsolutePath());
					sbDstSliderTick.append('\\');
					sbDstSliderTick.append("normal-slidertick");
					if (i + STARTING_HITSOUND_SET_NUMBER_OFFSET != 1) sbDstSliderTick.append(i + STARTING_HITSOUND_SET_NUMBER_OFFSET);
					sbDstSliderTick.append(".wav");
					
					if (COPY_KEYSOUND_FILES) Files.copy(Paths.get(sbSrcSliderEnd.toString()), Paths.get(sbDstSliderTick.toString()));
					else System.out.println(sbSrcSliderEnd + " -> " + sbDstSliderTick.toString());
				}
				
			}
			
			System.out.println("Generate inheriting timing points");
			//Generate inheriting timing points
			for (int key: keys){

				
				IntegerPair pair = keysoundCombinationAtTime.get(key);	
				
				if (pair.second == -1) continue;
				
				StringBuilder sb = new StringBuilder();
				sb.append(key);
				sb.append(",-100,4,1,");
				
				sb.append(uniqueKeysoundCombinationsNumbered.indexOf(pair) + STARTING_HITSOUND_SET_NUMBER_OFFSET);
				
				sb.append(",70,0,0");
				System.out.println(sb.toString());
			}
			
			
			System.out.println("generate notes");
			//Generate notes
			for (int key: keys){

				
				IntegerPair pair = keysoundCombinationAtTime.get(key);	
				
				//if (pair.second == -1) continue;
				
				StringBuilder sb = new StringBuilder();
				
				int x = key / 10 % 512;
				int y = key / 384 % 384;
				sb.append(x);
				sb.append(',');
				sb.append(y);
				sb.append(',');
				sb.append(key);
				sb.append(',');
				
				boolean isSlider = false;
				/*
				for (Note note: notes){
					if (note.time == key && note.noteType == 128) isSlider = true;
				}
				*/
				if (sliderStartTimestamps.contains(key)) isSlider = true;
				
				if (isSlider){
					sb.append(2);
					
					sb.append(',');
					if (pair.second == -2) sb.append(1);
					else sb.append(8);
					sb.append(',');
					sb.append("L|");
					sb.append(x + 50);
					sb.append(':');
					sb.append(y);
					sb.append(',');
					sb.append(1);
					sb.append(',');
					sb.append(80);
					sb.append(',');
					sb.append("0|0");
					sb.append(',');
					sb.append("0:0|3:0");
					sb.append(',');
					sb.append("1:1:0:0:");
				} 
				else {
					sb.append(1);
					
					sb.append(',');
					if (pair.second == -1) sb.append(1);
					else sb.append(8);
					sb.append(',');
					
					
					sb.append("1:1:0:0:");
					if (pair.second == -1)
						sb.append(keysounds.get(keysoundCombinationAtTime.get(key).first));
				}
				
				//sb.append(",-100,4,1,");
				
				
				//sb.append(uniqueKeysoundCombinationsNumbered.indexOf(pair));
				
				//sb.append(",70,0,0");
				System.out.println(sb.toString());
			}
			
		}
			

			
			
		
	}

}
