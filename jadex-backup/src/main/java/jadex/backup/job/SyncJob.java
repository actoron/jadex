package jadex.backup.job;

import jadex.backup.resource.BackupResource;
import jadex.backup.resource.IBackupResource;
import jadex.backup.swing.SyncJobPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;

import java.io.File;


/**
 *  Job to sync resources.
 */
public class SyncJob extends AbstractSyncJob
{
	//-------- attributes --------
	
	/** The local source. */ 
	protected String lres;
	
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
		super(id, name, gres);
		this.lres = lres;
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
	 *  Get the view.
	 */
	public Object getView(final IExternalAccess ea, boolean editable)
	{
		return new SyncJobPanel(ea, editable, this);
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
