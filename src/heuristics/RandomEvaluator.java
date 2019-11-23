package heuristics;

import java.util.Random;

import representation.Conf;

public class RandomEvaluator implements HeuristicInterface{
	
	private Random r;
	public RandomEvaluator() {
		r = new Random(845212);
	}

	
	@Override
	public int evaluate(Conf conf) {
		// TODO Auto-generated method stub
//		int c = r.nextInt();
//		System.out.println(c);
		return r.nextInt();
	}

}
