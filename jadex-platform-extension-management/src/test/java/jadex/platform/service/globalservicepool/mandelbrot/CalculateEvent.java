package jadex.platform.service.globalservicepool.mandelbrot;

/**
 * 
 */
public class CalculateEvent 
{
	/** The area data. */
	protected AreaData areadata;
	
	/** The progress data. */
	protected Integer progressdata;

	/**
	 *  Create a new CalculateEvent.
	 */
	public CalculateEvent() 
	{
	}
	
	/**
	 *  Create a new CalculateEvent.
	 */
	public CalculateEvent(AreaData areaData) 
	{
		this(areaData, null);
	}
	
	/**
	 *  Create a new CalculateEvent.
	 */
	public CalculateEvent(Integer progressData) 
	{
		this(null, progressData);
	}
	
	/**
	 *  Create a new CalculateEvent.
	 */
	public CalculateEvent(AreaData areaData, Integer progressData) 
	{
		this.areadata = areaData;
		this.progressdata = progressData;
	}

	/**
	 *  Get the areaData.
	 *  @return the areaData
	 */
	public AreaData getAreaData() 
	{
		return areadata;
	}

	/**
	 *  Set the areaData.
	 *  @param areaData The areaData to set
	 */
	public void setAreaData(AreaData areaData) 
	{
		this.areadata = areaData;
	}

	/**
	 *  Get the progressData.
	 *  @return the progressData
	 */
	public Integer getProgressData() 
	{
		return progressdata;
	}

	/**
	 *  Set the progressData.
	 *  @param progressData The progressData to set
	 */
	public void setProgressData(Integer progressData) 
	{
		this.progressdata = progressData;
	}
	
	
}
