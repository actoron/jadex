package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.commons.SReflect;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

/**
 *  A map point reflects how often the agent
 *  was near to this point.
 */
public class MapPoint
{
	//-------- attributes --------

	/** The location. */
	protected IVector2 location;
	
	/** The visit quantity. */
	protected int quantity;

	/** The seen value (1=just seen -> 0=never seen). */
	protected double seen;

	//-------- constructors --------
	
	/**
	 *  Bean constructor required for remote cleaner GUI.
	 */
	public MapPoint()
	{
	}
	
	/**
	 *  Create a new map point.
	 */
	public MapPoint(IVector2 location, int quantity, double seen)
	{
		this.location = location;
		this.quantity = quantity;
		this.seen = seen;
	}

	//-------- methods --------

	/**
	 *  Get the quantity.
	 *  @return The quantity.
	 */
	public int getQuantity()
	{
		return this.quantity;
	}

	/**
	 *  Set the quantity.
	 *  @param quantity The quantity.
	 */
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	/**
	 *  Get the seen.
	 *  @return The seen.
	 */
	public double getSeen()
	{
		return this.seen;
	}

	/**
	 *  Set the seen.
	 *  @param seen The seen.
	 */
	public void setSeen(double seen)
	{
		this.seen = seen;
	}
	
	/**
	 *  Get the location.
	 *  @return The location.
	 */
	public IVector2 getLocation()
	{
		return this.location;
	}

	/**
	 *  Set the location.
	 *  @param location The location to set.
	 */
	public void setLocation(IVector2 location)
	{
		this.location = location;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(this.getClass())
			+" loc: "+location+" quantity:"+quantity+" seen: "+seen;
	}

	//-------- static part --------

	/**
	 *  Create a map point raster.
	 *  @param numx The number of x points.
	 *  @param numy The number of y points.
	 */
	public static MapPoint[] getMapPointRaster(int numx, int numy, double width, double height)
	{
		double xwidth = width/(double)numx;
		double xstart = xwidth/2;
		double ywidth = height/(double)numy;
		double ystart = ywidth/2;

		MapPoint[] raster = new MapPoint[numx*numy];
		double yval = ystart;
		for(int y=0; y<numy; y++)
		{
			double xval = xstart;
			for(int x=0; x<numx; x++)
			{
				raster[y*numx+x] = new MapPoint(new Vector2Double(xval, yval), 0, 0);
				xval += xwidth;
			}
			yval += ywidth;
		}
		return raster;
	}
}