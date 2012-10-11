package jadex.backup.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SyncRequest
{
    protected static final SimpleDateFormat sdf 
    	= new SimpleDateFormat("hh:mm MMMM dd yyyy");

    protected static long cnt = 0;
    
	/** The sync source. */
	protected String source;

	/** The request state. */
	protected boolean finished;
	
	/** The creation date. */
	protected long date;
	
	/** The id. */
	protected long id;
	
	/** The entries. */
	protected List<SyncEntry> entries;
	
	/**
	 *  Create a new sync request.
	 */
	public SyncRequest()
	{
	}

	/**
	 *  Add a new sync entry.
	 */
	public void addSyncEntry(SyncEntry se)
	{
		if(entries==null)
			entries = new ArrayList<SyncEntry>();
		entries.add(se);
	}

	/**
	 *  Get the entries.
	 *  @return The entries.
	 */
	public List<SyncEntry> getEntries()
	{
		return entries;
	}

	/**
	 *  Set the entries.
	 *  @param entries The entries to set.
	 */
	public void setEntries(List<SyncEntry> entries)
	{
		this.entries = entries;
	}

	/**
	 *  Get the date.
	 *  @return The date.
	 */
	public long getDate()
	{
		return date;
	}

	/**
	 *  Set the date.
	 *  @param date The date to set.
	 */
	public void setDate(long date)
	{
		this.date = date;
	}

	/**
	 *  Get the finished.
	 *  @return The finished.
	 */
	public boolean isFinished()
	{
		return finished;
	}

	/**
	 *  Set the finished.
	 *  @param finished The finished to set.
	 */
	public void setFinished(boolean finished)
	{
		this.finished = finished;
	}
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public long getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "SyncRequest(date=" + (date==0? sdf.format(date): date) + ")";
	}
}
