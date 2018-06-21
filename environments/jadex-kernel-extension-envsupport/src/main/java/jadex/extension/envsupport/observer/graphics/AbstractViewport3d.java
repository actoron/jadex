package jadex.extension.envsupport.observer.graphics;

import java.awt.Canvas;
import java.awt.EventQueue;
import java.util.HashSet;
import java.util.Set;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.perspective.IPerspective;


public abstract class AbstractViewport3d implements IViewport3d
{
	
	/** The Space Controller */
	protected ISpaceController spacecontroller;
	
	/** Canvas for graphical output. */
	protected Canvas			canvas_;

	/** Size of the viewport without padding. */
	//TODO: should be Vector3Double someday...
	protected Vector3Double		size_;
	
	/** Flag aspect ratio preservation. */
	protected boolean			preserveAR_;
	
	/** Maximum displayable area */
	protected IVector3		areaSize_;

	/** Real size of the viewport including padding. */
	protected Vector2Double		paddedSize_;

//	/** Visuals that are handled as statics */
//	protected ArrayList<DrawableCombiner3d> _staticvisuals = new ArrayList<DrawableCombiner3d>();
	
	/** Known drawable Objects. */
	protected Set<Object>				drawObjects_;
	
	/** Virtual Viewport position. */
	protected IVector3			position_;
	
	/** Pixel-corrected viewport position. */
	protected IVector2			pixPosition_;

	/** List of objects that should be drawn. */
//	protected List<Object>				objectList_;
	
	/** IPropertyObject holding properties for layers. */
	protected IPerspective perspective;
	
	/** Flag to indicate that rendering is in progress. */
	protected volatile boolean	rendering;

	/** The listeners of the viewport. */
	private Set<Object>					listeners_;
	
//	/** The zoom limit */
//	private double				zoomLimit_;
	
	
	private boolean							isGrid_			= false;
	
	private boolean shader = true;
	
	private String camera = "Default";
	
	
	
	
	public AbstractViewport3d(IPerspective perspective, IVector3 areasize, boolean isGrid, boolean shader, String camera, ISpaceController spaceController)
	{
		rendering = false;
		this.perspective = perspective;
		size_ = new Vector3Double(1.0);
		position_ = Vector3Double.ZERO.copy();
		isGrid_	= isGrid;
		preserveAR_ = true;
		areaSize_ = areasize;
		paddedSize_ = new Vector2Double(1.0);
		drawObjects_ = new HashSet<Object>();
//		objectList_ = new ArrayList<Object>();
		listeners_ = new HashSet<Object>();
//		zoomLimit_ = 20.0;
		this.shader = shader;
		this.camera = camera;
		this.spacecontroller = spaceController;
	}
	
//	/**
//	 * Sets the current objects to draw.
//	 * 
//	 * @param objectList objects that should be drawn
//	 * 
//	 */
//	//TODO: no Array of Objects someday
//	public void setObjectList(List<Object[]> objectList)
//	{
//		objectList_.clear();
//		objectList_.addAll(objectList);
//	}
//	
//	/**
//	 * Sets the current staticobjects to draw.
//	 * 
//	 * @param staticvisuals staticobjects that should be drawn
//	 */
//	public void setStaticList(Collection<DrawableCombiner3d> staticvisuals)
//	{
//		_staticvisuals.clear();
//		_staticvisuals.addAll(staticvisuals);
//	}
	

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
	public IVector3 getSize()
	{
		return size_;
	}


	
	/**
	 * Gets the maximum displayable size.
	 * 
	 * @return maximum area size.
	 */
	public IVector3 getAreaSize()
	{
		return areaSize_;
	}
	
	/**
	 * Gets the maximum displayable size.
	 * 
	 * @return maximum area size.
	 */
	public IVector3 getAreaSize3d()
	{
		return areaSize_;
	}
	
	
	/**
	 * Sets the maximum displayable size.
	 * 
	 * @param areaSize maximum area size.
	 */
	public void setAreaSize(final IVector3 areaSize)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				areaSize_ = new Vector3Double(areaSize);
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
	 * Gets the position of the viewport.
	 */
	public IVector3 getPosition()
	{
		return position_.copy();
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
	
	public void isGridSpace(boolean isGrid)
	{
		isGrid_ = isGrid;

	}

	public boolean isShader() {
		return shader;
	}

	public void setShader(boolean shader) {
		this.shader = shader;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}

	public ISpaceController getSpaceController() {
		return spacecontroller;
	}

	public void getSpaceController(ISpaceController spacecontroller) {
		this.spacecontroller = spacecontroller;
	}
	
	
	
}
