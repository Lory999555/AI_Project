package representation;

public class DipoleMove implements Move {

	public enum typeMove{ BACKATTACK,FRONTATTACK,QUIETMOVE,MERGE,DEATH}
	
	private long fromSq;
	private long toSq;
	private int type;
	private boolean BLACK;
	private typeMove tP;
	private int dist;
	
	
	public DipoleMove(long fromSq, long toSq, int type, boolean black, typeMove tp) {
		this.fromSq = fromSq;
		this.toSq = toSq;
		this.type = type;
		this.BLACK = black;
		this.tP = tp;
	}
	
	public DipoleMove(){
	}
	
	/**
	 * encoding move in 23 bit: 6 fromSq + 6 toSq + 3 distance + 4 type + 1 color + 3 move type
	 * @param fromSq
	 * @param toSq
	 * @param type
	 * @param black
	 * @param tp
	 * @return int
	 */
	public int encodingMove(int fromSq, int toSq, int type, boolean black, typeMove tp) {
		System.out.println("f "+fromSq+", to "+toSq+ " = "+(Math.abs((fromSq>>3)-(toSq>>3))+" -> "+(Math.abs((fromSq>>3)-(toSq>>3))<<8)));
		return (fromSq<<17)|(toSq<<11)|(Math.abs((fromSq>>3)-(toSq>>3))<<8)|(type<<4)|((black?1:0)<<3)|(tp.ordinal());
	}
	
	@Override
	public String toString() {
		return tP.name()+ " from: " + fromSq + " to "+ toSq + " type: " + type + " BLACK: "+ BLACK ;
//		return "0";
	}
	
	@Override
	public boolean validOn(Conf input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Conf applyTo(Conf input) throws InvalidActionException {
		
		
		return null;
	}
	
	private void decodingMove(int code) {
		
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
