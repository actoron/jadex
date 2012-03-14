package jadex.base.gui.filetree;

import jadex.bridge.service.types.deployment.FileData;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 *  The remote jar file.
 */
public class RemoteJarFile extends FileData
{
	//-------- attributes --------

	/** The map of jar entries. */
	protected Map jarentries;
	
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
		Map jarentries, String relativepath, long lastmodified, char separator, int prefix)
	{
		super(filename, path, directory, displayname, lastmodified, separator, prefix);
		this.jarentries = jarentries;
		this.relativepath = relativepath;
	}
	
	//-------- methods --------
	
	/**
	 *  List the files.
	 *  @return The collection of files.
	 */
	public Collection listFiles()
	{
		return jarentries.get(relativepath)!=null? (Collection)jarentries.get(relativepath): Collections.EMPTY_LIST;
	}

	/**
	 *  Get the jarentries.
	 *  @return the jarentries.
	 */
	public Map getJarEntries()
	{
		return jarentries;
	}

	/**
	 *  Set the jarentries.
	 *  @param jarentries The jarentries to set.
	 */
	public void setJarEntries(Map jarentries)
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
	
}
