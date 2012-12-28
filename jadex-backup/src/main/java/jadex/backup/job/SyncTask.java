package jadex.backup.job;



import jadex.bridge.IComponentIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SyncTask extends Task
{
	/** The sync source. */
	protected SyncLocation source;
	
	/** The sync source. */
	protected SyncLocation target;

	
	/** The entries. */
	protected List<SyncTaskEntry> entries;
	
	/**
	 *  Create a new sync request.
	 */
	public SyncTask()
	{
	}
	
	/**
	 *  Create a new sync request.
	 */
	public SyncTask(String jobid, SyncLocation source, SyncLocation target, long date)
	{
		super(jobid, date);
		this.source = source;
		this.target = target;
	}

	/**
	 *  Add a new sync entry.
	 */
	public void addSyncEntry(SyncTaskEntry se)
	{
		if(entries==null)
			entries = new ArrayList<SyncTaskEntry>();
		entries.add(se);
	}

	/**
	 *  Get the entries.
	 *  @return The entries.
	 */
	public List<SyncTaskEntry> getEntries()
	{
		return entries;
	}

	/**
	 *  Set the entries.
	 *  @param entries The entries to set.
	 */
	public void setEntries(List<SyncTaskEntry> entries)
	{
		this.entries = entries;
	}
//
//	/**
//	 *  Get the source.
//	 *  @return The source.
//	 */
//	public String getSource()
//	{
//		return source;
//	}
//
//	/**
//	 *  Set the source.
//	 *  @param source The source to set.
//	 */
//	public void setSource(String source)
//	{
//		this.source = source;
//	}
	
	/**
	 *  Get an entry per id.
	 */
	public SyncTaskEntry getEntry(String id)
	{
		SyncTaskEntry ret = null;
		
		if(entries!=null)
		{
			for(SyncTaskEntry entry: entries)
			{
				if(id.equals(entry.getId()))
				{
					ret = entry;
				}
			}
		}
		
		return ret;
	}

	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public SyncLocation getSource()
	{
		return source;
	}

	/**
	 *  Set the source.
	 *  @param source The source to set.
	 */
	public void setSource(SyncLocation source)
	{
		this.source = source;
	}

	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public SyncLocation getTarget()
	{
		return target;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(SyncLocation target)
	{
		this.target = target;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "SyncTask(source="+source+", date=" + (date==0? sdf.format(date): date) + ")";
	}

	/**
	 * 
	 */
	public static class SyncLocation
	{
		/** The path. */
		protected String path;
		
		/** The platform. */
		protected IComponentIdentifier host;

		/**
		 * 
		 */
		public SyncLocation()
		{
		}
		
		/**
		 * 
		 */
		public SyncLocation(String path, IComponentIdentifier host)
		{
			this.path = path;
			this.host = host;
		}

		/**
		 *  Get the path.
		 *  @return The path.
		 */
		public String getPath()
		{
			return path;
		}

		/**
		 *  Set the path.
		 *  @param path The path to set.
		 */
		public void setPath(String path)
		{
			this.path = path;
		}

		/**
		 *  Get the host.
		 *  @return The host.
		 */
		public IComponentIdentifier getHost()
		{
			return host;
		}

		/**
		 *  Set the host.
		 *  @param host The host to set.
		 */
		public void setHost(IComponentIdentifier host)
		{
			this.host = host;
		}
	}
}
