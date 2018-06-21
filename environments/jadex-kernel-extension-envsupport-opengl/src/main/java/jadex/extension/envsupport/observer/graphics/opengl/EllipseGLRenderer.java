package jadex.extension.envsupport.observer.graphics.opengl;

import java.awt.Color;

import javax.media.opengl.GL;

import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;

public class EllipseGLRenderer extends AbstractGLRenderer
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
		GL gl = vp.getContext();
		int dList = 0;
		try
		{
			dList = ((Integer) primitive.getRenderInfo(0)).intValue();
		}
		catch (Exception e)
		{
			String listName = getClass().getName();
			Integer list = vp.getDisplayList(listName);
			if(list == null)
			{
				dList = gl.glGenLists(1);
				gl.glNewList(dList, GL.GL_COMPILE);
	
				gl.glBegin(GL.GL_TRIANGLE_FAN);
				gl.glVertex2d(0.0, 0.0);
				gl.glVertex2d(0.5, 0.0);
				for(int i = 1; i < 36; ++i)
				{
					double x = Math.PI * 2 / 36 * i;
					gl.glVertex2d(Math.cos(x) / 2.0, Math.sin(x) / 2.0);
				}
				gl.glVertex2d(0.5, 0.0);
				gl.glEnd();
				gl.glEndList();
	
				list = Integer.valueOf(dList);
				vp.setDisplayList(listName, list);
				
			}
			
			dList = list.intValue();
			primitive.setRenderInfo(0, list);
		}
		
		Color c = (Color)dc.getBoundValue(obj, primitive.getColor(), vp);
		if(c==null)
			c=Color.WHITE;
		gl.glColor4fv(c.getComponents(null), 0);
		if (setupMatrix(dc, primitive, obj, gl, vp))
			gl.glCallList(dList);
	}

}
