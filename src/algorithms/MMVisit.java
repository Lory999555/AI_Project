package algorithms;

import heuristics.HeuristicInterface;
import representation.Conf;
import representation.Conf.Status;
import representation.Move;


public class MMVisit implements AlgorithmInterface {

	static enum Ply {
		MAX, MIN
	};

	// for testing purpose
	private int searchednodes = 0;
	private int evaluatednodes = 0;
	private int maxDepth;
	private int startDepth;

	private HeuristicInterface hi;
	private boolean blackPlayer;
	private long searchCutoff;
	private boolean ibreak = false;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	public MMVisit(HeuristicInterface hi, boolean blackPlayer, int maxDepth,int startDepth) {
		this.startDepth=startDepth;
		this.maxDepth = maxDepth;
		this.hi = hi;
		this.blackPlayer = blackPlayer;
	}

	private MoveValue minimax_R(Conf conf, Move move, int depth, Ply step) {
		searchednodes++;
		Move bestMove = null;
		MoveValue searchResult = null;
		int value;
		if (timeUp()) {
			this.ibreak = true;
			return null;
		}
		// base case
		else if ((depth == 0)) {
			evaluatednodes++;
			return new MoveValue(move, hi.evaluate_R(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return new MoveValue(move, -5000);

		} else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return new MoveValue(move, 5000);
		}
		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			for (Move childmv : conf.getActions()) {
				searchResult = minimax_R(childmv.applyTo(conf), childmv, depth - 1, Ply.MIN);

				if (searchResult == null)
					return null;	
				
				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for (Move childmv : conf.getActions()) {
				searchResult = minimax_R(childmv.applyTo(conf), childmv, depth - 1, Ply.MAX);

				if (searchResult == null)
					return null;	
				
				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
			}
		}

		return new MoveValue(bestMove, value);
	}

	private MoveValue minimax_B(Conf conf, Move move, int depth, Ply step) {
		searchednodes++;
		Move bestMove = null;
		MoveValue searchResult = null;
		int value;
		// base case
		if (timeUp()) {
			this.ibreak = true;
			return null;
		}
		else if ((depth == 0)) {
			evaluatednodes++;
			return new MoveValue(move, hi.evaluate_B(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			evaluatednodes++;
			return new MoveValue(move, 5000);

		} else if (conf.getStatus() == Status.RedWon) {
			evaluatednodes++;
			return new MoveValue(move, -5000);
		}

		// recursive
		if (step == Ply.MAX) { // max step
			value = Integer.MIN_VALUE;
			for (Move childmv : conf.getActions()) {
				searchResult = minimax_B(childmv.applyTo(conf), childmv, depth - 1, Ply.MIN);

				if(searchResult==null)
					return null;
					
				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for (Move childmv : conf.getActions()) {
				searchResult = minimax_B(childmv.applyTo(conf), childmv, depth - 1, Ply.MAX);

				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
			}
		}

		return new MoveValue(bestMove, value);
	}

	public Move compute(Conf conf) {
		this.evaluatednodes = 0;
		this.searchednodes = 0;
		MoveValue newBest = null;
		MoveValue oldBest = null;
		this.searchCutoff = System.currentTimeMillis() + MAX_RUN_TIME;

		int d = startDepth;
		while (!timeUp() && d < maxDepth) {
			oldBest = newBest;
			if (!this.blackPlayer)
				newBest = minimax_R(conf, null, d, Ply.MAX);
			else
				newBest = minimax_B(conf, null, d, Ply.MAX);
			d++;

		}
		System.out.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes);
		if (this.ibreak)
			return oldBest.move;
		else
			return newBest.move;
	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return (System.currentTimeMillis() > searchCutoff - 30);
	}

	@Override
	public void warmUp(long millisec) {
		// TODO Auto-generated method stub
		
	}

}
