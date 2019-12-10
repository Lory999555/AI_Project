package gui2;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;

import gui.BoardGui;
import gui.BoardGui.PosCheck;

public class DipoleBoard extends JPanel{
	LinkedList<Dipole> ld = new LinkedList<Dipole>();
	
	public DipoleBoard() {
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		
		
		for (PosCheck posCheck : posBlack)
			if (posCheck != BoardGui.this.posCheck)
				posCheck.checker.draw(g, posCheck.cx, posCheck.cy, posCheck.checker.getType());
		for (PosCheck posCheck : posRed)
			if (posCheck != BoardGui.this.posCheck)
				posCheck.checker.draw(g, posCheck.cx, posCheck.cy, posCheck.checker.getType());

		// Draw dragged checker last so that it appears over any underlying
		// checker.

		if (posCheck != null)
			posCheck.checker.draw(g, posCheck.cx, posCheck.cy, 12);
	}
	
	
}