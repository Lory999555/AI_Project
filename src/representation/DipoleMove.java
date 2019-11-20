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

	public DipoleMove() {
	}
	
	/**
	 * encoding move in 23 bit: 6 fromSq + 6 toSq + 3 distance + 4 type + 1 color +
	 * 3 move type
	 * 
	 * @param fromSq
	 * @param toSq
	 * @param type
	 * @param black
	 * @param tp
	 * @return int
	 */
	public int encodingMove(int fromSq, int toSq, int type, boolean black, typeMove tp) {
		System.out.println("f " + fromSq + ", to " + toSq + " = "
				+ (Math.abs((fromSq >> 3) - (toSq >> 3)) + " -> " + (Math.abs((fromSq >> 3) - (toSq >> 3)) << 8)));
		return (fromSq << 17) | (toSq << 11) | (Math.abs((fromSq >>> 3) - (toSq >>> 3)) << 8) | (type << 4)
				| ((black ? 1 : 0) << 3) | (tp.ordinal());
	}

	public void decodingMove(int code) {
		tP = typeMove.values()[code & 0x7];
		black = (((code & 0x8) >>> 3) == 1 ? true : false);
		type = (code & 0xf0) >>> 4;
		dist = ((code & 0x700) >>> 8)-1;
		toSq = (code & 0x1f800) >>> 11;
		fromSq = (code & 0x7e0000) >>> 17;
	}

	@Override
	public String toString() {
		return tP.name() + " from: " + fromSq + " to " + toSq +" dist: "+ dist +" type: " + type + " BLACK: " + black;
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
//				se una pedina si muove completamente quali bitboard aggiornare? se stessa
//				e come capirlo? del tipo type - dist < 0?
//				aggiornare anche le bb pblack e pred in base a quale gicatore
//				sta muovendo così da scegliere from o to bb.
//			sarebbe utile mettere pblack e pred in una lista che coincida con il booleano
			res.setBoard(type, tmp.getBoard(type) ^ fromSq);
			res.setBoard(dist, tmp.getBoard(dist) ^ toSq);
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
	

	public Conf applyTo2(Conf input,int code) throws InvalidActionException, CloneNotSupportedException {
		int cont = 0;
		DipoleConf tmp = (DipoleConf) input;
		DipoleConf res = tmp.clone();
		res.setBlack(!input.isBlack());
		decodingMove(code);
		
		boolean allStack = type - dist == 0;

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
//			res.setBoard(type - dist, tmp.getBoard(type - dist) ^ fromSq);
			if (tmp.isBlack()) {
				if (allStack)
					res.setpBlack(tmp.getpBlack() ^ fromtoSq);
				else {
					res.setBoard(type - dist, tmp.getBoard(type - dist) ^ fromSq);
					res.setpBlack(tmp.getpBlack() | fromtoSq);					
				}
			}else {
				if (allStack)
					res.setpRed(tmp.getpRed() ^ fromtoSq);
				else {
					res.setBoard(type - dist, tmp.getBoard(type - dist) ^ fromSq);
					res.setpRed(tmp.getpRed() | fromtoSq);			
				}
			}
			break;
		case MERGE:
			res.setBoard(type, tmp.getBoard(type) ^ fromSq);					// toglie l'intero stack di dimensione type
			cont = 0;
			while((tmp.getBoard(cont) & toSq) == 0) {
				cont++;
			}
			res.setBoard(cont, tmp.getBoard(cont) ^ toSq);						// toglie l'intero stack da toSq			
			res.setBoard(type+cont, tmp.getBoard(type+cont) ^ toSq);			// inserisce in posizione toSq il nuovo stack dato dalla somma dei 2
			
			if (tmp.isBlack()) {
				if (allStack)
					res.setpBlack(tmp.getpBlack() ^ fromSq);

			}else {
				if (allStack)
					res.setpRed(tmp.getpRed() ^ fromSq);
			}
			break;

		case FRONTATTACK:
			res.setBoard(type, tmp.getBoard(type) ^ fromSq);
			res.setBoard(dist, tmp.getBoard(dist) ^ toSq);
			cont = 0;
			while((tmp.getBoard(cont) & toSq) == 0) {
				cont++;
			}
			if (tmp.isBlack()) {
				if (allStack)
					res.setpBlack(tmp.getpBlack() ^ fromtoSq);
				else {
					res.setBoard(type - dist, tmp.getBoard(type - dist) ^ fromSq);
					res.setpBlack(tmp.getpBlack() | fromtoSq);					
				}
			}else {
				if (allStack)
					res.setpRed(tmp.getpRed() ^ fromtoSq);
				else {
					res.setBoard(type - dist, tmp.getBoard(type - dist) ^ fromSq);
					res.setpRed(tmp.getpRed() | fromtoSq);			
				}
			}
			
			break;

		case BACKATTACK:
			break;

		}

		return res;

	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
