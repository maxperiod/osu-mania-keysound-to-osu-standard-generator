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
	
	private static String inputFolderPath = "E:/osu!/Songs/_import feel the o2jam/BeautifulDay  ImpactLine+NF - Feel The O2Jam! (Max Period) [Test HX].osu"; 
	private static String outputFolderPath = "E:/osu!/Songs/_import feel the o2jam java test";

	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		File sourceOsuFile = new File(inputFolderPath);
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
			
			String[] files = folder.list();
			for (String file: files)
				if (file.endsWith(".ogg") || file.endsWith(".wav"))
					keysounds.add(file);
				//System.out.println(file);
				
			BufferedReader br = new BufferedReader(new FileReader(sourceOsuFile));
			
			String section = "";
			
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
			
			for (Note note: notes){
				if (keysoundCombinationAtTime.containsKey(note.time)){
					keysoundCombinationAtTime.get(note.time).second = note.keysound;
					keysoundCombinationAtTime.get(note.time).rearrangeValuesSmallerFirst();
				}
				else {
					IntegerPair uniqueKeysoundCombination = new IntegerPair();
					uniqueKeysoundCombination.first = note.keysound;
					uniqueKeysoundCombination.second = -1;
					
					
					
					keysoundCombinationAtTime.put(note.time, uniqueKeysoundCombination);
					
				}
			}
			
			
			Set<Integer> keys = keysoundCombinationAtTime.keySet();
			for (int key: keys){
				IntegerPair pair = keysoundCombinationAtTime.get(key);
				System.out.println(key + ": " + pair.first + " / " + pair.second);
				uniqueKeysoundCombinations.add(pair);
				
				
			}
			/*
			for (IntegerPair uniqueKeysoundCombination: uniqueKeysoundCombinations){
				System.out.println(uniqueKeysoundCombination.first + " / " + uniqueKeysoundCombination.second);
			}
			*/
			for (IntegerPair uniqueKeysoundCombination: uniqueKeysoundCombinations){
				//System.out.println(uniqueKeysoundCombination.first + " / " + uniqueKeysoundCombination.second);
				uniqueKeysoundCombinationsNumbered.add(uniqueKeysoundCombination);
			}
			
			for (int i = 0; i < uniqueKeysoundCombinationsNumbered.size(); i ++){
				System.out.println(i + ": " + uniqueKeysoundCombinationsNumbered.get(i).first + " / " + uniqueKeysoundCombinationsNumbered.get(i).second);
			}
			//int i = 100;
			//for (IntegerPair uniqueKeysoundCombination: uniqueKeysoundCombinations){
			
			for (int i = 0; i < uniqueKeysoundCombinationsNumbered.size(); i ++){
				//System.out.println(i + ": " + uniqueKeysoundCombinationsNumbered.get(i).first + " / " + uniqueKeysoundCombinationsNumbered.get(i).second);
				//IntegerPair pair = uniqueKeysoundCombinationsNumbered.get(i);
				
				int primaryKeysound = uniqueKeysoundCombinationsNumbered.get(i).first;
				int additionKeysound = uniqueKeysoundCombinationsNumbered.get(i).second;
				
				StringBuilder sbSrc = new StringBuilder();
				sbSrc.append(folder.getAbsolutePath());
				sbSrc.append('\\');
				sbSrc.append(keysounds.get(primaryKeysound));
				

				
				StringBuilder sbDst = new StringBuilder();
				sbDst.append(outputFolder.getAbsolutePath());
				sbDst.append('\\');
				sbDst.append("normal-hitnormal");
				sbDst.append(i);
				sbDst.append(".ogg");								
				
				System.out.println(sbSrc + " -> " + sbDst.toString());
				
				Files.copy(Paths.get(sbSrc.toString()), Paths.get(sbDst.toString()));
				
				StringBuilder sbSrcAddition = new StringBuilder();
				if (additionKeysound != -1){
					sbSrcAddition.append(folder.getAbsolutePath());
					sbSrcAddition.append('\\');
					sbSrcAddition.append(keysounds.get(additionKeysound));
					
					StringBuilder sbDstAddition = new StringBuilder();
					sbDstAddition.append(outputFolder.getAbsolutePath());
					sbDstAddition.append('\\');
					sbDstAddition.append("normal-hitclap");
					sbDstAddition.append(i);
					sbDstAddition.append(".ogg");
					
					Files.copy(Paths.get(sbSrcAddition.toString()), Paths.get(sbDstAddition.toString()));
					System.out.println(sbSrcAddition + " -> " + sbDstAddition.toString());
				}
								
				
				
			}
			
			for (int key: keys){
				StringBuilder sb = new StringBuilder();
				sb.append(key);
				sb.append(",-100,4,1,");
				
				IntegerPair pair = keysoundCombinationAtTime.get(key);				
				sb.append(uniqueKeysoundCombinationsNumbered.indexOf(pair));
				
				sb.append(",70,0,0");
				System.out.println(sb.toString());
			}
			
		}
			

			
			
		
	}

}
