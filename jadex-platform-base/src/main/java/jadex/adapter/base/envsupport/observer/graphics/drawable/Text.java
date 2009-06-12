package jadex.adapter.base.envsupport.observer.graphics.drawable;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.font.TextMeasurer;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;

import org.codehaus.janino.Java.ThisReference;

import com.sun.opengl.util.j2d.TextRenderer;


import jadex.adapter.base.envsupport.environment.space2d.action.GetPosition;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.graphics.AbstractViewport;
import jadex.adapter.base.envsupport.observer.graphics.IViewport;
import jadex.adapter.base.envsupport.observer.graphics.TextInfo;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

/**
 * Drawable component for displaying text.
 */
public final class Text implements IDrawable
{
	/** Left Alignment */
	public final static int ALIGN_LEFT		= 0;
	
	/** Center Alignment */
	public final static int ALIGN_CENTER 	= 1;
	
	/** Right Alignment */
	public final static int ALIGN_RIGHT 	= 2;
	
	
	/** Viewport size (in pixels) on which the base font size is relative to */
	private final static float BASE_VIEWPORT_SIZE = 300.0f;
	
	/** Dummy FontRenderContext since we don't use FRCs */
	private final static FontRenderContext DUMMY_FRC = new FontRenderContext(null, true, true);
	
	/** Relative position or binding */
	private Object position;
	
	/** Font used for the text */
	private Font baseFont;
	
	/** Color of the font */
	private Color color;
	
	/** Lines of text */
	private String text;
	
	/** Text alignment */
	private int align;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCPos;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCSize;
	
	/** The condition deciding if the drawable should be drawn. */
	private IParsedExpression drawcondition;
	
	public Text()
	{
		this(null, null, null, null, 0, 0, null);
	}
	
	public Text(Object position, Font baseFont, Color color, String text, int align, int absFlags, IParsedExpression drawcondition)
	{
		enableDCPos = (absFlags & RotatingPrimitive.ABSOLUTE_POSITION) == 0;
		enableDCSize = (absFlags & RotatingPrimitive.ABSOLUTE_SIZE) == 0;
		this.align = align;
		if (position == null)
			position = Vector2Double.ZERO.copy();
		this.position = position;
		if (baseFont == null)
			baseFont = new Font(null);
		this.baseFont = baseFont;
		if (text == null)
			text = "";
		this.text = text;
		if (color == null)
			color = Color.WHITE;
		this.color = color;
		this.drawcondition = drawcondition;
	}
	
	/**
	 * Initializes the object for a Java2D viewport
	 * 
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void init(ViewportJ2D vp)
	{
	}

	/**
	 * Initializes the object for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp)
	{
	}
	
	/**
	 * Draws the object to a Java2D viewport
	 * 
	 * @param dc the DrawableCombiner drawing the object
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void draw(DrawableCombiner dc, Object obj, ViewportJ2D vp)
	{
		boolean draw = drawcondition==null;
		if(!draw)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$object", obj);
			draw = ((Boolean)drawcondition.getValue(fetcher)).booleanValue();
		}
		
		if (draw)
		{
			IVector2 position = ((IVector2)dc.getBoundValue(obj, this.position)).copy();
			IVector2 dcPos = Vector2Double.ZERO;
			if (enableDCPos)
				dcPos = (IVector2)dc.getBoundValue(obj, dc.getPosition());
			IVector2 dcScale = (IVector2)dc.getBoundValue(obj, dc.getSize());
			if((position == null) || (dcPos == null) || (dcScale == null))
			{
				return;
			}
			
			IVector2 canvasSize = vp.getCanvasSize();
			float fontscale = getBasicFontScale(canvasSize, vp.getAreaSize(), vp.getSize());
			if (enableDCSize)
			{
				position.multiply(dcScale);
				// Do not scale fintsize wrt. drawable combiner size.
//				fontscale *= dcScale.getMean().getAsFloat();
			}
			Font font = baseFont.deriveFont(baseFont.getSize() * fontscale);
			
			Graphics2D g = vp.getContext();
			
			IVector2 pos = getBasePosition(vp, dcPos, position, canvasSize, vp.getInvertX(), !vp.getInvertY());
			double xPos = pos.getXAsDouble();
			double yPos = pos.getYAsDouble();
			
			String text = getReplacedText(obj, this.text);
			String[] lines = text.split("(\n\r?)|(\r)");
			
			AffineTransform t = g.getTransform();
			g.setTransform(vp.getDefaultTransform());
			g.setColor(color);
			for (int i = 0; i < lines.length; ++i)
			{
				TextLayout tl = new TextLayout(lines[i], font, DUMMY_FRC);
				
				if (i != 0)
					yPos += tl.getAscent();
				tl.draw(g, (int) (xPos + getAlignment(tl)), (int) (yPos));
				
				yPos += (tl.getDescent() + tl.getLeading());
			}
			g.setTransform(t);
		}
	}
	
	/**
	 * Draws the object to an OpenGL viewport
	 * 
	 * @param dc the DrawableCombiner drawing the object
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void draw(DrawableCombiner dc, Object obj, ViewportJOGL vp)
	{
		boolean draw = drawcondition==null;
		if(!draw)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$object", obj);
			draw = ((Boolean)drawcondition.getValue(fetcher)).booleanValue();
		}
		
		if (draw)
		{
			IVector2 position = ((IVector2)dc.getBoundValue(obj, this.position)).copy();
			IVector2 dcPos = Vector2Double.ZERO;
			if (enableDCPos)
				dcPos = (IVector2)dc.getBoundValue(obj, dc.getPosition());//SObjectInspector.getVector2(obj, dc.getPosition());
			IVector2 dcScale = (IVector2)dc.getBoundValue(obj, dc.getSize());
			if((position == null) || (dcPos == null) || (dcScale == null))
			{
				return;
			}
			
			IVector2 canvasSize = vp.getCanvasSize();
			float fontscale = getBasicFontScale(canvasSize, vp.getAreaSize(), vp.getSize());
			if (enableDCSize)
			{
				position = position.copy().multiply(dcScale);
				// Do not scale fintsize wrt. drawable combiner size.
//				fontscale *= dcScale.getMean().getAsFloat();
			}
			Font font = baseFont.deriveFont(baseFont.getSize() * fontscale);;
				
			TextRenderer tr = vp.getTextRenderer(font);
			
			tr.setColor(color);
			
			IVector2 pos = getBasePosition(vp, dcPos, position, canvasSize, vp.getInvertX(), vp.getInvertY());
			
			double xPos = pos.getXAsDouble();
			double yPos = pos.getYAsDouble();
			
			String text = getReplacedText(obj, this.text);
			String[] lines = text.split("(\n\r?)|(\r)");
			
			for (int i = 0; i < lines.length; ++i)
			{
				
				TextLayout tl = new TextLayout(lines[i], font, DUMMY_FRC);
				
				if (i != 0)
					yPos -= tl.getAscent();

				tr.beginRendering(canvasSize.getXAsInteger(), canvasSize.getYAsInteger());
				tr.draw(lines[i], (int) (xPos + getAlignment(tl)), (int) yPos);
				tr.endRendering();
				
				yPos -= (tl.getDescent() + tl.getLeading());
			}
		}
	}
	
	private double getAlignment(TextLayout tl)
	{
		double xAlign = 0.0;
		switch(align)
		{
			case ALIGN_RIGHT:
				xAlign -= tl.getAdvance();
				break;
			case ALIGN_CENTER:
				xAlign -= tl.getAdvance() / 2.0f;
			case ALIGN_LEFT:
			default:
		}
		return xAlign;
	}
	
	private final static IVector2 getBasePosition(AbstractViewport vp, IVector2 dcPos, IVector2 position, IVector2 canvasSize, boolean invX, boolean invY)
	{
		IVector2 pos = vp.getPosition().copy().negate().add(vp.getObjectShift()).add(dcPos).add(position).divide(vp.getPaddedSize()).multiply(canvasSize);
		if (invX)
			pos.negateX().add(new Vector2Double(canvasSize.getXAsDouble(), 0));
		if (invY)
			pos.negateY().add(new Vector2Double(0, canvasSize.getYAsDouble()));
		return pos;
	}
	
	private final static float getBasicFontScale(IVector2 canvasSize, IVector2 areaSize, IVector2 size)
	{
		return ((Math.min(canvasSize.getXAsFloat(), canvasSize.getYAsFloat()) / BASE_VIEWPORT_SIZE) * areaSize.copy().divide(size).getMean().getAsFloat());
	}
	
	private final static String getReplacedText(Object obj, String text)
	{
		
		String[] tokens = text.split("\\$");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tokens.length; ++i)
		{
			if ((i & 1) == 0)
			{
				sb.append(tokens[i]);
			}
			else
			{
				if (tokens[i] == "")
				{
					sb.append("$");
				}
				else
				{
					sb.append(String.valueOf(SObjectInspector.getProperty(obj, tokens[i])));
				}
			}
		}
		
		return sb.toString();
	}
}
