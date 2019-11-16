package algorithms;

import representation.Move;


public class MoveValue implements Comparable<MoveValue> {

	Move move;
	MoveValue principalVariation;
	int value, previousValue;
	
	public MoveValue() {
		this.move = null;
		this.value = -1;		//non so se va bene -1
		this.previousValue = 0;
		this.principalVariation = null;
	}
	
	public MoveValue(Move a, int v) {
		this.move = a;
		this.value = v;
		this.previousValue = 0;
		this.principalVariation = null;
	}

	@Override
	public int compareTo(MoveValue other) {
		return Float.compare(this.value, other.value);
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(Action : ");
		sb.append(move);
		MoveValue pv = this.principalVariation;
		while (pv.move != null) {
			sb.append("->");
			sb.append(pv.move);
			pv = pv.principalVariation;
		}
		sb.append(" Value: " + value + ")");
		return sb.toString();
	}
	
}
