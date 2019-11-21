package heuristics;

import java.util.Random;

import representation.Conf;

public class BBEvaluator implements HeuristicInterface{
	
	private Random r;
	public BBEvaluator() {
		r = new Random(845212);
	}

	
	@Override
	public int evaluate(Conf conf) {
		// TODO Auto-generated method stub
		int c = r.nextInt();
		System.out.println(c);
		return c;
	}

}
