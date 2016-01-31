package osukeysoundgreentiming;

public class IntegerPair implements Comparable<IntegerPair>{
	public int first;
	public int second;
	
	@Override
	public int compareTo(IntegerPair o) {
		if (this == o) return 0;		
		//if (this.equals(o)) return 0;
		
		if (o.first < this.first) return 1;
		
		if (o.first > this.first) return -1;
		
		if (o.first == this.first){
			if (o.second < this.second) return 1;
			if (o.second > this.second) return -1;
			
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + first;
		result = prime * result + second;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntegerPair other = (IntegerPair) obj;
		if (first != other.first)
			return false;
		if (second != other.second)
			return false;
		return true;
	}
	
	

	public void rearrangeValuesSmallerFirst() {
		
		if (second != -1 && second != -2){
			
			if (second < first){
				int temp = first;
				first = second;
				second = temp;
					
			}
			
		}
		
	}

	@Override
	public String toString() {
		return "IntegerPair [first=" + first + ", second=" + second + "]";
	}
	
	
}
