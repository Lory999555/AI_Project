package algorithms;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import heuristics.HeuristicInterface;
import representation.Board;
import representation.Conf;
import representation.Move;

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
	private int maxdepth = MAX_SEARCH_DEPTH;
	private HeuristicInterface h;
	private boolean blackPlayer;
	private long searchCutoff;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds
	private static int MAX_SEARCH_DEPTH = 200;
	private HashMap<Long, TransEntry> transTable;
	private long[][] zobristTable;
	private long searchStartTime;

	public ABIDAgent() {
		this.h = h;
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
		return ((System.currentTimeMillis() - searchStartTime) >= MAX_RUN_TIME);
	}


	private long zobristHash(long[] pieces) {
		long key = 0;
		int i = 0;
		long tmp,bit;
		while(i < 12) {
			
			tmp=pieces[i];	
			while(tmp!=0) {
				bit=pieces[i] & -pieces[i];
				tmp^=bit;
				key^=zobristTable[Board.getSquare(bit)][i];
			}
			
			i++;
		}
		
//		for (int i = 0; i < 64; ++i) {
//			key ^= zobristTable[i][state[i]];
//		}
		return key;
	}

	private MoveValue alphaBetaWithMemory(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {
		int value, bestMove = 0;
		MoveValue searchResult;
		TransEntry trans;
		long hash = zobristHash(conf.getForHash());

		// trans table lookup
		if (transTable.containsKey(hash)) {
			trans = transTable.get(hash);
			if (trans.depth >= depth) {
				if (trans.lowerbound >= beta) {
					return new MoveValue(move.move, trans.lowerbound);
				}
				if (trans.upperbound <= alpha) {
					return new MoveScore(move.move, trans.upperbound);
				}
				alpha = Math.max(alpha, trans.lowerbound);
				beta = Math.min(beta, trans.upperbound);
			}
		}

		// base case
		if ((depth == 0) || terminal(move.state)) {
			return new MoveScore(move.move, evaluate(move.state));
		}

		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			for (ChildMove child : children(move, Ply.MAX, false)) {
				searchResult = alphaBetaWithMemory(child, alpha, beta, depth - 1, Ply.MIN);
				if (searchResult.score >= value) {
					value = searchResult.score;
					bestMove = child.move;
				}
				alpha = Math.max(alpha, value);
				if (alpha >= beta)
					break; // pruning
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for (ChildMove child : children(move, Ply.MIN, false)) {
				searchResult = alphaBetaWithMemory(child, alpha, beta, depth - 1, Ply.MAX);
				if (searchResult.score <= value) {
					value = searchResult.score;
					bestMove = child.move;
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

		return new MoveScore(bestMove, value);
	}

	private ArrayList<ChildMove> children(ChildMove parent, Ply step, boolean extraTurn) {
		ArrayList<ChildMove> childmoves = new ArrayList<ChildMove>();
		ChildMove child;

		if (step == Ply.MAX) { // our moves
			for (int i = 0; i < 6; ++i) {
				if (parent.state[i] > 0) {
					// move is valid
					if (extraTurn) { // if extra turn, treat as same move as parent
						child = new ChildMove(parent.move, Arrays.copyOf(parent.state, 14));
					} else {
						child = new ChildMove(i, Arrays.copyOf(parent.state, 14));
					}
					// sow seeds from i
					int j = i;
					int seeds = parent.state[i];
					child.state[i] = 0;
					while (seeds > 0) {
						++j;
						j %= 14;
						if (j < 13) { // don't place in opponent store
							--seeds;
							child.state[j] += 1;
						}
					}
					if (j == 6) { // extra turn
						if (terminal(child.state)) { // if move ends the game it can't give an extra turn
							childmoves.add(child);
						} else { // recursively find extra move children of this state
							childmoves.addAll(children(child, step, true));
						}
					} else {
						if ((j >= 0) && (j <= 5) && (child.state[j] == 1) && (child.state[12 - j] > 0)) { // empty house
																											// rule
							child.state[6] = child.state[6] + child.state[12 - j] + 1;
							child.state[j] = 0;
							child.state[12 - j] = 0;
						}
						childmoves.add(child);
					}
				}
			}
		} else { // enemy moves
			for (int i = 7; i < 13; ++i) {
				if (parent.state[i] > 0) {
					// move is valid
					if (extraTurn) {
						child = new ChildMove(parent.move, Arrays.copyOf(parent.state, 14));
					} else {
						child = new ChildMove(i, Arrays.copyOf(parent.state, 14));
					}
					// sow seeds from i
					int j = i;
					int seeds = parent.state[i];
					child.state[i] = 0;
					while (seeds > 0) {
						++j;
						j %= 14;
						if (j < 6) { // don't place in our store
							--seeds;
							child.state[j] += 1;
						}
					}
					if (j == 13) { // extra turn
						if (terminal(child.state)) { // if move ends the game it can't give an extra turn
							childmoves.add(child);
						} else { // recursively find extra move children of this state
							childmoves.addAll(children(child, step, true));
						}
					} else {
						if ((j >= 7) && (j <= 12) && (child.state[j] == 1) && (child.state[12 - j] > 0)) { // empty
																											// house
																											// rule
							child.state[13] = child.state[13] + child.state[12 - j] + 1;
							child.state[j] = 0;
							child.state[12 - j] = 0;
						}
						childmoves.add(child);
					}
				}
			}
		}
		return childmoves;
	}

	/**
	 * Allows the agent to nominate the house the agent would like to move seeds
	 * from. The agent will allways have control of houses 0-5 with store at 6. Any
	 * move other than 0-5 will result in a forfeit. An move from an empty house
	 * will result in a forfeit. A legal move will always be available. Assume your
	 * agent has 0.5 seconds to make a move.
	 * 
	 * @param board the current state of the game. The board is an int array of
	 *              length 14, indicating the 12 houses and 2 stores. The agent's
	 *              house are 0-5 and their store is 6. The opponents houses are
	 *              7-12 and their store is 13. Board[i] is the number of seeds in
	 *              house (store) i. board[(i+1}%14] is the next house (store)
	 *              anticlockwise from board[i]. This will be consistent between
	 *              moves of a normal game so the agent can maintain a strategy
	 *              space.
	 * @return the house the agent would like to move the seeds from this turn.
	 */
	public int compute(Conf conf) {
		this.evaluatednodes = 0;
		this.searchednodes = 0;
		this.maxdepth = MAX_SEARCH_DEPTH;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		int depth = 1;
		ChildMove state = new ChildMove(-1, board);
		MoveScore best;

		this.searchStartTime = System.currentTimeMillis();
		best = alphaBetaWithMemory(state, alpha, beta, depth, Ply.MAX);
		while ((depth < MAX_SEARCH_DEPTH) && (!timeUp())) {
			++depth;
			best = alphaBetaWithMemory(state, alpha, beta, depth, Ply.MAX);
		}
		return best.move;
	}
}
