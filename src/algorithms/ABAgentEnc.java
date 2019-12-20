package algorithms;

import algorithms.ABWMAgent.Ply;
import heuristics.HeuristicInterface;
import representation.Conf;
import representation.Move;
import representation.TimeOutException;
import representation.Conf.Status;
import representation.DipoleMove;

/**
 * Basic Alpha-Beta Pruning search for Mancala
 * 
 * OOP version for passing results
 */
public class ABAgentEnc implements AlgorithmInterfaceEnc {
	
	public static double tot = 0;
	public static double cont = 0;

	private int searchednodes = 0;
	private int evaluatednodes = 0;
	private boolean ibreak;
	private int maxDepth;
	private int startDepth;

	private HeuristicInterface hi;
	private boolean blackPlayer;
	private long searchCutoff;
	private int evaluatednodesold;
	private int searchednodesold;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	private DipoleMove dp;

	static enum Ply {
		MAX, MIN
	};

	public ABAgentEnc(HeuristicInterface hi, boolean blackPlayer, int startDepth, int maxDepth) {
		this.startDepth = startDepth;
		this.maxDepth = maxDepth;
		this.hi = hi;
		this.blackPlayer = blackPlayer;

		// to call encoding moves
		dp = new DipoleMove();

	}

	private MoveValueEnc alphaBeta_R(Conf conf, int move, int alpha, int beta, int depth, Ply step) {
		searchednodes++;
		int bestMove = 0;
		MoveValueEnc searchResult = null;
		int value;
		// base case
		if (timeUp()) {
			this.ibreak = true;
			return null;
		} else if ((depth == 0)) {
			evaluatednodes++;
			return new MoveValueEnc(move, hi.evaluate_R(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return new MoveValueEnc(move, -5000);
		} else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return new MoveValueEnc(move, 5000);
		}
		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			for (int childmv : conf.getEncodingActions(dp)) {

				searchResult = alphaBeta_R(dp.applyToEnc(conf, childmv), childmv, alpha, beta, depth - 1, Ply.MIN);

				if (searchResult == null)
					return null;

				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				alpha = Math.max(alpha, value);
				if (alpha >= beta)
					break; // pruning
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for (int childmv : conf.getEncodingActions(dp)) {
				searchResult = alphaBeta_R(dp.applyToEnc(conf, childmv), childmv, alpha, beta, depth - 1, Ply.MAX);

				if (searchResult == null)
					return null;

				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
		}

		return new MoveValueEnc(bestMove, value);
	}

	private MoveValueEnc alphaBeta_B(Conf conf, int move, int alpha, int beta, int depth, Ply step) {

		searchednodes++;
		int bestMove = 0;
		MoveValueEnc searchResult = null;
		int value;
		// base case

		// per invalidare l'ultima iterazione perchè potenzialmente errata
		if (timeUp()) {
			this.ibreak = true;
			return null;
		} else if ((depth == 0)) {
			evaluatednodes++;
			return new MoveValueEnc(move, hi.evaluate_B(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return new MoveValueEnc(move, 5000);

		} else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return new MoveValueEnc(move, -5000);
		}
		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			for (int childmv : conf.getEncodingActions(dp)) {
				searchResult = alphaBeta_B(dp.applyToEnc(conf, childmv), childmv, alpha, beta, depth - 1, Ply.MIN);

				if (searchResult == null)
					return null;

				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				alpha = Math.max(alpha, value);
				if (alpha >= beta)
					break; // pruning
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for (int childmv : conf.getEncodingActions(dp)) {
				searchResult = alphaBeta_B(dp.applyToEnc(conf, childmv), childmv, alpha, beta, depth - 1, Ply.MAX);

				if (searchResult == null)
					return null;

				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
		}

		return new MoveValueEnc(bestMove, value);
	}

	public int compute(Conf conf) {
		this.ibreak = false;
		evaluatednodes = 0;
		searchednodes = 0;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		MoveValueEnc newBest = null;
		MoveValueEnc oldBest = null;
		this.searchCutoff = System.currentTimeMillis() + MAX_RUN_TIME;
		int d = startDepth;
		while (!timeUp() && d <= maxDepth) {
			oldBest = newBest;
			if (!this.blackPlayer)
				newBest = alphaBeta_R(conf, 0, alpha, beta, d, Ply.MAX);
			else
				newBest = alphaBeta_B(conf, 0, alpha, beta, d, Ply.MAX);
			d++;


		}

		// craft a specific moves
		if (oldBest == null) {
			return conf.nullMoveEnc();
		}

		if (this.ibreak) {
			tot += (d - 2);
			cont++;
			System.out.println("\nEvaluate: " + oldBest.value + "\nEvaluatedNodes: " + evaluatednodes
					+ "\nSearchedNodes :" + searchednodes + "\ndepth :" + (d - 2) + "\ndepth avg :" + (tot / cont));
			return oldBest.move;
		} else {
			tot += d--;
			cont++;
			System.out.println("\nEvaluate: " + newBest.value + "\nEvaluatedNodes: " + evaluatednodes
					+ "\nSearchedNodes :" + searchednodes + "\ndepth :" + (d--) + "\ndepth avg :" + (tot / cont));
			return newBest.move;

		}

	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return (System.currentTimeMillis() > searchCutoff - 30);
	}
}
