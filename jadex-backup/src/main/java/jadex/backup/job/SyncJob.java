package jadex.backup.job;

import jadex.backup.swing.SyncJobPanel;
import jadex.bridge.IExternalAccess;

import java.util.ArrayList;
import java.util.List;


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
	
	/** The sync requests. */
	protected List<SyncRequest> requests;

	//-------- constructors --------
	
	/**
	 *  Create a new job.
	 */
	public SyncJob()
	{
	}
	
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
	 *  @return The local resource.
	 */
	public String getLocalResource()
	{
		return lres;
	}

	/**
	 *  Set the localResource.
	 *  @param lres The localResource to set.
	 */
	public void setLocalResource(String lres)
	{
		this.lres = lres;
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
	
//	/**
//	 *  Get the details about a job.
//	 *  @return The details.
//	 */
//	public String getDetails()
//	{
//		StringBuffer ret = new StringBuffer(lres);
//		if(gres!=null)
//			ret.append(", id: ").append(gres);
//		return ret.toString();
//	}
	
	/**
	 *  Get the agent type.
	 */
	public String getAgentType()
	{
		return "jadex/backup/job/SyncJobAgent.class";
	}
	
	/**
	 *  Get the view.
	 */
	public Object getView(final IExternalAccess ea, boolean editable)
	{
		return new SyncJobPanel(ea, editable, this);
	}
	
	/**
	 *  Get the syncRequests.
	 *  @return The syncRequests.
	 */
	public List<SyncRequest> getSyncRequests()
	{
		return requests;
	}

	/**
	 *  Set the syncRequests.
	 *  @param syncRequests The syncRequests to set.
	 */
	public void setSyncRequests(List<SyncRequest> syncRequests)
	{
		this.requests = syncRequests;
	}

	/**
	 * 
	 */
	public void addSyncRequest(SyncRequest sr)
	{
		if(requests==null)
			requests = new ArrayList<SyncRequest>();
		
		requests.add(sr);
	}
	
	/**
	 *  Get the string.
	 */
	public String toString()
	{
		return "SyncJob [lres=" + lres + ", gres=" + gres + ", id=" + id
			+ ", name=" + name + ", active=" + active + "]";
	}
}
