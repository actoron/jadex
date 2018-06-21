package jadex.bridge.service.types.filetransfer;

import java.util.ArrayList;
import java.util.Collection;

import jadex.base.RemoteJarFile;
import jadex.commons.Tuple2;

/**
 *  Collection of filedata for bulk transfer.
 */
public class BunchFileData extends FileData
{
	/** The data. */
	protected Collection<Tuple2<String, RemoteJarFile>> entries;
	
	/**
	 *  Create a new file data.
	 */
	public BunchFileData()
	{
	}
	
	/**
	 *  Create a new file data.
	 */
	public BunchFileData(Collection<Tuple2<String, RemoteJarFile>> entries)
	{
		this.entries = new ArrayList<Tuple2<String, RemoteJarFile>>(entries);
	}

	/**
	 *  Get the entries.
	 *  @return The entries.
	 */
	public Collection<Tuple2<String, RemoteJarFile>> getEntries()
	{
		return entries;
	}

	/**
	 *  Set the entries.
	 *  @param entries The entries to set.
	 */
	public void setEntries(Collection<Tuple2<String, RemoteJarFile>> entries)
	{
		this.entries = entries;
	}
	
	/**
	 *  Add an entry.
	 *  @param entry The entry.
	 */
	public void addEntry(Tuple2<String, RemoteJarFile> entry)
	{
		if(entries==null)
			entries = new ArrayList<Tuple2<String, RemoteJarFile>>();
		entries.add(entry);
	}

}
