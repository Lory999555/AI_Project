package algorithms;

import java.util.Random;

import heuristics.HeuristicInterface;
import memory.TranspositionTable;
import representation.Board;
import representation.Conf;
import representation.Move;
import representation.Conf.Status;

/**
 * Alpha-Beta Pruning search with memory for Mancala
 * 
 * OOP version for passing results
 */
public class ABWMAgent implements AlgorithmInterface {

	public static double tot = 0;
	public static double cont = 0;

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
	private int startDepth;

	private HeuristicInterface hi;
	private boolean blackPlayer;
	private long searchCutoff;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	private TranspositionTable<Long, TransEntry> transTable;
	private long[][] zobristTable;
	private boolean ibreak = false;

	public ABWMAgent(HeuristicInterface hi, boolean blackPlayer, int startDepth, int maxDepth) {
		// init zobrist table
		this.maxDepth = maxDepth;
		this.startDepth = startDepth;
		this.hi = hi;
		this.blackPlayer = blackPlayer;
		Random prng = new Random();
		zobristTable = new long[64][12];

		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 12; j++) {
				zobristTable[i][j] = prng.nextLong();
			}
		}

		// Provare con i conf senza hash
		// init transposition table
		transTable = new TranspositionTable<Long, TransEntry>(500000);
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

	private long zobristHash_2(long redP, long blackP) {
		long key = 0;
		int i = 0;
		long tmp, bit;
		tmp = redP ^ blackP;
		while (tmp != 0) {
			bit = tmp & -tmp;
			tmp ^= bit;
			key ^= zobristTable[Board.getSquare(bit)][0];
		}
		return key;

	}

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
					evaluatednodes++;
					return new MoveValue(move, trans.lowerbound);
				}
				if (trans.upperbound <= alpha) {
					evaluatednodes++;
					return new MoveValue(move, trans.upperbound);
				}
				alpha = Math.max(alpha, trans.lowerbound);
				beta = Math.min(beta, trans.upperbound);
			}
		}

		Move bestMove = null;
		MoveValue searchResult = null;
		int value;

		if ((depth == 0)) {
			evaluatednodes++;
			return new MoveValue(move, hi.evaluate_R(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return new MoveValue(move, -5000);
		}

		else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return new MoveValue(move, 5000);
		}

		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			for (Move childmv : conf.getActions()) {
				searchResult = alphaBetaWithMemory_R(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MIN);

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
			for (Move childmv : conf.getActions()) {
				searchResult = alphaBetaWithMemory_R(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MAX);

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

		assert (System.currentTimeMillis() < searchCutoff);

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
					evaluatednodes++;
					return new MoveValue(move, trans.lowerbound);
				}
				if (trans.upperbound <= alpha) {
					evaluatednodes++;
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
				searchResult = alphaBetaWithMemory_B(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MIN);

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
			for (Move childmv : conf.getActions()) {

				searchResult = alphaBetaWithMemory_B(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MAX);

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

		assert (System.currentTimeMillis() < searchCutoff);

		return new MoveValue(bestMove, value);
	}

	public Move compute(Conf conf) {
		this.evaluatednodes = 0;
		this.searchednodes = 0;
		this.ibreak = false;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		MoveValue newBest = null;
		MoveValue oldBest = null;
		this.searchCutoff = System.currentTimeMillis() + MAX_RUN_TIME;

		int d = startDepth;
		while (!timeUp() && d <= maxDepth) {
			oldBest = newBest;
			if (!this.blackPlayer)
				newBest = alphaBetaWithMemory_R(conf, null, alpha, beta, d, Ply.MAX);
			else
				newBest = alphaBetaWithMemory_B(conf, null, alpha, beta, d, Ply.MAX);
			d++;

		}

		// controllare se è possibile togliere le cose per le versioni non old perchè
		// forse non vengono mai usate in realtà!

		// vedere se è possibile tolgiere la maggior parte dei metodi timesUp andandoli
		// a sostituire con il check di this.ibreak
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

//	private boolean strongTimeUp() {
//		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
//				.indexOf("-agentlib:jdwp") > 0)
//			return false;
//		return (new Date().getTime() > (searchCutoff - 30));
//	}
}
