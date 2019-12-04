package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import heuristics.HeuristicInterface;
import logger.GraphVizPrinter;
import representation.Conf;
import representation.Conf.Status;
import representation.InvalidActionException;
import representation.Move;

public class MMAgent_v2 implements AlgorithmInterface {

	static enum Ply {
		MAX, MIN
	};

	private int searchednodes = 0;
	private int evaluatednodes = 0;
	private int maxDepth;

	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	private long searchStartTime;

	private HeuristicInterface hi;
	private boolean blackPlayer;
	private Map<Conf, Float> computedStates;
	private static final boolean DEBUG = false;

	public MMAgent_v2(HeuristicInterface hi, boolean blackPlayer, int maxDepth) {
		this.hi = hi;
		this.maxDepth=maxDepth;
		this.blackPlayer = blackPlayer;
		computedStates = new HashMap<Conf, Float>();
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

	public float miniMaxRecursor(Conf conf, float alpha, float beta, int depth, Ply step) {
		// Has this state already been computed?
		searchednodes++;
		if (computedStates.containsKey(conf))
			return computedStates.get(conf);
		// Specify us
		if (DEBUG)
			GraphVizPrinter.setState(conf);
		// Is this state done?
		if (depth == maxDepth || timeUp()) {
			evaluatednodes++;
			return hi.evaluate_R(conf);
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return -5000;

		} else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return 5000;
		}
		float value;
		// If not, recurse further. Identify the best actions to take.
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
					return value;
				// Updating alpha/beta values.
				if (value > alpha)
					alpha = value;
			}
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
					return value;
				if (value < beta)
					beta = value;
			}
		}

		
		// Store so we don't have to compute it again.
		return value;
	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return ((System.currentTimeMillis() - searchStartTime) >= MAX_RUN_TIME - 30);
	}

}
