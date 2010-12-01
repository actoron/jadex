package jadex.micro.examples.mandelbrot;

import java.util.Arrays;

/**
 *  Struct for calculation of a specific mandelbrot cutout.
 */
public class AreaData
{
	/** The x start. */
	protected double xstart;
	
	/** The x end. */
	protected double xend;
	
	/** The y start. */
	protected double ystart;
	
	/** The y end. */
	protected double yend;
	
	/** The x size. */
	protected int sizex;
	
	/** The y size. */
	protected int sizey;
	
	/** The max value where iteration is stopped. */
	protected int max;
	
	/** The number of parallel workers. */
	protected int par;
	
	/** The id. */
	protected Object id;
	
	/** The tasksize of a task (in pixel/points). */
	protected int tasksize; 
	
	/** The result data. */
	protected int[][] data;
	
	/**
	 *  Create a new area data.
	 */
	public AreaData(double xstart, double xend, double ystart, double yend,
		int sizex, int sizey, int max)
	{
		this(xstart, xend, ystart, yend, sizex, sizey, max, 0, 0, null, null);
	}
	
	/**
	 *  Create a new area data.
	 */
	public AreaData(double xstart, double xend, double ystart, double yend,
		int sizex, int sizey, int max, int par, int tasksize)
	{
		this(xstart, xend, ystart, yend, sizex, sizey, max, par, tasksize, null, null);
	}
	
	/**
	 *  Create a new area data.
	 */
	public AreaData(double xstart, double xend, double ystart, double yend,
		int sizex, int sizey, int max, int par, int tasksize, Object id, int[][] data)
	{
		this.xstart = xstart;
		this.xend = xend;
		this.ystart = ystart;
		this.yend = yend;
		this.sizex = sizex;
		this.sizey = sizey;
		this.max = max;
		this.par = par;
		this.tasksize = tasksize;
		this.id = id;
		this.data = data;
	}

	/**
	 *  Get the xstart.
	 *  @return the xstart.
	 */
	public double getXStart()
	{
		return xstart;
	}

	/**
	 *  Set the xstart.
	 *  @param xstart The xstart to set.
	 */
	public void setXStart(double xstart)
	{
		this.xstart = xstart;
	}

	/**
	 *  Get the xend.
	 *  @return the xend.
	 */
	public double getXEnd()
	{
		return xend;
	}

	/**
	 *  Set the xend.
	 *  @param xend The xend to set.
	 */
	public void setXEnd(double xend)
	{
		this.xend = xend;
	}

	/**
	 *  Get the ystart.
	 *  @return the ystart.
	 */
	public double getYStart()
	{
		return ystart;
	}

	/**
	 *  Set the ystart.
	 *  @param ystart The ystart to set.
	 */
	public void setYStart(double ystart)
	{
		this.ystart = ystart;
	}

	/**
	 *  Get the yend.
	 *  @return the yend.
	 */
	public double getYEnd()
	{
		return yend;
	}

	/**
	 *  Set the yend.
	 *  @param yend The yend to set.
	 */
	public void setYEnd(double yend)
	{
		this.yend = yend;
	}

	/**
	 *  Get the sizex.
	 *  @return the sizex.
	 */
	public int getSizeX()
	{
		return sizex;
	}

	/**
	 *  Set the sizex.
	 *  @param sizex The sizex to set.
	 */
	public void setSizeX(int sizex)
	{
		this.sizex = sizex;
	}

	/**
	 *  Get the sizey.
	 *  @return the sizey.
	 */
	public int getSizeY()
	{
		return sizey;
	}

	/**
	 *  Set the sizey.
	 *  @param sizey The sizey to set.
	 */
	public void setSizeY(int sizey)
	{
		this.sizey = sizey;
	}

	/**
	 *  Get the max value.
	 *  @return the max value.
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 *  Set the max value.
	 *  @param max The max value to set.
	 */
	public void setMax(int max)
	{
		this.max = max;
	}

	/**
	 *  Get the data.
	 *  @return the data.
	 */
	public int[][] getData()
	{
		return data;
	}

	/**
	 *  Set the data.
	 *  @param data The data to set.
	 */
	public void setData(int[][] data)
	{
		this.data = data;
	}

	/**
	 *  Get the par.
	 *  @return the par.
	 */
	public int getParallel()
	{
		return par;
	}

	/**
	 *  Set the par.
	 *  @param par The par to set.
	 */
	public void setParallel(int par)
	{
		this.par = par;
	}
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public Object getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(Object id)
	{
		this.id = id;
	}
	
	/**
	 *  Get the tasksize.
	 *  @return the tasksize.
	 */
	public int getTaskSize()
	{
		return tasksize;
	}

	/**
	 *  Set the tasksize.
	 *  @param tasksize The tasksize to set.
	 */
	public void setTaskSize(int tasksize)
	{
		this.tasksize = tasksize;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "AreaData (xstart=" + xstart + ", xend=" + xend + ", ystart="
			+ ystart + ", yend=" + yend + ", sizex=" + sizex + ", sizey="
			+ sizey + ", max=" + max + ", par=" + par + ", id=" + id
			+ ", tasksize=" + tasksize + ", data="
			+ (data != null ? Arrays.asList(data) : null) + ")";
	}
	
}
