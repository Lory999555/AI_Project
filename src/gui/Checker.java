package gui;

import java.awt.Color;
import java.awt.Graphics;

public final class Checker {
	private final static int DIMENSION = 50;

	private boolean black;

	public Checker(int i, boolean b) {
		this.type = i;
		this.black = b;
	}

	public void draw(Graphics g, int cx, int cy) {
		int x = cx - DIMENSION / 2;
		int y = cy - DIMENSION / 2;

		// Set checker color.

		g.setColor(black ? Color.BLACK : Color.RED);

		// Paint checker.

		g.fillOval(x, y, DIMENSION, DIMENSION);
		g.setColor(Color.WHITE);
		g.drawOval(x, y, DIMENSION, DIMENSION);
		g.drawString(String.valueOf(type), cx, cy);
	}

	public static boolean contains(int x, int y, int cx, int cy) {
		return (cx - x) * (cx - x) + (cy - y) * (cy - y) < DIMENSION / 2 * DIMENSION / 2;
	}
	
	private int type;
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	// The dimension is returned via a method rather than by accessing the
	// DIMENSION constant directly to avoid brittle code. If the constant was
	// accessed directly and I changed its value in Checker and recompiled only
	// this class, the old DIMENSION value would be accessed from external
	// classes whereas the new DIMENSION value would be used in Checker. The
	// result would be erratic code.

	public static int getDimension() {
		return DIMENSION;
	}

	public boolean isB() {
		return black;
	}
}