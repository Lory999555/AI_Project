package algorithms;

import java.util.concurrent.TimeoutException;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.scorer.BitBoardScorer;
import ut.veikotiit.checkers.transposition.TranspositionTable;

public class MtdF {

  private final TranspositionTable transpositionTable;
  private final long timeGiven;

  public MtdF(long timeGiven, TranspositionTable transpositionTable) {
    this.timeGiven = timeGiven;
    this.transpositionTable = transpositionTable;
  }

  
 /**
  *  function MTDF(root : node_type; f : integer; d : integer) : integer;

    g := f;
    upperbound := +INFINITY;
    lowerbound := -INFINITY;
    repeat
        if g == lowerbound then beta := g + 1 else beta := g;
        g := AlphaBetaWithMemory(root, beta - 1, beta, d);
        if g < beta then upperbound := g else lowerbound := g;
    until lowerbound >= upperbound;
    return g;
    
  * @param board
  * @param color
  * @param depth
  * @param firstGuess
  * @param startTime
  * @param scorer
  * @return
  * @throws TimeoutException
  */
  public double search(BitBoard board, Color color, int depth, int firstGuess, long startTime, BitBoardScorer scorer) throws TimeoutException {
    Negamax negamax = new Negamax(startTime, timeGiven, scorer, transpositionTable);
    int score = firstGuess;

    int upperBound = Integer.MAX_VALUE;
    int lowerBound = Integer.MIN_VALUE;

    while (lowerBound < upperBound) {
      int beta;
      if (score == lowerBound) {
        beta = score + 1;
      }
      else {
        beta = score;
      }

      score = negamax.recursive(board, color, beta - 1, beta, depth, depth > 1);

      if (score < beta) {
        upperBound = score;
      }
      else {
        lowerBound = score;
      }
    }

    return score;
  }
}
