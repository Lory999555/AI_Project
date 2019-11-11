package representation;

import java.util.List;

public class DipoleConf implements Conf {

	/*
	 * private long p1=0; private long p2=0; private long p3=0; private long p4=0;
	 * private long p5=0; private long p6=0; private long p7=0; private long p8=0;
	 * private long p9=0; private long p10=0; private long p11=0; private long
	 * p12=0;
	 */
	
	private long moves;
	
	private long frontAttack;
	private long backAttack;
	private long merge;
	private long death;
	private long quietMove;
	
	private long pBlack;
	private long pRed;
	private long FLAG;
	private long[] pieces = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	
	//Configurazione inzio partita
	public DipoleConf(String colour) {

		this.pieces[11] = 0x1000000000000008L;
		this.pBlack = 0x8;
		this.pRed = 0x1000000000000000L;
		if (colour == "BLACK") {
			this.FLAG = 1;
		} else {
			this.FLAG = -1;
		}

	}

	// ruota la bitboard di 180 gradi
	public long flip180(long x) {

		// flipping vertically
		long k1 = 0x00FF00FF00FF00FFL;
		long k2 = 0x0000FFFF0000FFFFL;
		x = ((x >> 8) & k1) | ((x & k1) << 8);
		x = ((x >> 16) & k2) | ((x & k2) << 16);
		x = (x >> 32) | (x << 32);

		// mirroring horizontally
		long k3 = 0x5555555555555555L;
		long k4 = 0x3333333333333333L;
		long k5 = 0x0f0f0f0f0f0f0f0fL;
		x = ((x >> 1) & k3) + 2 * (x & k3);
		x = ((x >> 2) & k4) + 4 * (x & k4);
		x = ((x >> 4) & k5) + 16 * (x & k5);

		return x;
	}

	private void allMove(){
		
	}
	
	// Ritorna la rosa di azione della pedina presa in considerazione
	private long getRose(long square, int type, long mine, long opponent) {

		return checkSquareMoves(square, -9, Board.b_r | Board.b_d, 1, mine, opponent, type)
				| checkSquareMoves(square, -16, Board.b2_d, 2, mine, opponent, type)
				| checkSquareMoves(square, -7, Board.b_l | Board.b_d, 1, mine, opponent, type)
				| checkSquareMoves(square, -2, Board.b2_r, 2, mine, opponent, type)
				| checkSquareMoves(square, 2, Board.b2_l, 2, mine, opponent, type)
				| checkSquareMoves(square, 7, Board.b_r | Board.b_u, 1, mine, opponent, type)
				| checkSquareMoves(square, 16, Board.b2_u, 2, mine, opponent, type)
				| checkSquareMoves(square, 9, Board.b_l | Board.b_u, 1, mine, opponent, type);
	}

	// Ritorna il movimento di una pedina in una sola direzione (ES N,NO,NE,S,SO...)
	private long checkSquareMoves(long square, int shift, long border, int addMove, long mine, long opponent,
			int type) {
		long notFreeSquare = opponent;
		long ret = 0;
		long tmp =0;
		int cont = addMove - 1;
		while ((square & border) == 0 && cont < type) {
			if (shift > 0) {
				square <<= shift;
			} else {
				square >>>= -shift;
			}
			notFreeSquare ^= pieces[cont] & (~mine);
			cont += addMove;
			tmp =square^(square & notFreeSquare);
			ret |= tmp;
		}
		ret ^= (ret & notFreeSquare);
		return ret;
	}

	// Ritorna la posizione esatta del pedone sottoforma di stringa (ES A8) a
	// partire da una bitboard
	public String DeBruijn(int position) {

		long b = position ^ (position - 1);
		int fold = (int) (b ^ (b >>> 32));

		return Board.SQUARE_NAMES[Board.BIT_TABLE[(fold * 0x783a9b23) >>> 26]];

	}

	// Conta il numero di 1 presenti all'interno di una bitboard
	public int popCount(long x) {
		int count = 0;
		while (x != 0) {
			count++;
			x &= x - 1; // reset LS1B
		}
		return count;
	}

	// SOMMATORIA DEI PEZZI PER IL PROPRIO VALORE VAL
	public int getMaterial() {
		int material = 0;

		if (pieces[0] != 0) {
			material += popCount(pieces[0]) * Board.VAL_ONE;
		}
		if (pieces[1] != 0) {
			material += popCount(pieces[1]) * Board.VAL_TW0;
		}
		if (pieces[2] != 0) {
			material += popCount(pieces[2]) * Board.VAL_THREE;
		}
		if (pieces[3] != 0) {
			material += popCount(pieces[3]) * Board.VAL_FOUR;
		}
		if (pieces[4] != 0) {
			material += popCount(pieces[4]) * Board.VAL_FIVE;
		}
		if (pieces[5] != 0) {
			material += popCount(pieces[5]) * Board.VAL_SIX;
		}
		if (pieces[6] != 0) {
			material += popCount(pieces[6]) * Board.VAL_SEVEN;
		}
		if (pieces[7] != 0) {
			material += popCount(pieces[7]) * Board.VAL_EIGHT;
		}
		if (pieces[8] != 0) {
			material += popCount(pieces[8]) * Board.VAL_NINE;
		}
		if (pieces[9] != 0) {
			material += popCount(pieces[9]) * Board.VAL_TEN;
		}
		if (pieces[10] != 0) {
			material += popCount(pieces[10]) * Board.VAL_ELEVEN;
		}
		if (pieces[11] != 0) {
			material += popCount(pieces[11]) * Board.VAL_TWELVE;
		}
		return material;
	}

	@Override
	public List<Move> getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float heuristic() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Conf getParentState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float heuristic2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String identifier() {
		// TODO Auto-generated method stub
		return null;
	}

}
