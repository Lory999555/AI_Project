package representation;

public class DipoleMove implements Move {

	public enum typeMove{ BACKATTACK,FRONTATTACK,QUIETMOVE,MERGE,DEATH}
	
	private long fromSq;
	private long toSq;
	private int type;
	private boolean BLACK;
	private typeMove tP;
	
	
	public DipoleMove(long fromSq, long toSq, int type, boolean Black, typeMove tp) {
		this.fromSq = fromSq;
		this.toSq = toSq;
		this.type = type;
		this.BLACK = this.BLACK;
		this.tP = tp;
	}
	
	@Override
	public String toString() {
		return tP.name()+ " from: " + fromSq + " to "+ toSq + " type: " + type + " BLACK: "+ BLACK ;
	}
	
	@Override
	public boolean validOn(Conf input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Conf applyTo(Conf input) throws InvalidActionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
