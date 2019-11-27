package converter;

import core.Main;
import representation.*;
import representation.DipoleMove.typeMove;

public class ConverterMove implements ConverterSignal {

	@Override
	public String generatePacket(Move a) {
		DipoleMove move = (DipoleMove) a;
		String toSQ = Board.DeBruijn(move.getToSq());
		String fromSQ = Board.DeBruijn(move.getFromSq());
		// String direction;
		if(move.gettP()== typeMove.DEATH) {
			if(move.isBlack()) 
				return "MOVE "+fromSQ+","+ Board.deathNoteBDirection[Board.getSquare(move.getFromSq())]+","+move.getDist();
			else 
				return "MOVE "+fromSQ+","+ Board.deathNoteRDirection[Board.getSquare(move.getFromSq())]+","+move.getDist();
		} else {
			if (fromSQ.charAt(0) > toSQ.charAt(0)) {
				if (fromSQ.charAt(1) > toSQ.charAt(1)) {
					return "MOVE "+fromSQ + "," + "NW" + "," + move.getDist();
				} else {
					if (fromSQ.charAt(1) < toSQ.charAt(1)) {
						return "MOVE "+fromSQ + "," + "NE" + "," + move.getDist();
					} else {
						return "MOVE "+fromSQ + "," + "N" + "," + move.getDist();
					}
				}
			} else {
				
			}
			if(fromSQ.charAt(0) == toSQ.charAt(0)) {
				if(fromSQ.charAt(1) > toSQ.charAt(1)) {
					return "MOVE "+fromSQ + "," + "W" + "," + move.getDist();
				}else {
					return "MOVE "+fromSQ + "," + "E" + "," + move.getDist();
				}
			}else {
				if (fromSQ.charAt(1) > toSQ.charAt(1)) {
					return "MOVE "+fromSQ + "," + "SW" + "," + move.getDist();
				} else {
					if (fromSQ.charAt(1) < toSQ.charAt(1)) {
						return "MOVE "+fromSQ + "," + "SE" + "," + move.getDist();
					} else {
						return "MOVE "+fromSQ + "," + "S" + "," + move.getDist();
					}
				}

			}
		}
		
	}

	public Move unpacking(String packet, Conf c) {
		DipoleConf move = (DipoleConf) c;
		String[] splitter = packet.split(",");
		int dist = Integer.parseInt(splitter[2]);
		int char1 = (int)splitter[0].charAt(0);
		int char2 = (int)splitter[0].charAt(1);
		int fromSQ = Board.stringToSquare(Character.toString(splitter[0].charAt(0)),
				Character.toString(splitter[0].charAt(1)));
		int toSQ;
		if (splitter[1].length() > 1) {
			toSQ = Board.toSquare(fromSQ, Character.toString(splitter[1].charAt(0)),
					Character.toString(splitter[1].charAt(1)), dist);
		} else {
			toSQ = Board.toSquare(fromSQ, Character.toString(splitter[1].charAt(0)), dist);
		}
		long fromBit = Board.squareToBitboard(fromSQ);
		long toBit = Board.squareToBitboard(toSQ);
		int type = move.getType(fromBit);
		
		if( 
			((splitter[1].equals("N")||splitter[1].equals("NE")|| splitter[1].equals("NW")) && char1-dist < 65) ||
			((splitter[1].equals("S")||splitter[1].equals("SE")|| splitter[1].equals("SW")) && char1+dist > 72) ||
			
			((splitter[1].equals("E")||splitter[1].equals("NE")|| splitter[1].equals("SE")) && char2+dist > 56) ||
			((splitter[1].equals("W")||splitter[1].equals("NW")|| splitter[1].equals("SW")) && char2-dist < 49)) {
			
			return new DipoleMove(fromBit,0, type, !Main.blackPlayer, typeMove.DEATH, dist);
		}
		else {
			
			typeMove typeM;
	
			if (Main.blackPlayer) {
				if (((toBit & move.getpBlack()) == 0) && ((toBit & move.getpRed()) == 0) ) {
					typeM = typeMove.QUIETMOVE;
				}
				else {
						if(((toBit & move.getpRed()) != 0)) {
							typeM = typeMove.MERGE;
						}else {
							int verify = (fromSQ >>> 3) - (toSQ >> 3);
							if (verify <= 0) {
								typeM = typeMove.BACKATTACK;
							}else {
								typeM = typeMove.FRONTATTACK;
							}
							
						}
				}
			} else {
				if (((toBit & move.getpBlack()) == 0) && ((toBit & move.getpRed()) == 0) ) {
					typeM = typeMove.QUIETMOVE;
				}
				else {
						if(((toBit & move.getpBlack()) != 0)) {
							typeM = typeMove.MERGE;
						}else {
							int verify = (fromSQ >>> 3) - (toSQ >> 3);
							if (verify >= 0) {
								typeM = typeMove.BACKATTACK;
							}else {
								typeM = typeMove.FRONTATTACK;
							}
							
						}
				}
			}
			return new DipoleMove(fromBit, toBit, type, !Main.blackPlayer, typeM, dist);
		}
		
	}

}
