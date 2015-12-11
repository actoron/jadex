package jadex.extension.envsupport.observer.graphics.opengl;

import java.awt.Color;

import javax.media.opengl.GL;

import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;

public class TriangleGLRenderer extends AbstractGLRenderer
{

	/**
	 * Draws the primitive.
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void draw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJOGL vp)
	{
		int dList = 0;
		try
		{
			dList = ((Integer) primitive.getRenderInfo(0));
		}
		catch (Exception e)
		{
			String listName = getClass().getName();
			Integer list = vp.getDisplayList(listName);
			if(list == null)
			{
				GL gl = vp.getContext();
				dList = gl.glGenLists(1);
				gl.glNewList(dList, GL.GL_COMPILE);

				gl.glBegin(GL.GL_TRIANGLES);
				gl.glVertex2d(0.0, 0.5);
				gl.glVertex2d(-(0.25 * Math.sqrt(3)), -0.25f);
				gl.glVertex2d((0.25 * Math.sqrt(3)), -0.25f);
				gl.glEnd();
				gl.glEndList();

				list = Integer.valueOf(dList);
				vp.setDisplayList(listName, list);
			}
			dList = list.intValue();
			primitive.setRenderInfo(0, list);
		}
		
		GL gl = vp.getContext();
		Color c = (Color) dc.getBoundValue(obj, primitive.getColor(), vp);
		if(c==null)
			c=Color.WHITE;
		gl.glColor4fv(c.getComponents(null), 0);
		if (setupMatrix(dc, primitive, obj, gl, vp))
			gl.glCallList(dList);
	}
}
