package jadex.adapter.base.envsupport.observer.graphics.layer;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

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
	 * Draws the layer to a Java2D viewport
	 * 
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(IVector2 areaSize, ViewportJ2D vp, Graphics2D g)
	{
		g.setColor(c_);
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(areaSize.getXAsFloat()
				/ vp.getCanvas().getWidth()));
		Line2D.Float line = new Line2D.Float();
		line.y1 = 0.0f;
		line.y2 = areaSize.getYAsFloat();
		for(float x = 0.0f; x <= areaSize.getXAsFloat(); x = x
				+ gridSize_.getXAsFloat())
		{
			line.x1 = x;
			line.x2 = x;
			g.draw(line);
		}
		g.setStroke(new BasicStroke(areaSize.getYAsFloat()
				/ vp.getCanvas().getHeight()));
		line.x1 = 0.0f;
		line.x2 = areaSize.getXAsFloat();
		for(float y = 0.0f; y <= areaSize.getYAsFloat(); y = y
				+ gridSize_.getYAsFloat())
		{
			line.y1 = y;
			line.y2 = y;
			g.draw(line);
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

		gl.glBegin(gl.GL_LINES);
		for(float x = 0.0f; x <= areaSize.getXAsFloat(); x = x
				+ gridSize_.getXAsFloat())
		{
			gl.glVertex2f(x, 0.0f);
			gl.glVertex2f(x, areaSize.getYAsFloat());
		}

		for(float y = 0.0f; y <= areaSize.getYAsFloat(); y = y
				+ gridSize_.getYAsFloat())
		{
			gl.glVertex2f(0.0f, y);
			gl.glVertex2f(areaSize.getXAsFloat(), y);
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
