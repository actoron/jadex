package jadex.micro.examples.mandelbrot;

import jadex.bridge.IComponentIdentifier;

import java.util.StringTokenizer;


/**
 * Struct for calculation of a specific mandelbrot cutout.
 */
public class AreaData
{
	/** The x start. */
	protected double				xstart;

	/** The x end. */
	protected double				xend;

	/** The y start. */
	protected double				ystart;

	/** The y end. */
	protected double				yend;

	/** The x offset. */
	protected int					xoff;

	/** The y offset. */
	protected int					yoff;

	/** The x size. */
	protected int					sizex;

	/** The y size. */
	protected int					sizey;

	/** The max value where iteration is stopped. */
	protected int					max;

	/** The number of parallel workers. */
	protected int					par;

	/** The calculator service provider id. */
	protected IComponentIdentifier	cid;

	/** The tasksize of a task (in pixel/points). */
	protected int					tasksize;

	/** The result data. */
	protected int[][]				data;
	
	/**
	 *  Create an empty area data.
	 */
	public AreaData()
	{
		// bean constructor
	}
	
	/**
	 * Create a new area data.
	 */
	public AreaData(double xstart, double xend, double ystart, double yend,
			int sizex, int sizey, int max, int par, int tasksize)
	{
		this(xstart, xend, ystart, yend, 0, 0, sizex, sizey, max, par, tasksize, null, null);
	}

	/**
	 * Create a new area data.
	 */
	public AreaData(double xstart, double xend, double ystart, double yend,
			int xoff, int yoff, int sizex, int sizey, int max, int par, int tasksize,
			IComponentIdentifier cid, int[][] data)
	{
		this.xstart = xstart;
		this.xend = xend;
		this.ystart = ystart;
		this.yend = yend;
		this.xoff = xoff;
		this.yoff = yoff;
		this.sizex = sizex;
		this.sizey = sizey;
		this.max = max;
		this.par = par;
		this.tasksize = tasksize;
		this.cid = cid;
		this.data = data;
	}

	/**
	 * Get the xstart.
	 * 
	 * @return the xstart.
	 */
	public double getXStart()
	{
		return xstart;
	}

	/**
	 * Set the xstart.
	 * 
	 * @param xstart The xstart to set.
	 */
	public void setXStart(double xstart)
	{
		this.xstart = xstart;
	}

	/**
	 * Get the xend.
	 * 
	 * @return the xend.
	 */
	public double getXEnd()
	{
		return xend;
	}

	/**
	 * Set the xend.
	 * 
	 * @param xend The xend to set.
	 */
	public void setXEnd(double xend)
	{
		this.xend = xend;
	}

	/**
	 * Get the ystart.
	 * 
	 * @return the ystart.
	 */
	public double getYStart()
	{
		return ystart;
	}

	/**
	 * Set the ystart.
	 * 
	 * @param ystart The ystart to set.
	 */
	public void setYStart(double ystart)
	{
		this.ystart = ystart;
	}

	/**
	 * Get the yend.
	 * 
	 * @return the yend.
	 */
	public double getYEnd()
	{
		return yend;
	}

	/**
	 * Set the yend.
	 * 
	 * @param yend The yend to set.
	 */
	public void setYEnd(double yend)
	{
		this.yend = yend;
	}

	/**
	 * Get the x offset.
	 * 
	 * @return the x offset.
	 */
	public int getXOffset()
	{
		return xoff;
	}

	/**
	 * Set the x offset.
	 * 
	 * @param xoff The x offset to set.
	 */
	public void setXOffset(int xoff)
	{
		this.xoff = xoff;
	}

	/**
	 * Get the y offset.
	 * 
	 * @return the y offset.
	 */
	public int getYOffset()
	{
		return yoff;
	}

	/**
	 * Set the y offset.
	 * 
	 * @param yoff The y offset to set.
	 */
	public void setYOffset(int yoff)
	{
		this.yoff = yoff;
	}

	/**
	 * Get the sizex.
	 * 
	 * @return the sizex.
	 */
	public int getSizeX()
	{
		return sizex;
	}

	/**
	 * Set the sizex.
	 * 
	 * @param sizex The sizex to set.
	 */
	public void setSizeX(int sizex)
	{
		this.sizex = sizex;
	}

	/**
	 * Get the sizey.
	 * 
	 * @return the sizey.
	 */
	public int getSizeY()
	{
		return sizey;
	}

	/**
	 * Set the sizey.
	 * 
	 * @param sizey The sizey to set.
	 */
	public void setSizeY(int sizey)
	{
		this.sizey = sizey;
	}

	/**
	 * Get the max value.
	 * 
	 * @return the max value.
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 * Set the max value.
	 * 
	 * @param max The max value to set.
	 */
	public void setMax(int max)
	{
		this.max = max;
	}

	/**
	 * Get the data.
	 * 
	 * @return the data.
	 */
	// Not called getData as it should not be serialized in XML.
	// todo: support @XMLExclude
	public int[][] fetchData()
	{
		return data;
	}

	/**
	 * Set the data.
	 * 
	 * @param data The data to set.
	 */
	public void setData(int[][] data)
	{
		this.data = data;
	}

	/**
	 * Get the data as a transferable string.
	 * 
	 * @return the data string.
	 */
	public String getDataString()
	{
		String	ret	= null;
		if(data!=null)
		{
			// create string in form of "rows cols\n1 2\n4 5\n7 8"
			StringBuffer	sbuf	= new StringBuffer();
			sbuf.append(data.length);
			sbuf.append(" ");
			sbuf.append(data[0].length);
			sbuf.append("\n");
			for(int i=0; i<data.length; i++)
			{
				if(i>0)
					sbuf.append("\n");
				for(int j=0; j<data[i].length; j++)
				{
					if(j>0)
						sbuf.append(" ");
					sbuf.append(data[i][j]);
				}
			}
			ret	= sbuf.toString();
		}
		return ret;
	}

	/**
	 * Set the data.
	 * 
	 * @param data The data to set.
	 */
	public void setDataString(String sdata)
	{
		StringTokenizer	stok	= new StringTokenizer(sdata);
		int	rows	= Integer.parseInt(stok.nextToken());
		int	cols	= Integer.parseInt(stok.nextToken());
		this.data	= new int[rows][cols];
		for(int i=0; i<data.length; i++)
		{
			for(int j=0; j<data[i].length; j++)
			{
				data[i][j]	= Integer.parseInt(stok.nextToken());
			}
		}
	}

	/**
	 * Get the par.
	 * 
	 * @return the par.
	 */
	public int getParallel()
	{
		return par;
	}

	/**
	 * Set the par.
	 * 
	 * @param par The par to set.
	 */
	public void setParallel(int par)
	{
		this.par = par;
	}

	/**
	 * Get the calculator id.
	 * 
	 * @return the calculator id.
	 */
	public IComponentIdentifier getCalculatorId()
	{
		return cid;
	}

	/**
	 * Set the calculator id.
	 * 
	 * @param id The calculator id to set.
	 */
	public void setCalculatorId(IComponentIdentifier cid)
	{
		this.cid = cid;
	}

	/**
	 * Get the tasksize.
	 * 
	 * @return the tasksize.
	 */
	public int getTaskSize()
	{
		return tasksize;
	}

	/**
	 * Set the tasksize.
	 * 
	 * @param tasksize The tasksize to set.
	 */
	public void setTaskSize(int tasksize)
	{
		this.tasksize = tasksize;
	}

	public String toString()
	{
		return "AreaData(x="+xoff+", y="+yoff+")";
	}
	
	/**
	 *	Value for identifying this area data. 
	 */
	public Object getId()
	{
		return toString();
	}

	// /**
	// * Get the string representation.
	// */
	// public String toString()
	// {
	// return "AreaData (xstart=" + xstart + ", xend=" + xend + ", ystart="
	// + ystart + ", yend=" + yend + ", sizex=" + sizex + ", sizey="
	// + sizey + ", max=" + max + ", par=" + par + ", id=" + id
	// + ", tasksize=" + tasksize + ", data="
	// + (data != null ? Arrays.asList(data) : null) + ")";
	// }


}
