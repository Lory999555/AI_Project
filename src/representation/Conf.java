package representation;

import java.util.List;

public interface Conf {

	public enum Status {
		PlayerOneWon, PlayerTwoWon, Draw, Ongoing
	}

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

	/**
	 * The estimated value of this State. Higher is better for the first player;
	 * lower for the second.
	 * 
	 * @return The estimated value of this State.
	 */
	public float heuristic();

	/**
	 * Return the Status of this game at this State.
	 * 
	 * @return The current Status.
	 */
	public Status getStatus();

	/**
	 * Get the parent State of this State.
	 * 
	 * @return This State's parent State.
	 */
	public Conf getParentState();

	/**
	 * Returns an alternate heuristic for comparing two heuristic functions.
	 * 
	 * @return The heuristic value.
	 */
	public float heuristic2();

	/**
	 * Returns a unique String identifier for this State.
	 * 
	 * @return The identifier.
	 */
	public String identifier();

}
