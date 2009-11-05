package com.daimler.client.gui.components.parts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * This class can be used to highlight the current line for any JTextComponent.
 * 
 * @author Santhosh Kumar T
 * @author Peter De Bruycker
 * @version 1.0
 */
public class CurrentLineHighlighter
{
	private static final String LINE_HIGHLIGHT = "linehilight"; // NOI18N - used
																// as
																// clientproperty
	private static final String PREVIOUS_CARET = "previousCaret"; // NOI18N -
																	// used as
																	// clientproperty
	private static Color col = new Color(255, 255, 204); // Color used for
															// highlighting the
															// line

	// to be used as static utility
	private CurrentLineHighlighter()
	{
	}

	// Installs CurrentLineHilighter for the given JTextComponent
	public static void install(JTextComponent c)
	{
		try
		{
			int i = c.getCaretPosition();
			Object obj = c.getHighlighter().addHighlight(0, 0, painter);
			c.putClientProperty(LINE_HIGHLIGHT, obj);
			c.putClientProperty(PREVIOUS_CARET, new Integer(c
					.getCaretPosition()));
			c.addCaretListener(caretListener);
			c.addMouseListener(mouseListener);
			c.addMouseMotionListener(mouseListener);
			Rectangle prev = c.modelToView(i);
			c.repaint(0, prev.y, c.getWidth(), prev.height);
		} catch (BadLocationException ignore)
		{
		}
	}

	// Uninstalls CurrentLineHighligher for the given JTextComponent
	public static void uninstall(JTextComponent c)
	{
		try
		{
			int i = c.getCaretPosition();
			c.getHighlighter().removeHighlight(
					c.getClientProperty(LINE_HIGHLIGHT));
			c.putClientProperty(LINE_HIGHLIGHT, null);
			c.putClientProperty(PREVIOUS_CARET, null);
			c.removeCaretListener(caretListener);
			c.removeMouseListener(mouseListener);
			c.removeMouseMotionListener(mouseListener);
			Rectangle prev = c.modelToView(i);
			c.repaint(0, prev.y, c.getWidth(), prev.height);
		} catch (Exception ignore)
		{
		}
	}

	private static CaretListener caretListener = new CaretListener()
	{
		public void caretUpdate(CaretEvent e)
		{
			JTextComponent c = (JTextComponent) e.getSource();
			currentLineChanged(c);
		}
	};

	private static MouseInputAdapter mouseListener = new MouseInputAdapter()
	{
		public void mousePressed(MouseEvent e)
		{
			JTextComponent c = (JTextComponent) e.getSource();
			currentLineChanged(c);
		}

		public void mouseDragged(MouseEvent e)
		{
			JTextComponent c = (JTextComponent) e.getSource();
			currentLineChanged(c);
		}
	};

	/**
	 * Fetches the previous caret location, stores the current caret location,
	 * If the caret is on another line, repaint the previous line and the
	 * current line
	 * 
	 * @param c
	 *            the text component
	 */
	private static void currentLineChanged(JTextComponent c)
	{
		try
		{
			int previousCaret = ((Integer) c.getClientProperty(PREVIOUS_CARET))
					.intValue();
			Rectangle prev = c.modelToView(previousCaret);
			Rectangle r = c.modelToView(c.getCaretPosition());
			c.putClientProperty(PREVIOUS_CARET, new Integer(c
					.getCaretPosition()));

			if (prev.y != r.y)
			{
				c.repaint(0, prev.y, c.getWidth(), r.height);
				c.repaint(0, r.y, c.getWidth(), r.height);
			}
		} catch (BadLocationException ignore)
		{
		}
	}

	private static Highlighter.HighlightPainter painter = new Highlighter.HighlightPainter()
	{
		public void paint(Graphics g, int p0, int p1, Shape bounds,
				JTextComponent c)
		{
			try
			{
				Rectangle r = c.modelToView(c.getCaretPosition());
				g.setColor(col);
				g.fillRect(0, r.y, c.getWidth(), r.height);
			} catch (BadLocationException ignore)
			{
			}
		}
	};
}