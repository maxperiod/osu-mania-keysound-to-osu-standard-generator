package osukeysoundgreentiming;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BeatmapLoader {
	
	public void loadBeatmap(Beatmap beatmap, String file) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String section = "";
		
		while(true){
			String line = br.readLine();
			if (line == null) break;
			if (line.matches("\\[.*\\]"))
				section = line;
			
			else if (section.equals("[General]")){
				if (!line.isEmpty()){
					String[] lineEntries = line.split(": ");					
					beatmap.generalFields.put(lineEntries[0], lineEntries[1]);
				}
					
			}
			
			else if (section.equals("[Editor]")){
				if (!line.isEmpty()){
					String[] lineEntries = line.split(": ");					
					beatmap.editorFields.put(lineEntries[0], lineEntries[1]);
				}
					
			}
			
			else if (section.equals("[Metadata]")){
				if (!line.isEmpty()){
					String[] lineEntries = line.split(":");
					if (lineEntries.length == 1)
						beatmap.metadataFields.put(lineEntries[0], "");
					else
						beatmap.metadataFields.put(lineEntries[0], lineEntries[1]);
				}
					
			}
			
			else if (section.equals("[Difficulty]")){
				if (!line.isEmpty()){
					String[] lineEntries = line.split(":");					
					beatmap.difficultyFields.put(lineEntries[0], Float.valueOf(lineEntries[1]));
				}
					
			}
			
			else if (section.equals("[Events]")){
				if (!line.isEmpty()){
					if (line.matches("Sample,.*")){
						String[] lineEntries = line.split(",");	
						StoryboardSoundSample sample = new StoryboardSoundSample();
						sample.time = Integer.parseInt(lineEntries[1]);
						sample.audioFile = lineEntries[3];
						sample.volume = Integer.parseInt(lineEntries[4]);
						beatmap.storyboardSoundSamples.add(sample);
					}
				}
			}
			
			else if (section.equals("[TimingPoints]")){
				if (!line.isEmpty()){				
					String[] lineEntries = line.split(",");	
					TimingPoint timingPoint = new TimingPoint();
					timingPoint.time = Integer.parseInt(lineEntries[0]);
					timingPoint.timePerBeat = Double.parseDouble(lineEntries[1]);
					timingPoint.meter = Integer.parseInt(lineEntries[2]);
					timingPoint.sampleType = Integer.parseInt(lineEntries[3]);
					timingPoint.sampleSet = Integer.parseInt(lineEntries[4]);
					timingPoint.volume = Integer.parseInt(lineEntries[5]);
					timingPoint.inherited = Integer.parseInt(lineEntries[6]);
					timingPoint.kiai = Integer.parseInt(lineEntries[7]);
					beatmap.timingPoints.add(timingPoint);				
				}
			}
			
			
			else if (section.equals("[HitObjects]")){
				if (!line.isEmpty()){
					
					String[] lineEntries = line.split(",");	
					Note note = new Note();
					note.x = note.time = Integer.parseInt(lineEntries[0]);
					note.y = Integer.parseInt(lineEntries[1]);
					note.time = Integer.parseInt(lineEntries[2]);
					note.flags = Integer.parseInt(lineEntries[3]);
					note.hitsound = Integer.parseInt(lineEntries[4]);						
					note.addition = lineEntries[5];
					beatmap.notes.add(note);
					
				}
			}
		}
		br.close();
	}
	
	

}
