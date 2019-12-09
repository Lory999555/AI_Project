package algorithms;



import algorithms.ABWMAgent.Ply;
import heuristics.HeuristicInterface;
import representation.Conf;
import representation.Move;
import representation.TimeOutException;
import representation.Conf.Status;


/**
 * Basic Alpha-Beta Pruning search for Mancala
 * 
 * OOP version for passing results
 */
public class ABAgent implements AlgorithmInterface {

	private int searchednodes = 0;
	private int evaluatednodes = 0;
	private boolean ibreak;
	private int maxDepth;
	private int startDepth;
	
	private HeuristicInterface hi;
	private boolean blackPlayer;
	private long searchCutoff;
	private int evaluatednodesold;
	private int searchednodesold;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	static enum Ply {
		MAX, MIN
	};

	public ABAgent(HeuristicInterface hi, boolean blackPlayer , int startDepth, int maxDepth) {
		this.startDepth=startDepth;
		this.maxDepth = maxDepth;
		this.hi = hi;
		this.blackPlayer = blackPlayer;

	}

	private MoveValue alphaBeta_R(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {
		searchednodes++;
		Move bestMove = null;
		MoveValue searchResult = null;
		int value;
		// base case
		if (timeUp()) {
			this.ibreak = true;
			return null;
		} else if ((depth == 0)) {
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

				searchResult = alphaBeta_R(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MIN);
				
				if(searchResult==null)
					return null;

				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				alpha = Math.max(alpha, value);
				if (alpha >= beta)
					break; // pruning
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for (Move childmv : conf.getActions()) {
				searchResult = alphaBeta_R(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MAX);
				
				if(searchResult==null)
					return null;

				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
		}

		return new MoveValue(bestMove, value);
	}

	private MoveValue alphaBeta_B(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {

		searchednodes++;
		Move bestMove = null;
		MoveValue searchResult = null;
		int value;
		// base case

		// per invalidare l'ultima iterazione perchè potenzialmente errata
		if (timeUp()) {
			this.ibreak = true;
			return null;
		} else if ((depth == 0)) {
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
				searchResult = alphaBeta_B(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MIN);
				
				if(searchResult==null)
					return null;

				if (searchResult.value > value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				alpha = Math.max(alpha, value);
				if (alpha >= beta)
					break; // pruning
			}
		} else { // min step
			value = Integer.MAX_VALUE;
			for (Move childmv : conf.getActions()) {
				searchResult = alphaBeta_B(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MAX);
				
				if(searchResult==null)
					return null;

				if (searchResult.value < value) {
					value = searchResult.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
		}

		return new MoveValue(bestMove, value);
	}

	public Move compute(Conf conf) {
		this.ibreak = false;
		this.evaluatednodes = 0;
		this.searchednodes = 0;
		this.evaluatednodesold = 0;
		this.searchednodesold = 0;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		MoveValue newBest = null;
		MoveValue oldBest = null;
		this.searchCutoff = System.currentTimeMillis() + MAX_RUN_TIME;
		int d = startDepth;
		while (!timeUp() && d <= maxDepth) {
			evaluatednodes = 0;
			searchednodes = 0;
			oldBest = newBest;
			if (!this.blackPlayer)
				newBest = alphaBeta_R(conf, null, alpha, beta, d, Ply.MAX);
			else
				newBest = alphaBeta_B(conf, null, alpha, beta, d, Ply.MAX);
			d++;

			if (!this.ibreak) {
				evaluatednodesold = evaluatednodes;
				searchednodesold = searchednodes;
			}

		}


		

		if (this.ibreak) {
			System.out
			.println("\nEvaluatedNodes: " + evaluatednodesold + "\nSearchedNodes :" + searchednodesold + "\ndepth :" + (d-2));
			return oldBest.move;
		}
		else {
			System.out
			.println("\nEvaluatedNodes: " + evaluatednodes + "\nSearchedNodes :" + searchednodes + "\ndepth :" + (d--));
			return newBest.move;

		}

	}

	private boolean timeUp() {
		if (java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.indexOf("-agentlib:jdwp") > 0)
			return false;
		return (System.currentTimeMillis() > searchCutoff - 30);
	}
}
