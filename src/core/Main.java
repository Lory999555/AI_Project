package core;

import java.util.Date;

public class Main {

	public static void main(String[] args) {
	
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
		
		


	}
	
	public static long flipVertical(long x) {
		   long k1 = 0x00FF00FF00FF00FFL;
		   long k2 = 0x0000FFFF0000FFFFL;
		   x = ((x >>>  8) & k1) | ((x & k1) <<  8);
		   x = ((x >>> 16) & k2) | ((x & k2) << 16);
		   x = ( x >>> 32)       | ( x       << 32);
		   return x;
		}
}