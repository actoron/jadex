package jadex.tools.comanalyzer.diagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import jadex.tools.comanalyzer.Component;

/**
 * The panel for component representation.
 */
public class ComponentCanvas extends JPanel {

	// -------- constants --------

	/** Font used for component label. */
	public static final Font componentFont = new Font("Helvetica", Font.PLAIN, 12);

	// -------- attributes --------

	/** The container for this panel */
	protected DiagramCanvas panelcan;

	// -------- conastructors --------

	/**
	 * Creates a new component panel for the diagram.
	 * 
	 * @param panelcan The panel for this component.
	 */
	public ComponentCanvas(DiagramCanvas panelcan) {

		this.setBackground(Color.white);
		this.panelcan = panelcan;

	}

	// -------- ComponentCanvas methods --------

	/**
	 * Sets the prefferred size for this component.
	 */
	public void setPreferredSize()
	{
		Dimension preferredSize = new Dimension();
		preferredSize.width = (panelcan.visible_components.size() * DiagramConstants.xDistTimeline);
		preferredSize.height = DiagramConstants.heightComponentbox + DiagramConstants.yOffsetComponentbox + DiagramConstants.heigtComponentboxTimeline;
		setPreferredSize(preferredSize);
		// importent fore scrollbars
		setSize(preferredSize);
		// setMaximumSize(preferredSize);
		// setMinimumSize(preferredSize);
	}

	/**
	 * Return the component for a given x- and y-coordinate.
	 * 
	 * @param x The x-coordinate.
	 * @param y The y-coordinate.
	 * @return The component or <code>null</code> if there is no component at the
	 * given point.
	 */
	public Component getComponent(int x, int y)
	{

		int x1, x2;
		int y1 = DiagramConstants.yOffsetComponentbox;
		int y2 = y1 + DiagramConstants.heightComponentbox;

		for (int i = 0; i < panelcan.visible_components.size(); i++) {
			Component component = (Component) panelcan.visible_components.get(i);

			x1 = DiagramConstants.yOffsetComponentbox + i * DiagramConstants.xDistTimeline;
			x2 = x1 + DiagramConstants.widthComponentbox;

			if ((x >= x1) && (x <= x2) && (y >= y1) && (y <= y2)) {

				return component;

			}

		}

		return null;
	}

	// -------- JComponent methods --------

	/**
	 * Paints the components
	 */
	public void paintComponent(Graphics g)

	{

		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		// iterate thru all the visible components
		for (int i = 0; i < panelcan.visible_components.size(); i++) {
			Component component = (Component) panelcan.visible_components.get(i);

			int x = DiagramConstants.yOffsetComponentbox + i * DiagramConstants.xDistTimeline;

			// draw shadow box
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.drawRect(x + 3, DiagramConstants.yOffsetComponentbox + 3, DiagramConstants.widthComponentbox, DiagramConstants.heightComponentbox);
			g2d.setColor(Color.GRAY);
			g2d.drawRect(x + 2, DiagramConstants.yOffsetComponentbox + 2, DiagramConstants.widthComponentbox, DiagramConstants.heightComponentbox);
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawRect(x + 1, DiagramConstants.yOffsetComponentbox + 1, DiagramConstants.widthComponentbox, DiagramConstants.heightComponentbox);

			// draw outline
			if (component.getState().equals(Component.STATE_DUMMY))
				g2d.setColor(Color.YELLOW);
			else if (component.getState().equals(Component.STATE_DEAD))
				g2d.setColor(Color.GRAY);
			else
				g2d.setColor(new Color(41, 123, 198));
			g2d.drawRect(x, DiagramConstants.yOffsetComponentbox, DiagramConstants.widthComponentbox, DiagramConstants.heightComponentbox);

			// draw inner box with gradient paint
			GradientPaint gradient = null;
			if (component.getState().equals(Component.STATE_DUMMY))
				gradient = new GradientPaint(x, DiagramConstants.yOffsetComponentbox + DiagramConstants.heightComponentbox, Color.WHITE, x,
						DiagramConstants.yOffsetComponentbox, Color.YELLOW);
			else if (component.getState().equals(Component.STATE_DEAD))
				gradient = new GradientPaint(x, DiagramConstants.yOffsetComponentbox + DiagramConstants.heightComponentbox, Color.WHITE, x,
						DiagramConstants.yOffsetComponentbox, Color.GRAY);
			else
				gradient = new GradientPaint(x, DiagramConstants.yOffsetComponentbox + DiagramConstants.heightComponentbox, Color.WHITE, x,
						DiagramConstants.yOffsetComponentbox, new Color(41, 123, 198));
			g2d.setPaint(gradient);
			g2d.fillRect(x + 1, DiagramConstants.yOffsetComponentbox + 1, DiagramConstants.widthComponentbox - 1,
					DiagramConstants.heightComponentbox - 1);

			// draw label
			g2d.setColor(Color.black);
			g2d.setFont(componentFont);
			FontMetrics fm = g2d.getFontMetrics();

			String aName = component.getId();

			int nameWidth = fm.stringWidth(aName);

			if (nameWidth < DiagramConstants.widthComponentbox) {

				g2d.drawString(aName, x + (DiagramConstants.widthComponentbox - nameWidth) / 2, DiagramConstants.yOffsetComponentbox
						+ (DiagramConstants.heightComponentbox / 2) + (fm.getAscent() / 2));

			} else {
				// Need to chop the string up into at most 2 or 3
				// pieces, truncating the rest.
				int len = aName.length();
				String part1;
				String part2;
				String part3;
				if (nameWidth < DiagramConstants.widthComponentbox * 2) {
					// Ok, it is not quite twice as big, so cut in half
					part1 = aName.substring(0, len / 2);
					part2 = aName.substring(len / 2);

					g2d.drawString(part1, x + (DiagramConstants.widthComponentbox - fm.stringWidth(part1)) / 2,
							DiagramConstants.yOffsetComponentbox + (DiagramConstants.heightComponentbox / 2) - (int) (fm.getAscent() * 0.2));
					g2d.drawString(part2, x + (DiagramConstants.widthComponentbox - fm.stringWidth(part2)) / 2,
							DiagramConstants.yOffsetComponentbox + (DiagramConstants.heightComponentbox / 2) + (int) (fm.getAscent() * 0.9));

				} else if (nameWidth < DiagramConstants.widthComponentbox * 3) {
					// Ok, it is not quite thrice as big, so cut in
					// three
					part1 = aName.substring(0, len / 3);
					part2 = aName.substring(len / 3, 2 * len / 3);
					part3 = aName.substring(2 * len / 3);

					g2d.drawString(part1, x + (DiagramConstants.widthComponentbox - fm.stringWidth(part1)) / 2,
							DiagramConstants.yOffsetComponentbox + (DiagramConstants.heightComponentbox / 2) - (int) (fm.getAscent() * 0.65));
					g2d.drawString(part2, x + (DiagramConstants.widthComponentbox - fm.stringWidth(part2)) / 2,
							DiagramConstants.yOffsetComponentbox + (DiagramConstants.heightComponentbox / 2) + (int) (fm.getAscent() * 0.3));
					g2d.drawString(part3, x + (DiagramConstants.widthComponentbox - fm.stringWidth(part3)) / 2,
							DiagramConstants.yOffsetComponentbox + (DiagramConstants.heightComponentbox / 2) + (int) (fm.getAscent() * 0.95));

				} else {
					// This is rounded down the size of each char.
					int approxCharWidth = nameWidth / component.getId().length();
					int charCount = DiagramConstants.widthComponentbox / approxCharWidth;
					part1 = aName.substring(0, charCount);
					if (aName.length() < (charCount * 2)) {
						part2 = aName.substring(charCount);
						part3 = "";
					} else {
						part2 = aName.substring(charCount, (charCount * 2));
						if (charCount * 3 > aName.length()) {
							part3 = aName.substring(charCount * 2);
						} else {
							part3 = aName.substring(charCount * 2, (charCount * 3));
						}
					}

					g2d.drawString(part1, x + (DiagramConstants.widthComponentbox - fm.stringWidth(part1)) / 2,
							DiagramConstants.yOffsetComponentbox + (DiagramConstants.heightComponentbox / 2) - (int) (fm.getAscent() * 0.65));
					g2d.drawString(part2, x + (DiagramConstants.widthComponentbox - fm.stringWidth(part2)) / 2,
							DiagramConstants.yOffsetComponentbox + (DiagramConstants.heightComponentbox / 2) + (int) (fm.getAscent() * 0.3));
					g2d.drawString(part3, x + (DiagramConstants.widthComponentbox - fm.stringWidth(part3)) / 2,
							DiagramConstants.yOffsetComponentbox + (DiagramConstants.heightComponentbox / 2) + (int) (fm.getAscent() * 0.95));

				}
			}

			// draw timlines (in component canvas!!)
			int x2 = DiagramConstants.getTimelineX(i);
			float[] dotting = {
					1.0f, 3.0f };
			Stroke DOTTED = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, dotting, 0f);
			g2d.setStroke(DOTTED);
			g2d.setColor(DiagramConstants.COLOR_TIMELINE);
			Line2D l = new Line2D.Double(x2, DiagramConstants.heightComponentbox + DiagramConstants.yOffsetComponentbox + 3, x2,
					DiagramConstants.heightComponentbox + DiagramConstants.yOffsetComponentbox + DiagramConstants.heigtComponentboxTimeline);
			g2d.draw(l);
			g2d.setStroke(new BasicStroke(1));

		}

	}

}
