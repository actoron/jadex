package jadex.bridge.service.types.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  Collection of filedata for bulk transfer.
 */
public class BunchFileData extends FileData
{
	/** The data. */
	protected Collection<FileData> entries;
	
	/**
	 *  Create a new file data.
	 */
	public BunchFileData()
	{
	}
	
	/**
	 *  Create a new file data.
	 */
	public BunchFileData(Collection<FileData> entries)
	{
		this.entries = entries;
	}

	/**
	 *  Get the entries.
	 *  @return The entries.
	 */
	public Collection<FileData> getEntries()
	{
		return entries;
	}

	/**
	 *  Set the entries.
	 *  @param entries The entries to set.
	 */
	public void setEntries(Collection<FileData> entries)
	{
		this.entries = entries;
	}
	
	/**
	 *  Add an entry.
	 *  @param entry The entry.
	 */
	public void addEntry(FileData entry)
	{
		if(entries==null)
			entries = new ArrayList<FileData>();
		entries.add(entry);
	}
	
}
