package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logger.GraphVizPrinter;
import representation.Action;
import representation.Conf;
import representation.Conf.Status;
import representation.InvalidActionException;

public class MinMax implements AlgorithmInterface {

	// Are we maximizing or minimizing?
	private boolean maximize;
	// The depth to which we should analyze the search space
	private int depth;

	// HashMap to avoid recalculating States
	private Map<Conf, Float> computedStates;
	// Used to generate a graph of the search space for each turn in SVG format
	private static final boolean DEBUG = false;

	/**
	 * Initialize this MiniMaxDecider.
	 * 
	 * @param maximize Are we maximizing or minimizing on this turn? True if the
	 *                 former.
	 * @param depth    The depth to which we should analyze the search space.
	 */
	public MinMax(boolean maximize, int depth) {
		this.maximize = maximize;
		this.depth = depth;
		computedStates = new HashMap<Conf, Float>();
	}

	/**
	 * Decide which state to go into. We manually MiniMax the first layer so we can
	 * figure out which heuristic is from which Action. Also, we want to be able to
	 * choose randomly between equally good options. "I'm the decider, and I decide
	 * what is best." - George W. Bush
	 * 
	 * @param conf The start State for our search.
	 * @return The Action we are deciding to take.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Action compute(Conf conf) {
		if (DEBUG)
			GraphVizPrinter.setState(conf);
		// Choose randomly between equally good options
		float value = maximize ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
		List<Action> bestActions = new ArrayList<Action>();
		// Iterate!
		int flag = maximize ? 1 : -1;
		for (Action action : conf.getActions()) {
			try {
				// Algorithm!
				Conf newState = action.applyTo(conf);
				float newValue = this.miniMaxRecursor(newState, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1,
						!this.maximize);
				if (DEBUG)
					GraphVizPrinter.setRelation(newState, newValue, conf);
				// Better candidates?
				if (flag * newValue > flag * value) {
					value = newValue;
					bestActions.clear();
				}
				// Add it to the list of candidates?
				if (flag * newValue >= flag * value)
					bestActions.add(action);
			} catch (InvalidActionException e) {
				throw new RuntimeException("Invalid action!");
			}
		}
		// Pick one of the best randomly
		Collections.shuffle(bestActions);
		// Graph?
		try {
			GraphVizPrinter.setDecision(bestActions.get(0).applyTo(conf));
		} catch (InvalidActionException e) {
			throw new RuntimeException("Invalid action!");
		}
		if (DEBUG)
			GraphVizPrinter.printGraphToFile();
		return bestActions.get(0);
	}

	/**
	 * The true implementation of the MiniMax algorithm! Thoroughly commented for
	 * your convenience.
	 * 
	 * @param state    The State we are currently parsing.
	 * @param alpha    The alpha bound for alpha-beta pruning.
	 * @param beta     The beta bound for alpha-beta pruning.
	 * @param depth    The current depth we are at.
	 * @param maximize Are we maximizing? If not, we are minimizing.
	 * @return The best point count we can get on this branch of the state space to
	 *         the specified depth.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public float miniMaxRecursor(Conf state, float alpha, float beta, int depth, boolean maximize) {
		// Has this state already been computed?
		if (computedStates.containsKey(state))
			return computedStates.get(state);
		// Specify us
		if (DEBUG)
			GraphVizPrinter.setState(state);
		// Is this state done?
		if (state.getStatus() != Status.Ongoing)
			return finalize(state, state.heuristic());
		// Have we reached the end of the line?
		if (depth == this.depth)
			return state.heuristic();
		// If not, recurse further. Identify the best actions to take.
		float value = maximize ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
		int flag = maximize ? 1 : -1;
		List<Action> test = state.getActions();
		for (Action action : test) {
			// Check it. Is it better? If so, keep it.
			try {
				Conf childState = action.applyTo(state);
				float newValue = this.miniMaxRecursor(childState, alpha, beta, depth + 1, !maximize);
				if (DEBUG)
					GraphVizPrinter.setRelation(childState, newValue, state);
				if (flag * newValue > flag * value)
					value = newValue;
			} catch (InvalidActionException e) {
				throw new RuntimeException("Invalid action!");
			}
			// Pruning!
			float pruner = maximize ? beta : alpha;
			if (flag * value > flag * pruner)
				return value;
			// Updating alpha/beta values.
			if (maximize && value > alpha)
				alpha = value;
			else if (!maximize && value < beta)
				beta = value;
		}
		// Store so we don't have to compute it again.
		return finalize(state, value);
	}

	/**
	 * Handy private function to stick into HashMap before returning. We don't
	 * always want to stick into our HashMap, so use carefully.
	 * 
	 * @param state The State we are hashing.
	 * @param value The value that State has.
	 * @return The value we were passed.
	 */
	private float finalize(Conf state, float value) {
		// THIS IS BROKEN DO NOT USE
		// computedStates.put(state, value);
		return value;
	}

}
