package core;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import algorithms.*;
import heuristics.*;
import representation.*;
import converter.ConverterMove;
import converter.SenderReceiver;
import heuristics.*;
import representation.*;
import representation.DipoleMove.typeMove;

import java.util.concurrent.Semaphore;
import representation.Conf.Status;

public class Main {
	
	public static Semaphore srSem= new Semaphore(0);
	public static Semaphore algSem= new Semaphore(0);
	public static boolean blackPlayer;

	public static void main(String[] args) throws InvalidActionException, CloneNotSupportedException {
		LAVORAMU();
		ConverterMove cm = new ConverterMove();
		Conf f = new DipoleConf(true);
		DipoleMove move = new DipoleMove(137438953472L,0,9,false, typeMove.DEATH,4);
		System.out.println(cm.generatePacket(move));
		//startServer();
		
//		Conf f = new DipoleConf(true);
//		LinkedList<Move> moves= new LinkedList<Move>();
//		moves= (LinkedList<Move>) f.getActions();
//		for(int i=0;i<moves.size();i++) {
//			System.out.println(moves.get(i).toString());
//		}
		/*
		Move choise;
		Conf state;

		HeuristicInterface hi = new BBEvaluator();
		AlgorithmInterface ai = new MTDFAgent(hi);

		state = new DipoleConf(false);
		
		
		System.out.println("\n\n---------------------------------------------------------------");
		System.out.println(state);
		System.out.println("---------------------------------------------------------------\n\n");

		choise = ai.compute(state);
		System.out.println("\n\n---------------------------------------------------------------");
		System.out.println(choise);
		System.out.println("---------------------------------------------------------------\n\n");

		state = choise.applyTo(state);
		System.out.println("\n\n---------------------------------------------------------------");
		System.out.println(state);
		System.out.println("---------------------------------------------------------------\n\n");

		
		while(state.getStatus()== Status.Ongoing) {
			choise = ai.compute(state);
			System.out.println("\n\n---------------------------------------------------------------");
			System.out.println(choise);
			System.out.println("---------------------------------------------------------------\n\n");
			
			state = choise.applyTo(state);
			System.out.println("\n\n---------------------------------------------------------------");
			System.out.println(state);
			System.out.println("---------------------------------------------------------------\n\n");
		
		}
		/**
		 * avviare il server (abbiamo un ogetto converter e si fa c.start) root
		 * istanzionio algoritmo ed euristica while(nextstate.getStatus() != vittorie)
		 * attesa sul semaforo //per capire che è il mio turno(o la mossa) if(è il mio
		 * turno) ai.compute che mi ridà la mossa c1.convert(mossa) //devono essere due
		 * entità diverse SR2.setmossa(stringa) //1 è proprio un covnertitore l'altro
		 * SR(sender receiver) else(devo codificare la mossa dell'avversario)
		 * SR2.getpacket //che mi ridà la stringa nextMovev= c1.decoder(string) // che
		 * mi ritorna la mossa next state = nextMove.applyto(root)
		 * 
		 * 
		 */
		
		System.out.println("FINITA");

	}


	public static void startServer() throws InvalidActionException, CloneNotSupportedException {
		blackPlayer=false;
		Conf state = new DipoleConf(blackPlayer);
		SenderReceiver sr = new SenderReceiver();
		sr.start();
		ConverterMove cm = new ConverterMove();
		int type = 11;
		Move move ;
		while(true) {
			try {
				algSem.acquire();
				
				if(sr.getStatus().equals("OPPONENT_MOVE")) {
					move= cm.unpacking(sr.getMove(),state);
					state= move.applyTo(state);
				}
				
				if(sr.getStatus().equals("YOUR_TURN")) {
					move = new DipoleMove(8,1024,type,blackPlayer,typeMove.QUIETMOVE,1);
					state= move.applyTo(state);
					System.out.println(cm.generatePacket(move));
					sr.setMove(cm.generatePacket(move));
					type-=2;
				}
				
				srSem.release();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void LAVORAMU() {
		Date date = new Date(2019 - 1900, 9, 18);
		Date now = new Date();
		// System.out.println(date.toString());
		// System.out.println(now.toString());
		long tiempu = now.getTime() - date.getTime();
		System.out.println("Giorni PERSI : " + tiempu / (1000 * 60 * 60 * 24)
				+ "\nhttps://gfycat.com/reasonabledismalkinglet-stinson-barney!!!");
	}

}
