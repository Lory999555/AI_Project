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
		int fromSQ = Board.stringToSquare(Character.toString(splitter[0].charAt(0)),
				Character.toString(splitter[0].charAt(1)));
		int toSQ;
		int dist = Integer.parseInt(splitter[2]);
		if (splitter[1].length() > 1) {
			toSQ = Board.toSquare(fromSQ, Character.toString(splitter[1].charAt(0)),
					Character.toString(splitter[1].charAt(1)), dist);
		} else {
			toSQ = Board.toSquare(fromSQ, Character.toString(splitter[1].charAt(0)), dist);
		}
		long fromBit = Board.squareToBitboard(fromSQ);
		long toBit = Board.squareToBitboard(toSQ);
		int type = move.getType(fromBit);
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
		DipoleMove moves = new DipoleMove(fromBit, toBit, type, !Main.blackPlayer, typeM, dist);
		System.out.println(moves.toString());
		return moves;
	}

}
