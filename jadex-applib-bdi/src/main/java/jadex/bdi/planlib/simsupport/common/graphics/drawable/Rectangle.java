package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

public class Rectangle extends ColoredPrimitive
{
	/** Rectangle2D for Java2D
	 */
	private static final Rectangle2D.Double J2D_RECTANGLE = new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
	
	/** Display list for OpenGL
	 */
	private int dList_;
	
	/** Generates a new Rectangle
	 * 
	 *  @param size size of the rectangle
	 *  @param c color of the rectangle
	 *  @param rotating if true, the resulting drawable will rotate depending on
	 *  	   the velocity
	 */
	public Rectangle(IVector2 size, Color c, boolean rotating)
	{
		setColor(c);
		setPosition(new Vector2Double(0.0));
        setSize(size);
        setVelocity(new Vector2Double(0.0));
        setRotating(rotating);
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
	
	public void draw(ViewportJ2D vp, Graphics2D g)
	{
		AffineTransform transform = g.getTransform();
        g.translate(px_, py_);
        g.scale(w_, h_);
        if (rotating_)
        {
        	g.rotate(rot_);
        }
        g.setColor(c_);
        g.fill(J2D_RECTANGLE);
        g.setTransform(transform);
	}
	
	public void draw(ViewportJOGL vp, GL gl)
	{
		gl.glPushMatrix();
		gl.glColor4fv(oglColor_, 0);
        gl.glTranslatef(px_, py_, 0.0f);
        gl.glScalef(w_, h_, 1.0f);
        if (rotating_)
        {
        	gl.glRotated(Math.toDegrees(rot_), 0.0, 0.0, 1.0);
        }
        
        gl.glCallList(dList_);
        
        gl.glPopMatrix();
	}
	
}
