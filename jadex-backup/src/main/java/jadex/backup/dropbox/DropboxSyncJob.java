package jadex.backup.dropbox;

import jadex.backup.job.AbstractSyncJob;
import jadex.backup.resource.IBackupResource;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;

/**
 *  Job to sync resources with dropbox folder.
 */
public class DropboxSyncJob extends AbstractSyncJob
{
	//-------- attributes --------
		
	/** The application key. */
	protected String akey; 
	
	/** The application secret. */
	protected String asecret; 
	
	/** The session key. */
	protected String skey; 
	
	/** The session secret. */
	protected String ssecret; 
	
	//-------- constructors --------
	
	/**
	 *  Create a new job.
	 */
	public DropboxSyncJob()
	{
		this(null, null, null, null, null, null, null);
	}
	
	/**
	 *  Create a new job.
	 */
	public DropboxSyncJob(String id, String name, String gres, 
		String akey, String asecret, String skey, String ssecret)
	{
		super(id, name, gres);
		this.akey = akey==null? "g60rq4ty063ap3q": akey;
		this.asecret = asecret==null? "2t6ipcy6of4g00o": asecret;
		this.skey = skey==null? "rhif2e2h0qtx8lr": skey;
		this.ssecret = ssecret==null? "wm4yffhot70h4rn": ssecret;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the view.
	 */
	public Object getView(final IExternalAccess ea, boolean editable)
	{
		return new DropboxSyncJobPanel(ea, editable, this);
	}
	
	/**
	 *  Get the akey.
	 *  @return The akey.
	 */
	public String getAppKey()
	{
		return akey;
	}

	/**
	 *  Set the akey.
	 *  @param akey The akey to set.
	 */
	public void setAppKey(String akey)
	{
		this.akey = akey;
	}

	/**
	 *  Get the asecret.
	 *  @return The asecret.
	 */
	public String getAppSecret()
	{
		return asecret;
	}

	/**
	 *  Set the asecret.
	 *  @param asecret The asecret to set.
	 */
	public void setAppSecret(String asecret)
	{
		this.asecret = asecret;
	}

	/**
	 *  Get the skey.
	 *  @return The skey.
	 */
	public String getSessionKey()
	{
		return skey;
	}

	/**
	 *  Set the skey.
	 *  @param skey The skey to set.
	 */
	public void setSessionKey(String skey)
	{
		this.skey = skey;
	}

	/**
	 *  Get the ssecret.
	 *  @return The ssecret.
	 */
	public String getSessionSecret()
	{
		return ssecret;
	}

	/**
	 *  Set the ssecret.
	 *  @param ssecret The ssecret to set.
	 */
	public void setSessionSecret(String ssecret)
	{
		this.ssecret = ssecret;
	}

	/**
	 *  Get the resource.
	 */
	public IBackupResource getResource(IComponentIdentifier cid)
	{
		try
		{
			return new DropboxBackupResource(gres, cid, akey, asecret, skey, ssecret);
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
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "DropboxSyncJob(gres=" + gres + ", akey=" + akey + ", asecret="
			+ asecret + ", skey=" + skey + ", ssecret=" + ssecret + ", id="
			+ id + ", name=" + name + ")";
	}
}

