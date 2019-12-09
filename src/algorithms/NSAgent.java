// NOTE: This is extra code, not a critical part of our Othello assignment
package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import heuristics.HeuristicInterface;
import representation.Conf;
import representation.Conf.Status;
import representation.Move;

public class NSAgent implements AlgorithmInterface {

	// Are we maximizing or minimizing?
	private boolean maximize;
	// The depth to which we should analyze the search space
	private int depth;
	// HashMap to avoid recalculating States
	private Map<Conf, Float> computedStates;
	private int maxdepth;
	// Used to generate a graph of the search space for each turn in SVG format
	private static final boolean DEBUG = true;
	private HeuristicInterface hi;

	
	public NSAgent(HeuristicInterface hi, boolean maximize, int depth) {
		this.hi = hi;
		this.maximize = maximize;
		this.depth = depth;
		computedStates = new HashMap<Conf, Float>();
	}

	@Override
	public Move compute(Conf conf) {

		float value = maximize ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
		List<Move> bestActions = new ArrayList<Move>();
		// Iterate!
		int flag = maximize ? 1 : -1;
		for (Move action : conf.getActions()) {
			// Algorithm!
			Conf newState = action.applyTo(conf);
			float newValue = -NegaScout(newState, 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);

			if (flag * newValue > flag * value) {
				value = newValue;
				bestActions.clear();
			}
			// Add it to the list of candidates?
			if (flag * newValue >= flag * value)
				bestActions.add(action);
		}

		Collections.shuffle(bestActions);

		return bestActions.get(0);
	}

	private float NegaScout(Conf conf, int depth, float alpha, float beta) {
		
		if (conf.getStatus() != Status.Ongoing || depth == this.depth) {
			return hi.evaluate_R(conf);
		}
		float a, b, t;
		int i;

		List<Move> actions = conf.getActions();
		int w = actions.size();

		// a = alpha;
		b = beta;
		for (i = 0; i < w; i++) {
			Conf successor = actions.get(i).applyTo(conf);
			a = -NegaScout(successor, depth + 1, -b, -alpha);
			if (alpha < a && a < beta && i > 0) {
				a = -NegaScout(successor, depth + 1, -beta, -alpha); /* re-search */
			}
			alpha = Math.max(alpha, a);

			if (alpha >= beta) {
				return alpha;
			}
			b = alpha + 1;
		}
		return alpha;
	}

}
