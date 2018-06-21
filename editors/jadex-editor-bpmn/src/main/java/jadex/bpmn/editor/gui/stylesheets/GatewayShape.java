package jadex.bpmn.editor.gui.stylesheets;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxRhombusShape;
import com.mxgraph.view.mxCellState;

import jadex.bpmn.editor.gui.GuiConstants;

public class GatewayShape extends mxRhombusShape
{
	/** Empty gateway shape. */
	public static final int GATEWAY_SHAPE_TYPE_EMPTY = 0;
	
	/** XOR gateway shape. */
	public static final int GATEWAY_SHAPE_TYPE_XOR = 1;
	
	/** AND gateway shape. */
	public static final int GATEWAY_SHAPE_TYPE_AND = 2;
	
	/** OR gateway shape. */
	public static final int GATEWAY_SHAPE_TYPE_OR = 3;
	
	/** Bar size as ratio of element size. */
	public static final double BAR_LENGTH_RATIO = 0.5;
	
	/** Bar size as ratio for xor of element size. */
	public static final double XOR_BAR_LENGTH_RATIO = 0.375;
	
	/** Bar thickness as ratio of rhombus side length. */
	public static final double BAR_WIDTH_RATIO = 0.1;
	
	// Helper Constants to speed calculation
	/** Half of PI */
	protected static final double PI2 = Math.PI * 0.5;
	
	/** Half of bar thickness as ratio of rhombus side length. */
	protected static final double BAR_WIDTH_RATIO2 = BAR_WIDTH_RATIO * 0.5;
	
	/** Base position shift. */
	protected static final double POS_BASE_SHIFT = 0.5 - BAR_LENGTH_RATIO * 0.5;
	
	/** Base position shift for xor. */
	protected static final double XOR_POS_BASE_SHIFT = 0.5 - XOR_BAR_LENGTH_RATIO * 0.5;
	
	/** The shape type */
	protected int shapetype;
	
	public GatewayShape(int shapetype)
	{
		this.shapetype = shapetype;
	}
	
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		super.paintShape(canvas, state);
		
		Graphics2D g = canvas.getGraphics();
		Rectangle rect = state.getRectangle();
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;
		
		switch (shapetype)
		{
			case GATEWAY_SHAPE_TYPE_XOR:
			{
				g.fill(getXorShape(x, y, w, h));
				break;
			}
				
			case GATEWAY_SHAPE_TYPE_AND:
			{
				g.fill(getAndShape(x, y, w, h));
				break;
			}
			
			case GATEWAY_SHAPE_TYPE_OR:
			{
				/*double blx = BAR_LENGTH_RATIO * w;
				double bly = BAR_LENGTH_RATIO * h;
				double basex = x + POS_BASE_SHIFT * w;
				double basey = y + POS_BASE_SHIFT * h;
				double strokewidth = Math.sqrt(w2 * w2 + h2 * h2) * BAR_WIDTH_RATIO;
				double sw2 = strokewidth * 0.5;
				Ellipse2D.Double ell = new Ellipse2D.Double(basex + sw2, basey + sw2, blx - strokewidth, bly - strokewidth);
				Stroke oldstroke = g.getStroke();
				BasicStroke stroke = new BasicStroke((float) strokewidth);
				g.setStroke(stroke);
				g.draw(ell);
				g.setStroke(oldstroke);*/
				g.fill(getOrShape(x, y, w, h));
				break;
			}
				
			case GATEWAY_SHAPE_TYPE_EMPTY:
			default:
		}
	}
	
	public static final Shape getXorShape(int x, int y, int w, int h)
	{
		double w2 = w * 0.5;
		double h2 = h * 0.5;
		double shift = Math.sqrt(w2 * w2 + h2 * h2) * BAR_WIDTH_RATIO2 * GuiConstants.SINE_45;
		double blx = XOR_BAR_LENGTH_RATIO * w;
		double bly = XOR_BAR_LENGTH_RATIO * h;
		double basex = x + XOR_POS_BASE_SHIFT * w;
		double basey = y + XOR_POS_BASE_SHIFT * h;
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
	
	public static final Shape getAndShape(int x, int y, int w, int h)
	{
		double w2 = w * 0.5;
		double h2 = h * 0.5;
		double basex = x + POS_BASE_SHIFT * w;
		double basey = y + h2;
		double shift = h * GuiConstants.SINE_45 * BAR_WIDTH_RATIO2;
		double bl = w * BAR_LENGTH_RATIO;
		GeneralPath bar = new GeneralPath();
		bar.moveTo(basex, basey - shift);
		bar.lineTo(basex, basey + shift);
		bar.lineTo(basex + bl, basey + shift);
		bar.lineTo(basex + bl, basey - shift);
		bar.closePath();
		Area ret = new Area(bar);
		basex = x + w2;
		basey = y + POS_BASE_SHIFT * h;
		shift = w * GuiConstants.SINE_45 * BAR_WIDTH_RATIO2;
		bl = h * BAR_LENGTH_RATIO;
		bar = new GeneralPath();
		bar.moveTo(basex + shift, basey);
		bar.lineTo(basex - shift, basey);
		bar.lineTo(basex - shift, basey + bl);
		bar.lineTo(basex + shift, basey + bl);
		bar.closePath();
		ret.add(new Area(bar));
		
		return ret;
	}
	
	public static final Shape getOrShape(int x, int y, int w, int h)
	{
		double w2 = w * 0.5;
		double h2 = h * 0.5;
		double blx = BAR_LENGTH_RATIO * w;
		double bly = BAR_LENGTH_RATIO * h;
		double basex = x + POS_BASE_SHIFT * w;
		double basey = y + POS_BASE_SHIFT * h;
		double strokewidth = Math.sqrt(w2 * w2 + h2 * h2) * BAR_WIDTH_RATIO * 2.0;
		double sw2 = strokewidth * 0.5;
		Shape ell = new Ellipse2D.Double(basex, basey, blx, bly);
		Area ret = new Area(ell);
		
		AffineTransform st = new AffineTransform();
		double sf = (double) (w - (strokewidth * 2.0)) / w;
		st.translate(sw2, sw2);
		st.translate(basex, basey);
		st.scale(sf, sf);
		ell = st.createTransformedShape(new Ellipse2D.Double(0.0, 0.0, blx, bly));
		ret.subtract(new Area(ell));
		
		return ret;
	}
}
