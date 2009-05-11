package jadex.adapter.base.envsupport.observer.graphics.drawable;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.graphics.AbstractViewport;
import jadex.adapter.base.envsupport.observer.graphics.SizedTexture;
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
	/** Relative position or binding */
	private Object position;
	
	/** Font used for the text */
	private Font baseFont;
	
	/** Color of the font */
	private Color color;
	
	/** Fixed text */
	private String text;
	
	/*private Font cachedFont;
	private Color cachedColor;
	private String cachedText;*/
	
	/** The condition deciding if the drawable should be drawn. */
	private IParsedExpression drawcondition;
	
	public Text()
	{
		this(null, null, null, null, null);
	}
	
	public Text(Object position, Font baseFont, Color color, String text, IParsedExpression drawcondition)
	{
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
			IVector2 position = SObjectInspector.getVector2(obj, this.position);
			IVector2 dcPos = SObjectInspector.getVector2(obj, dc.getPosition());
			if ((position == null) || (dcPos == null))
			{
				return;
			}
			
			Graphics2D g = vp.getContext();
			AffineTransform t = g.getTransform();
			BufferedImage image = vp.getTextImage(new TextInfo(baseFont, color, text));
			Canvas canvas = vp.getCanvas();
			IVector2 size = vp.getPaddedSize().copy().divide(new Vector2Double(canvas.getWidth(), canvas.getHeight())).
			multiply(new Vector2Double(image.getWidth(), image.getHeight()));

			
			g.translate(dcPos.getXAsDouble(), dcPos.getYAsDouble());
			g.translate(position.getXAsDouble() - (size.getXAsDouble() / 2.0),
					position.getYAsDouble() - (size.getYAsDouble() / 2.0));
			g.scale(size.getXAsDouble(), size.getYAsDouble());
			
			/*AffineTransform imageTransform = new AffineTransform();
			imageTransform.scale(1.0 / image.getWidth(), 1.0 / image.getHeight());*/
			g.drawImage(image, vp.getImageTransform(image.getWidth(), image.getHeight()), null);
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
			IVector2 position = SObjectInspector.getVector2(obj, this.position);
			IVector2 dcPos = SObjectInspector.getVector2(obj, dc.getPosition());
			if ((position == null) || (dcPos == null))
			{
				return;
			}
			
			GL gl = vp.getContext();
			gl.glPushMatrix();
			
			SizedTexture texture = vp.getTextTexture(new TextInfo(baseFont, color, text));
			IVector2 imgSize = texture.getSize();
			Canvas canvas = vp.getCanvas();
			IVector2 size = vp.getPaddedSize().copy().divide(new Vector2Double(canvas.getWidth(), canvas.getHeight())).
			multiply(imgSize);
			
			gl.glTranslatef(dcPos.getXAsFloat(), dcPos.getYAsFloat(), 0.0f);
			gl.glTranslatef(position.getXAsFloat(), position.getYAsFloat(), 0.0f);
			gl.glScalef(size.getXAsFloat(), size.getYAsFloat(), 1.0f);
			
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getTexId());

			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex2f(-0.5f, -0.5f);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex2f(0.5f, -0.5f);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex2f(0.5f, 0.5f);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex2f(-0.5f, 0.5f);
			gl.glEnd();

			gl.glDisable(GL.GL_TEXTURE_2D);
			
			gl.glPopMatrix();
		}
	}
}
