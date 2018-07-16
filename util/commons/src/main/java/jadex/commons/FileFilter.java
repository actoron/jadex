package jadex.commons;

import java.io.File;
import java.util.jar.JarEntry;

/**
 *  Filter for files and jar entries.
 */
public class FileFilter implements IFilter<Object>
{
	/** The filename. */
	protected String filename;
	
	/** The contains flag. */
	protected boolean contains;
	
	/** The suffix string. */
	protected String suffix;
	
	/**
	 *  Create a new file filter.
	 */
	public FileFilter(String filename, boolean contains)
	{
		this(filename, contains, ".class");
	}
	
	/**
	 *  Create a new file filter.
	 */
	public FileFilter(String suffix)
	{
		this(null, false, ".class");
	}
	
	/**
	 *  Create a new file filter.
	 */
	public FileFilter(String filename, boolean contains, String suffix)
	{
		this.filename = filename;
		this.contains = contains;
		this.suffix = suffix;
	}
	
	/**
	 *  Filter impl.
	 */
	public boolean filter(Object obj)
	{
		if(filename==null)
			return true;
		
		String	fn	= "";
		if(obj instanceof File)
		{
			File	f	= (File)obj;
			fn	= f.getName();
		}
		else if(obj instanceof JarEntry)
		{
			JarEntry	je	= (JarEntry)obj;
			fn	= je.getName();
		}
		
		return fn.endsWith(suffix) && (filename==null || (contains? fn.indexOf(filename)!=-1: fn.indexOf(filename)==-1));
	}
}