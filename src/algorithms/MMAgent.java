package algorithms;

import java.util.HashMap;

import heuristics.HeuristicInterface;
import representation.Conf;
import representation.Conf.Status;
import representation.InvalidActionException;
import representation.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MMAgent implements AlgorithmInterface {

	static enum Ply {
		MAX, MIN
	};

	// for testing purpose
	private int searchednodes = 0;
	private int evaluatednodes = 0;
	private int maxDepth;

	private HeuristicInterface hi;
	private boolean blackPlayer;
	private long searchCutoff;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	public MMAgent(HeuristicInterface hi, boolean blackPlayer,int maxDepth) {
		this.maxDepth=maxDepth;
		this.hi = hi;
		this.blackPlayer = blackPlayer;
	}

	private MoveValue minimax_R(Conf conf, Move move, int depth, Ply step) {
		searchednodes++;
		Move bestMove = null;
		MoveValue searchResult = null;
		int value;
		// base case
		if ((depth == 0) || timeUp()) {
			evaluatednodes++;
			return new MoveValue(move, hi.evaluate_R(conf));
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
					searchResult = minimax_R(childmv.applyTo(conf), childmv, depth - 1, Ply.MIN);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for (Move childmv : conf.getActions()) {
				try {
					searchResult = minimax_R(childmv.applyTo(conf), childmv, depth - 1, Ply.MAX);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
			}
		}

		return new MoveValue(bestMove, value);
	}

	private MoveValue minimax_B(Conf conf, Move move, int depth, Ply step) {
		searchednodes++;
		Move bestMove = null;
		MoveValue searchResult = null;
		int value;
		// base case
		if ((depth == 0) || timeUp()) {
			evaluatednodes++;
			return new MoveValue(move, hi.evaluate_B(conf));
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
					searchResult = minimax_B(childmv.applyTo(conf), childmv, depth - 1, Ply.MIN);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for (Move childmv : conf.getActions()) {
				try {
					searchResult = minimax_B(childmv.applyTo(conf), childmv, depth - 1, Ply.MAX);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
			}
		}

		return new MoveValue(bestMove, value);
	}

	public Move compute(Conf conf) {
		this.evaluatednodes = 0;
		this.searchednodes = 0;
		MoveValue best;
		this.searchCutoff = new Date().getTime() + MAX_RUN_TIME;
		if (!this.blackPlayer)
			best = minimax_R(conf, null, maxDepth, Ply.MAX);
		else
			best = minimax_B(conf, null, maxDepth, Ply.MAX);

		System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes);
		return best.move;
	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return (new Date().getTime() > searchCutoff - 30);
	}

}
