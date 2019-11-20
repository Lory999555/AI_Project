package heuristics;

import java.util.LinkedList;

import representation.Board;
import representation.Conf;
import representation.DipoleConf;

public class Evaluator {
	private int val[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 }; // valore delle pedine
	private double valPosition[] = { 2, 1.75, 1.50, 1.25, 1, 0.6, 0.3, 0 }; // valore della posizione in base alla riga
	private int mobilityB;
	private int backAttackB;
	private int frontAttackB;
	private int mobilityR;
	private int backAttackR;
	private int frontAttackR;

	public double evaluate(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval = material(dc) + mobilityR + frontAttackR + 2*backAttackR 
						- mobilityB - frontAttackB - 2*backAttackB;
		return eval;
	}
	
	public double evaluateMob(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval =  mobilityR - mobilityB;
		return eval;
	}
	
	public double evaluateMat(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval = material(dc);
		return eval;
	}
	
	public double evaluateAtt(Conf c) {
		DipoleConf dc = (DipoleConf) c;
		numberMovesBlack(dc);
		numberMovesRed(dc);
		double eval =  frontAttackR + 2*backAttackR 
						- frontAttackB - 2*backAttackB;
		return eval;
	}

	/**
	 * @param DipoleConf
	 * @return number of mobility (quiet move), number of backAttack and number of frontAttack
	 */
	private void numberMovesRed(DipoleConf c) {
		mobilityR = 0;
		backAttackR = 0;
		frontAttackR = 0;
		long pRed = c.getpRed();
		long pBlack = c.getpBlack();
		long pawn;
		while (pRed != 0) {
			pawn = pRed & -pRed;
			pRed ^= pawn;
			c.allMoves2(pawn, pBlack, pRed, c.getType(pawn), c.getPieces(), Board.movingBook);
			mobilityR += c.popCount(c.getQuietMove()|c.getMerge());
			backAttackR += c.popCount(c.getBackAttack());
			frontAttackR += c.popCount(c.getFrontAttack());
			}
	}
	
	/**
	 * @param DipoleConf
	 * @return number of mobility (quiet move), number of backAttack and number of frontAttack
	 */
	private int numberMovesBlack(DipoleConf c) {
		mobilityB = 0;
		backAttackB = 0;
		frontAttackB = 0;
		long pBlack = c.getpBlack();
		long pRed = c.getpRed();
		long pawn;
		while (pBlack != 0) {
			pawn = pBlack & -pBlack;
			pBlack ^= pawn;
			c.allMoves2(pawn, pRed, pBlack, c.getType(pawn), c.getPieces(), Board.movingBook);
			mobilityB += c.popCount(c.getQuietMove()|c.getMerge());
			backAttackB += c.popCount(c.getBackAttack());
			frontAttackB += c.popCount(c.getFrontAttack());
			}
		return 0;
	}

	/**
	 * @param DipoleConf
	 * @return returns the value of the pieces in relation to their position
	 */
	private double material(DipoleConf c) { // N.B. se l'euristica rimane così, posso richiamare il numberMove
											// direttamente nel while presente in questa classe
		double material = 0;
		long pieces;
		long pawn;
		int square;
		int type;
		if (c.isBlack())
			pieces = c.getpBlack();
		else
			pieces = c.getpRed();
		while (pieces != 0) {
			pawn = pieces & -pieces;
			pieces ^= pawn;
			square = c.getSquare(pawn);
			type = c.getType(pawn);
			material += (val[type] + valPosition[square >>> 3]);
		} // while
		return material;
	}
}
