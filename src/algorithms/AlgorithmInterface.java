package algorithms;

import representation.Conf;
import representation.Move;

public interface AlgorithmInterface {
	
	public Move compute(Conf conf);
	
	public void warmUp(long millisec);

}
