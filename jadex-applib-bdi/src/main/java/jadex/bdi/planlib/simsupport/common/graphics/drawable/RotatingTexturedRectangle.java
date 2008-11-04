package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import javax.media.opengl.GL;
import jadex.bdi.planlib.simsupport.common.graphics.Texture2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

public class RotatingTexturedRectangle extends RotatingPrimitive
{
	static private final long serialVersionUID = 0L;
    
    /** Texture path
     */
    protected String texturePath_;
    
    /** Texture for OpenGL operations
     */
    private Texture2D texture_;
    
    /** Image for Java2D operations
     */
    private BufferedImage image_;
    
    /** AffineTransform from image space to user space for Java2D.
     */
    private AffineTransform imageToUser_;
    
    /** Creates an unrotated TexturedCenteredRectangle at 0,0 and size 1,1.
     *
     *  @param texturePath resource path of the texture
     */
    public RotatingTexturedRectangle(String texturePath)
    {
        this(new Vector2Double(0.0),
             new Vector2Double(1.0),
             new Vector2Double(0.0),
             texturePath);
    }
    
    /** Creates a new TexturedCenteredRectangle drawable.
     *
     *  @param pos initial position
     *  @param size initial size
     *  @param velocity initial velocity
     *  @param texturePath resource path of the texture
     */
    public RotatingTexturedRectangle(IVector2 pos,
                                     IVector2 size,
                                     IVector2 velocity,
                                     String texturePath)
    {
        setPosition(pos);
        setSize(size);
        setVelocity(velocity);
        texturePath_ = texturePath;
        texture_ = null;
        image_ = null;
    }
    
    /** Copies a RotatingTexturedRectangle drawable from internal data.
     *
     *  @param original the original
     */
    private RotatingTexturedRectangle(RotatingTexturedRectangle original)
    {
    	px_ = original.px_;
    	py_ = original.py_;
    	w_ = original.w_;
    	h_ = original.h_;
    	rot_ = original.rot_;
    	texturePath_ = original.texturePath_;
    }
    
    public void init(ViewportJ2D vp, Graphics2D g)
    {
    	image_ = vp.getImage(texturePath_);
        imageToUser_ = new AffineTransform();
        imageToUser_.scale(1.0 / image_.getWidth(), 1.0 / image_.getHeight());
        //imageToUser_.translate(0.5, 0.5);
    }
    
    public void init(ViewportJOGL vp, GL gl)
    {
    	texture_ = vp.getClampedTexture(gl, texturePath_);
    }
    
    public synchronized void draw(ViewportJ2D vp, Graphics2D g)
    {
        AffineTransform transform = g.getTransform();
        g.rotate(rot_);
        g.translate(px_ - (w_ / 2), py_ - (w_ / 2));
        g.scale(w_, h_);
        g.drawImage(image_, imageToUser_, null);
        g.setTransform(transform);
    }
    
    public synchronized void draw(ViewportJOGL vp, GL gl)
    {
        gl.glPushMatrix();
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture_.getTexId());
        gl.glTranslatef(px_, py_, 0.0f);
        gl.glScalef(w_, h_, 1.0f);
        gl.glRotated(Math.toDegrees(rot_), 0.0, 0.0, 1.0);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2f(-0.5f, -0.5f);
        gl.glTexCoord2f(texture_.getMaxX(), 0.0f);
        gl.glVertex2f(0.5f, -0.5f);
        gl.glTexCoord2f(texture_.getMaxX(), texture_.getMaxY());
        gl.glVertex2f(0.5f, 0.5f);
        gl.glTexCoord2f(0.0f, texture_.getMaxY());
        gl.glVertex2f(-0.5f, 0.5f);
        gl.glEnd();
        
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }
    
    public IDrawable copy()
    {
    	RotatingTexturedRectangle newRect = new RotatingTexturedRectangle(this);
    	return newRect;
    }
}
