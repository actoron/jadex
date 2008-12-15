package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL;

import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

public class RegularPolygon extends ColoredPrimitive
{
	/** Vertex count
	 */
	private int vertices_;
	
	/** Path for Java2D.
	 */
	private GeneralPath path_;
	
	/** Display list for OpenGL
	 */
	private int dList_;
	
	/** Generates a size 1.0 triangle.
	 */
	public RegularPolygon()
	{
		this(new Vector2Double(1.0), 3, Color.WHITE, false);
	}
	
	/** Generates a new RegularPolygon.
	 * 
	 *  @param size size of the polygon
	 *  @param vertices number of vertices (corners)
	 *  @param c color of the polygon
	 *  @param rotating if true, the resulting drawable will rotate depending on
	 *  	   the velocity
	 */
	public RegularPolygon(IVector2 size, int vertices, Color c, boolean rotating)
	{
		setColor(c);
		setPosition(new Vector2Double(0.0));
        setSize(size);
        setVelocity(new Vector2Double(0.0));
        setRotating(rotating);
		vertices_ = vertices;
	}
	
	public void init(ViewportJ2D vp, Graphics2D g)
	{
		path_ = new GeneralPath();
		path_.moveTo(0.5f, 0.0f);
		for (int i = 1; i < vertices_; ++i)
		{
			double x = Math.PI * 2 / vertices_ * i;
			path_.lineTo((float)(Math.cos(x) / 2.0), (float)(Math.sin(x) / 2.0));
		}
		path_.closePath();
	}
	
	public void init(ViewportJOGL vp, GL gl)
	{
		String listName = getClass().getName() + "_" + new Integer(vertices_).toString();
		Integer list = vp.getDisplayList(listName);
		if (list == null)
		{
			int newList = gl.glGenLists(1);
			gl.glNewList(newList, GL.GL_COMPILE);
			
			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glVertex2d(0.0, 0.0);
			gl.glVertex2d(0.5, 0.0);
			for (int i = 1; i < vertices_; ++i)
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
        g.fill(path_);
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
