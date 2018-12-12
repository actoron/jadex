package jadex.extension.envsupport.observer.graphics.drawable3d;

import jadex.javaparser.IParsedExpression;

/**
 * 
 */
public class Sky3d extends Primitive3d
{

	/** Skyfile (one File). */
	protected String			_skyFile;
	
	/** Alternate: 6 Files for each Direction. */
	protected String			_skyPath;
	
	protected String			_west;
	protected String			_east;
	protected String			_north;
	protected String			_south;
	protected String			_up;
	protected String			_down;
	

	
	
	protected boolean _isSphere;

	/**
	 * Creates default Polygon.
	 * 
	 * @param modelPath resource path of the model
	 */
	public Sky3d(String skyFile, Boolean isSphere, String skyPath, String west, String east, String north, String south, String up, String down)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_SKY;
		_skyFile = skyFile;
		this._isSphere = isSphere!=null? isSphere: false;
	}

	/**
	 * Creates a new Polygon drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c modulation color or binding
	 * @param modelPath resource path of the texture
	 */
	public Sky3d(String skyFile, Boolean isSphere, String skyPath, String west, String east, String north, String south, String up, String down, IParsedExpression drawcondition)
	{
		super(Primitive3d.PRIMITIVE_TYPE_SKY, null, null, null, 0, null, null, skyFile,  drawcondition, "Off", null);
		this._skyFile = skyFile!=null? skyFile: "";
		this._isSphere = isSphere!=null? isSphere: false;
		this._skyPath = skyPath!=null? skyPath: "";
		
		this._west = west!=null? west: "";
		this._east = east!=null? east: "";
		this._north = north!=null? north: "";
		this._south = south!=null? south: "";
		this._up = up!=null? up: "";
		this._down = down!=null? down: "";
	}
	
	/**
	 *  Set the primitive type (Disabled).
	 *  @param type The type to set.
	 */
	public void setType(int type)
	{
		throw new RuntimeException("Set type not supported: " + getClass().getCanonicalName());
	}

	/**
	 * @return the _modelPath
	 */
	public String getSkyFile()
	{
		return _skyFile;
	}

	/**
	 * @param _modelPath the _modelPath to set
	 */
	public void setSkyFile(String skyFile)
	{
		this._skyFile = skyFile;
	}

	public boolean isSphere()
	{
		return _isSphere;
	}
	
	public void setIsSphere(Boolean isSphere)
	{
		this._isSphere = isSphere;
	}


	/**
	 * @return the west
	 */
	public String getWest()
	{
		return _west;
	}

	/**
	 * @param west the west to set
	 */
	public void setWest(String west)
	{
		this._west = west;
	}

	/**
	 * @return the east
	 */
	public String getEast()
	{
		return _east;
	}

	/**
	 * @param east the east to set
	 */
	public void setEast(String east)
	{
		this._east = east;
	}

	/**
	 * @return the north
	 */
	public String getNorth()
	{
		return _north;
	}

	/**
	 * @param north the north to set
	 */
	public void setNorth(String north)
	{
		this._north = north;
	}

	/**
	 * @return the south
	 */
	public String getSouth()
	{
		return _south;
	}

	/**
	 * @param south the south to set
	 */
	public void setSouth(String south)
	{
		this._south = south;
	}

	/**
	 * @return the up
	 */
	public String getUp()
	{
		return _up;
	}

	/**
	 * @param up the up to set
	 */
	public void setUp(String up)
	{
		this._up = up;
	}

	/**
	 * @return the down
	 */
	public String getDown()
	{
		return _down;
	}

	/**
	 * @param down the down to set
	 */
	public void setDown(String down)
	{
		this._down = down;
	}

	/**
	 * @return the _skyPath
	 */
	public String getSkyPath()
	{
		return _skyPath;
	}

	/**
	 * @param _skyPath the _skyPath to set
	 */
	public void setSkyPath(String skyPath)
	{
		this._skyPath = skyPath;
	}
	

}

