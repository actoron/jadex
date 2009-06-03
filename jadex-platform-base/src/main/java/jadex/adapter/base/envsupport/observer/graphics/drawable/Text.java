package jadex.adapter.base.envsupport.observer.graphics.drawable;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.font.TextMeasurer;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;

import com.sun.opengl.util.j2d.TextRenderer;


import jadex.adapter.base.envsupport.environment.space2d.action.GetPosition;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
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
	/** Viewport Height on which the base font size is relative to */
	private final static IVector2 BASE_VIEWPORT_SIZE = new Vector2Double(300.0);
	
	/** Relative position or binding */
	private Object position;
	
	/** Font used for the text */
	private Font baseFont;
	
	/** Color of the font */
	private Color color;
	
	/** Fixed text */
	private String text;
	
	/** Font scaling flag */
	private boolean fontscaling;
	
	/** The condition deciding if the drawable should be drawn. */
	private IParsedExpression drawcondition;
	
	public Text()
	{
		this(null, null, null, null, true, null);
	}
	
	public Text(Object position, Font baseFont, Color color, String text, boolean fontscaling, IParsedExpression drawcondition)
	{
		this.fontscaling = fontscaling;
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
			IVector2 position = (IVector2)dc.getBoundValue(obj, this.position);
			IVector2 dcPos = (IVector2)dc.getBoundValue(obj, dc.getPosition());//SObjectInspector.getVector2(obj, dc.getPosition());
			IVector2 dcScale = (IVector2)dc.getBoundValue(obj, dc.getSize());//SObjectInspector.getVector2(obj, dc.getSize());
			if((position == null) || (dcPos == null) || (dcScale == null))
			{
				return;
			}
			
			Graphics2D g = vp.getContext();
			Canvas canvas = vp.getCanvas();
			Font font = baseFont;
			if (fontscaling)
			{
				float fontscale = dcScale.getMean().getAsFloat() * (new Vector2Double(canvas.getWidth(), canvas.getHeight())).divide(BASE_VIEWPORT_SIZE).getMean().getAsFloat() * vp.getAreaSize().copy().divide(vp.getSize()).getMean().getAsFloat();
				font = font.deriveFont(baseFont.getSize() * fontscale);
			}
			String text = getReplacedText(obj);
			
			IVector2 pos = vp.getPosition().copy().negate().add(vp.getObjectShift()).add(dcPos).add(position).divide(vp.getPaddedSize()).multiply(new Vector2Double(canvas.getWidth(), canvas.getHeight()));
			if (vp.getInvertX())
				pos.negateX().add(new Vector2Double(canvas.getWidth(), 0));
			if (!vp.getInvertY())
				pos.negateY().add(new Vector2Double(0, canvas.getHeight()));
			Rectangle2D bounds = font.getStringBounds(text, new FontRenderContext(null, true, true));
			pos.subtract(new Vector2Double(bounds.getWidth() / 2.0, bounds.getHeight() / 2.0));
			
			g.setColor(color);
			g.setFont(font);
			AffineTransform t = g.getTransform();
			g.setTransform(vp.getDefaultTransform());
			TextLayout tl = new TextLayout(text, font, new FontRenderContext(null, true, true));
			tl.draw(g, pos.getXAsInteger(), pos.getYAsInteger());
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
			IVector2 position = (IVector2)dc.getBoundValue(obj, this.position);
			IVector2 dcPos = (IVector2)dc.getBoundValue(obj, dc.getPosition());//SObjectInspector.getVector2(obj, dc.getPosition());
			IVector2 dcScale = (IVector2)dc.getBoundValue(obj, dc.getSize());//SObjectInspector.getVector2(obj, dc.getSize());
			if((position == null) || (dcPos == null) || (dcScale == null))
			{
				return;
			}
			
			Canvas canvas = vp.getCanvas();
			Font font = baseFont;
			if (fontscaling)
			{
				float fontscale = dcScale.getMean().getAsFloat() * (new Vector2Double(canvas.getWidth(), canvas.getHeight())).divide(BASE_VIEWPORT_SIZE).getMean().getAsFloat() * vp.getAreaSize().copy().divide(vp.getSize()).getMean().getAsFloat();
				font = font.deriveFont(baseFont.getSize() * fontscale);
			}
			String text = getReplacedText(obj);
			
			TextRenderer tr = vp.getTextRenderer(font);
			tr.setColor(color);
			IVector2 pos = vp.getPosition().copy().negate().add(vp.getObjectShift()).add(dcPos).add(position).divide(vp.getPaddedSize()).multiply(new Vector2Double(canvas.getWidth(), canvas.getHeight()));
			if (vp.getInvertX())
				pos.negateX().add(new Vector2Double(canvas.getWidth(), 0));
			if (vp.getInvertY())
				pos.negateY().add(new Vector2Double(0, canvas.getHeight()));
			Rectangle2D bounds = tr.getBounds(text);
			pos.subtract(new Vector2Double(bounds.getWidth() / 2.0, bounds.getHeight() / 2.0));
			
			tr.beginRendering(canvas.getWidth(), canvas.getHeight());
			tr.draw(text, pos.getXAsInteger(), pos.getYAsInteger());
			tr.endRendering();
		}
	}
	
	private String getReplacedText(Object obj)
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
