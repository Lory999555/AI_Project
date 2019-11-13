package memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import representation.Conf;
import ut.veikotiit.checkers.bitboard.BitBoard;
import ut.veikotiit.checkers.bitboard.BitUtil;

public class TranspositionTable {

  private final static int WHITE_PIECE = 0;
  private final static int WHITE_KING = 1;
  private final static int BLACK_PIECE = 2;
  private final static int BLACK_KING = 3;

  private final Map<Conf, CachedValue> map = new HashMap<>();

  public void put(Conf conf_hash, CachedValue cachedValue) {
    map.put(conf_hash, cachedValue);
  }

  public CachedValue get(Conf conf_hash) {
    return map.get(conf_hash);
  }

  public void clear() {
    map.clear();
  }

  /*
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
  */
}
