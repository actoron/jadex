package jadex.bpmn.editor.gui.stylesheets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxEllipseShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

import jadex.bpmn.editor.gui.GuiConstants;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;

public class EventShape extends mxEllipseShape
{
	/** Inner circle ratio for creating double/fat circles. */
	protected static final double INNER_CIRCLE_RATIO = 0.075;
	
	/**
	 *  Sine value lookups for clock shape.
	 */
	protected static final double[] CLOCK_SIN_TABLE = new double[12];
	static
	{
		for (int i = 0; i < 12; ++i)
		{
			CLOCK_SIN_TABLE[i] = Math.sin(Math.PI / 6.0 * i);
		}
	}
	
	/**
	 *  Sine value lookups for pentagon shape.
	 */
	protected static final double[] PENTAGON_SIN_TABLE = new double[5];
	
	/**
	 *  Cosine value lookups for pentagon shape.
	 */
	protected static final double[] PENTAGON_COS_TABLE = new double[5];
	static
	{
		for (int i = 0; i < 5; ++i)
		{
			PENTAGON_SIN_TABLE[i] = Math.sin(Math.PI / 2.5 * i);
			PENTAGON_COS_TABLE[i] = Math.cos(Math.PI / 2.5 * i);
		}
	}
	
	/** Position of the clock's big hand in radians. */
	protected static final double BIG_HAND_POS = Math.PI * 17.0 / 30.0;
	
	/** Constant for triangle calculation. */
	protected static final double SQRT33 = 3.0 / Math.sqrt(3) * 0.5;
	
	/**
	 *  Paints the shape.
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		super.paintShape(canvas, state);
		
		Graphics2D g = canvas.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		Rectangle rect = state.getRectangle();
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;
		
		MActivity mactivity = (MActivity) ((VActivity) state.getCell()).getBpmnElement();
		String eventtype = mactivity.getActivityType();
		
		if (eventtype.startsWith("EventIntermediate"))
		{
			double circdistx = INNER_CIRCLE_RATIO * w;
			double circdisty = INNER_CIRCLE_RATIO * h;
			double innerx = x + circdistx;
			double innery = y + circdisty;
			double innerw = w - circdistx - circdistx;
			double innerh = h - circdisty - circdisty;
			g.draw(new Ellipse2D.Double(innerx, innery, innerw, innerh));
		}
		else if (eventtype.startsWith("EventEnd"))
		{
			double circdistx = INNER_CIRCLE_RATIO * w;
			double circdisty = INNER_CIRCLE_RATIO * h;
			double innerx = x + circdistx;
			double innery = y + circdisty;
			double innerw = w - circdistx - circdistx;
			double innerh = h - circdisty - circdisty;
			Area fatcircle = new Area(new Ellipse2D.Double(x, y, w, h));
			fatcircle.subtract(new Area(new Ellipse2D.Double(innerx, innery, innerw, innerh)));
			g.fill(fatcircle);
		}
		
		Shape symbol = null;
		boolean simple = true;
		if (eventtype.endsWith("Message"))
		{
			symbol = getLetterShape(x, y, w, h);
//			symbol = getBackArrowsShape(x, y, w, h);
			simple = false;
		}
		else if (eventtype.endsWith("Timer"))
		{
			symbol = getClockShape(x, y, w, h);
			simple = false;
		}
		else if (eventtype.endsWith("Compensation"))
		{
			symbol = getBackArrowsShape(x, y, w, h);
		}
		else if (eventtype.endsWith("Cancel"))
		{
			symbol = getXCrossShape(x, y, w, h);
		}
		else if (eventtype.endsWith("Rule"))
		{
			symbol = getPageShape(x, y, w, h);
			simple = false;
		}
		else if (eventtype.endsWith("Signal"))
		{
			symbol = getTriangleShape(x, y, w, h);
		}
		else if (eventtype.endsWith("Error"))
		{
			symbol = getBoltShape(x, y, w, h);
		}
		else if (eventtype.endsWith("Multiple"))
		{
			symbol = getPentagonShape(x, y, w, h);
		}
		else if (eventtype.endsWith("Terminate"))
		{
			symbol = getCircleShape(x, y, w, h);
		}
		
		if (symbol != null)
		{
			float width = mxUtils.getFloat(state.getStyle(), mxConstants.STYLE_STROKEWIDTH, 1) * (float) canvas.getScale();
			Stroke oldstroke = g.getStroke();
			g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			
			if (mactivity.isThrowing())
			{
				if (simple)
				{
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.fill(symbol);
				}
				else
				{
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
					g.fill(symbol);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setColor(mxUtils.getColor(state.getStyle(), mxConstants.STYLE_FILLCOLOR, Color.WHITE));
					g.draw(symbol);
				}
				
			}
			else
			{
				g.draw(symbol);
			}
			
			g.setStroke(oldstroke);
		}
		
		g.dispose();
	}
	
	/**
	 *  Creates a letter shape.
	 *  
	 *  @param x X-position.
	 *  @param y Y-position.
	 *  @param w Width.
	 *  @param h Height.
	 *  @return The shape.
	 */
	public static final Shape getLetterShape(double x, double y, double w, double h)
	{
		
		double sf = 0.62;
		double lw = w * sf;
		double lh = h / 3.0 * 2.0 * sf;
		double lx = x + (w - lw) * 0.5;
		double ly = y + (h - lh) * 0.5;
		 
		
		GeneralPath gp = new GeneralPath(new Rectangle2D.Double(lx, ly, lw, lh));
		gp.moveTo(lx, ly);
		gp.lineTo(lx + lw * 0.5, ly + lh / 3.0);
		gp.lineTo(lx + lw, ly);
		gp.closePath();
		gp.moveTo(lx, ly);
		gp.lineTo(lx + lw * 0.5, ly + lh / 3.0);
		gp.lineTo(lx + lw, ly);
		gp.closePath();
		
		return gp;
	}
	
	/**
	 *  Creates a clock shape.
	 *  
	 *  @param x X-position.
	 *  @param y Y-position.
	 *  @param w Width.
	 *  @param h Height.
	 *  @return The shape.
	 */
	public static final Shape getClockShape(double x, double y, double w, double h)
	{
		
		double sf = 0.65;
		x += (w - w * sf) * 0.5;
		y += (h - h * sf) * 0.5;
		w *= sf;
		h *= sf;
		double w2 = w * 0.5;
		double h2 = h * 0.5;
		
		
		Ellipse2D ell = new Ellipse2D.Double(x, y, w, h);
		GeneralPath gp = new GeneralPath(ell);
		
		sf = 0.92;
		x += (w - w * sf) * 0.5;
		y += (h - h * sf) * 0.5;
		w *= sf;
		h *= sf;
		w2 = w * 0.5;
		h2 = h * 0.5;
		double iw2 = w * 0.40;
		double ih2 = h * 0.40;
		double ix = x + w2 - iw2;
		double iy = y + h2 - ih2;
		
		for (int i = 0; i < 12; ++i)
		{
			gp.moveTo(x + w2 - CLOCK_SIN_TABLE[(i + 3) % 12] * w2, y + h2 - CLOCK_SIN_TABLE[i] * h2);
			gp.lineTo(ix + iw2 - CLOCK_SIN_TABLE[(i + 3) % 12] * iw2, iy + ih2 - CLOCK_SIN_TABLE[i] * ih2);
		}
		
		gp.moveTo(x + w2, y + h2);
		iw2 = w * 0.45;
		ih2 = h * 0.45;
		ix = x + w2 - iw2;
		iy = y + h2 - ih2;
		gp.lineTo(ix + iw2 - Math.cos(BIG_HAND_POS) * iw2, iy + ih2 - Math.sin(BIG_HAND_POS) * ih2);
		
		gp.moveTo(x + w2, y + h2);
		gp.lineTo(x + w2 * 1.5, y + h2);
		
		return gp;
	}
	
	/**
	 *  Creates a page shape.
	 *  
	 *  @param x X-position.
	 *  @param y Y-position.
	 *  @param w Width.
	 *  @param h Height.
	 *  @return The shape.
	 */
	public static final Shape getPageShape(double x, double y, double w, double h)
	{
		
		double sf = 0.52;
		double lh = h * sf;
		double lw = w * 0.8 * sf;
		double lx = x + (w - lw) * 0.5;
		double ly = y + (h - lh) * 0.5;
		GeneralPath gp = new GeneralPath();
		gp.moveTo(lx, ly);
		gp.lineTo(lx, ly + lh);
		gp.lineTo(lx + lw, ly + lh);
		gp.lineTo(lx + lw, ly);
		gp.closePath();
		double ddist = lh / 5.0;
		double dist = ddist;
		double ll = lw * 0.61;
		lx = lx + (lw - ll) * 0.5;
		for (int i = 0; i < 4; ++i)
		{
			gp.moveTo(lx, ly + dist);
			gp.lineTo(lx + ll, ly + dist);
			dist += ddist;
		}
		return gp;
	}
	
	/**
	 *  Creates a triangle shape.
	 *  
	 *  @param x X-position.
	 *  @param y Y-position.
	 *  @param w Width.
	 *  @param h Height.
	 *  @return The shape.
	 */
	public static final Shape getTriangleShape(double x, double y, double w, double h)
	{
		
		double sf = 0.7;
		double w2 = w;
		double th = h;
		w *= sf;
		h *= sf;
		x += (w2 - w) * 0.5;
		y += (th - h) * 0.5;
		w2 = w * 0.5;
		double tlx2 = w2 * SQRT33;
		th = h * 0.75;
		double centerx = x + w2;
		
		GeneralPath gp = new GeneralPath();
		gp.moveTo(centerx, y);
		gp.lineTo(centerx - tlx2, y + th);
		gp.lineTo(centerx + tlx2, y + th);
		gp.closePath();
		return gp;
	}
	
	/**
	 *  Creates a circle shape.
	 *  
	 *  @param x X-position.
	 *  @param y Y-position.
	 *  @param w Width.
	 *  @param h Height.
	 *  @return The shape.
	 */
	public static final Shape getCircleShape(double x, double y, double w, double h)
	{
		double sf = 0.4;
		x += (w - w * sf) * 0.5;
		y += (h - h * sf) * 0.5;
		w *= sf;
		h *= sf;
		
		Ellipse2D ell = new Ellipse2D.Double(x, y, w, h);
		return ell;
	}
	
	/**
	 *  Creates a pentagon shape.
	 *  
	 *  @param x X-position.
	 *  @param y Y-position.
	 *  @param w Width.
	 *  @param h Height.
	 *  @return The shape.
	 */
	public static final Shape getPentagonShape(double x, double y, double w, double h)
	{
		GeneralPath gp = new GeneralPath();
		
		double sf = 0.7;
		double w2 = w;
		double h2 = h;
		w *= sf;
		h *= sf;
		x += (w2 - w) * 0.5;
		y += (h2 - h) * 0.5;
		w2 = w * 0.5;
		h2 = h * 0.5;
		
		gp.moveTo(x + w2 - PENTAGON_SIN_TABLE[0] * w2, y + h2 - PENTAGON_COS_TABLE[0] * h2);
		for (int i = 1; i < 5; ++i)
		{
			gp.lineTo(x + w2 - PENTAGON_SIN_TABLE[i] * w2, y + h2 - PENTAGON_COS_TABLE[i] * h2);
		}
		gp.closePath();
		
		return gp;
	}
	
	/**
	 *  Creates a lightning bolt shape.
	 *  
	 *  @param x X-position.
	 *  @param y Y-position.
	 *  @param w Width.
	 *  @param h Height.
	 *  @return The shape.
	 */
	public static final Shape getBoltShape(double x, double y, double w, double h)
	{
		GeneralPath gp = new GeneralPath();
		
		double sf = 0.55;
		double w2 = w;
		double h2 = h;
		w *= sf;
		h *= sf;
		x += (w2 - w) * 0.5;
		y += (h2 - h) * 0.5;
		double w3 = w / 3;
		double w23 = w3 + w3;
		double ybottom = y + h;
		double maxthick = h * 0.4;
		
		gp.moveTo(x, ybottom);
		gp.lineTo(x + w3, y);
		gp.lineTo(x + w23, ybottom - maxthick);
		gp.lineTo(x + w, y);
		gp.lineTo(x + w23, ybottom);
		gp.lineTo(x + w3, y + maxthick);
		gp.closePath();
		
		return gp;
	}
	
	/**
	 *  Creates a back arrows shape.
	 *  
	 *  @param x X-position.
	 *  @param y Y-position.
	 *  @param w Width.
	 *  @param h Height.
	 *  @return The shape.
	 */
	public static final Shape getBackArrowsShape(double x, double y, double w, double h)
	{
		GeneralPath gp = new GeneralPath();
		
		double sf = 0.50;
		double w2 = w;
		double h2 = h;
		w *= sf;
		h *= sf;
		x += (w2 - w) * 0.415;
		y += (h2 - h) * 0.5;
		double ybottom = y + h;
		double yhalf = y + h * 0.5;
		double xhalf = x + w * 0.5;
		
		gp.moveTo(x, yhalf);
		gp.lineTo(xhalf, ybottom);
		gp.lineTo(xhalf, yhalf);
		gp.lineTo(x + w, ybottom);
		gp.lineTo(x + w, y);
		gp.lineTo(xhalf, yhalf);
		gp.lineTo(xhalf, y);
		gp.closePath();
//		gp.moveTo(x, y);
//		gp.lineTo(x, ybottom);
//		gp.lineTo(x + w, ybottom);
//		gp.lineTo(x + w, y);
//		gp.closePath();
		
		return gp;
	}
	
	/**
	 *  Creates an x-cross shape.
	 *  
	 *  @param x X-position.
	 *  @param y Y-position.
	 *  @param w Width.
	 *  @param h Height.
	 *  @return The shape.
	 */
	public static final Shape getXCrossShape(double x, double y, double w, double h)
	{
		double barwidthratio = 0.2;
		double barlengthratio = 0.45;
		double barwidthratio2 = barwidthratio * 0.5;
		double posbaseshift = 0.5 - barlengthratio * 0.5;
		double w2 = w * 0.5;
		double h2 = h * 0.5;
		double shift = Math.sqrt(w2 * w2 + h2 * h2) * barwidthratio2 * GuiConstants.SINE_45;
		double blx = barlengthratio * w;
		double bly = barlengthratio * h;
		double basex = x + posbaseshift * w;
		double basey = y + posbaseshift * h;
		GeneralPath bar = new GeneralPath();
		bar.moveTo(basex - shift, basey + shift);
		bar.lineTo(basex + shift, basey - shift);
		bar.lineTo(basex + blx + shift, basey + bly - shift);
		bar.lineTo(basex + blx - shift, basey + bly + shift);
		bar.closePath();
		Area ret = new Area(bar);
		basex += blx;
		bar = new GeneralPath();
		bar.moveTo(basex - shift, basey - shift);
		bar.lineTo(basex + shift, basey + shift);
		bar.lineTo(basex - blx + shift, basey + bly + shift);
		bar.lineTo(basex - blx - shift, basey + bly - shift);
		bar.closePath();
		ret.add(new Area(bar));
		
		return ret;
	}
}
