package algorithms;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import algorithms.ABWMAgent.Ply;
import heuristics.HeuristicInterface;
import representation.Board;
import representation.Conf;
import representation.InvalidActionException;
import representation.Move;
import representation.Conf.Status;

public class ABIDAgent implements AlgorithmInterface {

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

	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	private HashMap<Long, TransEntry> transTable;
	private long[][] zobristTable;
	private long searchStartTime;

	public ABIDAgent(HeuristicInterface hi, boolean blackPlayer,int maxDepth) {
		this.maxDepth=maxDepth;
		this.hi = hi;
		this.blackPlayer = blackPlayer;
		// init zobrist table
		Random prng = new Random();
		zobristTable = new long[64][12];
		for (int i = 0; i < 64; ++i) {
			for (int j = 0; j < 12; ++j) {
				zobristTable[i][j] = prng.nextLong();
			}
		}

		// init transposition table
		transTable = new HashMap<Long, TransEntry>();
	}

	private boolean timeUp() {
		return ((System.currentTimeMillis() - searchStartTime) >= MAX_RUN_TIME - 30);
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

		int value;
		MoveValue searchResult = null;
		Move bestMove = null;
		// recursive
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

		return new MoveValue(bestMove, value);
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

		int value;
		MoveValue searchResult = null;
		Move bestMove = null;
		// recursive
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

		return new MoveValue(bestMove, value);
	}

	public Move compute(Conf conf) {
		this.evaluatednodes = 0;
		this.searchednodes = 0;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		int depth = 1;
		MoveValue best;
		this.searchStartTime = System.currentTimeMillis();

		if (!this.blackPlayer) {
			best = alphaBetaWithMemory_R(conf, null, alpha, beta, depth, Ply.MAX);
			while ((depth < maxDepth) && (!timeUp())) {
				++depth;
				best = alphaBetaWithMemory_R(conf, null, alpha, beta, depth, Ply.MAX);
			}
		} else {
			best = alphaBetaWithMemory_B(conf, null, alpha, beta, depth, Ply.MAX);
			while ((depth < maxDepth) && (!timeUp())) {
				++depth;
				best = alphaBetaWithMemory_B(conf, null, alpha, beta, depth, Ply.MAX);
			}
		}
		
		System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes);
		return best.move;
	}
}
