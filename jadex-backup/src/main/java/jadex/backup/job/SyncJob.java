package jadex.backup.job;


/**
 *  Job to sync resources.
 */
public class SyncJob extends Job
{
	//-------- attributes --------
	
	/** The local source. */ 
	protected String lres;
	
	/** The global resource. */
	protected String gres;

	//-------- constructors --------
	
	/**
	 *  Create a new job.
	 */
	public SyncJob(String id, String name, String lres, String gres)
	{
		super(id, name);
		this.lres = lres;
		this.gres = gres;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the localResource.
	 *  @return The localResource.
	 */
	public String getLocalResource()
	{
		return lres;
	}

	/**
	 *  Set the localResource.
	 *  @param localResource The localResource to set.
	 */
	public void setLocalResource(String localResource)
	{
		this.lres = localResource;
	}

	/**
	 *  Get the globalResource.
	 *  @return The globalResource.
	 */
	public String getGlobalResource()
	{
		return gres;
	}

	/**
	 *  Set the globalResource.
	 *  @param globalResource The globalResource to set.
	 */
	public void setGlobalResource(String globalResource)
	{
		this.gres = globalResource;
	}
	
	/**
	 *  Get the details about a job.
	 *  @return The details.
	 */
	public String getDetails()
	{
		StringBuffer ret = new StringBuffer(lres);
		if(gres!=null)
			ret.append(", id: ").append(gres);
		return ret.toString();
	}
	
}
