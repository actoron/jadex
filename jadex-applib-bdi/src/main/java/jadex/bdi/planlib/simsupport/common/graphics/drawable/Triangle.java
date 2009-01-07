package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.media.opengl.GL;

public class Triangle extends ColoredPrimitive
{
	/** Triangle path for Java2D
	 */
	private static final GeneralPath J2D_TRIANGLE = new GeneralPath();
	static
	{
		J2D_TRIANGLE.moveTo(-0.5f, -0.5f);
		J2D_TRIANGLE.lineTo(0.5f, -0.5f);
		J2D_TRIANGLE.lineTo(0.0f, 0.5f);
		J2D_TRIANGLE.closePath();
	}
	
	/** Display list for OpenGL
	 */
	private int dList_;
	
	/** Generates a new Triangle
	 * 
	 *  @param size size of the triangle
	 *  @param shift shift from the centered position using scale(1.0, 1.0)
	 *  @param rotating if true, the resulting drawable will rotate depending on
	 *  	   the velocity
	 *  @param c color of the triangle
	 */
	public Triangle(IVector2 size, IVector2 shift, boolean rotating, Color c)
	{
		super(size, shift, rotating, c);
	}
	
	public void init(ViewportJ2D vp, Graphics2D g)
	{
	}
	
	public void init(ViewportJOGL vp, GL gl)
	{
		String listName = getClass().getName();
		Integer list = vp.getDisplayList(listName);
		if (list == null)
		{
			int newList = gl.glGenLists(1);
			gl.glNewList(newList, GL.GL_COMPILE);
			
			gl.glBegin(GL.GL_TRIANGLES);
			gl.glVertex2d(-0.5, -0.5);
			gl.glVertex2d(0.5, -0.5);
			gl.glVertex2d(0.0, 0.5);
			gl.glEnd();
			gl.glEndList();
			
			list = new Integer(newList);
			vp.setDisplayList(listName, list);
		}
		
		dList_ = list.intValue();
	}
	
	public void draw(ViewportJ2D vp, Graphics2D g)
	{
		AffineTransform transform = g.getTransform();
        setupMatrix(g);
        g.setColor(c_);
        g.fill(J2D_TRIANGLE);
        g.setTransform(transform);
	}
	
	public void draw(ViewportJOGL vp, GL gl)
	{
		gl.glPushMatrix();
		gl.glColor4fv(oglColor_, 0);
        setupMatrix(gl);
        
        gl.glCallList(dList_);
        
        gl.glPopMatrix();
	}
	
}
