package algorithms;

import representation.Move;


public class MoveValueEnc implements Comparable<MoveValueEnc> {

	int move;
	MoveValueEnc principalVariation;
	int value, previousValue;
	
	public MoveValueEnc(int a, int v) {
		this.move = a;
		this.value = v;
		this.previousValue = 0;
		this.principalVariation = null;
	}

	@Override
	public int compareTo(MoveValueEnc other) {
		return Float.compare(this.value, other.value);
	}
	
	
	@Override
	public String toString() {
		return null;
	}
	
}
