package gui2;

import java.awt.Color;
import java.awt.Graphics;

public final class Dipole
{
	
	
	private boolean black;
	private int type;
	
	
   private final static int DIMENSION = 60;

//   private CheckerType checkerType;
//
//   public Checker(CheckerType checkerType)
//   {
//      this.checkerType = checkerType;
//   }
   public Dipole(int type, boolean black)
   {
      this.type = type;
      this.black = black;
   }

   public void draw(Graphics g, int cx, int cy, int type)
   {
      int x = cx - DIMENSION / 2;
      int y = cy - DIMENSION / 2;

      // Set checker color.

//      g.setColor(checkerType == CheckerType.BLACK_REGULAR ||
//                 checkerType == CheckerType.BLACK_KING ? Color.BLACK : 
//                 Color.RED);
      g.setColor(black? Color.BLACK : Color.RED);

      // Paint checker.

      g.fillOval(x, y, DIMENSION, DIMENSION);
      g.setColor(Color.WHITE);
      g.drawOval(x, y, DIMENSION, DIMENSION);

//      if (checkerType == CheckerType.RED_KING || 
//          checkerType == CheckerType.BLACK_KING)
      String t = String.valueOf(type);
      g.drawString(t, cx-5, cy+5);
   }

   public static boolean contains(int x, int y, int cx, int cy)
   {
      return (cx - x) * (cx - x) + (cy - y) * (cy - y) < DIMENSION / 2 * 
             DIMENSION / 2;
   }

   // The dimension is returned via a method rather than by accessing the
   // DIMENSION constant directly to avoid brittle code. If the constant was
   // accessed directly and I changed its value in Checker and recompiled only
   // this class, the old DIMENSION value would be accessed from external 
   // classes whereas the new DIMENSION value would be used in Checker. The
   // result would be erratic code.
   
   public static int getDimension()
   {
      return DIMENSION;
   }

public int getType() {
	return type;
}

public boolean isB() {
	return black;
}
}