package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.javaparser.IParsedExpression;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import javax.media.opengl.GL;


public class RegularPolygon extends ColoredPrimitive
{
	/** Vertex count. */
	private int			vertices_;

	/** Shape for Java2D. */
	protected Shape	shape_;

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
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c the drawable's color or binding
	 * @param vertices number of vertices (corners)
	 */
	public RegularPolygon(Object position, Object rotation, Object size, int absFlags, Object c, int vertices, IParsedExpression drawcondition)
	{
		super(position, rotation, size, absFlags, c, drawcondition);
		vertices_ = vertices;
	}

	public void init(ViewportJ2D vp)
	{
		GeneralPath path = new GeneralPath();
		path.moveTo(0.5f, 0.0f);
		for(int i = 1; i < vertices_; ++i)
		{
			double x = Math.PI * 2 / vertices_ * i;
			path
					.lineTo((float)(Math.cos(x) / 2.0),
							(float)(Math.sin(x) / 2.0));
		}
		path.closePath();
		shape_ = path;
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

	public void doDraw(DrawableCombiner dc, Object obj, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		if(!setupMatrix(dc, obj, g))
			return;
		Color c = (Color) dc.getBoundValue(obj, color_);
		g.setColor(c);
		g.fill(shape_);
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
