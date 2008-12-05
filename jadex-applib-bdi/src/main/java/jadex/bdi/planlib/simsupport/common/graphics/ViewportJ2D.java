package jadex.bdi.planlib.simsupport.common.graphics;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bridge.ILibraryService;

import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

/** This class manages the GUI and all user interaction.
 */
public class ViewportJ2D extends AbstractViewport implements ComponentListener
{
    private Map imageCache_;
    
    /** Action that renders the frame.
     */
    private Runnable renderFrameAction_;
    
    /** Creates a new Viewport.
     *  
     *  @param libService the library service
     */
    public ViewportJ2D(ILibraryService libService)
    {
        libService_ = libService;
        size_ = new Vector2Double(1.0);
        preserveAR_ = true;
        newDrawables_ = Collections.synchronizedList(new LinkedList());
        objectList_ = Collections.synchronizedList(new ArrayList());
        preLayers_ = Collections.synchronizedList(new ArrayList());
        postLayers_ = Collections.synchronizedList(new ArrayList());
        imageCache_ = Collections.synchronizedMap(new HashMap());
        posX_ = 0.0f;
        posY_ = 0.0f;
        paddedSize_ = new Vector2Double(1.0);
        
        canvas_ = new ViewportCanvas();
        canvas_.addComponentListener(this);
        
        renderFrameAction_ = new Runnable()
		{
        	public void run()
			{
				canvas_.repaint();
			};
		};
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
    
    public void refresh()
    {
    	EventQueue.invokeLater(renderFrameAction_);
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
    
    private class ViewportCanvas extends Canvas
    {
    	private BufferedImage backBuffer_;
    	
    	public ViewportCanvas()
    	{
    		backBuffer_ = new BufferedImage(1,
    										1,
    										BufferedImage.TYPE_4BYTE_ABGR_PRE);
    	}
    	
    	public void paint(Graphics gfx)
    	{
    		try
        	{
    			if ((backBuffer_.getWidth() != getWidth()) ||
    				(backBuffer_.getHeight() != getHeight()))
    			{
    				backBuffer_ = new BufferedImage(getWidth(),
													getHeight(),
													BufferedImage.TYPE_4BYTE_ABGR_PRE);
    			}
    				
        		Graphics2D g = (Graphics2D) backBuffer_.getGraphics();
        		g.setColor(java.awt.Color.BLACK);
        		g.fillRect(0, 0, getWidth(), getHeight());
        		setupTransform(g);
        		
        		while (!newDrawables_.isEmpty())
        		{
        			IDrawable d = (IDrawable) newDrawables_.remove(0);
        			d.init(ViewportJ2D.this, g);
        		}
        		
        		synchronized(preLayers_)
                {
                    Iterator it = preLayers_.iterator();
                    while (it.hasNext())
                    {
                        ILayer l = (ILayer) it.next();
                        l.draw(paddedSize_, ViewportJ2D.this, g);
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
        				d.draw(ViewportJ2D.this, g);
        			}
        		}
        		
        		synchronized(postLayers_)
                {
                    Iterator it = postLayers_.iterator();
                    while (it.hasNext())
                    {
                        ILayer l = (ILayer) it.next();
                        l.draw(paddedSize_, ViewportJ2D.this, g);
                    }
                }

        		g.dispose();
        		
        		gfx.drawImage(backBuffer_, 0, 0, null);
        		gfx.dispose();
        	}
        	catch (IllegalStateException e)
        	{
        	}
    	}
    	
    	public void update(Graphics g)
    	{
    		paint(g);
    	}
    	
        private void setupTransform(Graphics2D g)
        {
            g.translate(0.0, backBuffer_.getHeight());
            g.scale((backBuffer_.getWidth() / paddedSize_.getXAsDouble()),
                    -(backBuffer_.getHeight() / paddedSize_.getYAsDouble()));
            g.translate(-posX_, -posY_);
        }
    }
}
