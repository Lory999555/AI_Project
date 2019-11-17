package heuristics;

import java.util.Random;

import representation.Conf;

public class BBEvaluator implements HeuristicInterface{
	
	private Random r;
	public BBEvaluator() {
		r = new Random(567825);
	}

	
	@Override
	public int evaluate(Conf conf) {
		// TODO Auto-generated method stub
		return r.nextInt();
	}

}
