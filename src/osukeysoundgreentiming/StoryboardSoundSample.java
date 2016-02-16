package osukeysoundgreentiming;

public class StoryboardSoundSample {
	public int time;
	public String audioFile;
	public int volume;
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Sample,");
		sb.append(time);
		sb.append(",0,\"");
		sb.append(audioFile);
		sb.append("\",");
		sb.append(volume);
		
		return sb.toString();
	}
}
