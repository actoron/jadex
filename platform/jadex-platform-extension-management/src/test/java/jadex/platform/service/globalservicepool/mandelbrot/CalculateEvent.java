package jadex.platform.service.globalservicepool.mandelbrot;

import jadex.bridge.IComponentIdentifier;

/**
 * 
 */
public class CalculateEvent 
{
	/** The area data. */
	protected AreaData areadata;
	
	/** The progress data. */
	protected Integer progressdata;

	/** The worker id. */
	protected IComponentIdentifier cid;
	
	/**
	 *  Create a new CalculateEvent.
	 */
	public CalculateEvent() 
	{
	}
	
	/**
	 *  Create a new CalculateEvent.
	 */
	public CalculateEvent(AreaData areaData, IComponentIdentifier cid) 
	{
		this(areaData, null, cid);
	}
	
	/**
	 *  Create a new CalculateEvent.
	 */
	public CalculateEvent(Integer progressdata, IComponentIdentifier cid) 
	{
		this(null, progressdata, cid);
	}
	
	/**
	 *  Create a new CalculateEvent.
	 */
	public CalculateEvent(AreaData areadata, Integer progressdata, IComponentIdentifier cid) 
	{
		this.areadata = areadata;
		this.progressdata = progressdata;
		this.cid = cid;
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

	/**
	 *  Get the cid.
	 *  @return the cid
	 */
	public IComponentIdentifier getCid()
	{
		return cid;
	}

	/**
	 *  Set the cid.
	 *  @param cid The cid to set
	 */
	public void setCid(IComponentIdentifier cid)
	{
		this.cid = cid;
	}
}
