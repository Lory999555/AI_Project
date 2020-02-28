package heuristics;

import representation.Conf;

public interface HeuristicInterface {
	
	public int evaluate_R(Conf conf);
	public int evaluate_B(Conf conf);
	public void print();
}
