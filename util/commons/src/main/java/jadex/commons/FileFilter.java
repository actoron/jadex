package jadex.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

/**
 *  Filter for files and jar entries.
 */
public class FileFilter implements IFilter<Object>
{
//	/** The filename. */
//	protected String filename;
//	
//	/** The contains flag. */
//	protected boolean contains;
//	
//	/** The suffix string. */
//	protected String suffix;
	
	/** The filename filters. */
	protected List<IFilter<String>> filters;
	
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
	public FileFilter(String filename, boolean contains, final String suffix)
	{
//		this.filename = filename;
//		this.contains = contains;
//		this.suffix = suffix;
		
		if(suffix!=null)
		{
			addFilenameFilter(new IFilter<String>()
			{
				public boolean filter(String fn)
				{
					return fn.endsWith(suffix);
				}
			});
		}
		
		if(filename!=null)
		{
			addFilenameFilter(new IFilter<String>()
			{
				public boolean filter(String fn)
				{
					return contains? fn.indexOf(filename)!=-1: fn.indexOf(filename)==-1;
				}
			});
		}
	}
	
	/**
	 *  Add a filename filter
	 *  @param filter The filter.
	 */
	public void addFilenameFilter(IFilter<String> filter)
	{
		if(filters==null)
			filters = new ArrayList<>();
		filters.add(filter);
	}
	
	/**
	 *  Filter impl.
	 */
	public boolean filter(Object obj)
	{
		boolean ret = true;
		
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
		else if(obj instanceof String)
		{
			fn = (String)obj;
		}
		
		if(filters!=null)
		{
			for(IFilter<String> filter: filters)
			{
				if(!filter.filter(fn))
				{
					ret = false;
					break;
				}
			}
		}
		
		return ret;
//		return fn.endsWith(suffix) && (filename==null || (contains? fn.indexOf(filename)!=-1: fn.indexOf(filename)==-1));
	}
	
	
}