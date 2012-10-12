package jadex.backup.job;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SyncTask extends Task
{
    public static final SimpleDateFormat sdf 
    	= new SimpleDateFormat("hh:mm MM dd yyyy");

	/** The sync source. */
	protected String source;
	
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
	public SyncTask(String source, long date)
	{
		super(date);
		this.source = source;
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

	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 *  Set the source.
	 *  @param source The source to set.
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "SyncTask(source="+source+", date=" + (date==0? sdf.format(date): date) + ")";
	}
}
