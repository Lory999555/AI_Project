package gui;

import java.awt.EventQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import algorithms.AlgorithmInterface;
import converter.ConverterMove;
import representation.Board;
import representation.Conf;
import representation.Move;

public class Checkers extends JFrame
{
   public Checkers(String title, boolean black, Conf state, Pattern p, ConverterMove cm, Move move_B, Move move_R, AlgorithmInterface ai_B, AlgorithmInterface ai_R)
   {
      super(title);
      setDefaultCloseOperation(EXIT_ON_CLOSE);

      BoardGui board = new BoardGui(black);
//      board.add(new Checker(CheckerType.BLACK_REGULAR), 1, 4);
//      board.add(new Checker(CheckerType.RED_KING), 8, 5);
      board.add(new Checker(12,true), 1, 4);
      board.add(new Checker(12,false), 8, 5);
      setContentPane(board);

      pack();
      setVisible(true);
      play(board, black, state, p, cm, move_B, move_R, ai_B, ai_R);
   }

private void play(BoardGui boardGui, boolean blackPlayer, Conf state, Pattern p, ConverterMove cm, Move move_B, Move move_R, AlgorithmInterface ai_B, AlgorithmInterface ai_R) {
	Board b = new Board();
	int col;
	int row;
	while (true) {
		if (blackPlayer) { //entro quando scrivo RED
			move_B = boardGui.getMove(b,state, blackPlayer);
			state = move_B.applyTo(state);
			
			move_B = ai_B.compute(state);
			state = move_B.applyTo(state);
			int from = b.getSquare(move_B.getFromSq());
			int to = b.getSquare(move_B.getToSq());
		
			System.out.println("FROMIF:"+from+"   TOOIF:"+to);
			
			boardGui.applyMove(from,to);
//			System.out.println(cm.generatePacket(move_B));

		} else {
//			System.out.println(state.toString());
			move_R = ai_R.compute(state);
			state = move_R.applyTo(state);
			int from = b.getSquare(move_R.getFromSq());
			int to = b.getSquare(move_R.getToSq());
			
			System.out.println("FROMELSE:"+from+"   TOOELSE:"+to);
			
			boardGui.applyMove(from,to);
//			System.out.println(cm.generatePacket(move_R));

//			System.out.println(state.toString());

//			String mossa = "a";
//			Matcher m = p.matcher(mossa);
//			while (!m.matches()) {
//				System.out.println("Inserisci mossa (ES:  H5,N,2)");
//				mossa = scan.nextLine();
//				m = p.matcher(mossa);
//			}
//			move_R = cm.unpackingLocal(mossa, state);
			
			move_R = boardGui.getMove(b,state, blackPlayer);
			state = move_R.applyTo(state);
		}
	}
	
}

//   public static void main(String[] args)
//   {
//      Runnable r = new Runnable()
//                   {
//                      @Override
//                      public void run()
//                      {
//                         new Checkers("Checkers");
//                      }
//                   };
//      EventQueue.invokeLater(r);
//   }
}