package algorithms;

import heuristics.HeuristicInterface;
import representation.Conf;
import representation.Move;
import representation.Conf.Status;

/**
 * all the comment is in Italian language. (English version is coming soon)
 * 
 * implementazione della visita depth first con Alpha-beta pruning e 
 * core iterative deepening. versione ricorsiva.
 * 
 * @author loren
 *
 */
public class ABVisit implements AlgorithmInterface {

	private boolean ibreak;
	private int maxDepth;
	private int startDepth;

	private int alpha;
	private int beta;

	private HeuristicInterface hi;
	private boolean blackPlayer;
	private long searchCutoff;
	private static long MAX_RUN_TIME = 1000; // maximum runtime in milliseconds

	static enum Ply {
		MAX, MIN
	};

	/**
	 * costruttore
	 * 
	 * @param hi          oggetto euristica
	 * @param blackPlayer muove il nero?
	 * @param startDepth  depth iniziale
	 * @param maxDepth    depth massima
	 */
	public ABVisit(HeuristicInterface hi, boolean blackPlayer, int startDepth, int maxDepth) {
		this.startDepth = startDepth;
		this.maxDepth = maxDepth;
		this.hi = hi;
		this.blackPlayer = blackPlayer;

	}

	/**
	 * variante RED dell'algoritmo di ricerca
	 * 
	 * @param conf  configurazione root locale
	 * @param move  mossa che ha generato tale root
	 * @param alpha valore di alpha
	 * @param beta  valore di beta
	 * @param depth depth locale
	 * @param step  turnazione
	 * @return		si ritorna in maniera ricorsiva la miglior mossa disponibile
	 */
	private MoveValue ABSearch_R(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {
		Move bestMove = null;
		MoveValue result = null;
		int value;

		if (timeUp()) {
			this.ibreak = true;
			return null;
		} else if ((depth == 0)) {
			return new MoveValue(move, hi.evaluate_R(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			return new MoveValue(move, -50000);
		} else if (conf.getStatus() == Status.RedWon) {
			return new MoveValue(move, 50000);
		}

		if (step == Ply.MAX) {
			value = Integer.MIN_VALUE;
			for (Move childmv : conf.getActions()) {

				result = ABSearch_R(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MIN);

				if (result == null)
					return null;

				if (result.value > value) {
					value = result.value;
					bestMove = childmv;
				}
				alpha = Math.max(alpha, value);
				if (alpha >= beta)
					break;
			}
			if(value == Integer.MIN_VALUE)
				value=Integer.MAX_VALUE;
		} else {
			value = Integer.MAX_VALUE;
			for (Move childmv : conf.getActions()) {
				result = ABSearch_R(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MAX);

				if (result == null)
					return null;

				if (result.value < value) {
					value = result.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
			if(value == Integer.MAX_VALUE)
				value=Integer.MIN_VALUE;
		}

		return new MoveValue(bestMove, value);
	}

	/**
	 * variante BLACK dell'algoritmo di ricerca
	 * 
	 * @param conf  configurazione root locale
	 * @param move  mossa che ha generato tale root
	 * @param alpha valore di alpha
	 * @param beta  valore di beta
	 * @param depth depth locale
	 * @param step  turnazione
	 * @return
	 */
	private MoveValue ABSearch_B(Conf conf, Move move, int alpha, int beta, int depth, Ply step) {

		Move bestMove = null;
		MoveValue result = null;
		int value;

		if (timeUp()) {
			this.ibreak = true;
			return null;
		} else if ((depth == 0)) {
			return new MoveValue(move, hi.evaluate_B(conf));
		} else if (conf.getStatus() == Status.BlackWon) {
			return new MoveValue(move, 50000);
		} else if (conf.getStatus() == Status.RedWon) {
			return new MoveValue(move, -50000);
		}

		if (step == Ply.MAX) {
			value = Integer.MIN_VALUE;
			for (Move childmv : conf.getActions()) {
				result = ABSearch_B(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MIN);

				if (result == null)
					return null;

				if (result.value > value) {
					value = result.value;
					bestMove = childmv;
				}
				alpha = Math.max(alpha, value);
				if (alpha >= beta)
					break;
			}
			if(value == Integer.MIN_VALUE)
				value=Integer.MAX_VALUE;
		} else {
			value = Integer.MAX_VALUE;
			for (Move childmv : conf.getActions()) {
				result = ABSearch_B(childmv.applyTo(conf), childmv, alpha, beta, depth - 1, Ply.MAX);

				if (result == null)
					return null;

				if (result.value < value) {
					value = result.value;
					bestMove = childmv;
				}
				beta = Math.min(beta, value);
				if (alpha >= beta)
					break;
			}
			if(value == Integer.MAX_VALUE)
				value=Integer.MIN_VALUE;
		}
		return new MoveValue(bestMove, value);
	}

	/**
	 * avvia la visita AB in una struttura iterative deepening, limitata dal tempo a
	 * disposizione che scarta l'ultima mossa se non viene chiuso tutto il livello.
	 */
	public Move compute(Conf conf) {
		this.searchCutoff = System.currentTimeMillis() + MAX_RUN_TIME;
		this.ibreak = false;
		alpha = Integer.MIN_VALUE;
		beta = Integer.MAX_VALUE;
		MoveValue newBest = null;
		MoveValue oldBest = null;
		int d = startDepth;
		while (!timeUp() && d <= maxDepth) {
			oldBest = newBest;
			if (!this.blackPlayer)
				newBest = ABSearch_R(conf, null, alpha, beta, d, Ply.MAX);
			else
				newBest = ABSearch_B(conf, null, alpha, beta, d, Ply.MAX);
			d++;

		}

		if (oldBest.move == null) {
			return conf.nullMove();
		}

		if (this.ibreak) {
			return oldBest.move;
		} else {
			return newBest.move;
		}

	}

	/**
	 * 
	 * @return true se è scaduto il tempo a disposizione, false altrimenti
	 */
	private boolean timeUp() {
		return (System.currentTimeMillis() > searchCutoff - 30);
	}

	@Override
	/**
	 * metodo utilizzato per le varianti con memoria
	 */
	public void warmUp(long millisec) {
	}
}
