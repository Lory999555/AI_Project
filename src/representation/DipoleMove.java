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
		return (fromSq << 17) | (toSq << 11) | (Math.abs((fromSq >> 3) - (toSq >> 3)) << 8) | (type << 4)
				| ((black ? 1 : 0) << 3) | (tp.ordinal());

	}

	// distanza utilizzata dalle mosse death e dalle mosse back attack in quanto la
	// distnza gliela passiamo da parametri
	public int encodingMove(int fromSq, int toSq, int dist, int type, boolean black, typeMove tp) {
		System.out.println("f " + fromSq + ", to " + toSq + " = "
				+ (Math.abs((fromSq >> 3) - (toSq >> 3)) + " -> " + (Math.abs((fromSq >> 3) - (toSq >> 3)) << 8)));
		return (fromSq << 17) | (toSq << 11) | (dist << 8) | (type << 4) | ((black ? 1 : 0) << 3) | (tp.ordinal());

	}

	public void decodingMove(int code) {
		tP = typeMove.values()[code & 0x7];
		black = (((code & 0x8) >>> 3) == 1 ? true : false);
		type = (code & 0xf0) >>> 4;
		dist = (code & 0x700) >>> 8;
		toSq = (code & 0x1f800) >>> 11;
		fromSq = (code & 0x7e0000) >>> 17;
	}

	@Override
	public String toString() {
		return tP.name() + " from: " + fromSq + " to " + toSq + " type: " + type + " BLACK: " + black + " dist: "
				+ dist;
//		return "0";
	}

	@Override
	public boolean validOn(Conf input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override

	public Conf applyTo(Conf input) throws InvalidActionException, CloneNotSupportedException {
		int cont;
		DipoleConf tmp = (DipoleConf) input;
		DipoleConf res = tmp.clone();
		res.setBlack(!input.isBlack());
		//decodingMove(code);

		boolean allStack = (type - dist) == -1;

		long fromtoSq = fromSq ^ toSq;

		switch (this.tP) {
		case QUIETMOVE:
//			andrebbero controllate varie cose come:
//				se una pedina si muove completamente quali bitboard aggiornare
//				e come capirlo? del tipo type - dist < 0?
//				aggiornare anche le bb pblack e pred in base a quale gicatore
//				sta muovendo così da scegliere from o to bb.
//			sarebbe utile mettere pblack e pred in una lista che coincida con il booleano
			
			res.setBoard(type, res.getBoard(type) ^ fromSq);
			res.setBoard(dist - 1, res.getBoard(dist - 1) ^ toSq);
//			res.setBoard(type - dist, tmp.getBoard(type - dist) ^ fromSq);
			if (tmp.isBlack()) {
				if (allStack)
					res.setpBlack(res.getpBlack() ^ fromtoSq);
				else {

					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpBlack(res.getpBlack() | fromtoSq);
				}
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromtoSq);
				else {

					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpRed(res.getpRed() | fromtoSq);
				}
			}
			break;
		case MERGE:

			res.setBoard(type, res.getBoard(type) ^ fromSq); // toglie l'intero stack di dimensione type
			cont = 0;

			while ((res.getBoard(cont) & toSq) == 0) {
				cont++;
			}

			res.setBoard(cont, res.getBoard(cont) ^ toSq); // toglie l'intero stack da toSq
			res.setBoard(dist + cont, res.getBoard(dist + cont) ^ toSq); // inserisce in posizione toSq il nuovo stack
																			// dato dalla somma dei 2

			if (tmp.isBlack()) {
				if (allStack)
					res.setpBlack(res.getpBlack() ^ fromSq);
				else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
				}
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromSq);
				else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
				}
			}
			break;

		case FRONTATTACK:
			res.setBoard(type, res.getBoard(type) ^ fromSq);
			cont = 0;
			while ((res.getBoard(cont) & toSq) == 0) {
				cont++;
			}
			res.setBoard(cont, res.getBoard(cont) ^ toSq); // tolgo pedina nemica
			res.setBoard(dist - 1, res.getBoard(dist - 1) ^ toSq);

			if (tmp.isBlack()) {
				if (allStack) {
					res.setpBlack(res.getpBlack() ^ fromtoSq);
				} else {

					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpBlack(res.getpBlack() | fromtoSq);
				}
				res.setpRed(res.getpRed() ^ toSq);
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromtoSq);
				else {

					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpRed(res.getpRed() | fromtoSq);
				}
				res.setpBlack(res.getpBlack() ^ toSq);
			}

			break;

		case BACKATTACK:
			res.setBoard(type, res.getBoard(type) ^ fromSq);
			cont = 0;
			while ((res.getBoard(cont) & toSq) == 0) {
				cont++;
			}
			res.setBoard(cont, res.getBoard(cont) ^ toSq); // tolgo pedina nemica
			res.setBoard(dist - 1, res.getBoard(dist - 1) ^ toSq);

			if (tmp.isBlack()) {
				if (allStack) {
					res.setpBlack(res.getpBlack() ^ fromtoSq);
				} else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpBlack(res.getpBlack() | fromtoSq);
				}
				res.setpRed(res.getpRed() ^ toSq);
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromtoSq);
				else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpRed(res.getpRed() | fromtoSq);
				}
				res.setpBlack(res.getpBlack() ^ toSq);
			}

			break;
		case DEATH:
			res.setBoard(type, res.getBoard(type) ^ fromSq);
			if (tmp.isBlack()) {
				if (allStack) {
					res.setpBlack(res.getpBlack() ^ fromSq);
				} else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
				}
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromSq);
				else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
				}
			}

		}

		return res;
	}

	
	public Conf applyTo2(Conf input, int code) throws InvalidActionException, CloneNotSupportedException {
		int cont = 0;
		DipoleConf tmp = (DipoleConf) input;
		DipoleConf res = tmp.clone();
		res.setBlack(!input.isBlack());
		decodingMove(code);

		boolean allStack = (type - dist) == -1;

		long fromtoSq = fromSq ^ toSq;

		switch (this.tP) {
		case QUIETMOVE:
//			andrebbero controllate varie cose come:
//				se una pedina si muove completamente quali bitboard aggiornare
//				e come capirlo? del tipo type - dist < 0?
//				aggiornare anche le bb pblack e pred in base a quale gicatore
//				sta muovendo così da scegliere from o to bb.
//			sarebbe utile mettere pblack e pred in una lista che coincida con il booleano
			
			res.setBoard(type, res.getBoard(type) ^ fromSq);
			res.setBoard(dist - 1, res.getBoard(dist - 1) ^ toSq);
//			res.setBoard(type - dist, tmp.getBoard(type - dist) ^ fromSq);
			if (tmp.isBlack()) {
				if (allStack)
					res.setpBlack(res.getpBlack() ^ fromtoSq);
				else {

					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpBlack(res.getpBlack() | fromtoSq);
				}
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromtoSq);
				else {

					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpRed(res.getpRed() | fromtoSq);
				}
			}
			break;
		case MERGE:

			res.setBoard(type, res.getBoard(type) ^ fromSq); // toglie l'intero stack di dimensione type
			cont = 0;

			while ((res.getBoard(cont) & toSq) == 0) {
				cont++;
			}

			res.setBoard(cont, res.getBoard(cont) ^ toSq); // toglie l'intero stack da toSq
			res.setBoard(type + cont, res.getBoard(type + cont) ^ toSq); // inserisce in posizione toSq il nuovo stack
																			// dato dalla somma dei 2

			if (tmp.isBlack()) {
				if (allStack)
					res.setpBlack(res.getpBlack() ^ fromSq);
				else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
				}
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromSq);
				else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
				}
			}
			break;

		case FRONTATTACK:
			res.setBoard(type, res.getBoard(type) ^ fromSq);
			cont = 0;
			while ((res.getBoard(cont) & toSq) == 0) {
				cont++;
			}
			res.setBoard(cont, res.getBoard(cont) ^ toSq); // tolgo pedina nemica
			res.setBoard(dist - 1, res.getBoard(dist - 1) ^ toSq);

			if (tmp.isBlack()) {
				if (allStack) {
					res.setpBlack(res.getpBlack() ^ fromtoSq);
				} else {

					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpBlack(res.getpBlack() | fromtoSq);
				}
				res.setpRed(res.getpRed() ^ toSq);
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromtoSq);
				else {

					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpRed(res.getpRed() | fromtoSq);
				}
				res.setpBlack(res.getpBlack() ^ toSq);
			}

			break;

		case BACKATTACK:
			res.setBoard(type, res.getBoard(type) ^ fromSq);
			cont = 0;
			while ((res.getBoard(cont) & toSq) == 0) {
				cont++;
			}
			res.setBoard(cont, res.getBoard(cont) ^ toSq); // tolgo pedina nemica
			res.setBoard(dist - 1, res.getBoard(dist - 1) ^ toSq);

			if (tmp.isBlack()) {
				if (allStack) {
					res.setpBlack(res.getpBlack() ^ fromtoSq);
				} else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpBlack(res.getpBlack() | fromtoSq);
				}
				res.setpRed(res.getpRed() ^ toSq);
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromtoSq);
				else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
					res.setpRed(res.getpRed() | fromtoSq);
				}
				res.setpBlack(res.getpBlack() ^ toSq);
			}

			break;
		case DEATH:
			res.setBoard(type, res.getBoard(type) ^ fromSq);
			if (tmp.isBlack()) {
				if (allStack) {
					res.setpBlack(res.getpBlack() ^ fromSq);
				} else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
				}
			} else {
				if (allStack)
					res.setpRed(res.getpRed() ^ fromSq);
				else {
					res.setBoard(type - dist, res.getBoard(type - dist) ^ fromSq);
				}
			}

		}

		return res;

	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
