package jadex.extension.envsupport.observer.graphics;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.perspective.IPerspective;

import java.awt.Canvas;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class AbstractViewport3d implements IViewport3d
{
	/** Canvas for graphical output. */
	protected Canvas			canvas_;

	/** Size of the viewport without padding. */
	//TODO: should be Vector3Double someday...
	protected Vector2Double		size_;
	
	/** Flag aspect ratio preservation. */
	protected boolean			preserveAR_;
	
	/** Maximum displayable area */
	protected IVector2		areaSize_;

	/** Real size of the viewport including padding. */
	protected Vector2Double		paddedSize_;

	/** Visuals that are handled as static´s */
	protected ArrayList<DrawableCombiner3d> _staticvisuals = new ArrayList<DrawableCombiner3d>();
	
	/** Known drawable Objects. */
	protected Set<Object>				drawObjects_;
	
	/** Virtual Viewport position. */
	protected IVector2			position_;
	
	/** Pixel-corrected viewport position. */
	protected IVector2			pixPosition_;

	/** List of objects that should be drawn. */
	protected List<Object>				objectList_;
	
	/** IPropertyObject holding properties for layers. */
	protected IPerspective perspective;
	
	/** Flag to indicate that rendering is in progress. */
	protected volatile boolean	rendering;

	/** The listeners of the viewport. */
	private Set<Object>					listeners_;
	
	/** The zoom limit */
	private double				zoomLimit_;
	
	
	public AbstractViewport3d(IPerspective perspective)
	{
		rendering = false;
		this.perspective = perspective;
		size_ = new Vector2Double(1.0);
		position_ = Vector2Double.ZERO.copy();
		preserveAR_ = true;
		areaSize_ = new Vector2Double(1.0);
		paddedSize_ = new Vector2Double(1.0);
		drawObjects_ = Collections.synchronizedSet(new HashSet<Object>());
		objectList_ = Collections.synchronizedList(new ArrayList<Object>());
		listeners_ = Collections.synchronizedSet(new HashSet<Object>());
		zoomLimit_ = 20.0;
	}
	
	/**
	 * Sets the current objects to draw.
	 * 
	 * @param objectList objects that should be drawn
	 * 
	 */
	//TODO: no Array of Objects someday
	public void setObjectList(List<Object[]> objectList)
	{
		synchronized(objectList_)
		{
			objectList_.clear();
			objectList_.addAll(objectList);
		}
	}
	
	/**
	 * Sets the current staticobjects to draw.
	 * 
	 * @param staticvisuals staticobjects that should be drawn
	 */
	public void setStaticList(Collection<DrawableCombiner3d> staticvisuals)
	{
		synchronized(_staticvisuals)
		{
			_staticvisuals.clear();
			_staticvisuals.addAll(staticvisuals);
		}
	}
	

	/**
	 * Returns the canvas that is used for displaying the objects.
	 */
	public Canvas getCanvas()
	{
		return canvas_;
	}
	
	/**
	 * Gets the size of the display area.
	 * 
	 * @return size of the display area, may be padded to preserve aspect
	 *        ratio
	 */
	public IVector2 getSize()
	{
		return size_;
	}


	
	/**
	 * Gets the maximum displayable size.
	 * 
	 * @return maximum area size.
	 */
	public IVector2 getAreaSize()
	{
		return areaSize_;
	}
	
	/**
	 * Sets the maximum displayable size.
	 * 
	 * @param areaSize maximum area size.
	 */
	public void setAreaSize(final IVector2 areaSize)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				areaSize_ = areaSize;
			}
		});
	}
	
	/**
	 * Returns the padded size
	 * @return padded size
	 */
	public IVector2 getPaddedSize()
	{
		return paddedSize_;
	}
	
	/**
	 * Returns the size of the canvas as a vector.
	 * @return size of the canvas in pixel
	 */
	public IVector2 getCanvasSize()
	{
		return new Vector2Double(canvas_.getWidth(), canvas_.getHeight());
	}
	
	/**
	 * Refreshes the size of the canvas.
	 */
	public void refreshCanvasSize()
	{
	}
	
	/**
	 * Gets the position of the viewport.
	 */
	public IVector2 getPosition()
	{
		return position_.copy();
	}
	
	/**
	 * Returns the size of a pixel.
	 * @retun size of a pixel
	 */
	public IVector2 getPixelSize()
	{
		Canvas canvas = canvas_;
		if (canvas == null)
			return Vector2Double.ZERO;
		return paddedSize_.copy().divide(new Vector2Double(canvas.getWidth(), canvas.getHeight()));
	}

	/**
	 * Sets the position of the viewport.
	 */
	public void setPosition(IVector2 pos)
	{
		position_ = pos;
		IVector2 pixSize = getPixelSize();
		pixPosition_ = position_.copy().divide(pixSize);
		pixPosition_ = (new Vector2Double(new Vector2Int(pixPosition_))).multiply(pixSize);
	}
	
	/**
	 * Sets the maximum zoom.
	 * @param zoomlimit the zoom limit
	 */
	public void setZoomLimit(double zoomlimit)
	{
		zoomLimit_ = zoomlimit;
	}

	/**
	 * Checks if this IViewport is showing on screen.
	 * 
	 * @return true if the IViewport is showing, false otherwise
	 */
	public boolean isShowing()
	{
		return canvas_.isShowing();
	}

	/**
	 * Adds a IViewportListener
	 * 
	 * @param listener new listener
	 */
	public void addViewportListener(IViewportListener listener)
	{
		listeners_.add(listener);
	}

	/**
	 * Removes a IViewportListener
	 * 
	 * @param listener the listener
	 */
	public void removeViewportListener(IViewportListener listener)
	{
		listeners_.remove(listener);
	}
	
	/**
	 *  Get the perspective.
	 *  @return The perspective.
	 */
	public IPerspective getPerspective()
	{
		return perspective;
	}
	
	
	
	
}
