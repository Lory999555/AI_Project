package core;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import algorithms.*;
import heuristics.*;
import representation.*;
import representation.Conf.Status;

public class Main {

	public static void main(String[] args) throws InvalidActionException, CloneNotSupportedException {
		LAVORAMU();

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
