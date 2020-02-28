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
import representation.Conf;
import representation.Conf.Status;
import representation.Move;

public class NMWMVisit implements AlgorithmInterface {

	private class SearchNode {
		short depth;
		float h;
		float alpha, beta;
	}

	// Are we maximizing or minimizing?
	private boolean maximize;
	// The depth to which we should analyze the search space
	private int depth;
	private int maxdepth;
	private long leafsHit;
	private Map<Conf, SearchNode> stateCache;
	private int cacheHits;
	private int cacheMisses;

	private HeuristicInterface hi;

	
	public NMWMVisit(HeuristicInterface hi, boolean maximize, int depth) {
		this.hi = hi;
		this.maximize = maximize;
		this.depth = depth;
	}


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

	
	private List<MoveValue> buildAVPList(List<Move> actions) {
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
		stateCache = new HashMap<Conf, SearchNode>(20000);

		float value = maximize ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
		// Iterate!
		int flag = maximize ? 1 : -1;
		float alpha = Float.NEGATIVE_INFINITY;
		float beta = Float.POSITIVE_INFINITY;
		List<MoveValue> actions = buildAVPList(conf.getActions());
		for (MoveValue a : actions) {

			Conf newState = a.move.applyTo(conf);
			float newValue = -NegaMax(newState, 1, -beta, -alpha, -flag);
			a.value = (int) newValue;
			if (maximize)
				alpha = Math.max(alpha, newValue);
			else
				beta = Math.min(beta, newValue);
		}

		Collections.sort(actions, Collections.reverseOrder());

		System.out.println("Hit " + leafsHit + " leaves. C-Misses:" + cacheMisses + " C-hits:" + cacheHits);
		return getRandomBestAction(actions);
	}

	private float NegaMax(Conf conf, int depth, float alpha, float beta, int color) {
		if (stateCache.containsKey(conf)) {
			SearchNode n = stateCache.get(conf);
			cacheHits++;
			if (n.depth >= depth) {
				return color * n.h;
			}
		}
		cacheMisses++;
		
		if (conf.getStatus() != Status.Ongoing || depth == this.depth) {
			leafsHit++;
			return color * hi.evaluate_R(conf);
		}
		for (Move mv : conf.getActions()) {
			Conf childState = mv.applyTo(conf);
			float nmValue = -NegaMax(childState, depth + 1, -beta, -alpha,
					-color);
			
			alpha = Math.max(alpha, nmValue);
			if (alpha > beta) {
				break;
			}
		}
		SearchNode sn = new SearchNode();
		sn.h = alpha;
		sn.alpha = alpha;
		sn.beta = beta;
		sn.depth = (short)depth;
		stateCache.put(conf, sn);
		return alpha;
	}


	@Override
	public void warmUp(long millisec) {
		// TODO Auto-generated method stub
		
	}

}
