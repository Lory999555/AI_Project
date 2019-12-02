package algorithms;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import heuristics.HeuristicInterface;
import representation.Board;
import representation.Conf;
import representation.Move;
import representation.Conf.Status;
import representation.InvalidActionException;

/**
 * Alpha-Beta Pruning search with memory for Mancala
 * 
 * OOP version for passing results
 */
public class ABWMAgent implements AlgorithmInterface {

	static enum Ply {
		MAX, MIN
	};

	static class TransEntry {
		public int depth;
		public int upperbound;
		public int lowerbound;

		public TransEntry() {
			this.depth = 0;
			this.upperbound = Integer.MAX_VALUE;
			this.lowerbound = Integer.MIN_VALUE;
		}
	}

	private int searchednodes = 0;
	private int evaluatednodes = 0;
	private int maxDepth;

	private HeuristicInterface hi;
	private boolean blackPlayer;
	private long searchCutoff;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	private HashMap<Long, TransEntry> transTable;
	private long[][] zobristTable;

	public ABWMAgent(HeuristicInterface hi, boolean blackPlayer,int maxDepth) {
		// init zobrist table
		this.maxDepth=maxDepth;
		this.hi = hi;
		this.blackPlayer = blackPlayer;
		Random prng = new Random();
		zobristTable = new long[64][12];
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 12; j++) {
				zobristTable[i][j] = prng.nextLong();
			}
		}

		// init transposition table
		transTable = new HashMap<Long, TransEntry>();
	}

	private long zobristHash(long[] pieces) {
		long key = 0;
		int i = 0;
		long tmp, bit;
		while (i < 12) {

			tmp = pieces[i];
			while (tmp != 0) {
				bit = tmp & -tmp;
				tmp ^= bit;
				key ^= zobristTable[Board.getSquare(bit)][i];
			}

			i++;
		}
		return key;
	}

	private MoveValue alphaBetaWithMemory_R(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {

		searchednodes++;

		TransEntry trans;
		long hash = zobristHash(conf.getForHash());

		// trans table lookup
		if (transTable.containsKey(hash)) {
			trans = transTable.get(hash);
			if (trans.depth >= depth) {
				if (trans.lowerbound >= beta) {
					return new MoveValue(move, trans.lowerbound);
				}
				if (trans.upperbound <= alpha) {
					return new MoveValue(move, trans.upperbound);
				}
				alpha = Math.max(alpha, trans.lowerbound);
				beta = Math.min(beta, trans.upperbound);
			}
		}

		// base case
		Move bestMove = null;
		MoveValue searchResult = null;
		int value;
		// base case
		if ((depth == 0)) {
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
					searchResult = alphaBetaWithMemory_R(childmv.applyTo(conf), childmv, alpha, beta, depth - 1,
							Ply.MIN);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			for (Move childmv : conf.getActions()) {
				try {
					searchResult = alphaBetaWithMemory_R(childmv.applyTo(conf), childmv, alpha, beta, depth - 1,
							Ply.MAX);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
		}

		// if (!strongTimeUp()) {
		// store trans table values
		trans = transTable.getOrDefault(hash, new TransEntry());

		if (trans.depth <= depth) {
			// fail low implies an upper bound
			if (value <= alpha) {
				trans.upperbound = value;
			}
			// fail high implies a lower bound
			else if (value >= beta) {
				trans.lowerbound = value;
			}
			// accurate minimax value
			else {
				trans.lowerbound = value;
				trans.upperbound = value;
			}
			trans.depth = depth;
			transTable.put(hash, trans);
		}
//		}

		return new MoveValue(bestMove, value);

	}

	private MoveValue alphaBetaWithMemory_B(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {

		searchednodes++;

		TransEntry trans;
		long hash = zobristHash(conf.getForHash());

		// trans table lookup
		if (transTable.containsKey(hash)) {
			trans = transTable.get(hash);
			if (trans.depth >= depth) {
				if (trans.lowerbound >= beta) {
					return new MoveValue(move, trans.lowerbound);
				}
				if (trans.upperbound <= alpha) {
					return new MoveValue(move, trans.upperbound);
				}
				alpha = Math.max(alpha, trans.lowerbound);
				beta = Math.min(beta, trans.upperbound);
			}
		}

		// base case

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
					searchResult = alphaBetaWithMemory_B(childmv.applyTo(conf), childmv, alpha, beta, depth - 1,
							Ply.MIN);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			for (Move childmv : conf.getActions()) {
				try {
					searchResult = alphaBetaWithMemory_B(childmv.applyTo(conf), childmv, alpha, beta, depth - 1,
							Ply.MAX);
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
		}

//		if (!strongTimeUp()) {
		// store trans table values
		trans = transTable.getOrDefault(hash, new TransEntry());

		if (trans.depth <= depth) {
			// fail low implies an upper bound
			if (value <= alpha) {
				trans.upperbound = value;
			}
			// fail high implies a lower bound
			else if (value >= beta) {
				trans.lowerbound = value;
			}
			// accurate minimax value
			else {
				trans.lowerbound = value;
				trans.upperbound = value;
			}
			trans.depth = depth;
			transTable.put(hash, trans);
		}
//		}

		return new MoveValue(bestMove, value);
	}

	public Move compute(Conf conf) {
		this.evaluatednodes = 0;
		this.searchednodes = 0;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		MoveValue best;
		long searchCutoff_old = System.currentTimeMillis();
		this.searchCutoff = new Date().getTime() + MAX_RUN_TIME;
		if (!this.blackPlayer)
			best = alphaBetaWithMemory_R(conf, null, alpha, beta, maxDepth, Ply.MAX);
		else
			best = alphaBetaWithMemory_B(conf, null, alpha, beta, maxDepth, Ply.MAX);

		System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes);
		System.out.println("tempo totale: " + (System.currentTimeMillis() - searchCutoff_old));
		assert (best.move != null);

		return best.move;
	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return (new Date().getTime() > (searchCutoff - 100));
	}

//	private boolean strongTimeUp() {
//		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
//				.indexOf("-agentlib:jdwp") > 0)
//			return false;
//		return (new Date().getTime() > (searchCutoff - 30));
//	}
}
