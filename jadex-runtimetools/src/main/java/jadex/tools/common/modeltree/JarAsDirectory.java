package jadex.tools.common.modeltree;

import jadex.commons.collection.MultiCollection;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import nuggets.PersistenceHelper;


/**
 *  A directory-like representation of a jar file.
 */
public class JarAsDirectory	extends File
{
	//-------- static part --------
	
	static
	{
		PersistenceHelper.registerDelegate(JarAsDirectory.class, new DJarAsDirectory());
	}
	
	//-------- attributes --------
	
	/** The path of the jar file. */
	protected String	jarpath;
	
	/** The timestamp of the file. */
	protected long	lastmodified;

	/** The entry. */
	protected ZipEntry	entry;
	
	/** The subentries contained in the entry. */
	protected File[]	entries;
	
	//-------- constructors -------- 
	
	/**
	 *  Create a directory representation of a jar file.
	 */
	public JarAsDirectory(String jarpath)
	{
		super(jarpath);
		this.jarpath	= jarpath;
		this.lastmodified	= Long.MIN_VALUE;
	}
	
	/**
	 *  Create a directory representation of a jar file entry.
	 */
	public JarAsDirectory(String jarpath, ZipEntry entry)
	{
		super(jarpath+"!/"+entry.getName());
		this.jarpath	= jarpath;
		this.entry	= entry;
		this.lastmodified	= Long.MIN_VALUE;
	}
			
	//-------- File methods --------
	
	public boolean isDirectory()
	{
//		System.out.println("is directory: "+entry+", "+(entry==null || entry.isDirectory()));
		return entry==null || entry.isDirectory();
	}
	
	public File[] listFiles(FileFilter filter)
	{
		File[]	ret;
		if(entries!=null)
		{
			List	list	= new ArrayList();
			for(int i=0; i<entries.length; i++)
			{
				if(filter.accept(entries[i]))
					list.add(entries[i]);
			}
			ret	= (File[])list.toArray(new File[list.size()]);
		}
		else
		{
			ret	= new File[0];
		}
//		System.out.println("list files: "+entry+", "+SUtil.arrayToString(ret));
		return ret;
	}
	
	public String getAbsolutePath()
	{
		String	ret;
		if(entry!=null)
		{
			if(jarpath.startsWith("/"))
				ret	= "jar:file:"+jarpath+"!/"+entry.getName();
			else
				ret	= "jar:file:/"+jarpath+"!/"+entry.getName();
		}
		else
		{
			ret	= jarpath;
		}
		return ret;
	}
	
	public long lastModified()
	{
		return entry==null ? lastmodified : entry.getTime();
	}
	
	//-------- extra methods --------

	/**
	 *  Refresh the jar entries.
	 */
	public boolean	refresh()
	{
		boolean	changed	= false;
		// Only the root node needs to be refreshed.
		if(entry==null && new File(jarpath).lastModified()>lastmodified)
		{
			changed	= true;
			this.lastmodified	= new File(jarpath).lastModified();
			// Read entries into multi-collection (path->entries).
			MultiCollection	entries	= new MultiCollection();
			try
			{
				JarFile	jar = new JarFile(jarpath);
				Enumeration	e	= jar.entries();
				while(e.hasMoreElements())
				{
					String	dir	= "/";
					ZipEntry	entry	= (ZipEntry)e.nextElement();
					int	slash	= entry.getName().lastIndexOf("/", entry.getName().length()-2);
					if(slash!=-1)
						dir	= entry.getName().substring(0, slash+1);
					entries.put(dir, entry);
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			// Recursively create files for entries.
			this.entries	= createFiles("/", entries);
			
		}
//		System.out.println("refresh: "+entry+", "+changed);
		return changed;
	}
	
	/**
	 *  Create the files for an entry.
	 *  Recursive implementation for directory entries.
	 */
	public File[] createFiles(String key, MultiCollection entries)
	{
		Collection	col	= entries.getCollection(key);
		JarAsDirectory[]	ret	= new JarAsDirectory[col.size()];
		Iterator	it	= col.iterator();
		for(int i=0; it.hasNext(); i++)
		{
			ret[i]	= new JarAsDirectory(jarpath, (ZipEntry)it.next());
			if(ret[i].isDirectory())
			{
				ret[i].entries	= createFiles(ret[i].entry.getName(), entries);
			}
		}
//		System.out.println("create files: "+key+", "+SUtil.arrayToString(ret));
		return ret;
	}
}