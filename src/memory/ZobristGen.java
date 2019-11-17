package memory;

import java.util.Random;

public class ZobristGen {

	private static int col = 60;

	private long[][] zobristTable; // da controllare come viene implementata

	public ZobristGen() {
		Random prng = new Random();
		zobristTable = new long[14][col];
		for (int i = 0; i < 14; ++i) {
			for (int j = 0; j < col; ++j) {
				zobristTable[i][j] = prng.nextLong();
			}
		}

	}
	
	/**
	 * Calcuates a Zobrist hash of a game state.
	 * 
	 * @param ls the game state
	 * @return the Zobrist hash of the state
	 */
	public long zobristHash(long[] ls) {	//rivederlo
		long key = 0;
		for (int i = 0; i < ls.length; ++i) {
			key ^= zobristTable[i][(int) ls[i] % col];
		}
		return key;
	}

}
