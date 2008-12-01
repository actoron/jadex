package jadex.bdi.planlib.simsupport.common.graphics;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.RotatingTexturedRectangle;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.Timer;

import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bridge.ILibraryService;

/** OpenGL/JOGL-based Viewport.
 *  This viewport attempts to use OpenGL for drawing.
 *  Exceptions/Errors may be thrown if OpenGL cannot be linked, also
 *  be sure to test isValid() afterwards to verify the availability of
 *  necessary extensions.
 */
public class ViewportJOGL extends AbstractViewport implements WindowListener
{
    /** Clamped texture cache
     */
    private Map clampedTextureCache_;
    
    /** Repeating texture cache
     */
    private Map repeatingTextureCache_;
    
    /** Display lists
     */
    private Map displayLists_;
    
    /** True, until the OpenGL context is initialized.
     */
    private volatile boolean uninitialized_;
    
    /** This will be true if the OpenGL context supports all necessary
     *  extensions.
     */
    private volatile boolean valid_;
    
    /** True, if non-power-of-two texture support is available.
     */
    private boolean npot_;
    
    /** Action that renders the frame.
     */
    private Runnable renderFrameAction_;
    
    /** Redraw Timer
     */
    private Timer timer_;
    
    /** Creates a new OpenGL-based viewport.
     *  May throw UnsatisfiedLinkError and RuntimeException if linking
     *  to OpenGL fails.
     *
     *  @param title Title of the window.
     *  @param fps target frames per second or no autmatic refresh if zero
     *  @param libService library service for loading resources.
     */
    public ViewportJOGL(String title, double fps, ILibraryService libService)
    {
    	posX_ = 0.0f;
    	posY_ = 0.0f;
    	libService_ = libService;
        uninitialized_ = true;
        preserveAR_ = true;
        valid_ = true;
        npot_ = false;
        newDrawables_ = Collections.synchronizedList(new LinkedList());
        objectList_ = Collections.synchronizedList(new ArrayList());
        preLayers_ = Collections.synchronizedList(new ArrayList());
        postLayers_ = Collections.synchronizedList(new ArrayList());
        clampedTextureCache_ = Collections.synchronizedMap(new HashMap());
        repeatingTextureCache_ = Collections.synchronizedMap(new HashMap());
        displayLists_ = Collections.synchronizedMap(new HashMap());
        size_ = new Vector2Double(1.0);
        paddedSize_ = new Vector2Double(1.0);
        frame_ = new Frame(title);
        frame_.setLayout(new BorderLayout());
        frame_.setSize(400, 400);
        frame_.setVisible(true);
        frame_.addWindowListener(this);
        
        try
        {
        	JOGLNativeLoader.loadJOGLLibraries();
        	GLCapabilities caps = new GLCapabilities();
        	caps.setDoubleBuffered(true);
        	caps.setHardwareAccelerated(true);
        	canvas_ = new GLCanvas(caps);
        	((GLCanvas) canvas_).addGLEventListener(new GLController());

        	frame_.add(canvas_, BorderLayout.CENTER);
        	frame_.setVisible(true);
        }
        catch (GLException e)
        {
        	close();
        	throw e;
        }
        catch (Error e)
        {
        	close();
        	throw e;
        }
        
        setSize(new Vector2Double(1.0));
        
        renderFrameAction_ = new Runnable()
    		{
    			public void run()
    			{
    				((GLCanvas) ViewportJOGL.this.canvas_).display();
    			}
    		};
        
        while (uninitialized_)
        {
            try
            {
                Thread.currentThread().sleep(100);
            }
            catch (InterruptedException e)
            {
            }
        }
        
        if (fps != 0.0)
        {
        	int delay = (int) (1000/fps);
        	timer_ = new Timer(delay, new ActionListener()
        	{
        		public void actionPerformed(ActionEvent e)
        		{
        			refresh();
        		}
        	});
        	timer_.start();
        }
    }
    
    public void refresh()
    {
    	EventQueue.invokeLater(renderFrameAction_);
    }
    
    /** Closes the viewport.
     */
    public void close()
    {
    	try {
			EventQueue.invokeAndWait(new Runnable()
				{
					public void run()
					{
						frame_.setVisible(false);
				    	frame_.dispose();
					}
				});
		}
    	catch (InterruptedException e)
		{
		}
    	catch (InvocationTargetException e)
		{
		}
    }
    
    /** Verifies the OpenGL context is valid and useable.
     */
    public boolean isValid()
    {
        return valid_;
    }
    
    /** Returns a repeating texture.
    *
    *  @param gl OpenGL interface
    *  @param path resource path of the texture
    *  @return the texture
    */
   public Texture2D getRepeatingTexture(GL gl, String path)
   {
       
       Texture2D texture = (Texture2D) repeatingTextureCache_.get(path);
       if (texture == null)
       {
           texture = loadTexture(gl, path, GL.GL_REPEAT);
           repeatingTextureCache_.put(path, texture);
       }
       
       return texture;
   }
    
    /** Returns a clamped texture.
     *
     *  @param gl OpenGL interface
     *  @param path resource path of the texture
     *  @return the texture
     */
    public Texture2D getClampedTexture(GL gl, String path)
    {
        
        Texture2D texture = (Texture2D) clampedTextureCache_.get(path);
        if (texture == null)
        {
            texture = loadTexture(gl, path, GL.GL_CLAMP_TO_EDGE);
            clampedTextureCache_.put(path, texture);
        }
        
        return texture;
    }
    
    /** Returns a previous generated display list or null if it doesn't exist
     *  
     *  @param listName name of the list
     *  @return previously generated display list
     */
    public Integer getDisplayList(String listName)
    {
    	return (Integer) displayLists_.get(listName);
    }
    
    /** Sets a display list.
     *  
     *  @param listName name of the list
     *  @param list the display list
     */
    public void setDisplayList(String listName, Integer list)
    {
    	displayLists_.put(listName, list);
    }
    
    // Window Events
    
    public void windowActivated(WindowEvent e)
    {
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        frame_.dispose();
    }

    public void windowDeactivated(WindowEvent e)
    {
    }
    
    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowOpened(WindowEvent e)
    {
    }
    
    private void setupMatrix(GL gl)
    { 
        gl.glLoadIdentity();
        gl.glOrtho(0.0, paddedSize_.getXAsDouble(),
        		   0.0, paddedSize_.getYAsDouble(),
        		   -0.5, 0.5);
        gl.glTranslated(-posX_, -posY_, 0.0);
    }
    
    
    
    /** Loads a Texture
     *
     *  @param gl OpenGL interface
     *  @param path texture resource path
     *  @param wrapParam wrap parameter
     */
    private synchronized Texture2D loadTexture(GL gl,
    										   String path,
    										   int wrapMode)
    {
        // Load image
        ClassLoader cl = libService_.getClassLoader();
        
        BufferedImage tmpImage = null;
        try
        {
            tmpImage = ImageIO.read(cl.getResource(path));
            AffineTransform tf = AffineTransform.getScaleInstance(1, -1);
            tf.translate(0, -tmpImage.getHeight());
            AffineTransformOp op = new AffineTransformOp(tf, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            tmpImage = op.filter(tmpImage, null);
        }
        catch (Exception e)
        {
        	System.err.println("Image not found: " + path);
        	throw new RuntimeException("Image not found: " + path);
        }
        
        int width = tmpImage.getWidth();
        int height = tmpImage.getHeight();
        if (!npot_)
        {
            width = (int) Math.pow(2, Math.ceil(Math.log(width) / Math.log(2)));
            height = (int) Math.pow(2, Math.ceil(Math.log(height)/Math.log(2)));
        }
        double maxX = (double) (tmpImage.getWidth()-1) / width;
        double maxY = (double) (tmpImage.getHeight()-1) / height;
        
        // Convert image data
        ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                										new int[] {8, 8, 8, 8},
                										true,
                										false,
                										ComponentColorModel.TRANSLUCENT,
                										DataBuffer.TYPE_BYTE);

        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
				   											   width,
				   											   height,
				   											   4,
				   											   null);
        BufferedImage image = new BufferedImage(colorModel,
        										raster,
        										false,
                                                new Hashtable());
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,width,height);
        g.setComposite(AlphaComposite.Src);
        int ow = tmpImage.getWidth();
        int oh = tmpImage.getHeight();
        g.drawImage(tmpImage, 0, 0, null);
        
        // Fill up padded textures, may be wrong approach for
        // clamped textures.
        g.drawImage(tmpImage, ow, 0, null);
        g.drawImage(tmpImage, 0, oh, null);
        g.drawImage(tmpImage, ow, oh, null);
        
        g.dispose();
        tmpImage = null;
        
        byte[] imgData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        ByteBuffer buffer = ByteBuffer.allocateDirect(imgData.length);
        buffer.order(ByteOrder.nativeOrder());
        buffer.put(imgData, 0, imgData.length);
        buffer.flip();
        
        // Prepare texture
        int[] texId = new int[1];
        gl.glGenTextures(1, texId, 0);
        Texture2D texture = new Texture2D(texId[0], (float)maxX, (float)maxY);
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texId[0]);
        
        gl.glTexParameteri(GL.GL_TEXTURE_2D,
                           GL.GL_TEXTURE_WRAP_S,
                           wrapMode);
        gl.glTexParameteri(GL.GL_TEXTURE_2D,
                           GL.GL_TEXTURE_WRAP_T,
                           wrapMode);
        GLU glu = new GLU();
        glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D,
        				      GL.GL_COMPRESSED_RGBA,
        				      image.getWidth(),
        				      image.getHeight(),
        				      GL.GL_RGBA,
        				      GL.GL_UNSIGNED_BYTE,
        				      buffer);
        gl.glDisable(GL.GL_TEXTURE_2D);
        
        
        
        return texture;
    }
    
    private class GLController implements GLEventListener
    {
        public void display(GLAutoDrawable drawable)
        {
            GL gl = drawable.getGL();
            
            setupMatrix(gl);
            
            while (!newDrawables_.isEmpty())
            {
            	IDrawable d = (IDrawable) newDrawables_.remove(0);
            	d.init(ViewportJOGL.this, gl);
            }
            
            gl.glClear(gl.GL_COLOR_BUFFER_BIT);
            
            synchronized(preLayers_)
            {
                Iterator it = preLayers_.iterator();
                while (it.hasNext())
                {
                    ILayer l = (ILayer) it.next();
                    l.draw(paddedSize_, ViewportJOGL.this, gl);
                }
            }
            
            synchronized(objectList_)
            {
                Iterator it = objectList_.iterator();
                while (it.hasNext())
                {
                	Object[] o = (Object[]) it.next();
    				IVector2 pos = (IVector2) o[0];
    				IVector2 vel = (IVector2) o[1];
    				IDrawable d = (IDrawable) o[2];
    				d.setPosition(pos);
    				if (vel != null)
    				{
    					d.setVelocity(vel);
    				}
    				else
    				{
    					d.setVelocity(Vector2Double.ZERO);
    				}
                    d.draw(ViewportJOGL.this, gl);
                }
            }
            
            synchronized(postLayers_)
            {
                Iterator it = postLayers_.iterator();
                while (it.hasNext())
                {
                    ILayer l = (ILayer) it.next();
                    l.draw(paddedSize_, ViewportJOGL.this, gl);
                }
            }
        }
        
        public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
        {
        }
        
        public void init(GLAutoDrawable drawable)
        {
            GL gl = drawable.getGL();
            gl.glViewport(0, 0, canvas_.getWidth(), canvas_.getHeight());
            gl.glMatrixMode(GL.GL_MODELVIEW);
            
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            
            setupMatrix(gl);
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            
            // Check for OpenGL version or extensions if needed
            if (gl.isExtensionAvailable("GL_VERSION_2_0") ||
                gl.isExtensionAvailable("GL_VERSION_2_1") ||
                gl.isExtensionAvailable("GL_ARB_texture_non_power_of_two"))
            {
                npot_ = true;
            }
            
            /**if (!(gl.isFunctionAvailable("glGenBuffers")))
            {
                valid_ = false;
            }
            else
            {
                
            }*/
            
            uninitialized_ = false;
        }
        
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
        {
            GL gl = drawable.getGL();
            setSize(size_);
            setupMatrix(gl);
        }
        
    }
}
