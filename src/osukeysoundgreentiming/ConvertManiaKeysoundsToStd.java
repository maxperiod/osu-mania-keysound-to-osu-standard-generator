package osukeysoundgreentiming;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
	private static String inputKeysoundOsuFile = "E:\\osu!\\Songs\\blank\\BeautifulDay  Impact Line - Song of Pain (Max Period) [keysound work].osu"; 
	private static String outputFolderPath = "E:\\osu!\\Songs\\blank\\";

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
			List<IntegerPair> uniqueKeysoundCombinationsNumbered = new ArrayList<IntegerPair>();
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
			
			Map<Integer, ArrayList<Note>> notesAtTime = ManiaColumnFunctions.getManiaNotesAtTimes(beatmap);
			
			beatmap.notes.clear();
			
			
			
			Set<Integer> keyset;
			
			keyset = notesAtTime.keySet();
			for (Integer key: keyset){
				
				ArrayList<Note> row = notesAtTime.get(key);
				if (row.get(0) == null && row.get(1) != null){
					Note temp = row.get(0);
					notesAtTime.get(key).set(0, row.get(1));
					notesAtTime.get(key).set(1, temp);
				}
				
				Note column1 = notesAtTime.get(key).get(0); 
				Note column2 = notesAtTime.get(key).get(1);							
				
				String noteKeysound = null;
				String additionKeysound = null;
				
				boolean column1IsSlider = column1 != null && column1.flags == 128 ? true : false;
				boolean column2IsSlider = column2 != null && column2.flags == 128 ? true : false;
				
				if (column1 != null){
					int keysoundSubColumn = column1IsSlider ? 5 : 4;
					String[] keysoundValues = column1.addition.split(":");
					noteKeysound = keysoundValues[keysoundSubColumn];
					
					if (column1.flags == 128){
						sliderStartTimestamps.add(key);
						sliderEndTimestamps.add(Integer.parseInt(keysoundValues[0]));
					}
				}
				if (column2 != null){
					int keysoundSubColumn = column2IsSlider ? 5 : 4;
					String[] keysoundValues = column2.addition.split(":");
					additionKeysound = keysoundValues[keysoundSubColumn];
					
					if (column2.flags == 128 && (column1 == null || column1.flags != 128)){
						sliderStartTimestamps.add(key);
						sliderEndTimestamps.add(Integer.parseInt(keysoundValues[0]));
					}
				}
				
				if (column1IsSlider || column2IsSlider || (noteKeysound != null && additionKeysound != null) || sliderEndTimestamps.contains(key)){
					IntegerPair keysoundPair = new IntegerPair();
					keysoundPair.first = keysounds.indexOf(noteKeysound);
					keysoundPair.second = keysounds.indexOf(additionKeysound);
					keysoundPair.rearrangeValuesSmallerFirst();
									
					keysoundCombinationAtTime.put(key, keysoundPair); 
					uniqueKeysoundCombinations.add(keysoundPair);
					
					if (column1IsSlider || column2IsSlider){
						Slider slider = new Slider();
						slider.x = key / 10 % 512;
						slider.y = key / 384 % 384;
						slider.time = key;
						slider.flags = 2;
						
						StringBuilder sb = new StringBuilder();
						
						sb.append("L|");
						sb.append(slider.x + 50);
						sb.append(':');
						sb.append(slider.y); 
						slider.sliderShape = sb.toString();		
								
						IntegerPair edgeAddition1 = new IntegerPair();
						edgeAddition1.first = 1;
						edgeAddition1.second = 1;
						slider.edgeAddition1 = edgeAddition1;
						
						IntegerPair edgeAddition2 = new IntegerPair();
						edgeAddition2.first = 3;
						edgeAddition2.second = 0;
						slider.edgeAddition2 = edgeAddition2;
						
						slider.repeat = 1;
						slider.pixelLength = 80;
						
						IntegerPair edgeHitsound = new IntegerPair();
						if (additionKeysound != null){
							slider.hitsound = 8;							
							edgeHitsound.first = 8;
							edgeHitsound.second = 0;						
						}
						else {
							slider.hitsound = 1;							
							edgeHitsound.first = 1;
							edgeHitsound.second = 0;							
						}
						slider.edgeHitsound = edgeHitsound;
						slider.addition = "1:1:0:0";
						
						beatmap.notes.add(slider);
		
					}
				}
				if (!column1IsSlider && !column2IsSlider) {
					Note circle = new Note();
					circle.x = key / 10 % 512;
					circle.y = key / 384 % 384;
					circle.time = key;
					circle.flags = 1;
					circle.hitsound = additionKeysound == null ? 1 : 8;
					
					StringBuilder sb = new StringBuilder();
					sb.append("1:1:0:0:");
					if (additionKeysound == null) sb.append(noteKeysound);
					circle.addition = sb.toString();
					beatmap.notes.add(circle);
				}
				/*
				if (column1IsSlider || column2IsSlider){
					Note note = new Note();
					
					//beatmap.notes.
				}
				*/
			}
			
			//for (IntegerPair pair: uniqueKeysoundCombinations) System.out.println(pair.first + ", " + pair.second);
			
			
			

			
			for (IntegerPair pair: uniqueKeysoundCombinations){
				
				uniqueKeysoundCombinationsNumbered.add(pair);
				//System.out.println(pair.first + ", " + pair.second);
			}
			
			keyset = keysoundCombinationAtTime.keySet(); 
			for (int key: keyset){
				TimingPoint timingPoint = new TimingPoint();
				timingPoint.time = key;
				timingPoint.timePerBeat = -100;
				timingPoint.meter = 4;				
				timingPoint.sampleType = 1;
				timingPoint.sampleSet = uniqueKeysoundCombinationsNumbered.indexOf(keysoundCombinationAtTime.get(key)) + STARTING_HITSOUND_SET_NUMBER_OFFSET;
				timingPoint.volume = 70;
				timingPoint.inherited = 0;
				timingPoint.kiai = 0;
				beatmap.timingPoints.add(timingPoint);
			}
									
			/*			  	
				IntegerPair pair = keysoundCombinationAtTime.get(key);	
				
				if (pair.second == -1) continue;
				
				StringBuilder sb = new StringBuilder();
				sb.append(key);
				sb.append(",-100,4,1,");
				
				sb.append(uniqueKeysoundCombinationsNumbered.indexOf(pair) + STARTING_HITSOUND_SET_NUMBER_OFFSET);
				
				sb.append(",70,0,0");
				System.out.println(sb.toString());
			 */
			
			Set<Integer> numberedKeysoundCombinationWithSliders = new TreeSet<Integer>();
			
			for (int i = 0; i < uniqueKeysoundCombinationsNumbered.size(); i ++){//IntegerPair keysoundCombination: uniqueKeysoundCombinationsNumbered){
				IntegerPair keysoundCombination = uniqueKeysoundCombinationsNumbered.get(i);
				
				boolean isSlider = false;
				for (int timestamp: sliderStartTimestamps){
					if (keysoundCombination.equals(keysoundCombinationAtTime.get(timestamp))) isSlider = true;
					//if (keysoundCombination == keysoundCombinationAtTime.get(timestamp)) isSlider = true;
				}
				if (isSlider){
					System.out.println("KS combination " + i + "is slider");
					numberedKeysoundCombinationWithSliders.add(i);
				}
				
			}
			
			for (int i = 0; i < uniqueKeysoundCombinationsNumbered.size(); i ++){
				//System.out.println(i + ": " + uniqueKeysoundCombinationsNumbered.get(i).first + " / " + uniqueKeysoundCombinationsNumbered.get(i).second);
				//IntegerPair pair = uniqueKeysoundCombinationsNumbered.get(i);
				
				int primaryKeysound = uniqueKeysoundCombinationsNumbered.get(i).first;
				int additionKeysound = uniqueKeysoundCombinationsNumbered.get(i).second;
				
				//if (additionKeysound == -1) continue;
				
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
			
			//System.out.println(beatmap.toString());
			
			beatmap.generalFields.put("Mode", "0");
	
			beatmap.metadataFields.put("Version", "Generated from " + beatmap.metadataFields.get("Version"));
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolderPath + '\\' + "output.osu"));
			bw.write(beatmap.toString());
			bw.close();
		}
		

			
			
		
	}

}
