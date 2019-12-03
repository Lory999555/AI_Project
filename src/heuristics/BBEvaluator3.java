package heuristics;

import java.util.LinkedList;

import representation.Board;
import representation.Conf;
import representation.Conf.Status;
import representation.DipoleConf;

public class BBEvaluator3 implements HeuristicInterface {
	private int val[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }; // valore delle pedine
//	private double valPositionR[] = { 2, 1.75, 1.50, 1.25, 1, 0.6, 0.4, 0.2 }; // valore della posizione in base alla riga
//	private double valPositionB[] = { 0.2, 0.4, 0.6, 1, 1.25, 1.50, 1.75, 2 };

//	private double valPositionR[] = { 2, 2, 4, 5, 4, 2, 1, 0 }; // valore della posizione in base alla riga
//	private double valPositionB[] = { 0, 1, 2, 4, 5, 4, 2, 2 };

	private double valPositionR[] = { 2, 2, 3, 3.5, 2.5, 1.5, 1, 0 }; // valore della posizione in base alla riga
	private double valPositionB[] = { 0, 1, 1.5, 2.5, 3.5, 3, 2, 2 };

	private int mobilityB;
	private int backAttackB;
	private int frontAttackB;
	private int mobilityR;
	private int backAttackR;
	private int frontAttackR;
	private long pRed;
	private long pBlack;
	private int maxMat = 35;
	private int maxMob = 30;
	private int maxFA = 13;
	private int maxBA = 13;

	public int evaluate_R(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		pRed = dc.getpRed();
		pBlack = dc.getpBlack();
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval;
		if (c.isBlack()) {
			eval = (calculatePercentage(materialR(dc), maxMat) + calculatePercentage(mobilityR, maxMob)  + calculatePercentage(frontAttackR, maxFA) * 1.5  + calculatePercentage(backAttackR, maxBA)*2 )
					- (calculatePercentage(materialB(dc), maxMat) + calculatePercentage(mobilityB, maxMob) + calculatePercentage(frontAttackB, maxFA) + 1.2 * calculatePercentage(backAttackB, maxBA));
		} else {
			eval = (calculatePercentage(materialR(dc), maxMat) + calculatePercentage(mobilityR, maxMob) + calculatePercentage(frontAttackR, maxFA) + 1.2 * calculatePercentage(backAttackR, maxBA))
					- (calculatePercentage(materialB(dc), maxMat) + calculatePercentage(mobilityB, maxMob) + 1.5 * calculatePercentage(frontAttackB, maxFA) + 2 * calculatePercentage(backAttackB, maxBA));
		}
//		System.out.println("evalR_____mobR="+mobilityR+" fronR= "+frontAttackR+" backR= "+ backAttackR+"\n"+"mobB="+mobilityB+" fronB= "+frontAttackB+" backB= "+ backAttackB+"\n");
		return (int) Math.round(eval);
	}

	public int evaluate_B(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		pRed = dc.getpRed();
		pBlack = dc.getpBlack();
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval;
		if (c.isBlack()) {
			eval = (calculatePercentage(materialB(dc), maxMat) + calculatePercentage(mobilityB, maxMob) + 1.5 * calculatePercentage(frontAttackB, maxFA) + 2 * calculatePercentage(backAttackB, maxBA))
					- (calculatePercentage(materialR(dc), maxMat) + calculatePercentage(mobilityR, maxMob) + calculatePercentage(frontAttackR, maxFA) + 1.2 * calculatePercentage(backAttackR, maxBA));
		} else {
			eval = (calculatePercentage(materialB(dc), maxMat) + calculatePercentage(mobilityB, maxMob) + calculatePercentage(frontAttackB, maxFA) + 1.2 * calculatePercentage(backAttackB, maxBA))
					- (calculatePercentage(materialR(dc), maxMat) + calculatePercentage(mobilityR, maxMob)  + calculatePercentage(frontAttackR, maxFA) * 1.5  + calculatePercentage(backAttackR, maxBA)*2 );
		}
//		System.out.println("evalB_____mobR="+mobilityR+" fronR= "+frontAttackR+" backR= "+ backAttackR+"\n"+"mobB="+mobilityB+" fronB= "+frontAttackB+" backB= "+ backAttackB+"\n");
		return (int) Math.round(eval);
	}

	public double evaluateMob(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval = mobilityR - mobilityB;
		return eval;
	}

	public double evaluateMat(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval = materialR(dc) - materialB(dc);
		return eval;
	}

	public double evaluateAtt(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval = frontAttackR + 2 * backAttackR - frontAttackB - 2 * backAttackB;
		return eval;
	}

	private void numberMovesRed(DipoleConf c) {
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
	}

	/**
	 * @param DipoleConf
	 * @return number of mobility (quiet move), number of backAttack and number of
	 *         frontAttack
	 */
	private void numberMovesBlack(DipoleConf c) {
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
	}

	/**
	 * @param DipoleConf
	 * @return returns the value of the pieces in relation to their position
	 */
	private double materialB(DipoleConf c) { // N.B. se l'euristica rimane così, posso richiamare il numberMove
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
		return material;
	}

	public double calculatePercentage(double obtained, double total) {
		if(obtained == 0)return 0;
        return obtained * 100 / total;
    }
}