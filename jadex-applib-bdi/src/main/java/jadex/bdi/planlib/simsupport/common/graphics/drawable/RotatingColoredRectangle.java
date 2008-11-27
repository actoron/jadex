package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

public class RotatingColoredRectangle extends RotatingColoredPrimitive
{
	static private final long serialVersionUID = 0L;
	
    private static final Rectangle2D.Double RECTANGLE =
        new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
    
    /** Creates an unrotated, white RotatingColoredRectangle at 0,0 and size 1,1.
     */
    public RotatingColoredRectangle()
    {
        this(new Vector2Double(1.0),
             Color.WHITE);
    }
    
    /** Creates a new RotatingColoredRectangle drawable.
     *
     *  @param pos initial position
     *  @param size initial size
     *  @param velocity initial velocity
     *  @param c color
     */
    public RotatingColoredRectangle(IVector2 size, Color c)
    {
    	setPosition(new Vector2Double(0.0));
        setSize(size);
        setVelocity(new Vector2Double(0.0));
        setColor(c);
    }
    
    /** Copies a RotatingColoredRectangle drawable from internal data.
     *
     *  @param original the original
     */
    private RotatingColoredRectangle(RotatingColoredRectangle original)
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
        AffineTransform oTf = new AffineTransform();
        oTf.translate(px_, py_);
        oTf.scale(w_, h_);
        oTf.rotate(rot_);
        g.transform(oTf);
        g.setColor(c_);
        g.fill(RECTANGLE);
        g.setTransform(transform);
    }
    
    public synchronized void draw(ViewportJOGL vp, GL gl)
    {
        gl.glPushMatrix();
        gl.glColor4fv(oglColor_, 0);
        gl.glTranslatef(px_, py_, 0.0f);
        gl.glScalef(w_, h_, 1.0f);
        gl.glRotated(Math.toDegrees(rot_), 0.0, 0.0, 1.0);
        
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2d(-0.5, -0.5);
        gl.glVertex2d(0.5, -0.5);
        gl.glVertex2d(0.5, 0.5);
        gl.glVertex2d(-0.5, 0.5);
        gl.glEnd();
        
        gl.glPopMatrix();
    }
    
    public IDrawable copy()
    {
    	RotatingColoredRectangle newDrawable = new RotatingColoredRectangle(this);
    	return newDrawable;
    }
}
