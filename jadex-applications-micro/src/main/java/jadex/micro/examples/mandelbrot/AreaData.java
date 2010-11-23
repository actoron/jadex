package jadex.micro.examples.mandelbrot;

/**
 * 
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
	
	/** The x step width. */
	protected double stepx;
	
	/** The y step width. */
	protected double stepy;
	
	/** The max value where iteration is stopped. */
	protected int max;
	
	/** The result data. */
	protected int[][] data;

	
	/**
	 *  Create a new area data.
	 */
	public AreaData(double xstart, double xend, double ystart, double yend,
			double stepx, double stepy, int max, int[][] data)
	{
		this.xstart = xstart;
		this.xend = xend;
		this.ystart = ystart;
		this.yend = yend;
		this.stepx = stepx;
		this.stepy = stepy;
		this.max = max;
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
	 *  Get the stepx.
	 *  @return the stepx.
	 */
	public double getStepX()
	{
		return stepx;
	}

	/**
	 *  Set the stepx.
	 *  @param stepx The stepx to set.
	 */
	public void setStepX(double stepx)
	{
		this.stepx = stepx;
	}

	/**
	 *  Get the stepy.
	 *  @return the stepy.
	 */
	public double getStepY()
	{
		return stepy;
	}

	/**
	 *  Set the stepy.
	 *  @param stepy The stepy to set.
	 */
	public void setStepY(double stepy)
	{
		this.stepy = stepy;
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
	
}
