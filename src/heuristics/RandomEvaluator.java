package heuristics;

import java.util.Random;

import representation.Conf;

public class RandomEvaluator implements HeuristicInterface{
	
	private Random r;
	public RandomEvaluator() {
		r = new Random(845212);
	}

	
	public int evaluate(Conf conf) {
		// TODO Auto-generated method stub
//		int c = r.nextInt();
//		System.out.println(c);
		return r.nextInt();
	}


	@Override
	public int evaluate_R(Conf conf) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int evaluate_B(Conf conf) {
		// TODO Auto-generated method stub
		return 0;
	}

}
