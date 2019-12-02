/*
 * NOTE: We are not actually using this code, this is just another search algorithm that we wrote.
 * Our latest implementation is in MTDDecider.java
 */

package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import heuristics.HeuristicInterface;
import logger.GraphVizPrinter;
import representation.Conf;
import representation.InvalidActionException;
import representation.Move;

public class NMAgent implements AlgorithmInterface {

	private class SearchNode {
		short depth;
		float h;
		float alpha, beta;
	}

	static enum Ply {
		MAX, MIN
	};

	private long searchStartTime;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	// Are we maximizing or minimizing?
	private boolean blackPlayer;
	// The depth to which we should analyze the search space
	private int depth;
	private int maxdepth;
	private long leafsHit;
	private Map<Conf, SearchNode> stateCache;
	private int cacheHits;
	private int cacheMisses;
	// Used to generate a graph of the search space for each turn in SVG format
	private static final boolean DEBUG = false;
	private static final boolean DEBUG_PRINT = false;

	private HeuristicInterface hi;
	private int evaluatednodes = 0;
	private int searchednodes = 0;

	/**
	 * Initialize this NegaMaxDecider.
	 * 
	 * @param maximize Are we maximizing or minimizing on this turn? True if the
	 *                 former.
	 * @param depth    The depth to which we should analyze the search space.
	 */
	public NMAgent(HeuristicInterface hi, boolean blackPlayer, int maxDepth) {
		this.blackPlayer = blackPlayer;
		this.hi = hi;
		this.maxdepth = maxDepth;
	}

	/**
	 * Returns a random action from among the best actions in the given list
	 * 
	 * @param actions The actions to examine
	 * @return The selected action
	 */
	private Move getRandomBestAction(List<MoveValue> actions) {
		List<Move> bestActions = new LinkedList<Move>();

		float bestV = actions.get(0).value;
		for (MoveValue avp : actions) {
			if (avp.value != bestV)
				break;

			bestActions.add(avp.move);
		}

		Collections.shuffle(bestActions);

		return bestActions.get(0);
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

	public Move compute(Conf conf) {
		leafsHit = 0;
		cacheHits = 0;
		cacheMisses = 0;
		stateCache = new HashMap<Conf, SearchNode>(1000000);
		if (DEBUG)
			GraphVizPrinter.setState(conf);
		// Choose randomly between equally good options
		float value = blackPlayer ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
		// Iterate!
		int flag = blackPlayer ? 1 : -1;
		float alpha = Float.NEGATIVE_INFINITY;
		float beta = Float.POSITIVE_INFINITY;
		List<MoveValue> actions = buildMVList(conf.getActions());
		for (MoveValue a : actions) {
			try {
				// Algorithm!
				Conf newState = a.move.applyTo(conf);
				float newValue = -NegaMax(newState, 1, -beta, -alpha, -flag);
				if (DEBUG)
					GraphVizPrinter.setRelation(newState, newValue, conf);
				a.value = (int) newValue;
				if (blackPlayer)
					alpha = Math.max(alpha, newValue);
				else
					beta = Math.min(beta, newValue);
			} catch (InvalidActionException | CloneNotSupportedException e) {
				throw new RuntimeException("Invalid action!");
			}
		}

		Collections.sort(actions, Collections.reverseOrder());

		// Graph?
		try {
			GraphVizPrinter.setDecision(actions.get(0).move.applyTo(conf));
		} catch (InvalidActionException | CloneNotSupportedException e) {
			throw new RuntimeException("Invalid action!");
		}
		if (DEBUG)
			GraphVizPrinter.printGraphToFile();
		System.out.println("Hit " + leafsHit + " leaves. C-Misses:" + cacheMisses + " C-hits:" + cacheHits);
		return getRandomBestAction(actions);
	}

	private void indentedPrint(int depth, String s) {
		/*
		 * for (int i=0; i < depth; i++) { System.out.print("\t"); }
		 * System.out.println(s);
		 */
	}

	private float NegaMax(Conf conf, int depth, float alpha, float beta, int color) throws InvalidActionException {
		if (stateCache.containsKey(conf)) {
			SearchNode n = stateCache.get(conf);
			cacheHits++;
			if (n.depth >= depth) {
				return color * n.h;
			}
		}
		cacheMisses++;
		if (DEBUG)
			GraphVizPrinter.setState(conf);
		if (conf.getStatus() != Conf.Ongoing || depth == this.depth) {
			indentedPrint(depth,
					"Fast returning at leaf. H:" + conf.heuristic() + " c:" + color + " ret:" + color * conf.heuristic());
			leafsHit++;
			return color * conf.heuristic();
		}
		indentedPrint(depth, "Starting child node examination. alpha: " + alpha + " beta:" + beta);
		for (Action a : conf.getActions()) {
			State childState = a.applyTo(conf);
			indentedPrint(depth, "Examining child from action:" + a);
			float nmValue = -NegaMax(childState, depth + 1, -beta, -alpha, -color);
			indentedPrint(depth, "Got value:" + nmValue);
			if (DEBUG)
				GraphVizPrinter.setRelation(childState, nmValue, conf);
			indentedPrint(depth, "Old alpha:" + alpha);
			alpha = Math.max(alpha, nmValue);
			indentedPrint(depth, "New alpha:" + alpha);
			if (alpha > beta) {
				indentedPrint(depth, "A-B Pruned. Alpha:" + alpha + " Beta:" + beta);
				break;
			}
		}
		SearchNode sn = new SearchNode();
		sn.h = alpha;
		sn.alpha = alpha;
		sn.beta = beta;
		sn.depth = (short) depth;
		// stateCache.put(s, sn);
		return alpha;
	}

}
