package jadex.micro.examples.mandelbrot;

import java.awt.Rectangle;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;

/**
 *  Object representing information about an ongoing calculation.
 */
public class ProgressData
{
	//-------- attributes --------
	
	/** The provider id. */
	protected IComponentIdentifier	providerid;
	
	/** The task id. */
	protected Object taskid;
	
	/** The area. */
	protected Rectangle	area;
	
	/** The image width. */
	protected int imagewidth;
	
	/** The image height. */
	protected int imageheight;
	
	/** The state (finished or not). */
	protected boolean finished;
	
	/** The display id. */
	protected String displayid;
	
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
	public ProgressData(IComponentIdentifier providerid, Object taskid, Rectangle area, boolean finished, 
		int imagewidth, int imageheight, String displayid)
	{
		this.providerid	= providerid;
		this.taskid	= taskid;
		this.area	= area;
		this.finished	= finished;
		this.imagewidth = imagewidth;
		this.imageheight = imageheight;
		this.displayid = displayid;
	}
	
	//-------- methods --------

	/**
	 *  Get the provider id.
	 */
	public IComponentIdentifier getProviderId()
	{
		return providerid;
	}
	
	/**
	 *  Set the provider id.
	 */
	public void setProviderId(IComponentIdentifier providerid)
	{
		this.providerid = providerid;
	}

	/**
	 *  Get the task id.
	 */
	public Object getTaskId()
	{
		return taskid;
	}

	/**
	 *  Set the task id.
	 */
	public void setTaskId(Object taskid)
	{
		this.taskid = taskid;
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
	 *  Get the displayid.
	 *  @return the displayid.
	 */
	public String getDisplayId()
	{
		return displayid;
	}

	/**
	 *  Set the displayid.
	 *  @param displayid The displayid to set.
	 */
	public void setDisplayId(String displayid)
	{
		this.displayid = displayid;
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
