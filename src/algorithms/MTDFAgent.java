package algorithms;

import java.util.Map;
import java.util.Random;

import core.Main;
import heuristics.HeuristicInterface;
import memory.TranspositionTable;
import memory.ZobristGen;
import representation.Move;
import representation.Board;
import representation.Conf;
import representation.Conf.Status;
import representation.InvalidActionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class MTDFAgent implements AlgorithmInterface {

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

	static enum Ply {
		MAX, MIN
	}

	// for testing purpose
	private int searchednodes = 0;
	private int evaluatednodes = 0;
	private int searchednodesold;
	private int evaluatednodesold;
	private int maxDepth;
	private int startDepth;

	private static int MAX_RECORD = 20000;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	// private HashMap<Long, TransEntry> transTable;
	private Map<Long, TransEntry> transTable;
	private long searchCutoff;
	private long[][] zobristTable;
	private HeuristicInterface hi;
	private boolean blackPlayer;
	private boolean ibreak = false;

	public MTDFAgent(HeuristicInterface hi, boolean blackPlayer, int startDepth, int maxDepth) {
		this.startDepth = startDepth;
		this.maxDepth = maxDepth;
		this.hi = hi;
		this.blackPlayer = blackPlayer;

		Random prng = new Random();
		zobristTable = new long[64][12];
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 12; j++) {
				zobristTable[i][j] = prng.nextLong();
			}
		}

		transTable = new TranspositionTable<Long, TransEntry>(MAX_RECORD);

	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return (System.currentTimeMillis() > searchCutoff - 30);
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

	public Move compute(Conf root) {
		this.evaluatednodes = 0;
		this.searchednodes = 0;
		this.evaluatednodesold = 0;
		this.searchednodesold = 0;
		this.ibreak = false;
		int depth = startDepth;
		int guess = 0;
		MoveValue newBest = null;
		MoveValue oldBest = null;

		if (this.blackPlayer) {

			this.searchCutoff = System.currentTimeMillis() + MAX_RUN_TIME;
			while (!timeUp() && (depth < maxDepth)) {
				evaluatednodes = 0;
				searchednodes = 0;
				oldBest = newBest;
				newBest = MTDF_B(root, guess, depth);

				if (!this.ibreak) {
					evaluatednodesold = evaluatednodes;
					searchednodesold = searchednodes;
					guess = newBest.value;
					++depth;
				} else {
					break;
				}

			}

			if (this.ibreak) {
				System.out.println("\nEvaluatedNodes: " + evaluatednodesold + "\nSearchedNodes :" + searchednodesold
						+ "\ndepth :" + (depth - 2));
				return oldBest.move;
			} else {
				System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes
						+ "\ndepth :" + (depth--));
				return newBest.move;

			}

		} else {

			this.searchCutoff = System.currentTimeMillis() + MAX_RUN_TIME;
			while (!timeUp() && (depth < maxDepth)) {
				evaluatednodes = 0;
				searchednodes = 0;
				oldBest = newBest;
				newBest = MTDF_R(root, guess, depth);

				if (!this.ibreak) {
					evaluatednodesold = evaluatednodes;
					searchednodesold = searchednodes;
					guess = newBest.value;
					++depth;
				} else {
					break;
				}

			}

			if (this.ibreak) {
				System.out.println("\nEvaluatedNodes: " + evaluatednodesold + "\nSearchedNodes :" + searchednodesold
						+ "\ndepth :" + (depth - 2));
				return oldBest.move;
			} else {
				System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes
						+ "\ndepth :" + (depth--));
				return newBest.move;

			}
		}
	}

	/**
	 * Performs MTD-f search by calling many zero-width alpha-beta searches.
	 *
	 * @param state the root state to search from
	 * @param guess the first best guess of minimax value
	 * @param depth the maximum search depth
	 * @return the move corresponding to the true minimax value
	 */
	private MoveValue MTDF_R(Conf state, int f, int depth) {
		int value, upperbound, lowerbound, beta;
		MoveValue mv = null;

		value = f;
		upperbound = Integer.MAX_VALUE;
		lowerbound = Integer.MIN_VALUE;
		beta = Math.max(value, lowerbound + 1);
		mv = alphaBetaWithMemory_R(state, null, beta - 1, beta, depth, Ply.MAX);
		while ((lowerbound < upperbound) && !this.ibreak) {
			beta = Math.max(value, lowerbound + 1);
			mv = alphaBetaWithMemory_R(state, mv.move, beta - 1, beta, depth, Ply.MAX);

			if (mv == null)
				return null;

			value = mv.value;
			if (value < beta) {
				upperbound = value;
			} else {
				lowerbound = value;
			}
		}

		return mv;

	}

	private MoveValue MTDF_B(Conf state, int f, int depth) {
		int value, upperbound, lowerbound, beta;
		MoveValue mv = null;

		value = f;
		upperbound = Integer.MAX_VALUE;
		lowerbound = Integer.MIN_VALUE;
		beta = Math.max(value, lowerbound + 1);
		mv = alphaBetaWithMemory_B(state, null, beta - 1, beta, depth, Ply.MAX);
		while ((lowerbound < upperbound) && !this.ibreak) {
			beta = Math.max(value, lowerbound + 1);
			mv = alphaBetaWithMemory_B(state, mv.move, beta - 1, beta, depth, Ply.MAX);

			if (mv == null)
				return null;

			value = mv.value;
			if (value < beta) {
				upperbound = value;
			} else {
				lowerbound = value;
			}
		}
		

		return mv;

	}

	/**
	 * Performs Minimax search using alpha-beta pruning with transposition tables.
	 *
	 * @param move  the game state to search from
	 * @param alpha the maximised lowerbound
	 * @param beta  the minimised upperbound
	 * @param depth the maximum search depth
	 * @param step  the current minimax step
	 * @return the move corresponding to the minimax value
	 */
	private MoveValue alphaBetaWithMemory_R(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {
		searchednodes++;

		if (timeUp()) {
			this.ibreak = true;
			return null;
		}

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

		int a, b, value = 0;
		Move bestMove = null;
		MoveValue searchResult;

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
			a = alpha; // save original alpha
			for (Move childmv : conf.getActions()) {
//				spesso potrebbero esserci mosse con ugual valore e vengono sovrascritte, si 
//				potrebbe implementare una sorta di randomizzazzione mettendole in una lista.
//				avevo pensato di fare un arrayList (serve na struttura che abbia accesso (per prendere
//				la mossa con un indice random), aggiunta in coda e clear/reset che siano costanti per far ciò

				searchResult = alphaBetaWithMemory_R(childmv.applyTo(conf), childmv, a, beta, depth - 1, Ply.MIN);

				if (searchResult == null)
					return null;

				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				a = Math.max(a, value);
				if (alpha >= beta)
					break; // prune
			}

		} else { // min step
			value = Integer.MAX_VALUE;
			b = beta; // save original beta
			for (Move childmv : conf.getActions()) {

				searchResult = alphaBetaWithMemory_R(childmv.applyTo(conf), childmv, alpha, b, depth - 1, Ply.MAX);

				if (searchResult == null)
					return null;

				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				b = Math.min(b, value);
				if (alpha >= beta)
					break; // prune
			}
//			tmpList = null;
		}

//		
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

	private MoveValue alphaBetaWithMemory_B(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {
		searchednodes++;

		if (timeUp()) {
			this.ibreak = true;
			return null;
		}

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

		int a, b, value = 0;
		Move bestMove = null;
		MoveValue searchResult;

		// base case
		if ((depth == 0)) {
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
			a = alpha; // save original alpha
			for (Move childmv : conf.getActions()) {
//				spesso potrebbero esserci mosse con ugual valore e vengono sovrascritte, si 
//				potrebbe implementare una sorta di randomizzazzione mettendole in una lista.
//				avevo pensato di fare un arrayList (serve na struttura che abbia accesso (per prendere
//				la mossa con un indice random), aggiunta in coda e clear/reset che siano costanti per far ciò

				searchResult = alphaBetaWithMemory_B(childmv.applyTo(conf), childmv, a, beta, depth - 1, Ply.MIN);

				if (searchResult == null)
					return null;

				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				a = Math.max(a, value);
				if (alpha >= beta)
					break; // prune
			}

		} else { // min step
			value = Integer.MAX_VALUE;
			b = beta; // save original beta
			for (Move childmv : conf.getActions()) {

				searchResult = alphaBetaWithMemory_B(childmv.applyTo(conf), childmv, alpha, b, depth - 1, Ply.MAX);

				if (searchResult == null)
					return null;

				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				b = Math.min(b, value);
				if (alpha >= beta)
					break; // prune
			}
//			tmpList = null;
		}

//		
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

}
