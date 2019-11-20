package core;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import algorithms.*;
import heuristics.*;
import representation.DipoleConf;
import representation.DipoleMove;

import representation.Move;

import representation.*;




public class Main {

	public static void main(String[] args) {
		long now = System.currentTimeMillis();
		DipoleConf prova = new DipoleConf(true);
		List<Move> mosse = prova.getActions();

		HeuristicInterface hi = new BBEvaluator();
		
		AlgorithmInterface ai = new MTDFAgent(hi);
		
		Conf root = new DipoleConf(false);
		//Conf root = new DipoleConf(false);
		LAVORAMU();
		System.out.println(root);
		Move choise = ai.compute(root);
		System.out.println(choise);

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
