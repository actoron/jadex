package jadex.adapter.base.envsupport;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;


/**
 * 
 */
public class MEnvTexturedRectangle
{
	//-------- attributes --------

	/** The shift x. */
	protected double shiftx;
	
	/** The shift x. */
	protected double shifty;
	
	/** The width. */
	protected double width;
	
	/** The height. */
	protected double height;
	
	/** The flag for rotating. */
	protected boolean rotating;
	
	/** The image path. */
	protected String imagepath;

	//-------- methods --------

	/**
	 *  Get the width.
	 *  @return The width.
	 */
	public double getWidth()
	{
		return this.width;
	}

	/**
	 *  Set the width.
	 *  @param width The width to set.
	 */
	public void setWidth(double width)
	{
		this.width = width;
	}
	
	/**
	 *  Get the height.
	 *  @return The height.
	 */
	public double getHeight()
	{
		return this.height;
	}

	/**
	 *  Set the height.
	 *  @param height The height to set.
	 */
	public void setHeight(double height)
	{
		this.height = height;
	}


	/**
	 * @return the shift
	 */
	public double getShiftX()
	{
		return this.shiftx;
	}

	/**
	 * @param shift the shift to set
	 */
	public void setShiftX(double shiftx)
	{
		this.shiftx = shiftx;
	}
	
	/**
	 * @return the shift
	 */
	public double getShiftY()
	{
		return this.shiftx;
	}

	/**
	 * @param shift the shift to set
	 */
	public void setShiftY(double shiftx)
	{
		this.shiftx = shiftx;
	}

	/**
	 * @return the rotating
	 */
	public boolean isRotating()
	{
		return this.rotating;
	}

	/**
	 * @param rotating the rotating to set
	 */
	public void setRotating(boolean rotating)
	{
		this.rotating = rotating;
	}

	/**
	 * @return the imagepath
	 */
	public String getImagePath()
	{
		return this.imagepath;
	}

	/**
	 * @param imagepath the imagepath to set
	 */
	public void setImagePath(String imagepath)
	{
		this.imagepath = imagepath;
	}
	
	/**
	 * 
	 */
	public IVector2 getShift()
	{
		return shiftx==0 && shifty==0? Vector2Double.ZERO: new Vector2Double(shiftx, shifty);
	}
	
	/**
	 * 
	 */
	public IVector2 getSize()
	{
		return width==0 && height==0? Vector2Double.ZERO: new Vector2Double(width, height);
	}
}
