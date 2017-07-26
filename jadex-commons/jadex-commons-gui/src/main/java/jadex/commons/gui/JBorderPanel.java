package jadex.commons.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

/** 
 * Creates a panel around a widgets to represent the border.
 *
 */
public class JBorderPanel extends JPanel
{
	/** Wrapped component. */
	protected JComponent wrappedcomponent;
	
	/**
	 *  Creates.
	 */
	public JBorderPanel(JComponent wrapped, Border border)
	{
		setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		this.wrappedcomponent = wrapped;
		setBorder(border);
		add(wrapped);
	}
	
	/**
	 *  Override.
	 */
	public Dimension getSize()
	{
		return getBorderedSize(wrappedcomponent.getSize());
	}
	
	/**
	 *  Override.
	 */
	public Dimension getMinimumSize()
	{
		return getBorderedSize(wrappedcomponent.getMinimumSize());
	}
	
	/**
	 *  Override.
	 */
	public Dimension getPreferredSize()
	{
		return getBorderedSize(wrappedcomponent.getPreferredSize());
	}
	
	/**
	 *  Override.
	 */
	public Dimension getMaximumSize()
	{
		return getBorderedSize(wrappedcomponent.getMaximumSize());
	}
	
	/**
	 *  Gets size with border.
	 */
	protected Dimension getBorderedSize(Dimension size)
	{
		Insets bi = getBorder().getBorderInsets(this);
		int w = (int) (size.getWidth() + bi.left + bi.right);
		int h = (int) (size.getHeight() + bi.top + bi.bottom);
		Dimension ret = new Dimension(w, h);
		return ret;
	}
}
