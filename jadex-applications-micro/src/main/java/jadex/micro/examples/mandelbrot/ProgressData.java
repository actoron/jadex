package jadex.micro.examples.mandelbrot;

import jadex.commons.SUtil;

import java.awt.Rectangle;

/**
 *  Object representing information about an ongoing calculation.
 */
public class ProgressData
{
	//-------- attributes --------
	
	/** The provider id. */
	protected Object	providerid;
	
	/** The area. */
	protected Rectangle	area;
	
	/** The image width. */
	protected int imagewidth;
	
	/** The image height. */
	protected int imageheight;
	
	/** The state (finished or not). */
	protected boolean	finished;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public ProgressData()
	{
	}
	
	/**
	 *  Create a new ProgressData.
	 */
	public ProgressData(Object providerid, Rectangle area, boolean finished, int imagewidth, int imageheight)
	{
		this.providerid	= providerid;
		this.area	= area;
		this.finished	= finished;
		this.imagewidth = imagewidth;
		this.imageheight = imageheight;
	}
	
	//-------- methods --------

	/**
	 *  Get the provider id.
	 */
	public Object getProviderId()
	{
		return providerid;
	}

	/**
	 *  Set the provider id.
	 */
	public void setProviderId(Object providerid)
	{
		this.providerid = providerid;
	}

	/**
	 *  Get the area.
	 */
	public Rectangle getArea()
	{
		return area;
	}

	/**
	 *  Set the area.
	 */
	public void setArea(Rectangle area)
	{
		this.area = area;
	}

	/**
	 *  Check if calculation is finished.
	 */
	public boolean isFinished()
	{
		return finished;
	}

	/**
	 *  Set the finished flag.
	 */
	public void setFinished(boolean finished)
	{
		this.finished = finished;
	}
	
	/**
	 *  Get the providerid.
	 *  @return the providerid.
	 */
	public Object getProviderid()
	{
		return providerid;
	}

	/**
	 *  Set the providerid.
	 *  @param providerid The providerid to set.
	 */
	public void setProviderid(Object providerid)
	{
		this.providerid = providerid;
	}

	/**
	 *  Get the imagewidth.
	 *  @return the imagewidth.
	 */
	public int getImageWidth()
	{
		return imagewidth;
	}

	/**
	 *  Set the imagewidth.
	 *  @param imagewidth The imagewidth to set.
	 */
	public void setImageWidth(int imagewidth)
	{
		this.imagewidth = imagewidth;
	}

	/**
	 *  Get the imageheight.
	 *  @return the imageheight.
	 */
	public int getImageHeight()
	{
		return imageheight;
	}

	/**
	 *  Set the imageheight.
	 *  @param imageheight The imageheight to set.
	 */
	public void setImageHeight(int imageheight)
	{
		this.imageheight = imageheight;
	}

	/**
	 *  Calculate the hash code.
	 */
	public int hashCode()
	{
		int result = 1;
		result = 31 * result + ((area == null) ? 0 : area.hashCode());
//		result = 31 * result + ((providerid == null) ? 0 : providerid.hashCode());
		return result;
	}

	/**
	 *  Test if two objects are equal.
	 */
	public boolean equals(Object obj)
	{
		boolean	ret	= this==obj;
		if(!ret && obj instanceof ProgressData)
		{
			ProgressData other = (ProgressData)obj;
			ret	= SUtil.equals(getArea(), other.getArea());
//				&& SUtil.equals(getProviderId(), other.getProviderId());
		}
		return ret;
	}
	
	/**
	 *  String representation.
	 */
	public String toString()
	{
		return "Progress("+providerid+", "+area+", finished="+finished+")";
	}
}
