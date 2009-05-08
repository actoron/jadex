package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.javaparser.IParsedExpression;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.media.opengl.GL;


public class RegularPolygon extends ColoredPrimitive
{
	/** Vertex count. */
	private int			vertices_;

	/** Path for Java2D. */
	private GeneralPath	path_;

	/** Display list for OpenGL. */
	private int			dList_;

	/**
	 * Generates a size 1.0 triangle.
	 */
	public RegularPolygon()
	{
		super();
		vertices_ = 3;
	}

	/**
	 * Generates a new RegularPolygon.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param c the drawable's color
	 * @param vertices number of vertices (corners)
	 */
	public RegularPolygon(Object position, Object xrotation, Object yrotation, Object zrotation, Object size, Color c, int vertices, IParsedExpression drawcondition)
	{
		super(position, xrotation, yrotation, zrotation, size, c, drawcondition);
		vertices_ = vertices;
	}

	public void init(ViewportJ2D vp)
	{
		path_ = new GeneralPath();
		path_.moveTo(0.5f, 0.0f);
		for(int i = 1; i < vertices_; ++i)
		{
			double x = Math.PI * 2 / vertices_ * i;
			path_
					.lineTo((float)(Math.cos(x) / 2.0),
							(float)(Math.sin(x) / 2.0));
		}
		path_.closePath();
	}

	public void init(ViewportJOGL vp)
	{
		GL gl = vp.getContext();
		String listName = getClass().getName() + "_"
				+ new Integer(vertices_).toString();
		Integer list = vp.getDisplayList(listName);
		if(list == null)
		{
			int newList = gl.glGenLists(1);
			gl.glNewList(newList, GL.GL_COMPILE);

			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glVertex2d(0.0, 0.0);
			gl.glVertex2d(0.5, 0.0);
			for(int i = 1; i < vertices_; ++i)
			{
				double x = Math.PI * 2 / vertices_ * i;
				gl.glVertex2d(Math.cos(x) / 2.0, Math.sin(x) / 2.0);
			}
			gl.glVertex2d(0.5, 0.0);
			gl.glEnd();
			gl.glEndList();

			list = new Integer(newList);
			vp.setDisplayList(listName, list);
		}

		dList_ = list.intValue();
	}

	public void doDraw(Object obj, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		AffineTransform transform = g.getTransform();
		if (!setupMatrix(obj, g))
			return;
		g.setColor(c_);
		g.fill(path_);
		g.setTransform(transform);
	}

	public void doDraw(Object obj, ViewportJOGL vp)
	{
		GL gl = vp.getContext();
		gl.glPushMatrix();
		gl.glColor4fv(oglColor_, 0);
		if (setupMatrix(obj, gl))
			gl.glCallList(dList_);
		
		gl.glPopMatrix();
	}
}
