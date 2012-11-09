package jadex.bpmn.editor.gui.stylesheets;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;

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

public class EventShape extends mxEllipseShape
{
	protected static final double INNER_CIRCLE_RATIO = 0.075;
	
	protected static final double[] CLOCK_SIN_TABLE = new double[12];
	static
	{
		for (int i = 0; i < 12; ++i)
		{
			CLOCK_SIN_TABLE[i] = Math.sin(Math.PI / 6.0 * i);
		}
	}
	
	protected static final double[] PENTAGON_SIN_TABLE = new double[5];
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
		if (eventtype.endsWith("Message"))
		{
			symbol = getLetterShape(x, y, w, h);
		}
		else if (eventtype.endsWith("Timer"))
		{
			symbol = getClockShape(x, y, w, h);
		}
		else if (eventtype.endsWith("Rule"))
		{
			symbol = getPageShape(x, y, w, h);
		}
		else if (eventtype.endsWith("Signal"))
		{
			symbol = getTriangleShape(x, y, w, h);
		}
		else if (eventtype.endsWith("Multiple"))
		{
			symbol = getPentagonShape(x, y, w, h);
		}
		
		if (symbol != null)
		{
			float width = mxUtils.getFloat(state.getStyle(), mxConstants.STYLE_STROKEWIDTH, 1) * (float) canvas.getScale();
			Stroke oldstroke = g.getStroke();
			g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			
			if (mactivity.isThrowing())
			{
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g.fill(symbol);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(mxUtils.getColor(state.getStyle(), mxConstants.STYLE_FILLCOLOR, Color.WHITE));
				g.draw(symbol);
				
			}
			else
			{
				g.draw(symbol);
			}
			
			g.setStroke(oldstroke);
		}
		
		g.dispose();
	}
	
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
		
		/*gp.moveTo(lx, ly);
		gp.lineTo(lx, ly + lh);
		gp.lineTo(lx + lw, ly + lh);
		gp.lineTo(lx + lw, ly);
		gp.lineTo(lx + lw * 0.5, ly + lh / 3.0);
		gp.lineTo(lx, ly);
		gp.lineTo(lx + lw, ly);
		gp.closePath();*/
		
		return gp;
	}
	
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
}
