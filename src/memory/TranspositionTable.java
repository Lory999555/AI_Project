package memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;

public class TranspositionTable {

  private final static int WHITE_PIECE = 0;
  private final static int WHITE_KING = 1;
  private final static int BLACK_PIECE = 2;
  private final static int BLACK_KING = 3;

  private final static long[] randomBitstrings = new long[200];

  static {
    Random random = new Random(58139);
    for (int i = 0; i < 200; i++) {
      randomBitstrings[i] = random.nextLong();
    }
  }

  private final Map<Long, CachedValue> map = new HashMap<>();

  public void put(BitBoard board, CachedValue cachedValue) {
    map.put(calculateHash(board), cachedValue);
  }

  public CachedValue get(BitBoard bitBoard) {
    return map.get(calculateHash(bitBoard));
  }

  public void clear() {
    map.clear();
  }

  private long calculateHash(BitBoard bitBoard) {
    long hash = 0;
    for (int bit : BitUtil.longToBits(bitBoard.getWhiteRegularPieces())) {
      hash ^= randomBitstrings[50 * WHITE_PIECE + bit];
    }
    for (int bit : BitUtil.longToBits(bitBoard.getWhiteKings())) {
      hash ^= randomBitstrings[50 * WHITE_KING + bit];
    }
    for (int bit : BitUtil.longToBits(bitBoard.getBlackRegularPieces())) {
      hash ^= randomBitstrings[50 * BLACK_PIECE + bit];
    }
    for (int bit : BitUtil.longToBits(bitBoard.getBlackKings())) {
      hash ^= randomBitstrings[50 * BLACK_KING + bit];
    }
    return hash;
  }
}
