package representation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import representation.DipoleMove.typeMove;

public class DipoleConf implements Conf, Cloneable {

	/*
	 * private long p1=0; private long p2=0; private long p3=0; private long p4=0;
	 * private long p5=0; private long p6=0; private long p7=0; private long p8=0;
	 * private long p9=0; private long p10=0; private long p11=0; private long
	 * p12=0;
	 */

	private long moves;

	private long frontAttack;
	private long backAttack;
	private long merge;
	private long death;
	private long quietMove;
	private long pBlack;
	private long pRed;
	private boolean black;
	private long[] pieces = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	static long blackSquare = 0x55aa55aa55aa55aaL;

	// Configurazione inzio partita
	public DipoleConf() {

		this.pieces[11] = 0x1000000000000008L;
		this.pRed = 0x8L;
		this.pBlack = 0x1000000000000000L;
		this.black = false;
//		this.pieces[1] = 0x1080000L;
//		this.pieces[2] = 0x2000000008L;
//		this.pieces[5] = 0x1000000000000000L;
//		this.pRed = 0x2001000008L;
//		this.pBlack = 0x1000000000080000L;
//		this.black = true;

	}

	// ritorna una fila verticale di bit in corrispondenza dello square passato
	private long rankMask(int sq) {
		return 0xffL << (sq & 56);
	}

	// ritorna una riga di bit in corrispondenza dello square passato
	private long fileMask(int sq) {
		return 0x0101010101010101L << (sq & 7);
	}

	// ritorna la diagonale primaria a partire dallo square passato
	private long diagonalMask(int sq) {
		long maindia = 0x8040201008040201L;
		int diag = 8 * (sq & 7) - (sq & 56);
		int nort = -diag & (diag >> 31);
		int sout = diag & (-diag >> 31);
		return (maindia >>> sout) << nort;
	}

	// ritorna la diagonale secondaria a partire dallo square passato
	private long antiDiagMask(int sq) {
		long maindia = 0x0102040810204080L;
		int diag = 56 - 8 * (sq & 7) - (sq & 56);
		int nort = -diag & (diag >> 31);
		int sout = diag & (-diag >> 31);
		return (maindia >>> sout) << nort;
	}

	// ritorna la rosa completa della pedina oresa in considerazione senza rimuovere
	// le mosse impossibili
	// e limitare i movimenti
	private long getRose2(int sq) {
		return rankMask(sq) | fileMask(sq) | diagonalMask(sq) | antiDiagMask(sq);
	}

	// rimuove le mosse impossibili attraverso l'ultilizzo di scanner. Utilizzato in
	// combinazione con getRose2
	private long removeImpossibleMove(long rose, int sq, long opponent, long mine, int type, long[] pieces) {
		long notFree = opponent;
		long ovest = fileMask(sq);
		long est = ovest;
		long sud = rankMask(sq);
		long nord = sud;
		long tmp = 0;
		int cont = 0;
		while (cont < type) {
			ovest ^= ovest & Board.b_l;
			est ^= est & Board.b_r;
			sud ^= sud & Board.b_d;
			nord ^= nord & Board.b_u;
			ovest <<= 1;
			est >>>= 1;
			sud >>>= 8;
			nord <<= 8;
			notFree ^= pieces[cont] & (~mine);
			tmp |= notFree & ovest;
			tmp |= notFree & est;
			tmp |= notFree & sud;
			tmp |= notFree & nord;
			cont++;
		}
		while (cont < 8) {
			ovest ^= ovest & Board.b_l;
			est ^= est & Board.b_r;
			sud ^= sud & Board.b_d;
			nord ^= nord & Board.b_u;
			ovest <<= 1;
			est >>>= 1;
			sud >>>= 8;
			nord <<= 8;
			tmp |= rose & ovest;
			tmp |= rose & est;
			tmp |= rose & sud;
			tmp |= rose & nord;
			cont++;
		}
		return rose ^ (rose & tmp);
	}

	// seconda implementazione di removeImpossibleMove dovuta all'aggiunta della
	// tabella moveBook in Board dove abbiamo
	// già le mosse limitate. Si vanno a rimuovere se esistono le pedine avversarie
	// che non possiamo mangiare
	// utilizzando 4 scan che vanno a nord sud est e ovest
	private long removeImpossibleMove2(long rose, int sq, long opponent, long mine, int type, long[] pieces) {
		long notFree = opponent;
		long ovest = fileMask(sq);
		long est = ovest;
		long sud = rankMask(sq);
		long nord = sud;
		long tmp = 0;
		int cont = 0;

		int typecond = type + 1;
		while (cont < typecond) {
			ovest ^= ovest & Board.b_l;
			est ^= est & Board.b_r;
			sud ^= sud & Board.b_d;
			nord ^= nord & Board.b_u;
			ovest <<= 1;
			est >>>= 1;
			sud >>>= 8;
			nord <<= 8;
			notFree ^= pieces[cont] & (~mine);
			tmp |= notFree & ovest;
			tmp |= notFree & est;
			tmp |= notFree & sud;
			tmp |= notFree & nord;
			cont++;
		}
		return rose ^ (rose & tmp);
	}

	// ritorna tutte le mosse possibili che può effettuare la pedina. Queste mosse
	// si hanno popolando le
	// variabili backAttack, frontAttack, QuietMove. Questa implementazione fa
	// utilizzo di getRose e removeImpossibleMove
	private void allMoves(long x, long opponent, long mines, int type, long[] pieces) {
		int sq = Board.getSquare(x);
		long rose = getRose2(sq);
		rose = removeImpossibleMove(rose & blackSquare ^ x, sq, opponent, mines, type, pieces);
		long backMask = x ^ (x - 1);
		backMask |= rankMask(sq);
		backMask = backMask & rose;
		backAttack = backMask & opponent;
		long frontMask = backMask ^ rose;
		frontAttack = frontMask & opponent;
		quietMove = frontMask ^ frontAttack;
		moves = backAttack | frontAttack | quietMove;
	}

	// ritorna tutte le mosse possibili che può effettuare la pedina. Queste mosse
	// si hanno popolando le
	// variabili backAttack, frontAttack, QuietMove e merge. Utilizza la struttura
	// dati creata in Board.
	// Da essa prende la rosa della pedina presa in considerazione, applica
	// removeImpossibleMove2 ritornando cosi la rosa con
	// le sole mosse possibili e infine divide la rosa nei differenti tipi di
	// movimento
	public void allMoves2(long x, long opponent, long mines, int type, long[] pieces, long[][] possibleMove) {
		assert (type < 12);
		if (type > 6)
			type = 6;
		int sq = Board.getSquare(x);
		long rose = possibleMove[type][sq];
		rose = removeImpossibleMove2(rose ^ x, sq, opponent, mines, type, pieces);
		long backMask = x ^ (x - 1);
		backMask |= rankMask(sq);
		backMask = backMask & rose;
		backAttack = backMask & opponent;
		long frontMask = backMask ^ rose;
		frontAttack = frontMask & opponent;
		merge = frontMask & mines;
		quietMove = frontMask ^ frontAttack ^ merge;
		moves = backAttack | frontAttack | quietMove | merge; // forse non serve
	}

//	// precalcolo rosa dell'intera scacchiera
//	public long[][] precalculations() {
//		long rose[][]= new long [7][64];
//		long tmp;
//		long square = blackSquare;
//		/*
//		for (int i = 0; i < 7; i++) {
//			for (int j = 1; j < 64; j += 1) {
//				 tmp= getRose2(j);
//			}
//		}int 
//		*/
//		long lsb;
//		int sq;
//		long aaa[] = {0,0,0,0,0,0,0,0,0,0,0,0};
//		while(square!=0) {
//			lsb = square & -square;
//			square ^= lsb;
//			sq = getSquare(lsb);
//			tmp = getRose2(sq);
//			tmp &= blackSquare;
//			rose[6][sq] = tmp;
//			rose[5][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 6, aaa);
//			rose[4][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 5, aaa);
//			rose[3][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 4, aaa);
//			rose[2][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 3, aaa);
//			rose[1][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 2, aaa);
//			rose[0][sq] = removeImpossibleMove(tmp, sq, 0, lsb, 1, aaa);	
//		}
//		System.out.println("{");
//		for(int i=0; i<7; i++) {
//			System.out.print("{");
//			for(int j=0; j<64; j++){
//				System.out.print(rose[i][j]+"L,");
//			}
//			System.out.print("}\n");
//		}
//		System.out.println("}");
//		
//		return rose;
//	}// precalculations

//	 // Ritorna la rosa di azione della pedina presa in considerazione private
//	 long getRose(long square, int type, long mine, long opponent) {
//	 
//	 return checkSquareMoves(square, -9, Board.b_r | Board.b_d, 1, mine, opponent,
//	 type) | checkSquareMoves(square, -16, Board.b2_d, 2, mine, opponent, type) |
//	 checkSquareMoves(square, -7, Board.b_l | Board.b_d, 1, mine, opponent, type)
//	 | checkSquareMoves(square, -2, Board.b2_r, 2, mine, opponent, type) |
//	 checkSquareMoves(square, 2, Board.b2_l, 2, mine, opponent, type) |
//	 checkSquareMoves(square, 7, Board.b_r | Board.b_u, 1, mine, opponent, type) |
//	 checkSquareMoves(square, 16, Board.b2_u, 2, mine, opponent, type) |
//	 checkSquareMoves(square, 9, Board.b_l | Board.b_u, 1, mine, opponent, type);
//	 }
//	 
//	 // Ritorna il movimento di una pedina in una sola direzione (ES
//	 N,NO,NE,S,SO...) private long checkSquareMoves(long square, int shift, long
//	 border, int addMove, long mine, long opponent, int type) { long notFreeSquare
//	 = opponent; long ret = 0; long tmp =0; int cont = addMove - 1; while ((square
//	 & border) == 0 && cont < type) { if (shift > 0) { square <<= shift; } else {
//	 square >>>= -shift; } notFreeSquare ^= pieces[cont] & (~mine); cont +=
//	 addMove; tmp =square^(square & notFreeSquare); ret |= tmp; } ret ^= (ret &
//	 notFreeSquare); return ret; }

	// Conta il numero di 1 presenti all'interno di una bitboard
	public int popCount(long x) {
		int count = 0;
		while (x != 0) {
			count++;
			x &= x - 1; // reset LS1B
		}
		return count;
	}

	// SOMMATORIA DEI PEZZI PER IL PROPRIO VALORE VAL
	public int getMaterial() {
		int material = 0;

		if (pieces[0] != 0) {
			material += popCount(pieces[0]) * Board.VAL_ONE;
		}
		if (pieces[1] != 0) {
			material += popCount(pieces[1]) * Board.VAL_TW0;
		}
		if (pieces[2] != 0) {
			material += popCount(pieces[2]) * Board.VAL_THREE;
		}
		if (pieces[3] != 0) {
			material += popCount(pieces[3]) * Board.VAL_FOUR;
		}
		if (pieces[4] != 0) {
			material += popCount(pieces[4]) * Board.VAL_FIVE;
		}
		if (pieces[5] != 0) {
			material += popCount(pieces[5]) * Board.VAL_SIX;
		}
		if (pieces[6] != 0) {
			material += popCount(pieces[6]) * Board.VAL_SEVEN;
		}
		if (pieces[7] != 0) {
			material += popCount(pieces[7]) * Board.VAL_EIGHT;
		}
		if (pieces[8] != 0) {
			material += popCount(pieces[8]) * Board.VAL_NINE;
		}
		if (pieces[9] != 0) {
			material += popCount(pieces[9]) * Board.VAL_TEN;
		}
		if (pieces[10] != 0) {
			material += popCount(pieces[10]) * Board.VAL_ELEVEN;
		}
		if (pieces[11] != 0) {
			material += popCount(pieces[11]) * Board.VAL_TWELVE;
		}
		return material;
	}

	@Override
	public List<Move> getActions() {
		long pawn;
		long mines;
		LinkedList<Move> actions = new LinkedList<Move>();
		if (!black) {
			mines = pRed;
			while (mines != 0) {
				// prende l'ultimo bit della bitBoard dei rossi
				pawn = mines & -mines;
				// toglie la pedina presa talla bitboard
				mines ^= pawn;
				int selectType = 0;
				int death = 0;

				while (selectType < 12) {
					if ((pawn & pieces[selectType]) != 0) {
						break;
					}
					selectType++;
				}

				// ritorniamo il tipo della pedina
				// selectType = getType(pawn);
				allMoves2(pawn, pBlack, pRed, selectType, pieces, Board.movingBook);
				// allMoves(pawn, pBlack, pRed, selectType, pieces);

				long temp;
				while (backAttack != 0) {
					temp = backAttack & -backAttack;
					backAttack ^= temp;
					int dist = Math.abs((Board.getSquare(pawn) >>> 3) - (Board.getSquare(temp) >>> 3));
					assert (Math.abs(Board.getSquare(pawn) - (Board.getSquare(temp))) > 0);
					// se la distanza è uguale a zero vuol dire che le pedine si trovano sulla
					// stessa riga pertanto la distanza sarà data dalla differenza dell'indice delle
					// due caselle
					if (dist != 0)
						actions.add(new DipoleMove(pawn, temp, selectType, black, typeMove.BACKATTACK, dist));
					else
						actions.add(new DipoleMove(pawn, temp, selectType, black, typeMove.BACKATTACK,
								Math.abs(Board.getSquare(pawn) - (Board.getSquare(temp)))));
				}
				while (frontAttack != 0) {
					temp = frontAttack & -frontAttack;
					frontAttack ^= temp;
					actions.add(new DipoleMove(pawn, temp, selectType, black, typeMove.FRONTATTACK,
							Math.abs((Board.getSquare(pawn) >>> 3) - (Board.getSquare(temp) >>> 3))));
				}
				while (quietMove != 0) {
					temp = quietMove & -quietMove;
					quietMove ^= temp;
					actions.add(new DipoleMove(pawn, temp, selectType, black, typeMove.QUIETMOVE,
							Math.abs((Board.getSquare(pawn) >>> 3) - (Board.getSquare(temp) >>> 3))));

				}
				while (merge != 0) {
					temp = merge & -merge;
					merge ^= temp;
					actions.add(new DipoleMove(pawn, temp, selectType, black, typeMove.MERGE,
							Math.abs((Board.getSquare(pawn) >>> 3) - (Board.getSquare(temp) >>> 3))));

				}
				death = Board.deathNoteRed[Board.getSquare(pawn)];
//				if (death <= selectType + 1) {
//					for (int i = death; i <= selectType + 1; i++) {
//						// generiamo una mossa per ogni morte che abbiamo. Es se la morte minima è 3 e
//						// la mia pedina è di tipo 6
//						// vado a creare la mossa morte 3,4,5,6.
//						actions.add(new DipoleMove(pawn, 0, selectType, black, typeMove.DEATH, i));
//					}
//				}
				if (death < selectType + 1) {
					actions.add(new DipoleMove(pawn, 0, selectType, black, typeMove.DEATH, death));
					actions.add(new DipoleMove(pawn, 0, selectType, black, typeMove.DEATH, selectType + 1));
				} else if (death == selectType + 1) {
					actions.add(new DipoleMove(pawn, 0, selectType, black, typeMove.DEATH, death));

				}

			}
			return actions;
		} else {
			long[] pieces180 = new long[12];
			long pBlack180;
			long pRed180;
			int cont = 0;
			int death = 0;
			while (cont < 12) {
				pieces180[cont] = Board.flip180(pieces[cont]);
				cont++;
			}
			pBlack180 = Board.flip180(pBlack);
			pRed180 = Board.flip180(pRed);
			mines = pBlack180;
			while (mines != 0) {
				pawn = mines & -mines;
				mines ^= pawn;
				int selectType = 0;

				while (selectType < 12) {
					if ((pawn & pieces180[selectType]) != 0) {
						break;
					}
					selectType++;
				}

				// selectType = getType180(pawn);
				allMoves2(pawn, pRed180, pBlack180, selectType, pieces180, Board.movingBook);
				// allMoves(pawn, pRed, pBlack, selectType, pieces);
				backAttack = Board.flip180(backAttack);
				frontAttack = Board.flip180(frontAttack);
				quietMove = Board.flip180(quietMove);
				merge = Board.flip180(merge);
				pawn = Board.flip180(pawn);
				long temp;
				while (backAttack != 0) {
					temp = backAttack & -backAttack;
					backAttack ^= temp;
					assert (Math.abs(Board.getSquare(pawn) - (Board.getSquare(temp))) > 0);
					int dist = Math.abs((Board.getSquare(pawn) >>> 3) - (Board.getSquare(temp) >>> 3));
					if (dist != 0)
						actions.addFirst(new DipoleMove(pawn, temp, selectType, black, typeMove.BACKATTACK, dist));
					else
						actions.addFirst(new DipoleMove(pawn, temp, selectType, black, typeMove.BACKATTACK,
								Math.abs(Board.getSquare(pawn) - (Board.getSquare(temp)))));
				}
				while (frontAttack != 0) {
					temp = frontAttack & -frontAttack;
					frontAttack ^= temp;
					actions.addFirst(new DipoleMove(pawn, temp, selectType, black, typeMove.FRONTATTACK,
							Math.abs((Board.getSquare(pawn) >>> 3) - (Board.getSquare(temp) >>> 3))));
				}
				while (quietMove != 0) {
					temp = quietMove & -quietMove;
					quietMove ^= temp;
					actions.addFirst(new DipoleMove(pawn, temp, selectType, black, typeMove.QUIETMOVE,
							Math.abs((Board.getSquare(pawn) >>> 3) - (Board.getSquare(temp) >>> 3))));
				}
				while (merge != 0) {
					temp = merge & -merge;
					merge ^= temp;
					actions.addFirst(new DipoleMove(pawn, temp, selectType, black, typeMove.MERGE,
							Math.abs((Board.getSquare(pawn) >>> 3) - (Board.getSquare(temp) >>> 3))));
				}
				death = Board.deathNoteBlack[Board.getSquare(pawn)];
//				if (death <= selectType + 1) {
//					for (int i = death; i <= selectType + 1; i++) {
//						// generiamo una mossa per ogni morte che abbiamo. Es se la morte minima è 3 e
//						// la mia pedina è di tipo 6
//						// vado a creare la mossa morte 3,4,5,6.
//						actions.add(new DipoleMove(pawn, 0, selectType, black, typeMove.DEATH, i));
//					}
//				}

				if (death < selectType + 1) {
					actions.add(new DipoleMove(pawn, 0, selectType, black, typeMove.DEATH, death));
					actions.add(new DipoleMove(pawn, 0, selectType, black, typeMove.DEATH, selectType + 1));
				} else if (death == selectType + 1) {
					actions.add(new DipoleMove(pawn, 0, selectType, black, typeMove.DEATH, death));

				}
			}
			return actions;
		}
	}

	/**
	 * Return encoding actions list
	 * 
	 * @return
	 */
	public List<Integer> getActions2(DipoleMove mossa) {
		long pawn;
		int sqPawn;
		long mines;
		int dist;

		List<Integer> actions = new ArrayList<Integer>();
		if (!black) {
			mines = pRed;
			while (mines != 0) {
				// prende l'ultimo bit della bitBoard dei rossi
				pawn = mines & -mines;
				// toglie la pedina presa talla bitboard
				mines ^= pawn;
				int selectType = 0;
				int death = 0;

				while (selectType < 12) {
					if ((pawn & pieces[selectType]) != 0) {
						break;
					}
					selectType++;
				}

				// ritorniamo il tipo della pedina
				// selectType = getType(pawn);
				allMoves2(pawn, pBlack, pRed, selectType, pieces, Board.movingBook);
				sqPawn = Board.getSquare(pawn);
				// allMoves(pawn, pBlack, pRed, selectType, pieces);
				long temp;
				while (backAttack != 0) {
					temp = backAttack & -backAttack;
					backAttack ^= temp;
					int sqTemp = Board.getSquare(temp);
					dist = (Math.abs((sqPawn >> 3) - (sqTemp >> 3)) << 8);

					// se la distanza è uguale a zero vuol dire che le pedine si trovano sulla
					// stessa riga pertanto la distanza sarà data dalla differenza dell'indice delle
					// due caselle
					if (dist == 0)
						dist = Math.abs(sqPawn - sqTemp);
					actions.add(mossa.encodingMove(sqPawn, sqTemp, dist, selectType, black, typeMove.BACKATTACK));
				}
				while (frontAttack != 0) {
					temp = frontAttack & -frontAttack;
					frontAttack ^= temp;
					actions.add(
							mossa.encodingMove(sqPawn, Board.getSquare(temp), selectType, black, typeMove.FRONTATTACK));
				}
				while (quietMove != 0) {
					temp = quietMove & -quietMove;
					quietMove ^= temp;
					actions.add(
							mossa.encodingMove(sqPawn, Board.getSquare(temp), selectType, black, typeMove.QUIETMOVE));
				}
				while (merge != 0) {
					temp = merge & -merge;
					merge ^= temp;
					actions.add(mossa.encodingMove(sqPawn, Board.getSquare(temp), selectType, black, typeMove.MERGE));
				}
				// Per la gestione della morte utilizziamo una struttura precalcolata dove
				// inseriamo la minima morte che si
				// ha prendendo in considerazione quella casella
				death = Board.deathNoteRed[sqPawn];
				if (death <= selectType + 1) {
					for (int i = death; i < selectType + 1; i++) {
						// generiamo una mossa per ogni morte che abbiamo. Es se la morte minima è 3 e
						// la mia pedina è di tipo 6
						// vado a creare la mossa morte 3,4,5,6.
						actions.add(mossa.encodingMove(sqPawn, 0, i, selectType, black, typeMove.DEATH));
					}
				}
			}
			return actions;
		} else {
			long[] pieces180 = new long[12];
			long pBlack180;
			long pRed180;
			int cont = 0;

			// flippiamo tutte le bitBoard in modo da calcolare la mossa per le pedine nere
			while (cont < 12) {
				pieces180[cont] = Board.flip180(pieces[cont]);
				cont++;
			}
			pBlack180 = Board.flip180(pBlack);
			pRed180 = Board.flip180(pRed);
			mines = pBlack180;

			while (mines != 0) {
				// prende l'ultimo bit della bitBoard dei rossi
				pawn = mines & -mines;
				// toglie la pedina presa talla bitboard
				mines ^= pawn;
				int selectType = 0;
				int death = 0;
				// ritorniamo il tipo della pedina

				while (selectType < 12) {
					if ((pawn & pieces180[selectType]) != 0) {
						break;
					}
					selectType++;
				}
				allMoves2(pawn, pRed180, pBlack180, selectType, pieces180, Board.movingBook);
				// allMoves(pawn, pRed, pBlack, selectType, pieces);

				// una volta calcolate le mosse possiamo riportare le pedine nella stessa
				// posizione di prima
				// riflippando tutto di nuovo di 180 gradi
				backAttack = Board.flip180(backAttack);
				frontAttack = Board.flip180(frontAttack);
				quietMove = Board.flip180(quietMove);
				merge = Board.flip180(merge);
				pawn = Board.flip180(pawn);

				sqPawn = Board.getSquare(pawn);
				long temp;
				while (backAttack != 0) {
					temp = backAttack & -backAttack;
					backAttack ^= temp;
					int sqTemp = Board.getSquare(temp);
					dist = (Math.abs((sqPawn >> 3) - (sqTemp >> 3)) << 8);
					if (dist == 0)
						dist = Math.abs(sqPawn - sqTemp);
					actions.add(mossa.encodingMove(sqPawn, Board.getSquare(temp), dist, selectType, black,
							typeMove.BACKATTACK));
				}
				while (frontAttack != 0) {
					temp = frontAttack & -frontAttack;
					frontAttack ^= temp;
					actions.add(
							mossa.encodingMove(sqPawn, Board.getSquare(temp), selectType, black, typeMove.FRONTATTACK));
				}
				while (quietMove != 0) {
					temp = quietMove & -quietMove;
					quietMove ^= temp;
					actions.add(mossa.encodingMove(Board.getSquare(pawn), Board.getSquare(temp), selectType, black,
							typeMove.QUIETMOVE));
				}
				while (merge != 0) {
					temp = merge & -merge;
					merge ^= temp;
					actions.add(mossa.encodingMove(Board.getSquare(pawn), Board.getSquare(temp), selectType, black,
							typeMove.MERGE));
				}
				death = Board.deathNoteBlack[sqPawn];
				if (death <= selectType + 1) {
					for (int i = death; i < selectType + 1; i++) {
						actions.add(mossa.encodingMove(sqPawn, 0, i, selectType, black, typeMove.DEATH));
					}
				}
			}
			return actions;
		}
	}


	// aggiornare lo stato mettendo le mosse massime (60 mosse);
	@Override
	public Status getStatus() {

		if (pBlack != 0) {
			if (pRed != 0) {
				return Status.Ongoing;
			} else {
				return Status.BlackWon;
			}
		}
		return Status.RedWon;

	}

	public long[] getConf() {
		return pieces.clone();
	}

	public long getMoves() {
		return moves;
	}

	public void setMoves(long moves) {
		this.moves = moves;
	}

	public long getFrontAttack() {
		return frontAttack;
	}

	public void setFrontAttack(long frontAttack) {
		this.frontAttack = frontAttack;
	}

	public long getBackAttack() {
		return backAttack;
	}

	public void setBackAttack(long backAttack) {
		this.backAttack = backAttack;
	}

	public long getDeath() {
		return death;
	}

	public void setDeath(long death) {
		this.death = death;
	}

	public long getQuietMove() {
		return quietMove;
	}

	public void setQuietMove(long quietMove) {
		this.quietMove = quietMove;
	}

	public long getMerge() {
		return merge;
	}

	public void setMerge(long merge) {
		this.merge = merge;
	}

	public long getpBlack() {
		return pBlack;
	}

	public void setpBlack(long pBlack) {
		this.pBlack = pBlack;
	}

	public long getpRed() {
		return pRed;
	}

	public void setpRed(long pRed) {
		this.pRed = pRed;
	}

	public boolean isBlack() {
		return black;
	}

	public void setBlack(boolean black) {
		this.black = black;
	}

	public long[] getPieces() {
		return pieces;
	}

	public long getBoard(int i) {
		return this.pieces[i];
	}

	public void setBoard(int i, long v) {
		this.pieces[i] = v;
	}

	public void setPieces(long[] pieces) {
		this.pieces = pieces;
	}

	public static long getBlackSquare() {
		return blackSquare;
	}

	public static void setBlackSquare(long blackSquare) {
		DipoleConf.blackSquare = blackSquare;
	}

	/**
	 * da testare per vedere se il tutto viene copiato bene e se non intralcia
	 * qualche meccanismo
	 * 
	 * 
	 */
	public DipoleConf clone() throws CloneNotSupportedException {
		DipoleConf tmp = (DipoleConf) super.clone();
		this.pieces = tmp.pieces.clone();
		return tmp;

	}

	public String toStringOld() {
		String tmp = Long.toBinaryString(pRed | pBlack);
		StringBuilder sb = new StringBuilder();
		int c = tmp.length() - 1;
		for (int i = 0; i < 8; i++) {
			sb.append('\n');
			for (int j = 0; j < 8; j++) {
				if (c >= 0) {
					sb.append(tmp.charAt(c));
					sb.append(' ');
					c--;
				} else {
					sb.append('0');
					sb.append(' ');
					c--;
				}
			}
		}
		return sb.reverse().toString();

	}

	public String toString() {
		StringBuilder sb = new StringBuilder("0000000000000000000000000000000000000000000000000000000000000000");
		for (int i = 0; i < pieces.length; i++) {
			long pred = pieces[i] & pRed;
			long pblack = pieces[i] & pBlack;
			String tmpr = Long.toBinaryString(pred);
			String tmpb = Long.toBinaryString(pblack);
			for (int j = tmpr.length() - 1; j >= 0; j--) {
				if (tmpr.charAt(j) == '1')
					switch (i) {
					case 0:
						sb.setCharAt(tmpr.length() - j - 1, 'A');
						break;
					case 1:
						sb.setCharAt(tmpr.length() - j - 1, 'B');
						break;
					case 2:
						sb.setCharAt(tmpr.length() - j - 1, 'C');
						break;
					case 3:
						sb.setCharAt(tmpr.length() - j - 1, 'D');
						break;
					case 4:
						sb.setCharAt(tmpr.length() - j - 1, 'E');
						break;
					case 5:
						sb.setCharAt(tmpr.length() - j - 1, 'F');
						break;
					case 6:
						sb.setCharAt(tmpr.length() - j - 1, 'G');
						break;
					case 7:
						sb.setCharAt(tmpr.length() - j - 1, 'H');
						break;
					case 8:
						sb.setCharAt(tmpr.length() - j - 1, 'I');
						break;
					case 9:
						sb.setCharAt(tmpr.length() - j - 1, 'L');
						break;
					case 10:
						sb.setCharAt(tmpr.length() - j - 1, 'M');
						break;
					case 11:
						sb.setCharAt(tmpr.length() - j - 1, 'N');
						break;
					}
			}

			for (int j = tmpb.length() - 1; j >= 0; j--) {
				if (tmpb.charAt(j) == '1')
					switch (i) {
					case 0:
						sb.setCharAt(tmpb.length() - j - 1, 'a');
						break;
					case 1:
						sb.setCharAt(tmpb.length() - j - 1, 'b');
						break;
					case 2:
						sb.setCharAt(tmpb.length() - j - 1, 'c');
						break;
					case 3:
						sb.setCharAt(tmpb.length() - j - 1, 'd');
						break;
					case 4:
						sb.setCharAt(tmpb.length() - j - 1, 'e');
						break;
					case 5:
						sb.setCharAt(tmpb.length() - j - 1, 'f');
						break;
					case 6:
						sb.setCharAt(tmpb.length() - j - 1, 'g');
						break;
					case 7:
						sb.setCharAt(tmpb.length() - j - 1, 'h');
						break;
					case 8:
						sb.setCharAt(tmpb.length() - j - 1, 'i');
						break;
					case 9:
						sb.setCharAt(tmpb.length() - j - 1, 'l');
						break;
					case 10:
						sb.setCharAt(tmpb.length() - j - 1, 'm');
						break;
					case 11:
						sb.setCharAt(tmpb.length() - j - 1, 'n');
						break;
					}
			}
		}
		sb.insert(56, '\n');
		sb.insert(48, '\n');
		sb.insert(40, '\n');
		sb.insert(32, '\n');
		sb.insert(24, '\n');
		sb.insert(16, '\n');
		sb.insert(8, '\n');
		for (int i = 0; i <= sb.length(); i += 2) {
			sb.insert(i, ' ');
		}

		return sb.reverse().toString();

	}

	@Override
	public long[] getForHash() {
		return this.getPieces();
	}

	// ritorna il tipo della pedina passata
	public int getType(long pawn) {
		int selectType = 0;
		while (selectType < 12) {
			if ((pawn & pieces[selectType]) != 0) {
				break;
			}
			selectType++;
		}
		return selectType;
	}

	// ritorna il tipo della pedina passata. utilizzato in caso la pedina sia
	// ruotata di 180
	public int getType180(long pawn) {
//		Board.flip180(pawn);
		long[] pieces180 = this.getPieces180();
		int selectType = 0;
		while (selectType < 12) {
			if ((pawn & pieces180[selectType]) != 0) {
				break;
			}
			selectType++;
		}
		return selectType;
	}

	// ruota tutti i pezzi di 180
	public long[] getPieces180() {
		int cont = 0;
		long[] pieces180 = new long[12];
		while (cont < 12) {
			pieces180[cont] = Board.flip180(pieces[cont]);
			cont++;
		}
		return pieces180;
	}

	public int evalFA() {	//ritorna la somma dei valori delle pedine nemiche attaccate
		int val = 0;
		long pawn;
		long fa = frontAttack;
		while (fa != 0) {
			pawn = fa & -fa;
			fa ^= pawn;
			val += (getType(pawn)+1);
//			System.out.println("front di "+pawn+"\n");
		}
		
//		if(val != 0)
//			System.out.println("val = "+val);
		
		
		return val;
	}

	public int evalBA() {
		int val = 0;
		long pawn;
		long ba = backAttack;
		while (ba != 0) {
			pawn = ba & -ba;
			ba ^= pawn;
			val += (getType(pawn)+1);
//			System.out.println("Back di "+pawn+" = "+ba+"\n");
		}
		
		
//		if(val != 0)
//			System.out.println("val = "+val);
		
		
		return val;
	}
	
	/***
	 * return the pawns number
	 * @param x
	 * @return
	 */
	public int pawnCount(long x) {
		long y;
		int c = 0;
		while (x != 0) {
			y = x & (-x);
			c+=(getType(y)+1);
			x &= x - 1; // reset LS1B
		}
		return c;
	}
	
//	public int pawnCount180(long x) {
//		long y;
//		int c = 0;
//		while (x != 0) {
//			y = x & (-x);
//			c+=(getType180(y)+1);
//			x &= x - 1; // reset LS1B
//		}
//		return c;
//	}

}
