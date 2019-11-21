package core;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import algorithms.*;
import heuristics.*;
import representation.*;



public class Main {

	public static void main(String[] args) {

		/**
		 * // long a = 263172;
		 * 
		 * // System.out.println(a); // long b = flipVertical(a); //
		 * System.out.println(b); long aa = 4; long[] ciccio = { aa }; //
		 * System.out.println(aa); // System.out.println(ciccio[0]); // aa=7;
		 * 
		 * // long now=System.currentTimeMillis(); long now = new Date().getTime();
		 * 
		 * for (int i = 0; i < 100000000; i++) { ciccio[0] ^= 1; } //
		 * System.out.println(ciccio[0]);
		 * 
		 * // long after= System.currentTimeMillis()-now; long after = new
		 * Date().getTime() - now; System.out.println(after);
		 **/
		long now = System.nanoTime();

		//LAVORAMU();
		DipoleConf prova = new DipoleConf(true);
		DipoleMove move = new DipoleMove();
//		List<Move> mosse = prova.getActions();
		List<Integer> mosse = prova.getActions2(move);
		for (int i=0;i< mosse.size();i++) {
//			System.out.println(mosse.get(i).toString());
			move.decodingMove(mosse.get(i));
			System.out.println(move.toString());
		}
		
		System.out.println(System.nanoTime()-now);
	}

	public static long flipVertical(long x) {
		long k1 = 0x00FF00FF00FF00FFL;
		long k2 = 0x0000FFFF0000FFFFL;
		x = ((x >>> 8) & k1) | ((x & k1) << 8);
		x = ((x >>> 16) & k2) | ((x & k2) << 16);
		x = (x >>> 32) | (x << 32);
		return x;
	}

	public static void LAVORAMU() {
		Date date = new Date(2019 - 1900, 9, 18);
		Date now = new Date();
		//System.out.println(date.toString());
		//System.out.println(now.toString());
		long tiempu = now.getTime() - date.getTime();
		System.out.println("Giorni PERSI : " + tiempu/(1000*60*60*24) +"\nhttps://gfycat.com/reasonabledismalkinglet-stinson-barney!!!");
	}
	
	
}
