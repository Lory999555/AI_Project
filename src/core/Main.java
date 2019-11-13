package core;



import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import representation.DipoleConf;
import representation.Move;

public class Main {

	public static void main(String[] args) {
		
		/*
	
		//long a = 263172;
		
		//System.out.println(a);
		//long b = flipVertical(a);
		//System.out.println(b);
		long aa=4;
		long[] ciccio= {aa};
		//System.out.println(aa);
		//System.out.println(ciccio[0]);
		//aa=7;
		
		//long now=System.currentTimeMillis();
		long now=new Date().getTime();
		
		for(int i=0;i<100000000;i++) {
			ciccio[0] ^= 1;
		}
		//System.out.println(ciccio[0]);
		
		//long after= System.currentTimeMillis()-now;
		long after=new Date().getTime() - now;
		System.out.println(after);
		
		*/
		
		DipoleConf prova = new DipoleConf("BLACK");
		List<Move> mosse = prova.getActions();
		for (int i=0;i< mosse.size();i++) {
			System.out.println(mosse.get(i).toString());
		}
		
	}
}
