package jadex.bdi.planlib.simsupport.common.graphics;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bridge.ILibraryService;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFrame;

public abstract class AbstractViewport implements IViewport
{
	/** Canvas for graphical output.
	 */
	protected Canvas canvas_;
	
	/** Library service for loading resources.
     */
    protected ILibraryService libService_;
	
	/** X-Coordinate of the viewport position.
	 */
    protected float posX_;
    
    /** Y-Coordinate of the viewport position.
	 */
    protected float posY_;
    
    /** Object shift x-coordinate.
     */
    protected float objShiftX_;
    
    /** Object shift y-coordinate.
     */
    protected float objShiftY_;
    
    /** Flag aspect ratio preservation.
     */
    protected boolean preserveAR_;
    
    /** Size of the viewport without padding.
     */
    protected IVector2 size_;
    
    /** Real size of the viewport including padding.
     */
    protected IVector2 paddedSize_;
	
	/** Newly registered drawableCombiners.
     */
    protected List newDrawableCombiners_;
    
    /** Registered object layers
     */
    protected SortedSet objectLayers_;
    
    /** List of objects that should be drawn.
     */
    protected List objectList_;
    
    /** Layers applied before drawable rendering
     */
    protected List preLayers_;
    
    /** Layers applied after drawable rendering
     */
    protected List postLayers_;
    
    /** The listeners of the viewport
     */
    private Set listeners_;
    
    public AbstractViewport()
    {
        posX_ = 0.0f;
        posY_ = 0.0f;
        preserveAR_ = true;
        size_ = new Vector2Double(1.0);
        paddedSize_ = size_.copy();
        newDrawableCombiners_ = Collections.synchronizedList(new LinkedList());
        objectLayers_ = Collections.synchronizedSortedSet(new TreeSet());
        objectList_ = Collections.synchronizedList(new ArrayList());
        preLayers_ = Collections.synchronizedList(new ArrayList());
        postLayers_ = Collections.synchronizedList(new ArrayList());
        listeners_ = Collections.synchronizedSet(new HashSet());
	}
    
    /** Sets the current objects to draw.
     * 
     *  @param objectList objects that should be drawn
     */
    public void setObjectList(List objectList)
    {
    	synchronized (objectList_)
    	{
    		objectList_.clear();
    		objectList_.addAll(objectList);
    	}
    }
    
    /** Registers a DrawableCombiner to be used in the object list.
     *  
     *  @param d the DrawableCombiner
     */
    public void registerDrawableCombiner(DrawableCombiner d)
    {
    	objectLayers_.addAll(d.getLayers());
        newDrawableCombiners_.add(d);
    }
    
    /** Returns the canvas that is used for displaying the objects.
     */
    public Canvas getCanvas()
    {
    	return canvas_;
    }
    
    /** Sets the pre-layers for the viewport.
     *  
     *  @param layers the pre-layers
     */
    public void setPreLayers(List layers)
    {
    	if (layers != null)
    	{
    		preLayers_ = new ArrayList(layers);
    	}
    	else
    	{
    		preLayers_ = new ArrayList();
    	}
    }
    
    /** Sets the post-layers for the viewport.
     *  
     *  @param layers the post-layers
     */
    public void setPostLayers(List layers)
    {
    	if (layers != null)
    	{
    		postLayers_ = new ArrayList(layers);
    	}
    	else
    	{
    		postLayers_ = new ArrayList();
    	}
    }
    
    /** Sets the size of the display area.
     *  
     *  @param size size of the display area, may be padded to preserve aspect ratio
     */
    public void setSize(IVector2 size)
    {
    	size_ = size;
    	
    	double width = 1.0;
		double height = 1.0;
    	if (preserveAR_)
    	{
    		double sizeAR = size.getXAsDouble() / size.getYAsDouble();
    		double windowAR = (double) canvas_.getWidth() / (double) canvas_.getHeight();
    		
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
    		
    		posX_ = (float) -((width - size.getXAsDouble()) / 2.0);
    		posY_ = (float) -((height - size.getYAsDouble()) / 2.0);
    	}
    	else
    	{
    		width = size.getXAsDouble();
    		height = size.getYAsDouble();
    	}
    	
    	paddedSize_ = new Vector2Double(width, height);
    }
    
    /** Sets the position of the bottom left corner of
     *  the viewport.
     */
    public void setPosition(IVector2 pos)
    {
        posX_ = pos.getXAsFloat();
        posY_ = pos.getYAsFloat();
    }
    
    public void setPreserveAspectRation(boolean preserveAR)
    {
    	preserveAR_ = preserveAR;
    	setSize(size_);
    }
    
    /** Sets the shift of all objects.
     */
    public void setObjectShift(IVector2 objectShift)
    {
    	objShiftX_ = objectShift.getXAsFloat();
    	objShiftY_ = objectShift.getYAsFloat();
    }
    
    /** Checks if this IViewport is showing on screen.
     *  
     *  @return true if the IViewport is showing, false otherwise
     */
    public boolean isShowing()
    {
    	return canvas_.isShowing();
    }
    
    /** Adds a IViewportListener
     *  
     *  @param listener new listener
     */
    public void addViewportListener(IViewportListener listener)
    {
    	listeners_.add(listener);
    }
    
    /** Removes a IViewportListener
     *  
     *  @param listener the listener
     */
    public void removeViewportListener(IViewportListener listener)
    {
    	listeners_.remove(listener);
    }
    
    /** Fires a left mouse click event
     * 
     *  @param position the clicked position
     */
    private void fireLeftMouseClickEvent(IVector2 position)
    {
    	synchronized (listeners_)
    	{
    		for (Iterator it = listeners_.iterator(); it.hasNext(); )
    		{
    			IViewportListener listener = (IViewportListener) it.next();
    			listener.leftClicked(position.copy());
    		}
    	}
    }
    
    /** Fires a right mouse click event
     * 
     *  @param position the clicked position
     */
    private void fireRightMouseClickEvent(IVector2 position)
    {
    	synchronized (listeners_)
    	{
    		for (Iterator it = listeners_.iterator(); it.hasNext(); )
    		{
    			IViewportListener listener = (IViewportListener) it.next();
    			listener.rightClicked(position.copy());
    		}
    	}
    }
    
    protected class MouseController implements MouseListener
    {
    	public void mouseClicked(MouseEvent e)
    	{
    	}
    	
    	public void mouseEntered(MouseEvent e)
    	{
    	}
    	
    	public void mouseExited(MouseEvent e)
    	{
    	}
    	
    	public void mousePressed(MouseEvent e)
    	{
    		if (e.getButton() == MouseEvent.BUTTON1)
    		{
    			Point p = e.getPoint();
    			double xFac = (paddedSize_.getXAsDouble()) / canvas_.getWidth();
    	        double yFac = (paddedSize_.getYAsDouble()) / canvas_.getHeight();
    	        IVector2 position = new Vector2Double((xFac * p.x) + posX_,
    	        									  (yFac * (canvas_.getHeight() - p.y)) + posY_);
    	        
    	        if (e.getButton() == MouseEvent.BUTTON1)
    	        {
    	        	fireLeftMouseClickEvent(position);
    	        }
    	        else if (e.getButton() == MouseEvent.BUTTON2)
    	        {
    	        	fireRightMouseClickEvent(position);
    	        }
    		}
    	}
    	
    	public void mouseReleased(MouseEvent e)
    	{
    	}
    }
}
