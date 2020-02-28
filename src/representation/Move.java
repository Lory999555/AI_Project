package representation;

public interface Move {
	public long getFromSq();
	public long getToSq();

	public boolean validOn(Conf input);
	
	public Conf applyTo(Conf input);

	public int getValue();

	@Override
	public String toString();

	@Override
	public boolean equals(Object obj);
	}

