package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import javax.media.opengl.GL;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.graphics.Texture2D;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

public class TexturedRectangle extends RotatingPrimitive
{
	static private final long serialVersionUID = 0L;
    
    /** Texture path
     */
    protected String texturePath_;
    
    /** Texture ID for OpenGL operations
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
    public TexturedRectangle(String texturePath)
    {
        this(new Vector2Double(1.0),
             new Vector2Double(0.0),
             false,
             texturePath);
    }
    
    /** Creates a new TexturedRectangle drawable.
     *
     *  @param size initial size
     *  @param shift shift from the centered position using scale(1.0, 1.0)
     *  @param rotating if true, the resulting drawable will rotate depending on
	 *  	   the velocity
	 *  @param texturePath resource path of the texture
     */
    public TexturedRectangle(IVector2 size,
    						 IVector2 shift,
                             boolean rotating,
                             String texturePath)
    {
        super(size, shift, rotating);
        texturePath_ = texturePath;
        texture_ = null;
        image_ = null;
    }
    
    public void init(ViewportJ2D vp, Graphics2D g)
    {
    	image_ = vp.getImage(texturePath_);
        imageToUser_ = new AffineTransform();
        imageToUser_.scale(1.0 / image_.getWidth(), 1.0 / image_.getHeight());
    }
    
    public void init(ViewportJOGL vp, GL gl)
    {
    	texture_ = vp.getClampedTexture(gl, texturePath_);
    }
    
    public synchronized void draw(ViewportJ2D vp, Graphics2D g)
    {
        AffineTransform transform = g.getTransform();
        g.translate(px_ - (w_ / 2), py_ - (h_ / 2));
        g.translate(shiftX_, shiftY_);
        g.scale(w_, h_);
        if (rotating_)
        {
        	g.rotate(rot_);
        }
        g.drawImage(image_, imageToUser_, null);
        g.setTransform(transform);
    }
    
    public synchronized void draw(ViewportJOGL vp, GL gl)
    {
        gl.glPushMatrix();
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture_.getTexId());
        setupMatrix(gl);
        
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
}
