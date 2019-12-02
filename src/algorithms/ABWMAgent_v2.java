// NOTE: This is extra code, not a critical part of our Othello assignment
package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import heuristics.HeuristicInterface;
import logger.GraphVizPrinter;
import representation.Conf;
import representation.Conf.Status;
import representation.InvalidActionException;
import representation.Move;

public class ABWMAgent_v2 implements AlgorithmInterface {

	static enum Ply {
		MAX, MIN
	};

	private static final int MIN_VAL = -5000;
	private static final int MAX_VAL = 5000;

	class SearchNode {
		float lowerbound = MIN_VAL, upperbound = MAX_VAL;
	}

	private static final boolean ITERATIVE_DEEPENING = false;

	private static final boolean DEBUG = true;

	private HashMap<Conf, SearchNode> transpositionTable;
	private boolean blackPlayer;

	// Time we have to compute a move in seconds
	private int searchTime;

	// Time we have left to search
	private long startTimeMillis;
	private HeuristicInterface hi;

	private int maxSearchDepth;

	public ABWMAgent_v2(HeuristicInterface hi, boolean blackPlayer, int maxSearchDepth) {
		this.hi = hi;
		this.blackPlayer = blackPlayer;
		searchTime = 1;
		this.maxSearchDepth = maxSearchDepth;
	}

	@Override
	public Move compute(Conf conf) {
		startTimeMillis = System.currentTimeMillis();
		transpositionTable = new HashMap<Conf, SearchNode>();

		List<MoveValue> actions = buildMVList(conf.getActions());

		int search_depth = ITERATIVE_DEEPENING ? maxSearchDepth : 1;

		for (int d = 1; d <= search_depth; d++) {

			// Easier to see than min and max int
			float alpha = MIN_VAL;
			float beta = MAX_VAL;

			if (DEBUG)
				GraphVizPrinter.setState(conf);
			for (MoveValue mv : actions) {
				Conf child;
				try {
					child = mv.move.applyTo(conf);
					int depth = ITERATIVE_DEEPENING ? d : maxSearchDepth;
					float mmv = AlphaBetaWithMemory(child, alpha, beta, depth - 1, Ply.MIN);
					if (DEBUG)
						GraphVizPrinter.setRelation(child, mmv, conf);
					mv.value = (int) mmv;

					/*
					 * // Update A-B bounds if (maximizer) alpha = Math.max(alpha, mmv); else beta =
					 * Math.min(beta, mmv);
					 */

				} catch (InvalidActionException | CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}

			Collections.sort(actions, Collections.reverseOrder());
			System.out.println("Best Action: " + actions.get(0));
			if (DEBUG) {
				if (ITERATIVE_DEEPENING)
					GraphVizPrinter.printGraphToFileWDeepening(d);
				else
					GraphVizPrinter.printGraphToFile();
			}
			if (times_up()) {
				System.out.println("ABWM got to depth " + d);
				break;
			}
		}

		return getRandomBestAction(actions);
	}

	/**
	 * Returns a random action from among the best actions in the given list
	 * 
	 * @param mv The actions to examine
	 * @return The selected action
	 */
	private Move getRandomBestAction(List<MoveValue> mv) {
		List<Move> bestActions = new LinkedList<Move>();

		float bestV = mv.get(0).value;
		for (MoveValue avp : mv) {
			if (avp.value != bestV)
				break;

			bestActions.add(avp.move);
		}

		Collections.shuffle(bestActions);

		return bestActions.get(0);
	}

	/**
	 * Checks to see if the maximum search time for this move has elapsed
	 * 
	 * @return true if we need to stop searching, false otherwise
	 */
	private boolean times_up() {
		return (System.currentTimeMillis() - startTimeMillis) > 1000 * searchTime;
	}

	/**
	 * Helper to create a list of ActionValuePairs with value of 0 from a list of
	 * actions
	 * 
	 * @param actions The actions to convert
	 * @return A list of actionvaluepairs
	 */
	private List<MoveValue> buildMVList(List<Move> actions) {
		List<MoveValue> res = new ArrayList<MoveValue>();

		for (Move a : actions) {
			MoveValue p = new MoveValue(a, 0);
			res.add(p);
		}

		return res;
	}

	private float AlphaBetaWithMemory(Conf conf, float alpha, float beta, int d, Ply step)
			throws InvalidActionException {
		float g;
		GraphVizPrinter.setState(conf);

		if (transpositionTable.containsKey(conf)) {
			if (DEBUG)
				GraphVizPrinter.setCached(conf);
			SearchNode node = transpositionTable.get(conf);
			// commented out a-b pruning for testing
			// if (node.lowerbound >= beta) return node.lowerbound;
			// if (node.upperbound <= alpha) return node.upperbound;
			alpha = alpha > node.lowerbound ? alpha : node.lowerbound;
			beta = beta < node.upperbound ? beta : node.upperbound;
		}

		if (d == 0 || conf.getStatus() != Status.Ongoing) {
			g = hi.evaluate_R(conf);
		} else {
			List<Move> actions = conf.getActions();

			if (step == Ply.MAX) {
				g = MIN_VAL;
				float a = alpha;

				for (int i = 0; i < actions.size()/* && g < beta */; i++) {
					Conf child;
					try {
						child = actions.get(i).applyTo(conf);
						float abwm = AlphaBetaWithMemory(child, a, beta, d - 1, Ply.MIN);
						if (DEBUG)
							GraphVizPrinter.setRelation(child, abwm, conf);
						g = g > abwm ? g : abwm;
						a = g > a ? g : a;
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else {
				g = MAX_VAL;
				float b = beta;

				for (int i = 0; i < actions.size()/* && g > alpha */; i++) {
					Conf child;
					try {
						child = actions.get(i).applyTo(conf);
						float abwm = AlphaBetaWithMemory(child, alpha, b, d - 1, Ply.MAX);
						if (DEBUG)
							GraphVizPrinter.setRelation(child, abwm, conf);
						g = g < abwm ? g : abwm;
						b = g < b ? g : b;
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		SearchNode node = new SearchNode();
		if (g <= alpha)
			node.upperbound = g;
		if (g > alpha && g < beta) {
			node.lowerbound = node.upperbound = g;
		}
		if (g >= beta)
			node.lowerbound = g;

		// transpositionTable.put(n, node);

		return g;
	}

}
