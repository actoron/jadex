package jadex.base.gui.modeltree;

import jadex.commons.collection.MultiCollection;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


/**
 *  A directory-like representation of a jar file.
 */
public class JarAsDirectory	extends File
{
	//-------- attributes --------
	
	/** The path of the jar file. */
	protected String	jarpath;
	
	/** The timestamp of the file. */
	protected long	lastmodified;

	/** The entry. */
	protected ZipEntry	entry;
	
	/** The subentries contained in the entry. */
	protected File[]	entries;
	
	/** The files for the entry paths (cached for easy access). */
	protected Map	entryfiles;
	
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
	
	public File[] listFiles(FilenameFilter filter)
	{
		File[]	ret;
		if(entries!=null)
		{
			List	list	= new ArrayList();
			for(int i=0; i<entries.length; i++)
			{
				if(filter.accept(entries[i].getParentFile(), entries[i].getName()))
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
	
	public File[] listFiles()
	{
		File[]	ret;
		if(entries!=null)
		{
			ret = entries;
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
	public synchronized boolean	refresh()
	{
		boolean	changed	= false;
		// Only the root node needs to be refreshed.
		if(entry==null && new File(jarpath).lastModified()>lastmodified)
		{
			changed	= true;
			this.lastmodified	= new File(jarpath).lastModified();
			// Read entries into multi-collection (path->entries).
			MultiCollection	entries	= createEntries();
			
			// Recursively create files for entries.
			this.entryfiles	= new HashMap();
			this.entries	= createFiles("/", entries);
			
		}
//		System.out.println("refresh: "+entry+", "+changed);
		return changed;
	}
	
	/**
	 * 
	 */
	public MultiCollection createEntries()
	{
		MultiCollection	entries	= new MultiCollection();
		Set	contained	= new HashSet();
		try
		{
			JarFile	jar = new JarFile(jarpath);
			Enumeration	e	= jar.entries();
			while(e.hasMoreElements())
			{
				ZipEntry	entry	= (ZipEntry)e.nextElement();
				boolean	finished	= false;
				while(!finished)
				{
					String	dir	= "/";
					int	slash	= entry.getName().lastIndexOf("/", entry.getName().length()-2);
					if(slash!=-1)
					{
						dir	= entry.getName().substring(0, slash+1);
					}
					if(!contained.contains(entry.getName()))
					{
						entries.put(dir, entry);
						contained.add(entry.getName());
					}
					
					if(dir.equals("/"))
					{
						finished	= true;
					}
					else
					{
						// Add directory entries manually, because some tools (like eclipse) do not include these in the jar.
						entry	= new ZipEntry(dir);
					}
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return entries;
	}
	
	/**
	 *  Check if the file exists.
	 */
	public boolean exists()
	{
		return true;	// hack???
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
			ZipEntry	entry	= (ZipEntry)it.next();
			ret[i]	= new JarAsDirectory(jarpath, entry);
			if(ret[i].isDirectory())
			{
				ret[i].entries	= createFiles(ret[i].entry.getName(), entries);
			}
			entryfiles.put(entry.getName(), ret[i]);
		}
//		System.out.println("create files: "+key+", "+SUtil.arrayToString(ret));
		return ret;
	}
	
	/**
	 *  Get the path to the jar file.
	 */
	public String	getJarPath()
	{
		return jarpath;
	}

	/**
	 *  Get the zip entry, if any (file pointer inside jar file).
	 *  The jar file itself (root) has no entry (i.e. entry=null).
	 */
	public ZipEntry	getZipEntry()
	{
		return entry;
	}
	
	/**
	 *  Get a file for an entry path.
	 */
	public synchronized File	 getFile(String path)
	{
		if(entryfiles==null)
			refresh();
		
		return (File)entryfiles.get(path);
	}

	/**
	 *  Get the entryfiles.
	 *  @return the entryfiles.
	 */
	public Map getEntryFiles()
	{
		return entryfiles;
	}
	
}