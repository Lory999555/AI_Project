package representation;

public class DipoleMove implements Move {

	public enum typeMove {
		BACKATTACK, FRONTATTACK, QUIETMOVE, MERGE, DEATH
	}

	private long fromSq;
	private long toSq;
	private int type; // è l'indice NON la tipologia
	private boolean black;
	private typeMove tP;
	private int dist;

	public DipoleMove(long fromSq, long toSq, int type, boolean black, typeMove tp, int dist) {
		this.fromSq = fromSq;
		this.toSq = toSq;
		this.type = type;
		this.black = black;
		this.tP = tp;
		this.dist = dist;

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
	
	public Conf applyTo(Conf input) throws InvalidActionException, CloneNotSupportedException {
		DipoleConf tmp = (DipoleConf) input;
		DipoleConf res = tmp.clone();
		res.setBlack(!input.isBlack());
		boolean allStack = type - dist < 0;

		long fromtoSq = fromSq ^ toSq;

		switch (this.tP) {
		case QUIETMOVE:
//			andrebbero controllate varie cose come:
//				se una pedina si muove completamente quali bitboard aggiornare
//				e come capirlo? del tipo type - dist < 0?
//				aggiornare anche le bb pblack e pred in base a quale gicatore
//				sta muovendo così da scegliere from o to bb.
//			sarebbe utile mettere pblack e pred in una lista che coincida con il booleano
			res.setBoard(type, tmp.getBoard(type) ^ fromSq);
			res.setBoard(dist, tmp.getBoard(dist) ^ toSq);
			res.setBoard(type - dist, tmp.getBoard(type - dist) ^ fromSq);
			if (tmp.isBlack())
				if (allStack)
					res.setpBlack(tmp.getpBlack() ^ fromtoSq);
				else
					res.setpBlack(tmp.getpBlack() | fromtoSq);
			else {
				if (allStack)
					res.setpRed(tmp.getpRed() ^ fromtoSq);
				else
					res.setpRed(tmp.getpRed() | fromtoSq);
			}
			break;
		case MERGE:
			res.setBoard(type, tmp.getBoard(type) ^ fromSq);
			res.setBoard(dist, tmp.getBoard(dist) ^ toSq);
			res.setBoard(type - dist, tmp.getBoard(type - dist) ^ fromSq);
			int c = 0;

//			trovo la pedina avversaria che viene mangiata
			while ((tmp.getBoard(c) & toSq) == 0L) {
				c++;
			}

			res.setBoard(c, tmp.getBoard(c) ^ toSq);
			break;

		case FRONTATTACK:
			break;
			
		case BACKATTACK:
			break;

		}

		return res;

	}
	
	private void decodingMove(int code) {
		
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
