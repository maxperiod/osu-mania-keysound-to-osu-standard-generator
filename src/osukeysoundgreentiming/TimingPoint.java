package osukeysoundgreentiming;

public class TimingPoint {
	public int time;
	public double timePerBeat;
	public int meter;
	public int sampleType;
	public int sampleSet;
	public int volume;
	public int inherited;
	public int kiai;
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(time);
		sb.append(',');
		sb.append(timePerBeat);
		sb.append(',');
		sb.append(meter);
		sb.append(',');
		sb.append(sampleType);
		sb.append(',');
		sb.append(sampleSet);
		sb.append(',');
		sb.append(volume);
		sb.append(',');
		sb.append(inherited);
		sb.append(',');
		sb.append(kiai);
		
		return sb.toString();
	}
}
