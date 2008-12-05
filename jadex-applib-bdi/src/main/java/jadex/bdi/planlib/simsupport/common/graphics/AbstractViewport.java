package jadex.bdi.planlib.simsupport.common.graphics;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bridge.ILibraryService;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    
    /** Flag aspect ratio preservation.
     */
    protected boolean preserveAR_;
    
    /** Size of the viewport without padding.
     */
    protected IVector2 size_;
    
    /** Real size of the viewport including padding.
     */
    protected IVector2 paddedSize_;
	
	/** Newly registered drawables.
     */
    protected List newDrawables_;
    
    /** List of objects that should be drawn.
     */
    protected List objectList_;
    
    /** Order in which the objects are drawn.
     */
    protected Comparator drawOrder_;
    
    /** Layers applied before drawable rendering
     */
    protected List preLayers_;
    
    /** Layers applied after drawable rendering
     */
    protected List postLayers_;
    
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
    
    /** Registers an IDrawable to be used in the object list.
     *  
     *  @param d the drawable
     */
    public void registerDrawable(IDrawable d)
    {
        newDrawables_.add(d);
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
    	preLayers_ = new ArrayList(layers);
    }
    
    /** Sets the post-layers for the viewport.
     *  
     *  @param layers the post-layers
     */
    public void setPostLayers(List layers)
    {
    	postLayers_ = new ArrayList(layers);
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
    
    /** Checks if this IViewport is showing on screen.
     *  
     *  @return true if the IViewport is showing, false otherwise
     */
    public boolean isShowing()
    {
    	return canvas_.isShowing();
    }

}
