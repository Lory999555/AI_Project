package memory;

import java.util.Random;

import representation.Board;

public class ZobristGen {


	private long[][] zobristTable;

	public ZobristGen() {
		Random prng = new Random();
		zobristTable = new long[64][12];
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 12; j++) {
				zobristTable[i][j] = prng.nextLong();
			}
		}

	}
	
	public long zobristHash(long[] pieces) {
		long key = 0;
		int i = 0;
		long tmp, bit;
		while (i < 12) {

			tmp = pieces[i];
			while (tmp != 0) {
				bit = tmp & -tmp;
				tmp ^= bit;
				key ^= zobristTable[Board.getSquare(bit)][i];
			}

			i++;
		}
		return key;
	}

}
