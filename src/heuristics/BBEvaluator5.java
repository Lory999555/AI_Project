package heuristics;

import representation.Board;
import representation.Conf;
import representation.DipoleConf;

public class BBEvaluator5 implements HeuristicInterface {
	private int val[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }; // valore delle pedine
	private double valPositionR[] = { 2, 2, 3, 3.5, 2.5, 1.5, 1, 0 }; // valore della posizione in base alla riga
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

	private double percNum = 3.5;		
	private double percMat = 2.5;
	private double percMob = 1.2;

	private double percFa1 = 1;
	private double percBa1 = 1.3;

	private double percFa2 = 1.5;
	private double percBa2 = 1.8;
	
	private double nB; // number of black pawn
	private double nR; // number of red pawn

	public int evaluate_R(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		pRed = dc.getpRed();
		pBlack = dc.getpBlack();
		calculateValBlack(dc);
		calculateValRed(dc);
		nB = calculatePercentage(dc.pawnCount(pBlack), 12);
		nR = calculatePercentage(dc.pawnCount(pRed), 12);

		double eval;
		if (c.isBlack()) {
			eval = (nR*percNum + materialR*percMat + mobilityR*percMob + frontAttackR *percFa2+ backAttackR*percBa2)
					- (nB*percNum + materialB*percMat + mobilityB*percMob + frontAttackB *percFa1+ backAttackB*percBa1);
		} else {
			eval = (nR*percNum + materialR*percMat + mobilityR*percMob + frontAttackR*percFa1 + backAttackR*percBa1)
					- (nB*percNum + materialB*percMat + mobilityB*percMob + frontAttackB*percFa2 + backAttackB*percBa2);
		}
		return (int) Math.round(eval);
	}

	public int evaluate_B(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		pRed = dc.getpRed();
		pBlack = dc.getpBlack();
		calculateValBlack(dc);
		calculateValRed(dc);
		nB = calculatePercentage(dc.pawnCount(pBlack), 12);
		nR = calculatePercentage(dc.pawnCount(pRed), 12);
		double eval;
		if (c.isBlack()) {
			eval = (nB*percNum + materialB*percMat + mobilityB*percMob + frontAttackB*percFa1 + backAttackB*percBa1)
					- (nR*percNum + materialR*percMat + mobilityR*percMob + frontAttackR*percFa2 + backAttackR*percBa2);
		} else {
			eval = (nB*percNum + materialB*percMat + mobilityB*percMob + frontAttackB*percFa2 + backAttackB*percBa2)
					- (nR*percNum + materialR*percMat + mobilityR*percMob + frontAttackR*percFa1 + backAttackR*percBa1);
		}
		return (int) Math.round(eval);
	}

	/**
	 * @param DipoleConf
	 * @return material, number of mobility (quiet move), number of backAttack and number of
	 *         frontAttack
	 */
	private void calculateValRed(DipoleConf c) {
		long pB = pBlack;
		long pR = pRed;
		materialR = 0;
		mobilityR = 0;
		backAttackR = 0;
		frontAttackR = 0;
		int square;
		int type;
		long pawn;
		while (pR != 0) {
			pawn = pR & -pR;
			pR ^= pawn;
			square = Board.getSquare(pawn);
			type = c.getType(pawn);
			assert (type < 12);
			materialR += (val[type] * valPositionR[square >>> 3]);
			c.allMoves2(pawn, pB, pR, type, c.getPieces(), Board.movingBook);
			mobilityR += c.popCount(c.getQuietMove() | c.getMerge());
			frontAttackR += c.evalFA();
			backAttackR += c.evalBA();
		}
		materialR = calculatePercentage(materialR, maxMat);
		mobilityR = calculatePercentage(mobilityR, maxMob);
		frontAttackR = calculatePercentage(frontAttackR, maxFA);
		backAttackR = calculatePercentage(backAttackR, maxBA);
	}

	/**
	 * @param DipoleConf
	 * @return material, number of mobility (quiet move), number of backAttack and number of
	 *         frontAttack
	 */
	private void calculateValBlack(DipoleConf c) {
		long pB = Board.flip180(pBlack);
		long pR = Board.flip180(pRed);
		materialB = 0;
		mobilityB = 0;
		backAttackB = 0;
		frontAttackB = 0;
		int square;
		int type;
		long pawn;
		while (pB != 0) {
			pawn = pB & -pB;
			pB ^= pawn;
			square = Board.getSquare(pawn); 
			type = c.getType180(pawn);
			assert (type < 12);
			materialB += (val[type] * valPositionR[square >>> 3]);
			c.allMoves2(pawn, pR, pB, type, c.getPieces180(), Board.movingBook);
			mobilityB += c.popCount(c.getQuietMove() | c.getMerge());
			c.setBackAttack(Board.flip180(c.getBackAttack()));
			c.setFrontAttack(Board.flip180(c.getFrontAttack()));
			frontAttackB += c.evalFA();
			backAttackB += c.evalBA();
		}
		materialB = calculatePercentage(materialB, maxMat);
		mobilityB = calculatePercentage(mobilityB, maxMob);
		frontAttackB = calculatePercentage(frontAttackB, maxFA);
		backAttackB = calculatePercentage(backAttackB, maxBA);
	}


	public double calculatePercentage(double obtained, int total) {
		if (obtained == 0)
			return 0;
		return obtained * 5000 / total;
	}
	
	

	public void print() {
		System.out.println("MatR = " + materialR + "   MatB = " + materialB + "\n MobR = " + mobilityR + "   MobB = "
				+ mobilityB + "\n FatR = " + frontAttackR + "   FatB = " + frontAttackB + "\n BatR = " + backAttackR
				+ "   BatB = " + backAttackB + "\n nb = " + nB + "nr =" + nR);
	}
}