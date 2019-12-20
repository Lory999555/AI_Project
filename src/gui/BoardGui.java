package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import core.Main;
import representation.Board;
import representation.Conf;
import representation.DipoleConf;
import representation.DipoleMove;
import representation.Move;
import representation.DipoleMove.typeMove;

public class BoardGui extends JComponent {
	// dimension of checkerboard square (25% bigger than checker)

	private final static int SQUAREDIM = (int) (Checker.getDimension() * 1.5);

	// dimension of checkerboard (width of 8 squares)

	private final int BOARDDIM = 8 * SQUAREDIM;

	// preferred size of Board component

	private Dimension dimPrefSize;

	// dragging flag -- set to true when user presses mouse button over checker
	// and cleared to false when user releases mouse button

	private boolean inDrag = false;

	// displacement between drag start coordinates and checker center coordinates

	private int deltax, deltay;

	// reference to positioned checker at start of drag

	private PosCheck posCheck;

	// center location of checker at start of drag

	private int oldcx, oldcy, newcx, newcy;

	// list of Checker objects and their initial positions

	private List<PosCheck> posBlack;
	private List<PosCheck> posRed;
	
	private boolean mossa = true;

	public BoardGui(boolean black) {
		posBlack = new ArrayList<>();
		posRed = new ArrayList<>();
		dimPrefSize = new Dimension(BOARDDIM, BOARDDIM);

		if (!black) {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent me) {
					// Obtain mouse coordinates at time of press.

					int x = me.getX();
					int y = me.getY();

					// Locate positioned checker under mouse press.

					for (PosCheck posCheck : posBlack)
						if (Checker.contains(x, y, posCheck.cx, posCheck.cy)) {
							BoardGui.this.posCheck = posCheck;
							oldcx = posCheck.cx;
							oldcy = posCheck.cy;
							deltax = x - posCheck.cx;
							deltay = y - posCheck.cy;
							inDrag = true;
							return;
						}
				}

				@Override
				public void mouseReleased(MouseEvent me) {
					// When mouse released, clear inDrag (to
					// indicate no drag in progress) if inDrag is
					// already set.

					if (inDrag)
						inDrag = false;
					else
						return;

					// Snap checker to center of square.

					int x = me.getX();
					int y = me.getY();
					posCheck.cx = (x - deltax) / SQUAREDIM * SQUAREDIM + SQUAREDIM / 2;
					posCheck.cy = (y - deltay) / SQUAREDIM * SQUAREDIM + SQUAREDIM / 2;
					newcx = posCheck.cx;
					newcy = posCheck.cy;

					// Do not move checker onto an occupied square.

					for (PosCheck posCheck : posBlack)
						if (posCheck != BoardGui.this.posCheck && posCheck.cx == BoardGui.this.posCheck.cx
								&& posCheck.cy == BoardGui.this.posCheck.cy) {
							BoardGui.this.posCheck.cx = oldcx;
							BoardGui.this.posCheck.cy = oldcy;
						}
					posCheck = null;
					repaint();
					mossa = false;
				}
			});
		} else {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent me) {
					// Obtain mouse coordinates at time of press.

					int x = me.getX();
					int y = me.getY();

					// Locate positioned checker under mouse press.

					for (PosCheck posCheck : posRed)
						if (Checker.contains(x, y, posCheck.cx, posCheck.cy)) {
							BoardGui.this.posCheck = posCheck;
							oldcx = posCheck.cx;
							oldcy = posCheck.cy;
							deltax = x - posCheck.cx;
							deltay = y - posCheck.cy;
							inDrag = true;
							return;
						}
				}

				@Override
				public void mouseReleased(MouseEvent me) {
					// When mouse released, clear inDrag (to
					// indicate no drag in progress) if inDrag is
					// already set.

					if (inDrag)
						inDrag = false;
					else
						return;

					// Snap checker to center of square.

					int x = me.getX();
					int y = me.getY();
					posCheck.cx = (x - deltax) / SQUAREDIM * SQUAREDIM + SQUAREDIM / 2;
					posCheck.cy = (y - deltay) / SQUAREDIM * SQUAREDIM + SQUAREDIM / 2;
					
					newcx = posCheck.cx;
					newcy = posCheck.cy;

					// Do not move checker onto an occupied square.

					for (PosCheck posCheck : posRed)
						if (posCheck != BoardGui.this.posCheck && posCheck.cx == BoardGui.this.posCheck.cx
								&& posCheck.cy == BoardGui.this.posCheck.cy) {
							BoardGui.this.posCheck.cx = oldcx;
							BoardGui.this.posCheck.cy = oldcy;
						}
					posCheck = null;
					repaint();
					
					
					mossa = false;
				}
			});
		}

		// Attach a mouse motion listener to the applet. That listener listens
		// for mouse drag events.

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent me) {
				if (inDrag) {
					// Update location of checker center.

					posCheck.cx = me.getX() - deltax;
					posCheck.cy = me.getY() - deltay;
					repaint();
				}
			}
		});

	}
	
	public Move getMove(Board b, Conf state, boolean blackPlayer) {
		DipoleConf s = (DipoleConf) state;
//		while(mossa) {
//			repaint();
//			System.out.println("sssssssssssssssssssssssssssssssssssssss");
//		}
		int colF = ((oldcx-(SQUAREDIM/2))/SQUAREDIM)+1;
		int rowF = ((oldcy-(SQUAREDIM/2))/SQUAREDIM)+1;
		System.out.println("colF:"+colF+"   rowF:"+rowF);
		int colT = ((newcx-(SQUAREDIM/2))/SQUAREDIM)+1;
		int rowT = ((newcy-(SQUAREDIM/2))/SQUAREDIM)+1;
		System.out.println("colT:"+colT+"   rowT:"+rowT);
		
		int from = ((9-colF)*(9-rowF))-1;		//prende numeri da 0 a 63 (con il -1), vanno bene per il fromBit????/////////////////////////////////////////
		int to = ((9-colT)*(9-rowT))-1;
		System.out.println("FROM:"+from+"   TOOO:"+to);
		int dist = Math.abs((from >>> 3) - (to >>> 3));
		System.out.println("DIST"+dist);
		long fromBit = b.squareToBitboard(from);
		long toBit = b.squareToBitboard(to);
		int type = s.getType(fromBit)-1;
		
		typeMove typeM;		
		if (blackPlayer) {
			if (((toBit & s.getpBlack()) == 0) && ((toBit & s.getpRed()) == 0) ) {
				typeM = typeMove.QUIETMOVE;
			}
			else {
					if(((toBit & s.getpRed()) != 0)) {
						typeM = typeMove.MERGE;
					}else {
						int verify = (from >>> 3) - (to >> 3);
						if (verify <= 0) {
							typeM = typeMove.BACKATTACK;
						}else {
							typeM = typeMove.FRONTATTACK;
						}
						
					}
			}
		} else {
			if (((toBit & s.getpBlack()) == 0) && ((toBit & s.getpRed()) == 0) ) {
				typeM = typeMove.QUIETMOVE;
			}
			else {
					if(((toBit & s.getpBlack()) != 0)) {
						typeM = typeMove.MERGE;
					}else {
						int verify = (from >>> 3) - (to >> 3);
						if (verify >= 0) {
							typeM = typeMove.BACKATTACK;
						}else {
							typeM = typeMove.FRONTATTACK;
						}
						
					}
			}
		}
		return new DipoleMove(fromBit, toBit , type, !blackPlayer, typeM, dist);
	}
	
	public void applyMove(int from, int to) {
		int colF = 8 - (from - (from / 8) * 8);////////////////righe e colonne nella notazione della grafica, con 1 in alto a sinistra e 64 in basso a dx
		int rowF = 8 - (from / 8);
		int colT = 8 - (to - (to / 8) * 8);
		int rowT = 8 - (to / 8);
		System.out.println("applyMove colF:"+colF+" rowF:"+rowF+" colT:"+colT+" rowT:"+rowT+" from:::"+from+" to:::"+to);
		
		int xF = (colF - 1) * SQUAREDIM + SQUAREDIM / 2;
		int yF = (rowF - 1) * SQUAREDIM + SQUAREDIM / 2;
		for (PosCheck posCheck : posBlack) {
			if (Checker.contains(xF, yF, posCheck.cx, posCheck.cy)) {
				BoardGui.this.posCheck = posCheck;
				oldcx = posCheck.cx;
				oldcy = posCheck.cy;
				deltax = xF - posCheck.cx;
				deltay = yF - posCheck.cy;
			}
		}
		for (PosCheck posCheck : posRed) {
			if (Checker.contains(xF, yF, posCheck.cx, posCheck.cy)) {
				BoardGui.this.posCheck = posCheck;
				oldcx = posCheck.cx;
				oldcy = posCheck.cy;
				deltax = xF - posCheck.cx;
				deltay = yF - posCheck.cy;
			}
		}
		int xT = (colT - 1) * SQUAREDIM + SQUAREDIM / 2;
		int yT = (rowT - 1) * SQUAREDIM + SQUAREDIM / 2;
		posCheck.cx = (xT - deltax) / SQUAREDIM * SQUAREDIM + SQUAREDIM / 2;
		posCheck.cy = (yT - deltay) / SQUAREDIM * SQUAREDIM + SQUAREDIM / 2;
		// Do not move checker onto an occupied square.

		for (PosCheck posCheck : posBlack)
			if (posCheck != BoardGui.this.posCheck && posCheck.cx == BoardGui.this.posCheck.cx
					&& posCheck.cy == BoardGui.this.posCheck.cy) {
				BoardGui.this.posCheck.cx = oldcx;
				BoardGui.this.posCheck.cy = oldcy;
			}
		posCheck = null;
		repaint();
	}

//   public Board()
//   {
//      posChecks = new ArrayList<>();
//      dimPrefSize = new Dimension(BOARDDIM, BOARDDIM);
//
//      addMouseListener(new MouseAdapter()
//                       {
//                          @Override
//                          public void mousePressed(MouseEvent me)
//                          {
//                             // Obtain mouse coordinates at time of press.
//
//                             int x = me.getX();
//                             int y = me.getY();
//
//                             // Locate positioned checker under mouse press.
//
//                             for (PosCheck posCheck: posChecks)
//                                if (Checker.contains(x, y, posCheck.cx, 
//                                                     posCheck.cy))
//                                {
//                                   Board.this.posCheck = posCheck;
//                                   oldcx = posCheck.cx;
//                                   oldcy = posCheck.cy;
//                                   deltax = x - posCheck.cx;
//                                   deltay = y - posCheck.cy;
//                                   inDrag = true;
//                                   return;
//                                }
//                          }
//
//                          @Override
//                          public void mouseReleased(MouseEvent me)
//                          {
//                             // When mouse released, clear inDrag (to
//                             // indicate no drag in progress) if inDrag is
//                             // already set.
//
//                             if (inDrag)
//                                inDrag = false;
//                             else
//                                return;
//
//                             // Snap checker to center of square.
//
//                             int x = me.getX();
//                             int y = me.getY();
//                             posCheck.cx = (x - deltax) / SQUAREDIM * SQUAREDIM + 
//                                           SQUAREDIM / 2;
//                             posCheck.cy = (y - deltay) / SQUAREDIM * SQUAREDIM + 
//                                           SQUAREDIM / 2;
//
//                             // Do not move checker onto an occupied square.
//
//                             for (PosCheck posCheck: posChecks)
//                                if (posCheck != Board.this.posCheck && 
//                                    posCheck.cx == Board.this.posCheck.cx &&
//                                    posCheck.cy == Board.this.posCheck.cy)
//                                {
//                                   Board.this.posCheck.cx = oldcx;
//                                   Board.this.posCheck.cy = oldcy;
//                                }
//                             posCheck = null;
//                             repaint();
//                          }
//                       });
//
//      // Attach a mouse motion listener to the applet. That listener listens
//      // for mouse drag events.
//
//      addMouseMotionListener(new MouseMotionAdapter()
//                             {
//                                @Override
//                                public void mouseDragged(MouseEvent me)
//                                {
//                                   if (inDrag)
//                                   {
//                                      // Update location of checker center.
//
//                                      posCheck.cx = me.getX() - deltax;
//                                      posCheck.cy = me.getY() - deltay;
//                                      repaint();
//                                   }
//                                }
//                             });
//
//   }

	public void add(Checker checker, int row, int col) {
		if (row < 1 || row > 8)
			throw new IllegalArgumentException("row out of range: " + row);
		if (col < 1 || col > 8)
			throw new IllegalArgumentException("col out of range: " + col);
		PosCheck posCheck = new PosCheck();
		posCheck.checker = checker;
		posCheck.cx = (col - 1) * SQUAREDIM + SQUAREDIM / 2;
		posCheck.cy = (row - 1) * SQUAREDIM + SQUAREDIM / 2;
		if (checker.isB()) {
			for (PosCheck _posCheck : posBlack)
				if (posCheck.cx == _posCheck.cx && posCheck.cy == _posCheck.cy)
					throw new AlreadyOccupiedException("square at (" + row + "," + col + ") is occupied");
			posBlack.add(posCheck);
		} else {
			for (PosCheck _posCheck : posRed)
				if (posCheck.cx == _posCheck.cx && posCheck.cy == _posCheck.cy)
					throw new AlreadyOccupiedException("square at (" + row + "," + col + ") is occupied");
			posRed.add(posCheck);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return dimPrefSize;
	}

	@Override
	protected void paintComponent(Graphics g) {
		paintCheckerBoard(g);
		for (PosCheck posCheck : posBlack)
			if (posCheck != BoardGui.this.posCheck)
				posCheck.checker.draw(g, posCheck.cx, posCheck.cy);
		for (PosCheck posCheck : posRed)
			if (posCheck != BoardGui.this.posCheck)
				posCheck.checker.draw(g, posCheck.cx, posCheck.cy);

		// Draw dragged checker last so that it appears over any underlying
		// checker.

		if (posCheck != null)
			posCheck.checker.draw(g, posCheck.cx, posCheck.cy);
	}

	private void paintCheckerBoard(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Paint checkerboard.

		for (int row = 0; row < 8; row++) {
			g.setColor(((row & 1) != 0) ? Color.BLACK : Color.WHITE);
			for (int col = 0; col < 8; col++) {
				g.fillRect(col * SQUAREDIM, row * SQUAREDIM, SQUAREDIM, SQUAREDIM);
				g.setColor((g.getColor() == Color.BLACK) ? Color.WHITE : Color.BLACK);
			}
		}
	}

	// positioned checker helper class
	private class PosCheck {
		public Checker checker;
		public int cx;
		public int cy;
	}

}