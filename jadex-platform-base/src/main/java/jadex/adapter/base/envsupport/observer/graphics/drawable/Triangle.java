package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.javaparser.IParsedExpression;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.media.opengl.GL;


public class Triangle extends ColoredPrimitive
{
	/** Triangle path for Java2D. */
	private static final GeneralPath J2D_TRIANGLE = new GeneralPath();
	static
	{
		J2D_TRIANGLE.moveTo(0.0f, 0.5f);
		J2D_TRIANGLE.lineTo((float)-(0.25 * Math.sqrt(3)), -0.25f);
		J2D_TRIANGLE.lineTo((float)(0.25 * Math.sqrt(3)), -0.25f);
		J2D_TRIANGLE.closePath();
	}

	/** Display list for OpenGL. */
	private int dList_;

	/**
	 * Generates a new Triangle
	 */
	public Triangle()
	{
		super();
	}

	/**
	 * Generates a new Triangle
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param c the drawable's color or binding
	 */
	public Triangle(Object position, Object rotation, Object size, int absFlags, Object c, IParsedExpression drawcondition)
	{
		super(position, rotation, size, absFlags, c, drawcondition);
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

			gl.glBegin(GL.GL_TRIANGLES);
			gl.glVertex2d(0.0, 0.5);
			gl.glVertex2d(-(0.25 * Math.sqrt(3)), -0.25f);
			gl.glVertex2d((0.25 * Math.sqrt(3)), -0.25f);
			gl.glEnd();
			gl.glEndList();

			list = new Integer(newList);
			vp.setDisplayList(listName, list);
		}

		dList_ = list.intValue();
	}

	public void doDraw(DrawableCombiner dc, Object obj, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		AffineTransform transform = g.getTransform();
		if(!setupMatrix(dc, obj, g))
			return;
		Color c = (Color) dc.getBoundValue(obj, color_);
		g.setColor(c);
		g.fill(J2D_TRIANGLE);
		g.setTransform(transform);
	}

	public void doDraw(DrawableCombiner dc, Object obj, ViewportJOGL vp)
	{
		GL gl = vp.getContext();
		Color c = (Color) dc.getBoundValue(obj, color_);
		gl.glColor4fv(c.getComponents(null), 0);
		if (setupMatrix(dc, obj, gl))
			gl.glCallList(dList_);
	}

}
