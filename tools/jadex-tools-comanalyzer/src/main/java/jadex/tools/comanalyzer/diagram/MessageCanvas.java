package jadex.tools.comanalyzer.diagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.Iterator;

import javax.swing.JPanel;

import edu.uci.ics.jung.graph.util.Pair;
import jadex.commons.SUtil;
import jadex.tools.comanalyzer.Component;
import jadex.tools.comanalyzer.Message;
import jadex.tools.comanalyzer.PaintMaps;


/**
 * The panel for message representation.
 */
public class MessageCanvas extends JPanel implements Serializable
{
	//-------- constants --------

	/** The tolerance for  message selection */
	public static final int V_TOL = 4;

	public static final int H_TOL = 4;

	/** Font used for message label. */
	public static final Font plain_font = new Font("SanSerif", Font.PLAIN, 12);

	//	public static Font bold_font = new Font("Helvetia", Font.BOLD, 10);
	public static final Font bold_font = new Font("SanSerif", Font.BOLD, 12);

	// -------- attributs --------

	/** The paint mode (e.g. paint by convid) */
	private int paintMode;

	/** The container for this panel */
	protected DiagramCanvas panelcan;

	/** Show labels for messages */
	protected boolean show_label;

	/** Bold font for message labels */
	protected boolean label_bold;

	/**
	 * Creates a new message canvas for the diagram.
	 * @param panelcan The panel for this component.
	 */
	public MessageCanvas(DiagramCanvas panelcan)
	{
		super();
		this.setBackground(Color.white);
		this.panelcan = panelcan;

		// default options
		show_label = true;
		label_bold = false;
		paintMode = PaintMaps.PAINTMODE_DEFAULT;
	}

	// -------- MessageCanvas methods --------

	/**
	 * Set the preffered size according to component and message count.
	 */
	public void setPreferredSize()
	{
		Dimension preferredSize = new Dimension();
		preferredSize.width = (panelcan.visible_components.size() * DiagramConstants.xDistTimeline);
		preferredSize.height = DiagramConstants.yDistTimeline + (panelcan.visible_messages.size() * DiagramConstants.yDistTimeline);
		setPreferredSize(preferredSize);
		// important for scrollbars
		setSize(preferredSize);
	}


	/**
	 * Return the message for a given x- and y-coordinate.
	 * 
	 * @param x The x-coordinate.
	 * @param y The y-coordinate.
	 * @return The message or <code>null</code> if there is no message at the given point.
	 */
	public Message getMessage(int x, int y)
	{
		int msgnr = 0;

		for(Iterator iter = panelcan.visible_messages.keySet().iterator(); iter.hasNext();)
		{
			Message message = (Message)iter.next();

			// get the x- and y-coordinates for the message
			Component sender = (Component)((Pair)panelcan.visible_messages.get(message)).getFirst();
			Component receiver = (Component)((Pair)panelcan.visible_messages.get(message)).getSecond();
			int posSource = panelcan.visible_components.indexOf(sender);
			int posDest = panelcan.visible_components.indexOf(receiver);

			int x1 = DiagramConstants.getTimelineX(posSource);
			int x2 = DiagramConstants.getTimelineX(posDest);
			int yt = DiagramConstants.getTimelineY(msgnr++);

			boolean inRangeX = x1 < x2 ? (x >= x1 - H_TOL) && (x <= x2 + H_TOL) : (x >= x2 - H_TOL) && (x <= x1 + H_TOL);
			boolean inRangeY = (y >= yt - V_TOL) && (y <= yt + V_TOL);

			if(inRangeX && inRangeY)
			{
				return message;
			}
		}

		return null;
	}

	/**
	 * Set the paint mode.
	 * @param paintMode The paint mode to set.
	 */
	public void setPaintMode(int paintMode)
	{
		this.paintMode = paintMode;
	}

	/**
	 * @return The paint mode.
	 */
	public int getPaintMode()
	{
		return paintMode;
	}

	// -------- JComponent methods --------

	/** 
	 * Paints the timelines and the messages.
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;

		int agntnr = 0;
		int msgcount = panelcan.visible_messages.size();

		// add the timelines first (for each component)
		for(Iterator iter = panelcan.visible_components.iterator(); iter.hasNext();)
		{
			iter.next();

			int x = DiagramConstants.getTimelineX(agntnr++);
			float[] dotting = {1.0f, 3.0f};
			Stroke DOTTED = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, dotting, 0f);
			g2d.setStroke(DOTTED);
			g2d.setColor(DiagramConstants.COLOR_TIMELINE);
			Line2D l = new Line2D.Double(x, 0, x, DiagramConstants.yDistTimeline * (msgcount + 1));
			g2d.draw(l);
		}

		int msgnr = 0;
		String lastId = null; // for combining multicasts

		// iterate thru every visible message
		for(Iterator iter = panelcan.visible_messages.keySet().iterator(); iter.hasNext();)
		{
			Message message = (Message)iter.next();

			// get the x- and y-coordinates for the message
			Component sender = (Component)((Pair)panelcan.visible_messages.get(message)).getFirst();
			Component receiver = (Component)((Pair)panelcan.visible_messages.get(message)).getSecond();
			int posSource = panelcan.visible_components.indexOf(sender);
			int posDest = panelcan.visible_components.indexOf(receiver);
			int x1 = DiagramConstants.getTimelineX(posSource);
			int x2 = DiagramConstants.getTimelineX(posDest);
			int y = DiagramConstants.getTimelineY(msgnr++);

			if(show_label)
			{
				g2d.setColor(Color.black);
				g2d.setFont(label_bold ? bold_font : plain_font);

				// The label: performativ (content)
				// TODO: make it customizable
				String perf = (String)message.getParameter(Message.PERFORMATIVE);
				perf = perf + " (" + head(30, message.getParameter(Message.CONTENT)) + " )";
				// center the string between the timelines (x1 and x2)
				int perfWidth = g2d.getFontMetrics().stringWidth(perf);
				int xString = (x2 > x1 ? x1 + ((x2 - x1) / 2) : x2 + ((x1 - x2) / 2)) - perfWidth / 2;
				g2d.drawString(perf, xString, y + DiagramConstants.yOffsetMessageLabel);

			}

			// get color for current message and paint mode
			Paint msgColor = panelcan.getToolTab().getPaintMaps().getMessagePaint(message, paintMode);
			// draw message line with gradient paint
			Paint msgPaint = new GradientPaint(x1, y, Color.WHITE, x2, y, (Color)msgColor, true);
			Line2D msgLine = new Line2D.Double(x1, y, x2, y);
			g2d.setStroke(new BasicStroke(2));
			g2d.setPaint(msgPaint);
			g2d.draw(msgLine);

			// combine multicast by connecting endpoints
//			boolean combine = message.getParameter(Message.ID).equals(lastId);
			boolean combine = SUtil.equals(message.getId(), lastId);
			int ext = combine ? DiagramConstants.yDistTimeline : 0;
			Line2D multiLine = new Line2D.Double(x1, y - ext - (DiagramConstants.arrowHeight / 2), x1, y + (DiagramConstants.arrowHeight / 2));
			g2d.setPaint(msgColor);
			g2d.setStroke(new BasicStroke(3));
			g2d.draw(multiLine);

			// draw arrow
			GeneralPath s = getArrow(x2, y, x2 > x1 ? false : true);
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setPaint(Color.lightGray);
			g2d.fill(s);
			g2d.setPaint(Color.darkGray);
			g2d.draw(s);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			// draw message numbers
			String msgNumber = message.getParameter(Message.SEQ_NO).toString();
			g2d.setColor(DiagramConstants.COLOR_MESSAGENUMBER);
			g2d.setStroke(new BasicStroke(1));
			g2d.drawString(msgNumber, DiagramConstants.xOffsetMessageNumber, y + DiagramConstants.yOffsetMessageNumber);

			// save last ID for multicast check
//			lastId = (String)message.getParameter(Message.ID);
			lastId = (String)message.getId();
		}
	}

	//-------- helper methods --------

	private String head(int n, Object o)
	{
		String s = " ";
		if(o == null)
		{
			return s;
		}
		if(o instanceof String)
		{
			s = (String)o;
		}
		else
		{
			s = o.toString();
		}
		try
		{
			return s.substring(0, n) + "...";
		}
		catch(Exception any)
		{
		}
		return s;
	}

//	private String tail(int n, String s)
//	{
//		try
//		{
//			return s.substring(s.length() - n, s.length());
//		}
//		catch(Exception any)
//		{
//			return s;
//		}
//	}


	/**
	 * Returns an arrowhead in the shape of an triangle with an
	 * triangle notch taken out. It is placed with the arrowhead to the right
	 * unless it is rotated. The arrowhead points alwaws to (x,y)
	 * 
	 * @param x The x cooardinate for the arrow head.
	 * @param y The y cooardinate for the arrow head.
	 * @param rotate <code>true</code> if the arrowhead is placed to the left, 
	 * otherwhise it is placed to the right
	 * @return The Shape of the arrow.
	 */
	private GeneralPath getArrow(float x, float y, boolean rotate)
	{

		GeneralPath arrow = new GeneralPath();
		float height = DiagramConstants.arrowHeight * (rotate ? -1 : 1);
		float width = DiagramConstants.arrowWidth * (rotate ? -1 : 1);
		float notch_width = DiagramConstants.arrowNotch * (rotate ? -1 : 1);

		arrow.moveTo(x, y);
		arrow.lineTo(x - width, y + height / 2.0f);
		arrow.lineTo(x - (width - notch_width), y);
		arrow.lineTo(x - width, y - height / 2.0f);
		arrow.lineTo(x, y);

		return arrow;
	}

}
