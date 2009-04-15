package jadex.adapter.base.envsupport;


/**
 * 
 */
public class MEnvTexturedRectangle
{
	//-------- attributes --------

	/** The shift. */
	protected double shift;
	
	/** The size. */
	protected double size;
	
	/** The flag for rotating. */
	protected boolean rotating;
	
	/** The image path. */
	protected String imagepath;

	//-------- methods --------

	/**
	 *  Get the size.
	 *  @return The size.
	 */
	public double getSize()
	{
		return this.size;
	}
	
	/**
	 *  Set the size.
	 *  @param size The size to set.
	 */
	public void setSize(double size)
	{
		this.size = size;
	}

	/**
	 * @return the shift
	 */
	public double getShift()
	{
		return this.shift;
	}

	/**
	 * @param shift the shift to set
	 */
	public void setShift(double shift)
	{
		this.shift = shift;
	}

	/**
	 * @return the rotating
	 */
	public boolean getRotating()
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
}
