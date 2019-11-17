package algorithm;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import heuristics.HeuristicInterface;
import memory.ZobristGen;
import representation.Move;
import representation.Conf;
import representation.InvalidActionException;

import java.util.Date;

/**
 * MTD-f search implementation for Mancala Based on pseudocode from:
 * people.csail.mit.edu/plaat/mtdf.html
 *
 * CITS3001 Lab6
 *
 * Jesse Wyatt (20756971)
 */
public class MTDFAgent implements AlgorithmInterface {

	/**
	 * Inner class to hold move / score pairs
	 */

	/**
	 * static class MoveScore { public Move move; public int score;
	 * 
	 * public MoveScore(Move move, int score) { this.move = move; this.score =
	 * score; }
	 * 
	 * public MoveScore() { this.move = null; // qui si metteva -1, potrebbe essere
	 * utile creare una mossa nulla. this.score = -1; } }
	 * 
	 * 
	 * Inner class to hold move / child-state pairs
	 */
	/**
	 * static class ChildMove { public Move move; public Conf conf;
	 * 
	 * public ChildMove(Move move, Conf conf) { this.move = move; this.conf = conf;
	 * }
	 * 
	 * public ChildMove(Conf conf) { this.move = null; // prima metteva -10 per non
	 * far mettere roba nell TT this.conf = conf; } }
	 * 
	 **/
	/**
	 * Inner class to hold transposition table entries
	 */
	static class TransEntry {
		public int depth;
		public int upperbound;
		public int lowerbound;

		public TransEntry() {
			this.depth = 0;
			this.upperbound = 200;
			this.lowerbound = -200;
		}
	}

	static enum Ply {
		MAX, MIN
	}

	private static int MAX_SEARCH_DEPTH = 100;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds
	private HashMap<Long, TransEntry> transTable;
	private long searchCutoff;
	private ZobristGen zg;
	private HeuristicInterface h;

	/**
	 * Constructs an instance of the AI agent for gameplay.
	 * 
	 * Initialises the bitstring table for Zobrist hashing and a hashtable to map
	 * transpositions.
	 */
	public MTDFAgent(HeuristicInterface h) {
		// init zobrist table
		zg = new ZobristGen();

		// init transposition table
		transTable = new HashMap<Long, TransEntry>();

		// init heuristic function
		this.h = h;
	}

	/**
	 * Checks to see if the move timer is nearly up. Uses java.util.Date to avoid
	 * overzealous filtering of "Syst*m" calls.
	 * 
	 * @return true if time is up, otherwise false
	 */
	private boolean timeUp() {
		return (new Date().getTime() > searchCutoff);
	}

	/**
	 * Checks if a game state is a terminal state.
	 * 
	 * @param state the game state
	 * @return true is state is terminal, false otherwise
	 */

	/*
	 * private boolean terminal(int[] state) { // if south empty then state is
	 * terminal int count = 0; for (int i = 0; i < 6; ++i) count += state[i]; if
	 * (count == 0) return true;
	 * 
	 * // if north empty then state is terminal count = 0; for (int i = 7; i < 13;
	 * ++i) count += state[i]; if (count == 0) return true;
	 * 
	 * // else state not terminal return false; }
	 * 
	 */
	/**
	 * Estimates the value of a game state.
	 * 
	 * @param state the game state
	 * @return the estimated value of the state
	 */
	/*
	 * private int evaluate(int[] state) { int score = 0;
	 * 
	 * // check endgame conditions if (terminal(state)) { for (int i = 0; i < 7;
	 * ++i) score += state[i]; for (int i = 7; i < 14; ++i) score -= state[i];
	 * 
	 * if (score > 0) { return 100; // victory } else if (score < 0) { return -100;
	 * // loss } else { return 0; // draw } }
	 * 
	 * // calculate board value for (int i = 0; i < 6; ++i) { if ((state[i] == 0) &&
	 * (state[12 - i] > 0)) { // empty house rule score += state[12 - i]; //
	 * potential empty house captures are worth half } else { score += state[i]; //
	 * house seeds have standard value } } score += 2 * state[6]; // siloed seeds
	 * are double
	 * 
	 * for (int i = 7; i < 13; ++i) { if ((state[i] == 0) && (state[12 - i] > 0)) {
	 * score -= state[12 - i]; } else { score -= state[i]; } } score -= 2 *
	 * state[13];
	 * 
	 * return score; }
	 */

	/**
	 * Checks move to see if valid, and if the corresponding state is contained in
	 * the transposition table.
	 *
	 * @param move the game move to be checked
	 * @param hash the Zobrist hash of the equivalent move state
	 * @return true if move valid and state contained in transposition table
	 */

	private boolean check(long hash) {

		return transTable.containsKey(hash);

	}

	/**
	 * Public API call for requesting moves from the agent. The game is assumed to
	 * be the Kalah(6,3) variant with 6 houses per side, and initially 3 seeds per
	 * house.
	 *
	 * The board is an int array of length 14. Each board entry indicates the number
	 * of seeds located in that pit. The agent's houses are 0-5 and their store is
	 * 6. The opponent's houses are 7-12 and their store is 13. Seeds are played
	 * anti-clockwise and board ordering is circular with the pit at index 0
	 * following the pit at index 13.
	 *
	 * @param board the current game state
	 * @return the house the agent would like to play from this turn
	 */
	public Move compute(Conf root) {
		int depth = 1;
		int guess = 0;
		MoveValue best;
		// ChildMove state;

		// The search must be initialised with an invalid "parent" move to avoid
		// premature transposition table association
		// state = new ChildMove(board);

		this.searchCutoff = new Date().getTime() + MAX_RUN_TIME;
		best = MTDF(root, guess, depth);
		while ((depth < MAX_SEARCH_DEPTH) && (!timeUp())) {
			++depth;
			best = MTDF(root, guess, depth);
			guess = best.value;
		}

		return best.move;
	}

	/**
	 * Performs MTD-f search by calling many zero-width alpha-beta searches.
	 *
	 * @param state the root state to search from
	 * @param guess the first best guess of minimax value
	 * @param depth the maximum search depth
	 * @return the move corresponding to the true minimax value
	 */
	private MoveValue MTDF(Conf state, int f, int depth) {
		int value, upperbound, lowerbound, beta;
		MoveValue mv = new MoveValue();

		value = f;
		upperbound = Integer.MAX_VALUE;
		lowerbound = Integer.MIN_VALUE;
		do {
			beta = Math.max(value, lowerbound + 1);
			mv = alphaBetaWithMemory(state, mv.move, beta - 1, beta, depth, Ply.MAX);
			value = mv.value;
			if (value < beta) {
				upperbound = value;
			} else {
				lowerbound = value;
			}
		} while (lowerbound < upperbound);

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
	private MoveValue alphaBetaWithMemory(Conf conf, Move prec, int alpha, int beta, int depth, Ply step) {
		int a, b, value = 0;
		Move bestMove = null;
		MoveValue searchResult;
		TransEntry trans;
		Conf tmp = null;
		long hash = zg.zobristHash(conf.getConf());

		// base case
		if ((depth == 0) || conf.getStatus() != Conf.Status.Ongoing) {
			return new MoveValue(prec, h.evaluate(conf));
		}

		// trans table lookup
		if (check(hash)) { // potrebbe essere utile incapsulare il tutto in una classe e gestirla da li
			trans = transTable.get(hash); // uitlizzando tecniche specifiche (LRU)
			if (trans.depth >= depth) {
				if (trans.lowerbound >= beta) {
					return new MoveValue(prec, trans.lowerbound);
				}
				if (trans.upperbound <= alpha) {
					return new MoveValue(prec, trans.upperbound);
				}
				alpha = Math.max(alpha, trans.lowerbound);
				beta = Math.min(beta, trans.upperbound);
			}
		}

		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			a = alpha; // save original alpha
			// for (ChildMove child : children(move, Ply.MAX, false)) {
			for (Move childm : conf.getActions()) {
				try {
					tmp=childm.applyTo(conf);
				} catch (InvalidActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				searchResult = alphaBetaWithMemory(tmp, childm, a, beta, depth - 1, Ply.MIN);
				if (searchResult.value >= value) {
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
			for (Move childm : conf.getActions()) {
				searchResult = alphaBetaWithMemory(tmp,childm, alpha, b, depth - 1, Ply.MAX);
				if (searchResult.value <= value) {
					value = searchResult.value;
					bestMove = childm;
				}
				b = Math.min(b, value);
				if (alpha >= beta)
					break; // prune
			}
		}

		// store trans table values
		if (transTable.containsKey(hash)) { // no getOrDefault in Java 1.5
			trans = transTable.get(hash);
		} else {
			trans = new TransEntry();
		}

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

	/**
	 * Generates all valid child states of a move.
	 *
	 * @param parent    the parent state
	 * @param step      the current minimax step (the player to generate moves)
	 * @param extraTurn true if recursively calculating children for an extra turn,
	 *                  false otherwise
	 * @return a list of all valid child moves
	 */

	/**
	 * private ArrayList<ChildMove> children(ChildMove parent, Ply step, boolean
	 * extraTurn) { ArrayList<ChildMove> childmoves = new ArrayList<ChildMove>();
	 * ChildMove child;
	 * 
	 * if (step == Ply.MAX) { //our moves for (int i = 0; i < 6; ++i) { if
	 * (parent.state[i] > 0) { //move is valid if (extraTurn) { // if extra turn,
	 * treat as same move as parent child = new ChildMove(parent.move,
	 * Arrays.copyOf(parent.state, 14)); } else { child = new ChildMove(i,
	 * Arrays.copyOf(parent.state, 14)); } //sow seeds from i int j = i; int seeds =
	 * parent.state[i]; child.state[i] = 0; while(seeds > 0) { ++j; j %= 14; if (j
	 * != 13) { //don't place in opponent store --seeds; child.state[j] += 1; } } if
	 * (j == 6) { //extra turn if (terminal(child.state)) { //if move ends the game
	 * it can't give an extra turn childmoves.add(child); } else { //recursively
	 * find extra move children of this state childmoves.addAll(children(child,
	 * step, true)); } } else { if ((j >= 0) && (j <= 5) && (child.state[j] == 1) &&
	 * (child.state[12-j] > 0)) { //empty house rule child.state[6] = child.state[6]
	 * + child.state[12-j] + 1; child.state[j] = 0; child.state[12-j] = 0; }
	 * childmoves.add(child); } } } } else { //enemy moves for (int i = 7; i < 13;
	 * ++i) { if (parent.state[i] > 0) { //move is valid if (extraTurn) { child =
	 * new ChildMove(parent.move, Arrays.copyOf(parent.state, 14)); } else { child =
	 * new ChildMove(i, Arrays.copyOf(parent.state, 14)); } //sow seeds from i int j
	 * = i; int seeds = parent.state[i]; child.state[i] = 0; while(seeds > 0) { ++j;
	 * j %= 14; if (j != 6) { //don't place in our store --seeds; child.state[j] +=
	 * 1; } } if (j == 13) { //extra turn if (terminal(child.state)) { //if move
	 * ends the game it can't give an extra turn childmoves.add(child); } else {
	 * //recursively find extra move children of this state
	 * childmoves.addAll(children(child, step, true)); } } else { if ((j >= 7) && (j
	 * <= 12) && (child.state[j] == 1) && (child.state[12-j] > 0)) { //empty house
	 * rule child.state[13] = child.state[13] + child.state[12-j] + 1;
	 * child.state[j] = 0; child.state[12-j] = 0; } childmoves.add(child); } } } }
	 * return childmoves; }
	 **/
	/**
	 * The agents name.
	 * 
	 * @return a hardcoded string, the name of the agent.
	 */
	public String name() {
		return "MTD-f Agent";
	}

	/**
	 * A method to reset the agent for a new game.
	 */
	public void reset() {
		// nuffin goes ere
	}

}
