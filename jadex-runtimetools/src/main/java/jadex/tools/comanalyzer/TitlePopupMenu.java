package jadex.tools.comanalyzer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;


/**
 * A generic class for popupmenue with title. Note that their is a label
 * property indeed, but the basic ui dont use it.
 */
public class TitlePopupMenu extends JPopupMenu
{

	// -------- attributes --------

	/** The title. */
	protected String title;

	// -------- constructor --------

	/**
	 * Creates a popup with no title.
	 */
	public TitlePopupMenu()
	{
		this(null);
	}

	/**
	 * Creates a popup with title.
	 * 
	 * @param title The title.
	 */
	public TitlePopupMenu(String title)
	{
		this.title = title;
		setBorder(new TitleBorder(getBorder()));
	}

	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title)
	{
		this.title = title;
		invalidate();
		repaint();
	}

	/**
	 * @return The title.
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Overides the JPopupMenu method. Dont set label for JPopupMenu since some
	 * L&F might draw their own titles.
	 */
	public void setLabel(String label)
	{
		// NOP
	}

	// -------- inner classes --------

	/**
	 * A nice looking title with gradient paint ;) It is drawn on the border of
	 * the popup by first drawing the delegate and on top of that the title.
	 */
	private class TitleBorder extends AbstractBorder
	{

		/** The space below the title text */
		protected static final int TEXT_SPACING = 2;

		/** The space above the title text */
		protected static final int GROOVE_HEIGHT = 5;

		/** The original border for the popup menu */
		protected Border delegate;

		/**
		 * @param delegate The original Border of the popup.
		 */
		public TitleBorder(Border delegate)
		{
			this.delegate = delegate;
		}

		/**
		 * Returns the insets of the border.
		 * 
		 * @param c The component for which this border insets value applies
		 */
		public Insets getBorderInsets(Component c)
		{
			// return getBorderInsets(c, new Insets(0, 0, 0, 0));
			Insets i = delegate.getBorderInsets(((JPopupMenu)c));
			return getBorderInsets(c, new Insets(i.top, i.left, i.bottom, i.right));
		}

		/**
		 * Reinitialize the insets parameter with this Border's current Insets.
		 * 
		 * @param c the component for which this border insets value applies
		 * @param insets the object to be reinitialized
		 */
		public Insets getBorderInsets(Component c, Insets insets)
		{
			int descent = 0;
			int ascent = 16;

			String title = ((TitlePopupMenu)c).getTitle();

			// if there is no title return standard insets
			if(title == null)
				return insets;

			Font font = UIManager.getFont("PopupMenu.font"); // ("MenuItem.font");
			if(font == null)
				return insets;

			FontMetrics fm = c.getFontMetrics(font);
			if(fm != null)
			{
				descent = fm.getDescent();
				ascent = fm.getAscent();
			}

			// calculate the top border insets for the title text
			insets.top += ascent + descent + TEXT_SPACING + GROOVE_HEIGHT;
			return insets;
		}

		/**
		 * Paints the border for the popup and draws the title on top of it.
		 */
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
		{
			delegate.paintBorder(c, g, x, y, width, height);

			Graphics2D g2d = (Graphics2D)g;
			// get the colors and font for the title
			Color background = UIManager.getColor("MenuItem.selectionBackground");
			Color foreground = UIManager.getColor("MenuItem.foreground");
			Font font = UIManager.getFont("PopupMenu.font"); // ("MenuItem.font");

			// the standard L&F uses antialiasing for the text
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// draw the gradient background
			GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getInsets().top - GROOVE_HEIGHT, background);
			g2d.setPaint(gradient);
			g2d.fillRect(1, 1, width - 2, getInsets().top - GROOVE_HEIGHT);

			String title = ((TitlePopupMenu)c).getTitle();

			// if there is no title return
			if(title == null)
				return;

			// calculate the position for the title
			FontMetrics fm = getFontMetrics(font);
			int ascent = fm.getAscent();
			int stringWidth = fm.stringWidth(title);
			Point textLoc = new Point();
			textLoc.y = ascent + TEXT_SPACING;
			textLoc.x = ((width - stringWidth) / 2);

			// draw the title
			g2d.setFont(font);
			g2d.setColor(foreground);
			g2d.drawString(title, textLoc.x, textLoc.y);
		}
	}

}