package jadex.base;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import jadex.bridge.service.types.filetransfer.FileData;

/**
 *  The remote jar file.
 */
public class RemoteJarFile extends FileData
{
	//-------- attributes --------

	/** The map of jar entries. */
	protected Map<String, Collection<FileData>> jarentries;
	
	/** The relative path inside of the jar file. */
	protected String relativepath;
	
	//-------- constructors --------

	/**
	 *  Create a remote jar file.
	 */
	public RemoteJarFile()
	{
	}

	/**
	 *  Create a remote jar file.
	 */
	public RemoteJarFile(String filename, String path, boolean directory, String displayname, 
		Map<String, Collection<FileData>> jarentries, String relativepath, long lastmodified, char separator, int prefix, long size)
	{
		super(filename, path, directory, true, displayname, lastmodified, separator, prefix, size);
		this.jarentries = jarentries;
		this.relativepath = relativepath;
	}
	
	//-------- methods --------
	
	/**
	 *  List the files.
	 *  @return The collection of files.
	 */
	public Collection<FileData> listFiles()
	{
		Collection<FileData>	ret;
		if(jarentries.containsKey(relativepath))
		{
			ret	= jarentries.get(relativepath);
		}
		else
		{
			ret	= Collections.emptyList();
		}
		return ret;
	}

	/**
	 *  Get the jarentries.
	 *  @return the jarentries.
	 */
	public Map<String, Collection<FileData>> getJarEntries()
	{
		return jarentries;
	}

	/**
	 *  Set the jarentries.
	 *  @param jarentries The jarentries to set.
	 */
	public void setJarEntries(Map<String, Collection<FileData>> jarentries)
	{
		this.jarentries = jarentries;
	}

	/**
	 *  Get the relativepath.
	 *  @return the relativepath.
	 */
	public String getRelativePath()
	{
		return relativepath;
	}

	/**
	 *  Set the relativepath.
	 *  @param relativepath The relativepath to set.
	 */
	public void setRelativePath(String relativepath)
	{
		this.relativepath = relativepath;
	}
	
	/**
	 *  Get path name.
	 */
	public String getPathName()
	{
		String ret = null;
		if(relativepath!=null)
		{
			int idx = relativepath.lastIndexOf("/");
			if(idx!=-1)
			{
				ret = relativepath.substring(0, idx);
			}
		}
		return ret;
//		return relativpath==null? null: relativpath.substring(1);
	}
	
	/**
	 *  Test if the jar is already resolved, ie.
	 *  remote call has been performed.
	 */
	public boolean isResolved()
	{
		return jarentries!=null;
	}
	
	
}
