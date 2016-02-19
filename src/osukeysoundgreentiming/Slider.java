package osukeysoundgreentiming;

public class Slider extends Note {
	public String sliderShape;
	public int repeat;
	public int pixelLength;
	public IntegerPair edgeHitsound;
	public IntegerPair edgeAddition1;
	public IntegerPair edgeAddition2;
	
	@Override
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
		sb.append(sliderShape);
		sb.append(',');
		sb.append(repeat);
		sb.append(',');
		sb.append(pixelLength);
		sb.append(',');
		sb.append(edgeHitsound.first);
		sb.append('|');
		sb.append(edgeHitsound.second);
		sb.append(',');
		sb.append(edgeAddition1.first);
		sb.append(':');
		sb.append(edgeAddition1.second);
		sb.append('|');
		sb.append(edgeAddition2.first);
		sb.append(':');
		sb.append(edgeAddition2.second);
		sb.append(',');
		sb.append(addition);			
		
		return sb.toString();		
		
	}
}


