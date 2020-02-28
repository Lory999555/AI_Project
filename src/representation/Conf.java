package representation;

import java.util.List;

public interface Conf {

	public enum Status {
		BlackWon, RedWon, Draw, Ongoing
	}
	/**
	 * return all representation of configuration
	 * @return a list of bitboard that represent the actual configuration.
	 */
	
	
	public long[] getForHash();
	
	public boolean isBlack();
	/**
	 * Get the Actions we can take at this State of the game. Ordering should
	 * reflect preferred traversal order. If no order preference exists, randomness
	 * might be a good idea if we're limited on time. Have to suppress warnings
	 * because List<Subtype> is not a sub-type of List<Supertype>
	 * 
	 * @return A List of possible Actions we can take at this State.
	 */
	@SuppressWarnings("rawtypes")
	public List<Move> getActions();
	
	public List<Integer> getEncodingActions(Move m);
	
	public Move nullMove();
	
	public int nullMoveEnc();

	/**
	 * The estimated value of this State. Higher is better for the first player;
	 * lower for the second. 
	 * 
	 * @return The estimated value of this State.
	 */
	public Status getStatus();


}
