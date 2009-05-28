package jadex.adapter.base.envsupport.observer.graphics.layer;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;


/**
 * A layer for displaying a grid.
 */
public class GridLayer implements ILayer
{
	private IVector2	gridSize_;

	/** Color of the grid. */
	protected Color		c_;

	/** OpenGL color cache. */
	protected float[]	oglColor_;
	
	/**
	 * Creates a new gridlayer with a grid size of 1.0.
	 * 
	 * @param c color of the grid
	 */
	public GridLayer(Color c)
	{
		this(new Vector2Double(1.0), c);
	}

	/**
	 * Creates a new gridlayer.
	 * 
	 * @param gridSize size of each grid rectangle
	 * @param c color of the grid
	 */
	public GridLayer(IVector2 gridSize, Color c)
	{
		gridSize_ = gridSize.copy();
		c_ = c;
		oglColor_ = new float[4];
		oglColor_[0] = c_.getRed() / 255.0f;
		oglColor_[1] = c_.getGreen() / 255.0f;
		oglColor_[2] = c_.getBlue() / 255.0f;
		oglColor_[3] = c_.getAlpha() / 255.0f;
	}
	
	/**
	 * Initializes the layer for a Java2D viewport
	 * 
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void init(ViewportJ2D vp, Graphics2D g)
	{
	}

	/**
	 * Initializes the layer for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp, GL gl)
	{
	}

	/**
	 * Draws the layer to a Java2D viewport
	 * 
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(IVector2 areaSize, ViewportJ2D vp, Graphics2D g)
	{
		g.setColor(c_);
		
		IVector2 pixSize = vp.getPixelSize();
		
		//Hack
		IVector2 step = areaSize.copy().subtract(pixSize).divide(areaSize.copy().divide(gridSize_));
		
		for (float x = 0.0f; x < areaSize.getXAsFloat(); x = x + step.getXAsFloat())
		{
			Rectangle2D.Float r = new Rectangle2D.Float(x, 0.0f, pixSize.getXAsFloat(), areaSize.getYAsFloat());
			g.fill(r);
		}
		
		for (float y = 0.0f; y < areaSize.getYAsFloat(); y = y + step.getYAsFloat())
		{
			Rectangle2D.Float r = new Rectangle2D.Float(0.0f, y, areaSize.getXAsFloat(), pixSize.getYAsFloat());
			g.fill(r);
		}
	}

	/**
	 * Draws the layer to an OpenGL viewport
	 * 
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void draw(IVector2 areaSize, ViewportJOGL vp, GL gl)
	{
		gl.glColor4fv(oglColor_, 0);
		
		IVector2 pixSize = vp.getPixelSize();
		
		gl.glBegin(GL.GL_QUADS);
		
		IVector2 step = areaSize.copy().subtract(pixSize).divide(areaSize.copy().divide(gridSize_));
		
		for (float x = 0.0f; x < areaSize.getXAsFloat(); x = x + step.getXAsFloat())
		{
			gl.glVertex2f(x, 0.0f);
			gl.glVertex2f(x, areaSize.getYAsFloat());
			gl.glVertex2f(x + pixSize.getXAsFloat(), areaSize.getYAsFloat());
			gl.glVertex2f(x + pixSize.getXAsFloat(), 0.0f);
		}
		
		for (float y = 0.0f; y < areaSize.getYAsFloat(); y = y + step.getYAsFloat())
		{
			gl.glVertex2f(0.0f, y);
			gl.glVertex2f(areaSize.getXAsFloat(), y);
			gl.glVertex2f(areaSize.getXAsFloat(), y + pixSize.getXAsFloat());
			gl.glVertex2f(0.0f, y + pixSize.getXAsFloat());
		}
		
		gl.glEnd();
	}

	/**
	 * Provides a copy of the layer.
	 */
	public ILayer copy()
	{
		return new GridLayer(gridSize_, c_);
	}
}
