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

public class ScalableRegularPolygon extends ScalablePrimitive
{
	/** Color of the Polygon.
	 */
	private Color c_;
	
	/** The vertices.
	 */
	private List vertices_;
	
	/** Path for Java2D.
	 */
	private GeneralPath path_;
	
	/** Generates a size 1.0 triangle.
	 */
	public ScalableRegularPolygon()
	{
		this(new Vector2Double(1.0), 3, Color.WHITE);
	}
	
	/** Generates a new ScalableRegularPolygon.
	 * 
	 *  @param size size of the polygon
	 *  @param vertices number of vertices (corners)
	 *  @param c color of the polygon
	 */
	public ScalableRegularPolygon(IVector2 size, int vertices, Color c)
	{
		c_ = c;
		vertices_ = new ArrayList();
		double op = 0.0;
		IVector2 vertex = new Vector2Double(Math.sin(op) / 2.0, Math.cos(op) / 2.0);
		for (int i = 0; i < vertices; ++i)
		{
			System.out.print(vertex.getXAsDouble());
			System.out.print(", ");
			System.out.println(vertex.getYAsDouble());
			vertices_.add(vertex);
			op += (2.0 * Math.PI) / vertices;
			vertex = new Vector2Double(Math.sin(op) / 2.0, Math.cos(op) / 2.0);
		}
	}
	
	/** Private copy constructor.
	 *
	 *  @param other the original
	 */
	private ScalableRegularPolygon(ScalableRegularPolygon other)
	{
		vertices_ = new ArrayList(other.vertices_);
		c_ = other.c_;
	}
	
	public void init(ViewportJ2D vp, Graphics2D g)
	{
		path_ = new GeneralPath();
		boolean first = true;
		for (Iterator it = vertices_.iterator(); it.hasNext(); )
		{
			IVector2 vertex = (IVector2) it.next();
			if (first)
			{
				path_.moveTo(vertex.getXAsFloat(), vertex.getYAsFloat());
				first = false;
			}
			else
			{
				path_.lineTo(vertex.getXAsFloat(), vertex.getYAsFloat());
			}
		}
		path_.closePath();
	}
	
	public void init(ViewportJOGL vp, GL gl)
	{
	}
	
	public void draw(ViewportJ2D vp, Graphics2D g)
	{
		AffineTransform transform = g.getTransform();
        g.translate(px_, py_);
        g.scale(w_, h_);
        g.setColor(c_);
        g.fill(path_);
        g.setTransform(transform);
	}
	
	public void draw(ViewportJOGL vp, GL gl)
	{
		gl.glPushMatrix();
        gl.glColor4d(c_.getRed(), c_.getGreen(), c_.getBlue(), c_.getAlpha());
        gl.glTranslatef(px_, py_, 0.0f);
        gl.glScalef(w_, h_, 1.0f);
        
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glVertex2f(0.0f, 0.0f);
        for (Iterator it = vertices_.iterator(); it.hasNext(); )
        {
        	IVector2 vertex = (IVector2) it.next();
        	gl.glVertex2f(vertex.getXAsFloat(), vertex.getYAsFloat());
        }
        gl.glVertex2f(((IVector2) vertices_.get(0)).getXAsFloat(),
        			  ((IVector2) vertices_.get(0)).getYAsFloat());
        gl.glEnd();
        
        gl.glPopMatrix();
	}
	
	public IDrawable copy()
	{
		return new ScalableRegularPolygon(this);
	}

}
