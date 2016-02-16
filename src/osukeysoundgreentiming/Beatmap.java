package osukeysoundgreentiming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Beatmap {
	public Map<String, String> generalFields;
	public Map<String, String> metadataFields;
	public Map<String, String> editorFields;
	public Map<String, Float> difficultyFields;
	public List<StoryboardSoundSample> storyboardSoundSamples;
	public List<TimingPoint> timingPoints;
	public List<Note> notes;
	
	public Beatmap(){
		generalFields = new HashMap<String, String>();
		metadataFields = new HashMap<String, String>();
		editorFields = new HashMap<String, String>();
		difficultyFields = new HashMap<String, Float>();
		storyboardSoundSamples = new ArrayList<StoryboardSoundSample>();
		timingPoints = new ArrayList<TimingPoint>();
		notes = new ArrayList<Note>();
	}
	//public List<Integer>
	

	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("osu file format v14\n");
		sb.append('\n');
		sb.append("[General]\n");
		
		Set<String> keyset;
		
		keyset = generalFields.keySet();
		for (String key: keyset){
			sb.append(key);
			sb.append(": ");
			sb.append(generalFields.get(key));
			sb.append('\n');
		}
		
		sb.append('\n');
		sb.append("[Metadata]\n");
		
		keyset = metadataFields.keySet();
		for (String key: keyset){
			sb.append(key);
			sb.append(": ");
			sb.append(metadataFields.get(key));
			sb.append('\n');
		}
		
		sb.append('\n');
		sb.append("[Editor]\n");
		
		keyset = editorFields.keySet();
		for (String key: keyset){
			sb.append(key);
			sb.append(":");
			sb.append(editorFields.get(key));
			sb.append('\n');
		}
		
		sb.append('\n');
		sb.append("[Difficulty]\n");
		
		keyset = difficultyFields.keySet();
		for (String key: keyset){
			sb.append(key);
			sb.append(":");
			sb.append(difficultyFields.get(key));
			sb.append('\n');
		}
		
		sb.append('\n');
		sb.append("[Events]\n");
		sb.append("//Background and Video events\n");
		sb.append("//Break Periods\n");
		sb.append("//Storyboard Layer 0 (Background)\n");
		sb.append("//Storyboard Layer 1 (Fail)\n");
		sb.append("//Storyboard Layer 2 (Pass)\n");
		sb.append("//Storyboard Layer 3 (Foreground)\n");
		sb.append("//Storyboard Sound Samples\n");
		
		for (StoryboardSoundSample sample: storyboardSoundSamples){
			sb.append(sample.toString());			
			sb.append('\n');
		}
		
		sb.append('\n');
		sb.append("[TimingPoints]\n");
		
		for (TimingPoint timingPoint: timingPoints){
			sb.append(timingPoint.toString());
			sb.append('\n');
		}
		
		sb.append('\n');
		sb.append("[HitObjects]\n");
				
		for (Note note: notes){
			sb.append(note.toString());
			sb.append('\n');
		}
	
		return sb.toString();
	}
}
