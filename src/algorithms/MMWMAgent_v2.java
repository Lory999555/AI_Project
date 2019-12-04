// NOTE: This is extra code, not a critical part of our Othello assignment
package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import heuristics.HeuristicInterface;
import logger.GraphVizPrinter;
import representation.Conf;
import representation.Conf.Status;
import representation.InvalidActionException;
import representation.Move;

public class MMWMAgent_v2 implements AlgorithmInterface {

	private static final int MIN_VAL = -5000;
	private static final int MAX_VAL = 5000;

	private class SearchNode {
		float lowerbound = MIN_VAL, upperbound = MAX_VAL;
	}

	static enum Ply {
		MAX, MIN
	};

	private long searchStartTime;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	// Are we maximizing or minimizing?
	private boolean blackPlayer;
	// The depth to which we should analyze the search space
	private int maxDepth;
	// HashMap to avoid recalculating States
	private HashMap<Conf, SearchNode> computedStates;
	// Used to generate a graph of the search space for each turn in SVG format
	private static final boolean DEBUG = false;
	private HeuristicInterface hi;
	private int evaluatednodes = 0;
	private int searchednodes = 0;

	/**
	 * Initialize this MiniMaxDecider.
	 * 
	 * @param maximize Are we maximizing or minimizing on this turn? True if the
	 *                 former.
	 * @param maxDepth The depth to which we should analyze the search space.
	 */
	public MMWMAgent_v2(HeuristicInterface hi, boolean blackPlayer, int maxDepth) {
		this.hi = hi;
		this.blackPlayer = blackPlayer;
		this.maxDepth = maxDepth;
		computedStates = new HashMap<Conf, SearchNode>();
	}

	public Move compute(Conf conf) {

		this.searchStartTime = System.currentTimeMillis();
		this.evaluatednodes = 0;
		this.searchednodes = 0;

		if (DEBUG)
			GraphVizPrinter.setState(conf);
		// Choose randomly between equally good options
		float value = Float.NEGATIVE_INFINITY;
		List<Move> bestActions = new ArrayList<Move>();
		// Iterate!
		for (Move action : conf.getActions()) {
			try {
				// Algorithm!
				Conf newState = action.applyTo(conf);
				float newValue = this.miniMaxRecursor(newState, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1,
						Ply.MIN);
				if (DEBUG)
					GraphVizPrinter.setRelation(newState, newValue, conf);
				// Better candidates?
				if (newValue > value) {
					value = newValue;
					bestActions.clear();
				}
				// Add it to the list of candidates?
				if (newValue >= value)
					bestActions.add(action);
			} catch (InvalidActionException | CloneNotSupportedException e) {
				throw new RuntimeException("Invalid action!");
			}
		}
		// Pick one of the best randomly
		Collections.shuffle(bestActions);
		// Graph?
		try {
			GraphVizPrinter.setDecision(bestActions.get(0).applyTo(conf));
		} catch (InvalidActionException | CloneNotSupportedException e) {
			throw new RuntimeException("Invalid action!");
		}
		if (DEBUG)
			GraphVizPrinter.printGraphToFile();

		System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes);

		return bestActions.get(0);
	}

	/**
	 * The true implementation of the MiniMax algorithm! Thoroughly commented for
	 * your convenience.
	 * 
	 * @param conf     The State we are currently parsing.
	 * @param alpha    The alpha bound for alpha-beta pruning.
	 * @param beta     The beta bound for alpha-beta pruning.
	 * @param depth    The current depth we are at.
	 * @param blackPlayer Are we maximizing? If not, we are minimizing.
	 * @return The best point count we can get on this branch of the state space to
	 *         the specified depth.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public float miniMaxRecursor(Conf conf, float alpha, float beta, int depth, Ply step) {
		// Specify us

		searchednodes++;
		if (DEBUG)
			GraphVizPrinter.setState(conf);
		// Has this state already been computed?
		if (computedStates.containsKey(conf)) {
			if (DEBUG)
				GraphVizPrinter.setCached(conf);
			SearchNode node = computedStates.get(conf);

			if (node.lowerbound >= beta)
				return node.lowerbound;
			if (node.upperbound <= alpha)
				return node.upperbound;
			alpha = alpha > node.lowerbound ? alpha : node.lowerbound;
			beta = beta < node.upperbound ? beta : node.upperbound;
		}
		// Is this state done?
		if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			float value = 5000;
			// Store so we don't have to compute it again.
			SearchNode node = new SearchNode();
			if (value <= alpha)
				node.upperbound = value;
			if (value > alpha && value < beta) {
				node.lowerbound = node.upperbound = value;
			}
			if (value >= beta)
				node.lowerbound = value;
			computedStates.put(conf, node);
			return value;
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			float value = -5000;
			// Store so we don't have to compute it again.
			SearchNode node = new SearchNode();
			if (value <= alpha)
				node.upperbound = value;
			if (value > alpha && value < beta) {
				node.lowerbound = node.upperbound = value;
			}
			if (value >= beta)
				node.lowerbound = value;
			computedStates.put(conf, node);
			return value;
		}
		// Have we reached the end of the line?
		if (depth == this.maxDepth || timeUp()) {
			evaluatednodes++;
			return hi.evaluate_R(conf);

		}
		// If not, recurse further. Identify the best actions to take.
		float value;

		if (step == Ply.MAX) {
			value = Float.NEGATIVE_INFINITY;
			List<Move> test = conf.getActions();
			for (Move action : test) {
				// Check it. Is it better? If so, keep it.
				try {
					Conf childState = action.applyTo(conf);
					float newValue = this.miniMaxRecursor(childState, alpha, beta, depth + 1, Ply.MIN);
					if (DEBUG)
						GraphVizPrinter.setRelation(childState, newValue, conf);
					if (newValue > value)
						value = newValue;
				} catch (InvalidActionException | CloneNotSupportedException e) {
					throw new RuntimeException("Invalid action!");
				}
				// Pruning!
				float pruner = beta;
				if (value > pruner)
					break;
				// Updating alpha/beta values.
				if (value > alpha)
					alpha = value;
			}
			// Store so we don't have to compute it again.
			SearchNode node = new SearchNode();
			if (value <= alpha)
				node.upperbound = value;
			if (value > alpha && value < beta) {
				node.lowerbound = node.upperbound = value;
			}
			if (value >= beta)
				node.lowerbound = value;

			computedStates.put(conf, node);

		} else {
			value = Float.POSITIVE_INFINITY;

			List<Move> test = conf.getActions();

			for (Move action : test) {
				// Check it. Is it better? If so, keep it.
				try {
					Conf childState = action.applyTo(conf);
					float newValue = this.miniMaxRecursor(childState, alpha, beta, depth + 1, Ply.MAX);
					if (DEBUG)
						GraphVizPrinter.setRelation(childState, newValue, conf);
					if (newValue < value)
						value = newValue;
				} catch (InvalidActionException | CloneNotSupportedException e) {
					throw new RuntimeException("Invalid action!");
				}
				// Pruning!
				float pruner = alpha;
				if (value < pruner)
					break;
				// Updating alpha/beta values.
				if (value < beta)
					beta = value;
			}
			// Store so we don't have to compute it again.
			SearchNode node = new SearchNode();
			if (value <= alpha)
				node.upperbound = value;
			if (value > alpha && value < beta) {
				node.lowerbound = node.upperbound = value;
			}
			if (value >= beta)
				node.lowerbound = value;

			computedStates.put(conf, node);

		}

		return value;
	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return ((System.currentTimeMillis() - searchStartTime) >= MAX_RUN_TIME - 30);
	}

}