package jadex.micro.examples.mandelbrot_new;

import java.awt.Rectangle;

import jadex.bridge.IComponentIdentifier;

/**
 * 
 */
public class PartDataChunk 
{
	protected short[] data;
	
	protected int progress;
	
	/** The display id. */
	protected String displayid;
	
	protected int xstart;
	
	protected int ystart;
	
	protected Rectangle area;
	
	protected int imagewidth;
	
	protected int imageheight;
	
	protected IComponentIdentifier worker;
	
	/**
	 *  Create a new result
	 *  @param data The data.
	 */
	public PartDataChunk() 
	{
	}
	
	/**
	 *  Create a new result
	 *  @param data The data.
	 */
	public PartDataChunk(int progress, short[] data, int xstart, int ystart, IComponentIdentifier worker) 
	{
		this.progress = progress;
		this.data = data;
		this.xstart = xstart;
		this.ystart = ystart;
		this.worker = worker;
		
		//System.out.println("chunk: "+data.length+" "+xstart+" "+ystart+" "+progress);
	}

	/**
	 * @return the data
	 */
	public short[] getData() 
	{
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(short[] data) 
	{
		this.data = data;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() 
	{
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) 
	{
		this.progress = progress;
	}

	/**
	 * @return the displayid
	 */
	public String getDisplayId() 
	{
		return displayid;
	}

	/**
	 * @param displayid the displayid to set
	 */
	public void setDisplayId(String displayid) 
	{
		this.displayid = displayid;
	}

	/**
	 * @return the area
	 */
	public Rectangle getArea() 
	{
		return area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(Rectangle area) 
	{
		this.area = area;
	}

	/**
	 * @return the xstart
	 */
	public int getXStart() 
	{
		return xstart;
	}

	/**
	 * @param xstart the xstart to set
	 */
	public void setXStart(int xstart) 
	{
		this.xstart = xstart;
	}

	/**
	 * @return the ystart
	 */
	public int getYStart() 
	{
		return ystart;
	}

	/**
	 * @param ystart the ystart to set
	 */
	public void setYStart(int ystart) 
	{
		this.ystart = ystart;
	}

	/**
	 * @return the imagewidth
	 */
	public int getImageWidth() 
	{
		return imagewidth;
	}

	/**
	 * @param imagewidth the imagewidth to set
	 */
	public void setImageWidth(int imagewidth) 
	{
		this.imagewidth = imagewidth;
	}

	/**
	 * @return the imageheight
	 */
	public int getImageHeight() 
	{
		return imageheight;
	}

	/**
	 * @param imageheight the imageheight to set
	 */
	public void setImageHeight(int imageheight) 
	{
		this.imageheight = imageheight;
	}

	/**
	 * @return the worker
	 */
	public IComponentIdentifier getWorker() 
	{
		return worker;
	}

	/**
	 * @param worker the worker to set
	 */
	public void setWorker(IComponentIdentifier worker) 
	{
		this.worker = worker;
	}

	@Override
	public String toString() 
	{
		return "PartDataChunk [progress=" + progress + ", xstart=" + xstart + ", ystart=" + ystart + "]";
	}
	
}
