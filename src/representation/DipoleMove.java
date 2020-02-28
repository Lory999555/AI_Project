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
	};

	
	@Override
	public String toString() {
		return tP.name() + " from: " + Board.getSquare(fromSq) + " to " + Board.getSquare(toSq) + " type: " + type
				+ " BLACK: " + black + " dist: " + dist;
	}

	@Override
	public boolean validOn(Conf input) {
		return false;
	}

	@Override

	public Conf applyTo(Conf input) {
		assert (input != null);
		DipoleConf tmp = (DipoleConf) input;
		DipoleConf res = null;
		try {
			res = tmp.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.setBlack(!input.isBlack());
		if(this.dist==0)
			return res;
		int cont;
		assert (type < 12);

		// Mi indica se si va a muovere tutto lo stack oppure solo una minima parte
		boolean allStack = (type - dist) == -1;

		long fromtoSq = fromSq ^ toSq;

		switch (this.tP) {
		case QUIETMOVE:
			// elimina la pedina from dalla bitBoard che ne insica il tipo. Es se noi
			// spostiamo
			// una pedina da 6 andiamo ad elimniare questa pedina dal tipo 6
			res.setBoard(type, res.getBoard(type) ^ fromSq);
			
			// inseriamo nella casella di destinazione il numero di pedine spostate che
			// corrisponde alla distanza meno 1
			res.setBoard(dist - 1, res.getBoard(dist - 1) ^ toSq);
			
			if (tmp.isBlack()) {
				if (allStack)
					// se spostiamo tutto lo stack
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


	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getFromSq() {
		return fromSq;
	}

	public void setFromSq(long fromSq) {
		this.fromSq = fromSq;
	}

	public long getToSq() {
		return toSq;
	}

	public void setToSq(long toSq) {
		this.toSq = toSq;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public typeMove gettP() {
		return tP;
	}

	public void settP(typeMove tP) {
		this.tP = tP;
	}

	public int getDist() {
		return dist;
	}

	public void setDist(int dist) {
		this.dist = dist;
	}

	public boolean isBlack() {
		return black;
	}

	public void setBlack(boolean black) {
		this.black = black;
	}

}
