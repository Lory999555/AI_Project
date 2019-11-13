package memory;

import java.util.Random;

public class ZobristGen {

	private static int N_SEEDS = 3 * 12;

	private long[][] zobristTable; // da controllare come viene implementata

	public ZobristGen() {
		Random prng = new Random();
		zobristTable = new long[14][N_SEEDS + 1];
		for (int i = 0; i < 14; ++i) {
			for (int j = 0; j < N_SEEDS + 1; ++j) {
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
		for (int i = 0; i < 14; ++i) {
			key ^= zobristTable[i][(int) ls[i]];
		}
		return key;
	}

}
