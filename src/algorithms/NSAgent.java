// NOTE: This is extra code, not a critical part of our Othello assignment
package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithms.ABWMAgent.Ply;
import heuristics.HeuristicInterface;
import representation.Conf;
import representation.Conf.Status;
import representation.Move;

public class NSAgent implements AlgorithmInterface {

	// Are we maximizing or minimizing?
	private boolean maximize;
	// The depth to which we should analyze the search space
	private int startDepth;
	private int maxDepth;
	// HashMap to avoid recalculating States
	// Used to generate a graph of the search space for each turn in SVG format
	private static final boolean DEBUG = true;
	private static final long MAX_RUN_TIME = 1000;
	private HeuristicInterface hi;
	private int evaluatednodes;
	private int searchednodes;
	private int evaluatednodesold;
	private int searchednodesold;
	private boolean ibreak;
	private long searchCutoff;
	private boolean blackPlayer;

	public NSAgent(HeuristicInterface hi, boolean blackPlayer, int startDepth, int maxDepth) {
		this.hi = hi;
		this.blackPlayer = blackPlayer;
		this.maximize = true;
		this.startDepth = startDepth;
		this.maxDepth = maxDepth;
//		computedStates = new HashMap<Conf, Float>();
	}

	public Move compute(Conf conf) {
		this.evaluatednodes = 0;
		this.searchednodes = 0;
		this.evaluatednodesold = 0;
		this.searchednodesold = 0;
		this.ibreak = false;
		Move newBest = null;
		Move oldBest = null;
		this.searchCutoff = System.currentTimeMillis() + MAX_RUN_TIME;

		int d = startDepth;
		while (!timeUp() && d <= maxDepth) {
			evaluatednodes = 0;
			searchednodes = 0;
			oldBest = newBest;
			if (!this.blackPlayer)
				newBest = start(conf, d);
			else
				newBest = start(conf, d);
			d++;

			if (!this.ibreak) {
				evaluatednodesold = evaluatednodes;
				searchednodesold = searchednodes;
			}

		}

		// controllare se è possibile togliere le cose per le versioni non old perchè
		// forse non vengono mai usate in realtà!

		// vedere se è possibile tolgiere la maggior parte dei metodi timesUp andandoli
		// a sostituire con il check di this.ibreak
		if (this.ibreak) {
			System.out.println("\nEvaluatedNodes: " + evaluatednodesold + "\nSearchedNodes :" + searchednodesold
					+ "\ndepth :" + (d - 2));
			return oldBest;
		} else {
			System.out.println(
					"\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes + "\ndepth :" + (d--));
			return newBest;

		}
	}

	public Move start(Conf conf, int d) {

		float value = maximize ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
		List<Move> bestActions = new ArrayList<Move>();
		// ho invertito il flag
		int flag = maximize ? -1 : 1;
		for (Move action : conf.getActions()) {
			// Algorithm!
			Conf newState = action.applyTo(conf);
			float newValue = -NegaScout(newState, 1, d, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);

			if (Float.isNaN(newValue))
				return null;

			if (flag * newValue > flag * value) {
				value = newValue;
				bestActions.clear();
			}
			// Add it to the list of candidates?
			if (flag * newValue >= flag * value)
				bestActions.add(action);
		}

		if (bestActions.isEmpty())
			return null;

		Collections.shuffle(bestActions);

		return bestActions.get(0);
	}

	private float NegaScout(Conf conf, int depth, int d, float alpha, float beta) {

		if (timeUp()) {
			this.ibreak = true;
			return Float.NaN;
		}

		// mettere quelli di 5000
		if (conf.getStatus() != Status.Ongoing || depth == d) {
			return hi.evaluate_R(conf);
		}
		float a, b;
		int i;

		List<Move> actions = conf.getActions();
		int w = actions.size();

		// a = alpha;
		b = beta;
		for (i = 0; i < w; i++) {
			Conf successor = actions.get(i).applyTo(conf);
			a = -NegaScout(successor, depth + 1, d, -b, -alpha);
			if (alpha < a && a < beta && i > 0) {
				a = -NegaScout(successor, depth + 1, d, -beta, -alpha); /* re-search */
			}
			if (Float.isNaN(a))
				return Float.NaN;

			alpha = Math.max(alpha, a);

			if (alpha >= beta) {
				return alpha;
			}
			b = alpha + 1;
		}
		return alpha;
	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return (System.currentTimeMillis() > searchCutoff - 30);
	}

}
