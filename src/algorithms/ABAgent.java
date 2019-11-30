package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import heuristics.HeuristicInterface;
import representation.Conf;
import representation.Move;
import representation.Conf.Status;
import representation.InvalidActionException;

/**
 * Basic Alpha-Beta Pruning search for Mancala
 * 
 * OOP version for passing results
 */
public class ABAgent implements AlgorithmInterface {

	private int searchednodes = 0;
	private int evaluatednodes = 0;
	private int maxdepth = MAX_SEARCH_DEPTH;
	private HeuristicInterface h;
	private boolean blackPlayer;
	private long searchCutoff;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds
	private static int MAX_SEARCH_DEPTH = 200;

	static enum Ply {
		MAX, MIN
	};

	public ABAgent(HeuristicInterface h, boolean blackPlayer) {
		this.h = h;
		this.blackPlayer = blackPlayer;

	}

	private MoveValue alphaBeta_R(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {
		if (depth < maxdepth)
			maxdepth = depth;
		searchednodes++;
		Move bestMove = null;
		MoveValue searchResult = null;
		int value;
		// base case
		if ((depth == 0) || conf.getStatus() != Status.Ongoing || timeUp()) {
			evaluatednodes++;
			return new MoveValue(move, h.evaluate_R(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return new MoveValue(move, -5000);

		} else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return new MoveValue(move, 5000);
		}
		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			for (Move childmv : conf.getActions()) {
				try {
					searchResult = alphaBeta_R(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MIN);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value >= value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				alpha = Math.max(alpha, value);
				if (alpha >= beta)
					break; // pruning
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for ( Move childmv : conf.getActions()) {
				try {
					searchResult = alphaBeta_R(childmv.applyTo(conf),childmv, alpha, beta, depth - 1, Ply.MAX);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value <= value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
		}

		return new MoveValue(bestMove, value);
	}
	
	private MoveValue alphaBeta_B(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {
		if (depth < maxdepth)
			maxdepth = depth;
		searchednodes++;
		Move bestMove = null;
		MoveValue searchResult = null;
		int value;
		// base case
		if ((depth == 0) || conf.getStatus() != Status.Ongoing || timeUp()) {
			evaluatednodes++;
			return new MoveValue(move, h.evaluate_B(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return new MoveValue(move, 5000);

		} else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return new MoveValue(move, -5000);
		}
		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			for (Move childmv : conf.getActions()) {
				try {
					searchResult = alphaBeta_B(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MIN);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value >= value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				alpha = Math.max(alpha, value);
				if (alpha >= beta)
					break; // pruning
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for ( Move childmv : conf.getActions()) {
				try {
					searchResult = alphaBeta_B(childmv.applyTo(conf),childmv, alpha, beta, depth - 1, Ply.MAX);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value <= value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
		}

		return new MoveValue(bestMove, value);
	}


	public Move compute(Conf conf) {
		this.evaluatednodes=0;
		this.searchednodes=0;
		this.maxdepth=MAX_SEARCH_DEPTH;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		int depth = MAX_SEARCH_DEPTH;
		MoveValue best;
		this.searchCutoff = new Date().getTime() + MAX_RUN_TIME;
		if(!this.blackPlayer)
			best = alphaBeta_R(conf, null, alpha, beta, depth, Ply.MAX);
		else
			best = alphaBeta_B(conf, null, alpha, beta, depth, Ply.MAX);
		System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes+ "\nMaxDepth: "+(MAX_SEARCH_DEPTH-maxdepth));
		
		
		return best.move;
	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return (new Date().getTime() > searchCutoff - 30);
	}
}
