package osukeysoundgreentiming;

public class Note {
	public int x;
	public int y;
	public int time;
	public int flags;
	public int hitsound;
	public String addition;
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(x);
		sb.append(',');
		sb.append(y);
		sb.append(',');
		sb.append(time);
		sb.append(',');
		sb.append(flags);
		sb.append(',');
		sb.append(hitsound);
		sb.append(',');
		sb.append(addition);		
		
		return sb.toString();
	}
}
