package jadex.backup.dropbox;

import jadex.backup.job.Job;
import jadex.backup.resource.BackupResource;
import jadex.backup.resource.IBackupResource;
import jadex.backup.swing.SyncJobPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;

import java.io.File;

/**
 *  Job to sync resources with dropbox folder.
 */
public class DropboxSyncJob extends Job
{
	//-------- attributes --------
		
	/** The global resource. */
	protected String gres;
	
	/** The . */ 
	protected String lres;

	
	//-------- constructors --------
	
	/**
	 *  Create a new job.
	 */
	public DropboxSyncJob()
	{
	}
	
	/**
	 *  Create a new job.
	 */
	public DropboxSyncJob(String id, String name, String gres)
	{
		super(id, name);
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
		return "jadex/backup/job/processing/SyncJobProcessingAgent.class";
	}
	
	/**
	 *  Get the view.
	 */
	public Object getView(final IExternalAccess ea, boolean editable)
	{
		return null;//new SyncJobPanel(ea, editable, this);
	}
	
	/**
	 *  Get the resource.
	 */
	public IBackupResource getResource(IComponentIdentifier cid)
	{
		try
		{
			return new BackupResource(gres, new File(lres), cid);
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
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

