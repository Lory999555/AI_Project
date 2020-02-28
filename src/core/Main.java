package core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import algorithms.*;
import heuristics.*;
import representation.*;
import representation.Conf.Status;
import converter.ConverterMove;
import converter.SenderReceiver;
import java.util.concurrent.Semaphore;


public class Main {

	private static HeuristicInterface hi5;

	private static AlgorithmInterface ai_R;
	private static AlgorithmInterface ai_B;

	private static Conf state;
	private static Move move_R;
	private static Move move_B;

	public static Semaphore srSem = new Semaphore(0);
	public static Semaphore algSem = new Semaphore(0);
	public static boolean blackPlayer;

	public static void main(String[] args) throws CloneNotSupportedException{
		InetAddress ip = null;
		try {
			ip = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
		}
		int port = Integer.parseInt(args[1]);

		hi5 = new BBEvaluator5();

		ai_R = new ABVisit(hi5, false, 5, 15);

		ai_B = new ABVisit(hi5, true, 5, 15);

		state = new DipoleConf();

		startServer(ip, port);
	}

	public static void startServer(InetAddress ip, int port) throws CloneNotSupportedException {
		// blackPlayer = false;
		SenderReceiver sr = new SenderReceiver(ip, port);
		sr.start();
		ConverterMove cm = new ConverterMove();
		// int type = 11;
		while (true) {
			try {
				algSem.acquire();

				if (sr.getStatus().equals("MESSAGE All players connected")) {
					if (Main.blackPlayer) {
						ai_B.warmUp(15000);
					} else {
						ai_R.warmUp(15000);
					}
				} else if (sr.getStatus().equals("OPPONENT_MOVE")) {
					if (Main.blackPlayer) {
						move_R = cm.unpacking(sr.getMove(), state);
						state = move_R.applyTo(state);
					} else {
						move_B = cm.unpacking(sr.getMove(), state);
						state = move_B.applyTo(state);
					}
				}

				else if (sr.getStatus().equals("YOUR_TURN")) {
					if (Main.blackPlayer) {
						move_B = ai_B.compute(state);
						state = move_B.applyTo(state);
						sr.setMove(cm.generatePacket(move_B));
					} else {
						move_R = ai_R.compute(state);
						state = move_R.applyTo(state);
						sr.setMove(cm.generatePacket(move_R));
					}

				} else if (sr.getStatus().equals("DEFEAT")) {
					break;
				} else if (sr.getStatus().equals("VICTORY")) {
					break;
				}

				srSem.release();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}