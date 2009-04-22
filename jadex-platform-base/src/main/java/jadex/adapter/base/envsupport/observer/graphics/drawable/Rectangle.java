package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;


public class Rectangle extends ColoredPrimitive
{
	/** Rectangle2D for Java2D. */
	private static final Rectangle2D.Double	J2D_RECTANGLE	= new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);

	/** Display list for OpenGL. */
	private int dList_;

	/**
	 * Generates a new Rectangle
	 */
	public Rectangle()
	{
		super();
	}

	/**
	 * Generates a new Rectangle
	 * 
	 * @param position position or position-binding
	 * @param rotation rotation or rotation-binding
	 * @param size size or size-binding
	 * @param c the drawable's color
	 */
	public Rectangle(Object position, Object rotation, Object size, Color c)
	{
		super(position, rotation, size, c);
	}

	public void init(ViewportJ2D vp)
	{
	}

	public void init(ViewportJOGL vp)
	{
		String listName = getClass().getName();
		Integer list = vp.getDisplayList(listName);
		if(list == null)
		{
			GL gl = vp.getContext();
			int newList = gl.glGenLists(1);
			gl.glNewList(newList, GL.GL_COMPILE);

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex2d(-0.5, -0.5);
			gl.glVertex2d(0.5, -0.5);
			gl.glVertex2d(0.5, 0.5);
			gl.glVertex2d(-0.5, 0.5);
			gl.glEnd();
			gl.glEndList();

			list = new Integer(newList);
			vp.setDisplayList(listName, list);
		}

		dList_ = list.intValue();
	}

	public void draw(Object obj, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		AffineTransform transform = g.getTransform();
		if (!setupMatrix(obj, g))
			return;
		g.setColor(c_);
		g.fill(J2D_RECTANGLE);
		g.setTransform(transform);
	}

	public void draw(Object obj, ViewportJOGL vp)
	{
		GL gl = vp.getContext();
		gl.glPushMatrix();
		gl.glColor4fv(oglColor_, 0);
		if (setupMatrix(obj, gl))
			gl.glCallList(dList_);

		gl.glPopMatrix();
	}

}
