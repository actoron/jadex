package jadex.bdi.planlib.simsupport.common.graphics;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.RotatingTexturedRectangle;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bridge.ILibraryService;

import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Timer;

/** This class manages the GUI and all user interaction.
 */
public class ViewportJ2D extends Frame implements IViewport,
									 			  WindowListener,
									 			  ComponentListener
{
    //constants
    public final static int DOUBLE_BUFFER_STRATEGY = 2;
    public final static int TRIPLE_BUFFER_STRATEGY = 3;
    
    private final static String DEFAULT_TITLE = "Viewport";
    private final static int    DEFAULT_BUFFER_STRATEGY=DOUBLE_BUFFER_STRATEGY;
    
    /** Library service for loading resources.
     */
    private ILibraryService libService_;
    
    //configuration
    private double directionLineScale_;
    private int    dotRadius_;
    private int    dotShift_;
    private int    bufferStrategy_;
    
    private Canvas canvas_;
    private BufferStrategy strategy_;
    private RenderingHints rh_;
    
    private double posX_;
    private double posY_;
    
    /** Size of the viewport without padding.
     */
    private IVector2 size_;
    
    /** Real size of the viewport including padding.
     */
    private IVector2 paddedSize_;
    
    /** Flag aspect ratio preservation.
     */
    private boolean preserveAR_;
    
    private Map imageCache_;
    
    /** Newly added drawables.
     */
    private List newDrawables_;
    
    /** Current drawable buffer used for rendering
     */
    private List drawables_;
    
    /** Layers applied before drawable rendering
     */
    private List preLayers_;
    
    /** Layers applied after drawable rendering
     */
    private List postLayers_;
    
    /** Action that renders the frame.
     */
    private Runnable renderFrameAction_;
    
    /** Redraw Timer
     */
    private Timer timer_;
    
    /** Creates a new Viewport.
     * 
     *  @param title title of the viewport window
     *  @param fps target frames per second 
     */
    public ViewportJ2D(String title, double fps, ILibraryService libService)
    {
        this(title, DEFAULT_BUFFER_STRATEGY, fps, libService);
    }
    
    /** Creates a new Viewport.
     *  
     *  @param title title of the viewport window
     *  @param bufferStrategy buffer strategy to use
     *  @param fps target frames per second or no autmatic refresh if zero
     */
    public ViewportJ2D(String title, int bufferStrategy, double fps, ILibraryService libService)
    {
        super(title);
        libService_ = libService;
        size_ = new Vector2Double(1.0);
        preserveAR_ = true;
        drawables_ = Collections.synchronizedList(new ArrayList());
        newDrawables_ = Collections.synchronizedList(new LinkedList());
        preLayers_ = Collections.synchronizedList(new ArrayList());
        postLayers_ = Collections.synchronizedList(new ArrayList());
        imageCache_ = Collections.synchronizedMap(new HashMap());
        bufferStrategy_ = bufferStrategy;
        rh_ = new RenderingHints(RenderingHints.KEY_RENDERING,
                                 RenderingHints.VALUE_RENDER_DEFAULT);
        posX_ = 0.0;
        posY_ = 0.0;
        paddedSize_ = new Vector2Double(1.0);
        
        canvas_ = new Canvas();
        try
        {
            EventQueue.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                    	setResizable(true);
                        add(canvas_);
                        addWindowListener(ViewportJ2D.this);
                        canvas_.addComponentListener(ViewportJ2D.this);
                        setIgnoreRepaint(true);
                        canvas_.setIgnoreRepaint(true);
                        setBackground(null);
                        canvas_.setBackground(null);
                        pack();
                        setSize(300, 300);
                        setVisible(true);
                        canvas_.createBufferStrategy(bufferStrategy_);
                        strategy_ = canvas_.getBufferStrategy();
                    }
                });
        }
        catch (InterruptedException e)
        {
        }
        catch (InvocationTargetException e)
        {
        }
        
        renderFrameAction_ = new Runnable()
		{
        	public void run()
			{
				renderFrame();
			};
		};
        
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
    
    /** Sets the position of the bottom left corner of
     *  the viewport.
     */
    public void setPosition(IVector2 pos)
    {
        posX_ = pos.getXAsDouble();
        posY_ = pos.getYAsDouble();
    }
    
    public void setSize(IVector2 size)
    {
    	size_ = size;
    	
    	double width = 1.0;
		double height = 1.0;
    	if (preserveAR_)
    	{
    		double sizeAR = size.getXAsDouble() / size.getYAsDouble();
    		double windowAR = (double) getWidth() / (double) getHeight();
    		
    		if (sizeAR > windowAR)
    		{
    			width = size.getXAsDouble();
    			height = size.getYAsDouble() * sizeAR / windowAR;
    		}
    		else
    		{
    			width = size.getXAsDouble() / sizeAR * windowAR;
    			height = size.getYAsDouble();
    		}
    	}
    	else
    	{
    		width = size.getXAsDouble();
    		height = size.getYAsDouble();
    	}
    	
    	paddedSize_ = new Vector2Double(width, height);
    }
    
    public void setPreserveAspectRation(boolean preserveAR)
    {
    	preserveAR_ = preserveAR;
    	setSize(size_);
    }
    
    /** Returns an image for texturing
     *
     *  @param path resource path of the image
     */
    public BufferedImage getImage(String path)
    {
        BufferedImage image = (BufferedImage) imageCache_.get(path);
        
        if (image == null)
        {
            image = loadImage(path);
            imageCache_.put(path, image);
        }
        
        return image;
    }
    
    /** Adds an IDrawable to the scene.
     *  
     *  @param d the drawable
     */
    public void addDrawable(IDrawable d)
    {
        newDrawables_.add(d);
    }
    
    /** Removes an IDrawable from the scene.
     *  
     *  @param d the drawable
     */
    public void removeDrawable(IDrawable d)
    {
        drawables_.remove(d);
    }
    
    public void setPreLayers(List layers)
    {
    	preLayers_ = new ArrayList(layers);
    }
    
    public void setPostLayers(List layers)
    {
    	postLayers_ = new ArrayList(layers);
    }
    
    public void refresh()
    {
    	EventQueue.invokeLater(renderFrameAction_);
    }
    
    /** Commits a frame to the screen
     */
    private void renderFrame()
    {
    	try
    	{
    		Graphics2D g = (Graphics2D) strategy_.getDrawGraphics();
    		g.setColor(java.awt.Color.BLACK);
    		g.fillRect(0, 0, canvas_.getWidth(), canvas_.getHeight());
    		setupTransform(g);
    		
    		while (!newDrawables_.isEmpty())
    		{
    			IDrawable d = (IDrawable) newDrawables_.remove(0);
    			d.init(this, g);
    			drawables_.add(d);
    		}
    		
    		synchronized(preLayers_)
            {
                Iterator it = preLayers_.iterator();
                while (it.hasNext())
                {
                    ILayer l = (ILayer) it.next();
                    //TODO: change back to padded size
                    l.draw(size_, this, g);
                }
            }
    		
    		synchronized(drawables_)
    		{
    			Iterator it = drawables_.iterator();
    			while (it.hasNext())
    			{
    				IDrawable d = (IDrawable) it.next();
    				d.draw(this, g);
    			}
    		}
    		
    		synchronized(postLayers_)
            {
                Iterator it = postLayers_.iterator();
                while (it.hasNext())
                {
                    ILayer l = (ILayer) it.next();
                    l.draw(paddedSize_, this, g);
                }
            }

    		g.dispose();
    		strategy_.show();
    	}
    	catch (IllegalStateException e)
    	{
    	}
    }
    
    public void close()
    {
    	this.dispose();
    }
    
    private void setupTransform(Graphics2D g)
    {
        g.translate(0.0, canvas_.getHeight());
        g.scale((canvas_.getWidth() / paddedSize_.getXAsDouble()),
                -(canvas_.getHeight() / paddedSize_.getYAsDouble()));
        g.translate(-posX_, -posY_);
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
        this.dispose();
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
    
    // Component events
    public void componentHidden(ComponentEvent e)
    {
    }
    
    public void componentMoved(ComponentEvent e)
    {
    }
    
    public void componentResized(ComponentEvent e)
    {
    	setSize(size_);
    }
    
    public void componentShown(ComponentEvent e)
    {
    }
    
    /** Loads an image.
     *
     *  @param path resource path of the image
     */
    private BufferedImage loadImage(String path)
    {
        ClassLoader cl = libService_.getClassLoader();
        
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(cl.getResource(path));
            AffineTransform tf = AffineTransform.getScaleInstance(1, -1);
            tf.translate(0, -image.getHeight());
            AffineTransformOp op = new AffineTransformOp(tf, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = op.filter(image, null);
        }
        catch (Exception e)
        {
        }
        
        return image;
    }
}
