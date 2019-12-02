package algorithms;

import java.util.Map;
import core.Main;
import heuristics.HeuristicInterface;
import memory.TranspositionTable;
import memory.ZobristGen;
import representation.Move;
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
	private int maxDepth;

	private static int MAX_RECORD = 5000;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	// private HashMap<Long, TransEntry> transTable;
	private Map<Long, TransEntry> transpositionTable;
	private long searchCutoff;
	private ZobristGen zg;
	private HeuristicInterface h;
	private boolean blackPlayer;
	private ArrayList equalValueMoves;

	public MTDFAgent(HeuristicInterface h, boolean blackPlayer, int maxDepth) {
		
		this.maxDepth=maxDepth;
		// init zobrist table
		zg = new ZobristGen();

		// init transposition table
		// transTable = new HashMap<Long, TransEntry>();
		transpositionTable = new TranspositionTable<Long,TransEntry>(MAX_RECORD);

		// init heuristic function
		this.h = h;

		this.blackPlayer = blackPlayer;
	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return (new Date().getTime() > searchCutoff - 30);
	}

	private boolean check(long hash) {

		return transpositionTable.containsKey(hash);

	}

	public Move compute(Conf root) {
		this.evaluatednodes=0;
		this.searchednodes=0;
		int depth = 1;
		int guess = 0;
		MoveValue best = null;

		// ChildMove state;

		// The search must be initialised with an invalid "parent" move to avoid
		// premature transposition table association
		// state = new ChildMove(board);
		if (this.blackPlayer) {

			this.searchCutoff = new Date().getTime() + MAX_RUN_TIME;
//		best = MTDF(root, guess, depth);
			while ((depth < maxDepth) && !timeUp() /** || debug < debug_max */
			) {
				// debug++;
//			depth++;
				++depth;
				best = MTDF_B(root, guess, depth);

				guess = best.value;

				// System.out.println("\n-------------- "+depth+" ---------------\n");

			}
			assert (best.move != null);
			System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes);
			System.out
					.println("\n FINAL depth: -------------- " + depth + " --------------- MOVE : " + best.move + "\n");
			return best.move;

		} else {
			this.searchCutoff = new Date().getTime() + MAX_RUN_TIME;
//			best = MTDF(root, guess, depth);
			while ((depth < maxDepth) && !timeUp() /** || debug < debug_max */
			) {
				// debug++;
				++depth;
				best = MTDF_R(root, guess, depth);
				guess = best.value;

				// System.out.println("\n-------------- "+depth+" ---------------\n");

			}
			assert (best.move != null);
			System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes);
			System.out
					.println("\n FINAL depth: -------------- " + depth + " --------------- MOVE : " + best.move + "\n");
			return best.move;
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
		do {
			beta = Math.max(value, lowerbound + 1);
			mv = alphaBetaWithMemory_R(state, mv.move, beta - 1, beta, depth, Ply.MAX);
			value = mv.value;
			if (value < beta) {
				upperbound = value;
			} else {
				lowerbound = value;
			}
		} while ((lowerbound < upperbound) && !timeUp());

		return mv;

	}

	private MoveValue MTDF_B(Conf state, int f, int depth) {
		int value, upperbound, lowerbound, beta;
		MoveValue mv = null;

		value = f;
		upperbound = Integer.MAX_VALUE;
		lowerbound = Integer.MIN_VALUE;
		do {
			beta = Math.max(value, lowerbound + 1);
			mv = alphaBetaWithMemory_B(state, mv.move, beta - 1, beta, depth, Ply.MAX);
			value = mv.value;
			if (value < beta) {
				upperbound = value;
			} else {
				lowerbound = value;
			}
		} while ((lowerbound < upperbound) && !timeUp());

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
	private MoveValue alphaBetaWithMemory_R(Conf conf, Move prec, int alpha, int beta, int depth, Ply step) {
		searchednodes++;
		int a, b, value = 0;
		Move bestMove = null;
		MoveValue searchResult;
//		NodeInfo trans;
		Conf tmp = null;
//		long hash = zg.zobristHash(conf.getForHash());

		// base case
		if ((depth == 0) || timeUp()) {
			evaluatednodes++;
//			System.out.println(
//					"valuto: " + prec + " || valore =" + h.evaluate_R(conf) + "\n configurazione: \n" + conf + "\n");
			return new MoveValue(prec, h.evaluate_R(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return new MoveValue(prec, -5000);

		} else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return new MoveValue(prec, 5000);
		}

		// trans table lookup
		/**
		 * if (check(hash)) { // potrebbe essere utile incapsulare il tutto in una
		 * classe e gestirla da li trans = transpositionTable.get(hash); // uitlizzando
		 * tecniche specifiche (LRU) System.out.println("entro nella tt con: "+hash+" ||
		 * della conf: \n"+conf+"\n"); if (trans.depth >= depth) { if (trans.lowerbound
		 * >= beta) { System.out.println("creo la mossa con il lower bound: "+prec);
		 * return new MoveValue(prec, trans.lowerbound); } if (trans.upperbound <=
		 * alpha) { System.out.println("creo la mossa con l'upper bound: "+prec); return
		 * new MoveValue(prec, trans.upperbound); } System.out.println("Non creo la
		 * mossa"); alpha = Math.max(alpha, trans.lowerbound); beta = Math.min(beta,
		 * trans.upperbound); } }
		 */
		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			a = alpha; // save original alpha
			// for (ChildMove child : children(move, Ply.MAX, false)) {
			for (Move childm : conf.getActions()) {
				try {
					tmp = childm.applyTo(conf); // MI DA ERRORE PERCHè NON C'è L'APPLY TO
					// System.out.println("analizzo la mossa: "+ childm+" || tmp: \n"+tmp+"\n");

				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

//				spesso potrebbero esserci mosse con ugual valore e vengono sovrascritte, si 
//				potrebbe implementare una sorta di randomizzazzione mettendole in una lista.
//				avevo pensato di fare un arrayList (serve na struttura che abbia accesso (per prendere
//				la mossa con un indice random), aggiunta in coda e clear/reset che siano costanti per far ciò

				searchResult = alphaBetaWithMemory_R(tmp, childm, a, beta, depth - 1, Ply.MIN);
				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childm;
				}
				a = Math.max(a, value);
				if (alpha >= beta)
					break; // prune
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			b = beta; // save original beta
//			Move childm;
//			LinkedList<Move> tmpList = (LinkedList<Move>) conf.getActions();
//			while(!tmpList.isEmpty()) {
			for (Move childm : conf.getActions()) {
//				childm = tmpList.pop();
				try {
					tmp = childm.applyTo(conf);
					// System.out.println("analizzo la mossa: "+ childm+" || tmp: \n"+tmp+"\n");
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				searchResult = alphaBetaWithMemory_R(tmp, childm, alpha, b, depth - 1, Ply.MAX);
				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childm;
				}
				b = Math.min(b, value);
				if (alpha >= beta)
					break; // prune
			}
//			tmpList = null;
		}

//		
//		// store trans table values
//		if (transpositionTable.containsKey(hash)) { // no getOrDefault in Java 1.5
//			trans = transpositionTable.get(hash);
//		} else {
//			trans = new NodeInfo();
//		}
//
//		if (trans.depth <= depth) {
//			// fail low implies an upper bound
//			if (value <= alpha) {
//				trans.upperbound = value;
//			}
//			// fail high implies a lower bound
//			else if (value >= beta) {
//				trans.lowerbound = value;
//			}
//			// accurate minimax value
//			else {
//				trans.lowerbound = value;
//				trans.upperbound = value;
//			}
//			trans.depth = depth;
//			transpositionTable.put(hash, trans);
//			//System.out.println("metto nella tt: "+hash+"\n"+conf);
//		}

		return new MoveValue(bestMove, value);
	}

	private MoveValue alphaBetaWithMemory_B(Conf conf, Move prec, int alpha, int beta, int depth, Ply step) {
		searchednodes++;
		int a, b, value = 0;
		Move bestMove = null;
		MoveValue searchResult;
//		NodeInfo trans;
		Conf tmp = null;
//		long hash = zg.zobristHash(conf.getForHash());

		// base case
		if ((depth == 0) || timeUp()) {
			evaluatednodes++;
//			System.out.println(
//					"valuto: " + prec + " || valore =" + h.evaluate_B(conf) + "\nconfigurazione: \n" + conf + "\n");
			return new MoveValue(prec, h.evaluate_B(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return new MoveValue(prec, +5000);

		} else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return new MoveValue(prec, -5000);
		}

		// trans table lookup
		/**
		 * if (check(hash)) { // potrebbe essere utile incapsulare il tutto in una
		 * classe e gestirla da li trans = transpositionTable.get(hash); // uitlizzando
		 * tecniche specifiche (LRU) System.out.println("entro nella tt con: "+hash+" ||
		 * della conf: \n"+conf+"\n"); if (trans.depth >= depth) { if (trans.lowerbound
		 * >= beta) { System.out.println("creo la mossa con il lower bound: "+prec);
		 * return new MoveValue(prec, trans.lowerbound); } if (trans.upperbound <=
		 * alpha) { System.out.println("creo la mossa con l'upper bound: "+prec); return
		 * new MoveValue(prec, trans.upperbound); } System.out.println("Non creo la
		 * mossa"); alpha = Math.max(alpha, trans.lowerbound); beta = Math.min(beta,
		 * trans.upperbound); } }
		 */
		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			a = alpha; // save original alpha
			// for (ChildMove child : children(move, Ply.MAX, false)) {
//			Move childm;
//			LinkedList<Move> tmpList = (LinkedList<Move>) conf.getActions();
//			while(!tmpList.isEmpty()) {
			for (Move childm : ((LinkedList<Move>) conf.getActions())) {
//				childm = tmpList.getFirst();
				try {
					tmp = childm.applyTo(conf); // MI DA ERRORE PERCHè NON C'è L'APPLY TO
					// System.out.println("analizzo la mossa: "+ childm+" || tmp: \n"+tmp+"\n");

				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				searchResult = alphaBetaWithMemory_B(tmp, childm, a, beta, depth - 1, Ply.MIN);
				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childm;
				}
				a = Math.max(a, value);
				if (alpha >= beta)
					break; // prune
			}
			// dovrebbe preoccuparsene il garbage collector
//			tmpList=null;
		} else { // min step
			value = Integer.MAX_VALUE;
			b = beta; // save original beta
			for (Move childm : conf.getActions()) {
				try {
					tmp = childm.applyTo(conf);
					// System.out.println("analizzo la mossa: "+ childm+" || tmp: \n"+tmp+"\n");
				} catch (InvalidActionException | CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				searchResult = alphaBetaWithMemory_B(tmp, childm, alpha, b, depth - 1, Ply.MAX);
				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childm;
				}
				b = Math.min(b, value);
				if (alpha >= beta)
					break; // prune
			}
		}

//		
//		// store trans table values
//		if (transpositionTable.containsKey(hash)) { // no getOrDefault in Java 1.5
//			trans = transpositionTable.get(hash);
//		} else {
//			trans = new NodeInfo();
//		}
//
//		if (trans.depth <= depth) {
//			// fail low implies an upper bound
//			if (value <= alpha) {
//				trans.upperbound = value;
//			}
//			// fail high implies a lower bound
//			else if (value >= beta) {
//				trans.lowerbound = value;
//			}
//			// accurate minimax value
//			else {
//				trans.lowerbound = value;
//				trans.upperbound = value;
//			}
//			trans.depth = depth;
//			transpositionTable.put(hash, trans);
//			//System.out.println("metto nella tt: "+hash+"\n"+conf);
//		}

		return new MoveValue(bestMove, value);
	}

}
