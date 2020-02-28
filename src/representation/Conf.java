package representation;

import java.util.List;

public interface Conf {

	public enum Status {
		BlackWon, RedWon, Draw, Ongoing
	}

	public long[] getForHash();

	public boolean isBlack();

	public List<Move> getActions();

	public Move nullMove();

	public Status getStatus();

}
