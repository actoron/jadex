package jadex.extension.envsupport.observer.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.MouseInputAdapter;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.perspective.IPerspective;


public abstract class AbstractViewport implements IViewport
{
	/** The Space Controller */
	protected ISpaceController spacecontroller;
	
	/** Axis inversion flag */
	protected IVector2			inversionFlag_;

	/** Canvas for graphical output. */
	protected Canvas			canvas_;

	/** The background color. */
	protected Color bgColor_;

	/** Virtual Viewport position. */
	protected IVector2			position_;
	
	/** Pixel-corrected viewport position. */
	protected IVector2			pixPosition_;

	/** Object shift x-coordinate. */
	protected float				objShiftX_;

	/** Object shift y-coordinate. */
	protected float				objShiftY_;

	/** Flag aspect ratio preservation. */
	protected boolean			preserveAR_;

	/** Size of the viewport without padding. */
	protected Vector2Double		size_;
	
	/** Maximum displayable area */
	protected Vector2Double		areaSize_;

	/** Real size of the viewport including padding. */
	protected Vector2Double		paddedSize_;

	/** Known drawable Objects. */
	protected Set				drawObjects_;

	/** Registered object layers. */
	protected SortedSet			objectLayers_;

	/** List of objects that should be drawn. */
	protected List				objectList_;

	/** Layers applied before drawable rendering */
	protected Layer[]				preLayers_;

	/** Layers applied after drawable rendering */
	protected Layer[]				postLayers_;
	
	/** IPropertyObject holding properties for layers. */
	protected IPerspective perspective;
	
	/** Flag to indicate that rendering is in progress. */
	protected volatile boolean	rendering;

	/** The listeners of the viewport. */
	private Set					listeners_;
	
	/** The zoom limit */
	private double				zoomLimit_;

	public AbstractViewport(IPerspective perspective)
	{
		rendering = false;
		this.perspective = perspective;
		bgColor_ = Color.BLACK;
		inversionFlag_ = new Vector2Int(0);
		position_ = Vector2Double.ZERO.copy();
		preserveAR_ = true;
		size_ = new Vector2Double(1.0);
		areaSize_ = new Vector2Double(1.0);
		paddedSize_ = new Vector2Double(1.0);
		drawObjects_ = Collections.synchronizedSet(new HashSet());
		objectLayers_ = Collections.synchronizedSortedSet(new TreeSet());
		objectList_ = Collections.synchronizedList(new ArrayList());
		preLayers_ = new Layer[0];
		postLayers_ = new Layer[0];
		listeners_ = Collections.synchronizedSet(new HashSet());
		zoomLimit_ = 20.0;
	}
	
	/**
	 * Sets the background color.
	 * @param bgColor the background color
	 */
	public void setBackground(Color bgColor)
	{
		bgColor_ = bgColor;
	}

	/**
	 * Sets the current objects to draw.
	 * 
	 * @param objectList objects that should be drawn
	 */
	public void setObjectList(List objectList)
	{
		synchronized(objectList_)
		{
			objectList_.clear();
			objectList_.addAll(objectList);
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
	 * Sets the pre-layers for the viewport.
	 * 
	 * @param layers the pre-layers
	 */
	public void setPreLayers(Layer[] layers)
	{
		// Synchronization necessary?
//		synchronized(preLayers_)
//		{
			if(layers != null)
			{
				preLayers_ = layers.clone();
			}
			else
			{
				preLayers_ = new Layer[0];
			}
//		}
	}

	/**
	 * Sets the post-layers for the viewport.
	 * 
	 * @param layers the post-layers
	 */
	public void setPostLayers(Layer[] layers)
	{
//		synchronized(postLayers_)
//		{
			if(layers != null)
			{
				postLayers_ = layers.clone();
			}
			else
			{
				postLayers_ = new Layer[0];
			}
//		}
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
	 * Sets the size of the display area.
	 * 
	 * @param size size of the display area, may be padded to preserve aspect
	 *        ratio
	 */
	public void setSize(IVector2 size)
	{
		Canvas canvas = canvas_;
		if (canvas == null)
			return;
		
		if (areaSize_.copy().divide(size).getMean().getAsDouble() > zoomLimit_)
			return;
		size_ = new Vector2Double(size);

		double width = 1.0;
		double height = 1.0;
		if(preserveAR_)
		{
			double sizeAR = size.getXAsDouble() / size.getYAsDouble();
			double windowAR = (double)canvas.getWidth()
					/ (double)canvas.getHeight();

			if(sizeAR > windowAR)
			{
				width = size.getXAsDouble();
				//height = size.getXAsDouble() / windowAR;
				double pixX = width / canvas.getWidth();
				height = canvas.getHeight() * pixX;
			}
			else
			{
				//width = size.getYAsDouble() * windowAR;
				height = size.getYAsDouble();
				double pixY = height / canvas.getHeight();
				width = canvas.getWidth() * pixY;
			}
		}
		else
		{
			width = size.getXAsDouble();
			height = size.getYAsDouble();
		}

		paddedSize_ = new Vector2Double(width, height);
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
				areaSize_ = new Vector2Double(areaSize);
				setSize(areaSize.copy());

				if (preserveAR_)
				{
					setPosition(paddedSize_.copy().subtract(areaSize_).multiply(0.5).negate());
				}
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
	 * Returns the clipping box.
	 * @return clipping box
	 */
	public Rectangle getClippingBox()
	{
		Rectangle box = new Rectangle();
		IVector2 pixSize = getPixelSize();
		IVector2 boxStart = pixPosition_.copy().divide(pixSize).negate();
		box.x = (int)Math.round(boxStart.getXAsDouble());
		box.y = (int)Math.round(boxStart.getYAsDouble());
		IVector2 boxSize = areaSize_.copy().divide(pixSize);
		box.width = (int) Math.ceil(boxSize.getXAsDouble());
		box.height = (int) Math.ceil(boxSize.getYAsDouble());
		
		return box;
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
	 * Sets the position of the viewport.
	 */
	public void setPosition(IVector2 pos)
	{
		position_ = pos;
		IVector2 pixSize = getPixelSize();
		pixPosition_ = position_.copy().divide(pixSize);
		pixPosition_ = (new Vector2Double(new Vector2Int(pixPosition_))).multiply(pixSize);
	}

	public void setPreserveAspectRation(boolean preserveAR)
	{
		preserveAR_ = preserveAR;
		setSize(size_);
	}

	/**
	 * Returns true if the x-axis is inverted (right-left instead of
	 * left-right).
	 * 
	 * @return true, if the x-axis is inverted
	 */
	public boolean getInvertX()
	{
		return inversionFlag_.getXAsInteger() > 0;
	}

	/**
	 * Returns true if the y-axis is inverted (top-down instead of bottom-up).
	 * 
	 * @return true, if the y-axis is inverted
	 */
	public boolean getInvertY()
	{
		return inversionFlag_.getYAsInteger() > 0;
	}

	/**
	 * If set to true, inverts the x-axis (right-left instead of left-right).
	 * 
	 * @param b if true, inverts the x-axis
	 */
	public void setInvertX(boolean b)
	{
		if(b)
		{
			inversionFlag_ = new Vector2Int(1, inversionFlag_.getYAsInteger());
		}
		else
		{
			inversionFlag_ = new Vector2Int(0, inversionFlag_.getYAsInteger());
		}
	}

	/**
	 * If set to true, inverts the y-axis (top-down instead of bottom-up).
	 * 
	 * @param b if true, inverts the y-axis
	 */
	public void setInvertY(boolean b)
	{
		if(b)
		{
			inversionFlag_ = new Vector2Int(inversionFlag_.getXAsInteger(), 1);
		}
		else
		{
			inversionFlag_ = new Vector2Int(inversionFlag_.getXAsInteger(), 0);
		}
	}
	
	/**
	 * Gets the shift of all objects.
	 */
	public IVector2 getObjectShift()
	{
		return new Vector2Double(objShiftX_, objShiftY_);
	}

	/**
	 * Sets the shift of all objects.
	 */
	public void setObjectShift(IVector2 objectShift)
	{
		objShiftX_ = objectShift.getXAsFloat();
		objShiftY_ = objectShift.getYAsFloat();
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

	/**
	 * Fires a left mouse click event
	 * 
	 * @param position the clicked position
	 */
	private void fireLeftMouseClickEvent(IVector2 position)
	{
		synchronized(listeners_)
		{
			for(Iterator it = listeners_.iterator(); it.hasNext();)
			{
				IViewportListener listener = (IViewportListener)it.next();
				listener.leftClicked(position.copy());
			}
		}
	}

	/**
	 * Converts pixel coordinates into world coordinates
	 * 
	 * @param pixelX pixel x-coordinate
	 * @param pixelY pixel y-coordinate
	 * @return world coordinates
	 */
	public IVector2 getWorldCoordinates(int pixelX, int pixelY)
	{
		if(getInvertX())
		{
			pixelX = canvas_.getWidth() - pixelX;
		}

		if(getInvertY())
		{
			pixelY = canvas_.getHeight() - pixelY;
		}

		double xFac = (paddedSize_.getXAsDouble()) / canvas_.getWidth();
		double yFac = (paddedSize_.getYAsDouble()) / canvas_.getHeight();
		IVector2 position = new Vector2Double((xFac * pixelX) + position_.getXAsDouble(),
				(yFac * (canvas_.getHeight() - pixelY)) + position_.getYAsDouble());

		return position;
	}

	protected class MouseController extends MouseInputAdapter implements MouseWheelListener
	{
		private IVector2 lastDragPos;
		
		public MouseController()
		{
			lastDragPos = null;
		}
		
		public void mousePressed(MouseEvent e)
		{
			if(e.getButton() == MouseEvent.BUTTON1)
			{
				IVector2 position = getWorldCoordinates(e.getX(), e.getY());
				fireLeftMouseClickEvent(position);
			}
			else if(e.getButton() == MouseEvent.BUTTON3)
			{
				if (e.getClickCount() == 1)
				{
					lastDragPos = (new Vector2Double(e.getX(), e.getY())).multiply(getPixelSize());
				}
				else
				{
					setAreaSize(areaSize_);
				}
			}
		}
		
		public void mouseWheelMoved(MouseWheelEvent e)
		{
			IVector2 oldMousePos = getWorldCoordinates(e.getX(), e.getY());
			IVector2 zoomShift = size_.copy().multiply(0.1 * -e.getWheelRotation());
			IVector2 size = size_.copy().subtract(zoomShift);
			setSize(size);
			IVector2 newMousePos = getWorldCoordinates(e.getX(), e.getY());
			IVector2 pos = getPosition().copy().subtract(newMousePos.subtract(oldMousePos));
			setPosition(pos);
		}
		
		public void mouseDragged(MouseEvent e)
		{
			if (lastDragPos != null)
			{
				IVector2 position = (new Vector2Double(e.getX(), e.getY())).multiply(getPixelSize());
				IVector2 diff = position.copy().subtract(lastDragPos);
				if (getInvertX())
					diff.negateX();
				if (!getInvertY())
					diff.negateY();
				lastDragPos = position;
				setPosition(getPosition().copy().subtract(diff));
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON3)
				lastDragPos = null;
		}
	}

	public ISpaceController getSpaceController() {
		return spacecontroller;
	}

	public void getSpaceController(ISpaceController spacecontroller) {
		this.spacecontroller = spacecontroller;
	}

}
