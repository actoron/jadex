package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.media.opengl.GL;

public class RotatingColoredTriangle extends RotatingColoredPrimitive
{
	static private final long serialVersionUID = 0L;
	
    private static final GeneralPath TRIANGLE = new GeneralPath();
    static
    {
        TRIANGLE.moveTo(-0.5f, -0.5f);
        TRIANGLE.lineTo(0.0f, 0.5f);
        TRIANGLE.lineTo(0.5f, -0.5f);
        TRIANGLE.closePath();
    }
    
    /** Creates a new CenteredTriangle.
     *
     *  @param pos initial position
     *  @param size initial size
     *  @param velocity initial velocity
     *  @param c color
     */
    public RotatingColoredTriangle(IVector2 pos,
            				IVector2 size,
            				IVector2 velocity,
                            Color c)
    {
    	setPosition(pos);
        setSize(size);
        setVelocity(velocity);
        setColor(c);
        
        
    }
    
    /** Copies a RotatingColoredTriangle drawable from internal data.
     *
     *  @param original the original
     */
    private RotatingColoredTriangle(RotatingColoredTriangle original)
    {
    	px_ = original.px_;
    	py_ = original.py_;
    	w_ = original.w_;
    	h_ = original.h_;
    	rot_ = original.rot_;
    	setColor(original.c_);
    }
    
    public void init(ViewportJ2D vp, Graphics2D g)
    {
    }
    
    public void init(ViewportJOGL vp, GL gl)
    {
    }
    
    public synchronized void draw(ViewportJ2D vp, Graphics2D g)
    {
        AffineTransform transform = g.getTransform();
        g.translate(px_, py_);
        g.scale(w_, h_);
        g.rotate(rot_);
        g.setColor(c_);
        g.fill(TRIANGLE);
        g.setTransform(transform);
    }
    
    public synchronized void draw(ViewportJOGL vp, GL gl)
    {
        gl.glPushMatrix();
        gl.glColor4fv(oglColor_, 0);
        gl.glTranslatef(px_, py_, 0.0f);
        gl.glRotated(Math.toDegrees(rot_), 0.0, 0.0, 1.0);
        gl.glScalef(w_, h_, 1.0f);
        
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2d(-0.5, -0.5);
        gl.glVertex2d(0.5, -0.5);
        gl.glVertex2d(0.0, 0.5);
        gl.glEnd();
        
        gl.glPopMatrix();
    }
    
    public IDrawable copy()
    {
    	RotatingColoredTriangle newTriangle = new RotatingColoredTriangle(this);
    	return newTriangle;
    }
}
