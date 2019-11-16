package algorithms;

import java.util.List;
import java.util.concurrent.TimeoutException;

import ut.veikotiit.checkers.Color;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.moves.Move;
import ut.veikotiit.checkers.scorer.BitBoardScorer;
import ut.veikotiit.checkers.transposition.TranspositionTable;

public class IterativeDeepeningSearcher {

  private final int max_depth;
  private final int timeGiven;
  private Negamax negamax;

  public IterativeDeepeningSearcher(int max_depth, int timeGiven) {
    this.max_depth = max_depth;
    this.timeGiven = timeGiven;
  }
  
  /**
   * function iterative_deepening(root : node_type) : integer;

    firstguess := 0;
    for d = 1 to MAX_SEARCH_DEPTH do
        firstguess := MTDF(root, firstguess, d);
        if times_up() then break;
    return firstguess;
    
   * @param bitBoard
   * @param scorer
   * @return
   */

  public Move findBestMove(BitBoard bitBoard, BitBoardScorer scorer) {
    long startTime = System.currentTimeMillis();
    negamax = new Negamax(startTime, timeGiven, scorer, new TranspositionTable());

    int depth = 0;
    Move bestMove = null;
    while (depth < max_depth) {
      depth += 1;
      try {
        bestMove = findMoveAtDepth(bitBoard, scorer, depth);
      } catch (TimeoutException e) {
        break;
      }
    }

    return bestMove;
  }

  private Move findMoveAtDepth(BitBoard bitBoard, BitBoardScorer scorer, int depth) throws TimeoutException {
    List<BitBoard> childBoards = bitBoard.getChildBoards();

    int bestScore = Integer.MIN_VALUE;
    Move bestMove = null;
    for (BitBoard childBoard : childBoards) {
      int score = calculateScore(childBoard, bitBoard.getNextColor(), scorer, depth);
      if (score > bestScore) {
        bestScore = score;
        bestMove = childBoard.getPreviousMove();
      }
    }

//    System.out.println(bitBoard.getNextColor() + " at " + depth + ": " + bestScore);
    return bestMove;
  }

  private int calculateScore(BitBoard board, Color color, BitBoardScorer scorer, int depth) throws TimeoutException {
    boolean checkTime = depth > 1;
    return negamax.recursive(board, color, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, checkTime);
  }
}
