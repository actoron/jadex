package jadex.base.gui.modeltree;

import java.util.Collection;
import java.util.Map;

/**
 * 
 */
public class RemoteJarFile extends RemoteFile
{
	/** The map of jar entries. */
	protected Map jarentries;
	
//	/** The jar path. */
//	protected String jarpath;
	
	/** The relative path inside of the jar file. */
	protected String relativepath;
	
	/**
	 * 
	 */
	public RemoteJarFile()
	{
	}

	/**
	 * 
	 */
	public RemoteJarFile(String filename, String path, boolean directory, Map jarentries, String relativepath)
	{
		super(filename, path, directory);
		this.jarentries = jarentries;
//		this.jarpath = jarpath;
		this.relativepath = relativepath;
	}
	
	/**
	 * 
	 */
	public Collection listFiles()
	{
		return (Collection)jarentries.get(relativepath);
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

//	/**
//	 *  Get the jarpath.
//	 *  @return the jarpath.
//	 */
//	public String getJarPath()
//	{
//		return jarpath;
//	}
//
//	/**
//	 *  Set the jarpath.
//	 *  @param jarpath The jarpath to set.
//	 */
//	public void setJarPath(String jarpath)
//	{
//		this.jarpath = jarpath;
//	}

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
