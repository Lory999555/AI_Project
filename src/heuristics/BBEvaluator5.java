package heuristics;

import java.util.LinkedList;

import representation.Board;
import representation.Conf;
import representation.Conf.Status;
import representation.DipoleConf;

public class BBEvaluator5 implements HeuristicInterface {
	private int val[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }; // valore delle pedine
//	private double valPositionR[] = { 2, 1.75, 1.50, 1.25, 1, 0.6, 0.4, 0.2 }; // valore della posizione in base alla riga
//	private double valPositionB[] = { 0.2, 0.4, 0.6, 1, 1.25, 1.50, 1.75, 2 };

//	private double valPositionR[] = { 2, 2, 4, 5, 4, 2, 1, 0 }; // valore della posizione in base alla riga
//	private double valPositionB[] = { 0, 1, 2, 4, 5, 4, 2, 2 };

	private double valPositionR[] = { 2, 2, 3, 3.5, 2.5, 1.5, 1, 0 }; // valore della posizione in base alla riga
	private double valPositionB[] = { 0, 1, 1.5, 2.5, 3.5, 3, 2, 2 };

	private double mobilityB;
	private double backAttackB;
	private double frontAttackB;
	private double mobilityR;
	private double backAttackR;
	private double frontAttackR;
	private double materialR;
	private double materialB;
	private long pRed;
	private long pBlack;
	private int maxMat = 35;
	private int maxMob = 30;
	private int maxFA = 13;
	private int maxBA = 13;

	private int percNum = 35;
	private int percMat = 20;
	private int percMob = 12;

	private int percFa1 = 13;
	private int percBa1 = 15;

	private int percFa2 = 15;
	private int percBa2 = 18;

	private double nB; // number of black pawn
	private double nR; // number of red pawn

	public int evaluate_R(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		pRed = dc.getpRed();
		pBlack = dc.getpBlack();
//		calculateValBlack(dc);
//		calculateValRed(dc);
		materialR = materialR(dc);
		materialB = materialB(dc);
		nB = calculatePercentage(dc.pawnCount(pBlack), 12, percNum);
		nR = calculatePercentage(dc.pawnCount(pRed), 12, percNum);

		double eval;
		if (c.isBlack()) {
			calculateValBlack(dc, percFa1, percBa1);
			calculateValRed(dc, percFa2, percBa2);
			eval = (nR + materialR + mobilityR + frontAttackR + backAttackR)
					- (nB + materialB + mobilityB + frontAttackB + backAttackB);
		} else {
			calculateValBlack(dc, percFa2, percBa2);
			calculateValRed(dc, percFa1, percBa1);
			eval = (nR + materialR + mobilityR + frontAttackR + backAttackR)
					- (nB + materialB + mobilityB + frontAttackB + backAttackB);
		}
		return (int) Math.round(eval);
	}

	public int evaluate_B(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		pRed = dc.getpRed();
		pBlack = dc.getpBlack();
//		calculateValBlack(dc);
//		calculateValRed(dc);
		materialR = materialR(dc);
		materialB = materialB(dc);
		nB = calculatePercentage(dc.pawnCount(pBlack), 12, percNum);
		nR = calculatePercentage(dc.pawnCount(pRed), 12, percNum);
		double eval;
		if (c.isBlack()) {
			calculateValBlack(dc, percFa2, percBa2);
			calculateValRed(dc, percFa1, percBa1);
			eval = (nB + materialB + mobilityB + frontAttackB + backAttackB)
					- (nR + materialR + mobilityR + frontAttackR + backAttackR);
		} else {
			calculateValBlack(dc, percFa1, percBa1);
			calculateValRed(dc, percFa2, percBa2);
			eval = (nB + materialB + mobilityB + frontAttackB + backAttackB)
					- (nR + materialR + mobilityR + frontAttackR + backAttackR);
		}
		return (int) Math.round(eval);
	}

//	public double evaluateMob(Conf c) {
//		DipoleConf dc = (DipoleConf) c;
//		calculateValBlack(dc);
//		calculateValRed(dc);
//		double eval = mobilityR - mobilityB;
//		return eval;
//	}
//
//	public double evaluateMat(Conf c) {
//		DipoleConf dc = (DipoleConf) c;
//		calculateValBlack(dc);
//		calculateValRed(dc);
//		double eval = materialR(dc) - materialB(dc);
//		return eval;
//	}
//
//	public double evaluateAtt(Conf c) {
//		DipoleConf dc = (DipoleConf) c;
//		calculateValBlack(dc);
//		calculateValRed(dc);
//		double eval = frontAttackR + 2 * backAttackR - frontAttackB - 2 * backAttackB;
//		return eval;
//	}

	private void calculateValRed(DipoleConf c, int percFa, int percBa) {
		long pB = pBlack;
		long pR = pRed;
		mobilityR = 0;
		backAttackR = 0;
		frontAttackR = 0;
		long pawn;
		while (pR != 0) {
			pawn = pR & -pR;
			pR ^= pawn;
			c.allMoves2(pawn, pB, pR, c.getType(pawn), c.getPieces(), Board.movingBook);
			mobilityR += c.popCount(c.getQuietMove() | c.getMerge());
			frontAttackR += c.evalFA();
			backAttackR += c.evalBA();
		}
		mobilityR = calculatePercentage(mobilityR, maxMob, percMob);
		frontAttackR = calculatePercentage(frontAttackR, maxFA, percFa);
		backAttackR = calculatePercentage(backAttackR, maxBA, percBa);
	}

	/**
	 * @param percBa12
	 * @param percFa12
	 * @param DipoleConf
	 * @return number of mobility (quiet move), number of backAttack and number of
	 *         frontAttack
	 */
	private void calculateValBlack(DipoleConf c, int percFa, int percBa) {
		long pB = Board.flip180(pBlack);
		long pR = Board.flip180(pRed);
		mobilityB = 0;
		backAttackB = 0;
		frontAttackB = 0;
		long pawn;
		while (pB != 0) {
			pawn = pB & -pB;
			pB ^= pawn;
			c.allMoves2(pawn, pR, pB, c.getType180(pawn), c.getPieces180(), Board.movingBook);
			mobilityB += c.popCount(c.getQuietMove() | c.getMerge());
			c.setBackAttack(Board.flip180(c.getBackAttack()));
			c.setFrontAttack(Board.flip180(c.getFrontAttack()));
			frontAttackB += c.evalFA();
			backAttackB += c.evalBA();
		}
		mobilityB = calculatePercentage(mobilityB, maxMob, percMob);
		frontAttackB = calculatePercentage(frontAttackB, maxFA, percFa);
		backAttackB = calculatePercentage(backAttackB, maxBA, percBa);
	}

	/**
	 * @param DipoleConf
	 * @return returns the value of the pieces in relation to their position
	 */
	private double materialB(DipoleConf c) { // N.B. se l'euristica rimane così, posso richiamare il calculateVal
		// direttamente nel while presente in questa classe
		double material = 0;
		long pieces = pBlack;
		long pawn;
		int square;
		int type;
		while (pieces != 0) {
			pawn = pieces & -pieces;
			pieces ^= pawn;
			square = Board.getSquare(pawn);
			type = c.getType(pawn);
			assert (type < 12);
			material += (val[type] * valPositionB[square >>> 3]);
		} // while
		material = calculatePercentage(material, maxMat, percMat);
		return material;
	}

	private double materialR(DipoleConf c) { // N.B. se l'euristica rimane così, posso richiamare il numberMove
		// direttamente nel while presente in questa classe
		double material = 0;
		long pieces = pRed;
		long pawn;
		int square;
		int type;
		while (pieces != 0) {
			pawn = pieces & -pieces;
			pieces ^= pawn;
			square = Board.getSquare(pawn);
			type = c.getType(pawn);
			assert (type < 12);
			material += (val[type] * valPositionR[square >>> 3]);
		} // while
		material = calculatePercentage(material, maxMat, percMat);
		return material;
	}

	public double calculatePercentage(double obtained, int total) {
		if (obtained == 0)
			return 0;
		return obtained * 100 / total;
	}

	public double calculatePercentage(double obtained, int total, int percentage) {
		if (obtained == 0)
			return 0;
		return obtained * percentage / total;
	}

	public void print() {
		System.out.println("MatR = " + materialR + "   MatB = " + materialB + "\n MobR = " + mobilityR + "   MobB = "
				+ mobilityB + "\n FatR = " + frontAttackR + "   FatB = " + frontAttackB + "\n BatR = " + backAttackR
				+ "   BatB = " + backAttackB + "\n nb = " + nB + "nr =" + nR);
	}
}