package jadex.base;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import jadex.commons.collection.MultiCollection;


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
	protected String entrystr;
	protected Boolean dir;
	
	/** The subentries contained in the entry. */
	protected File[] entries;
	
	/** The files for the entry paths (cached for easy access). */
	protected Map<String, JarAsDirectory>	entryfiles;
	
	/** The refresh flag (normally only for root jar file but in remote case also for entries (partial jars). */
	protected boolean refresh;
	
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
	public JarAsDirectory(String jarpath, String entrystr, boolean dir, boolean refresh)
	{
		super(jarpath+"!/"+entrystr);
		this.jarpath	= jarpath;
		this.entrystr	= entrystr;
		this.refresh = refresh;
		this.dir = dir? Boolean.TRUE: Boolean.FALSE;
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
		return dir!=null? dir.booleanValue(): entry==null || entry.isDirectory();
	}
	
	public File[] listFiles(FileFilter filter)
	{
		File[]	ret;
		if(entries!=null)
		{
			List<File>	list	= new ArrayList<File>();
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
			List<File>	list	= new ArrayList<File>();
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
		// Old
//		String	ret;
//		String	jarname	= jarpath.replace('\\', '/');
//		if(entry!=null)
//		{
////			if(jarpath.startsWith("/"))
//				ret	= "jar:file:"+jarname+"!/"+entry.getName();
////			else
////				ret	= "jar:file:/"+jarname+"!/"+entry.getName();
//		}
//		else
//		{
////			ret	= jarpath;
//			ret = "jar:file:"+jarname+"!/";
//		}
//		return ret;

		// New
		String	ret;
		String jarurl;
		try
		{
			jarurl = new File(jarpath).toURI().toURL().toString();
		}
		catch(MalformedURLException e)
		{
			jarurl	= "file:"+jarpath.replace('\\', '/');
		}
		
		if(getEntryName()!=null)
		{
//			if(jarpath.startsWith("/"))
				ret	= "jar:"+jarurl+"!/"+getEntryName();
//			else
//				ret	= "jar:file:/"+jarname+"!/"+entry.getName();
		}
		else
		{
			ret	= jarpath;
			
			// The variant below delivers a jar path for the jar file itself which is not correct
			// as the jar notation should be used for files inside of the jar.
//			ret = "jar:"+jarurl+"!/";
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
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
		if(isRefresh() && new File(jarpath).lastModified()>lastmodified)
		{
			changed	= true;
			this.lastmodified	= new File(jarpath).lastModified();
			// Read entries into multi-collection (path->entries).
			MultiCollection<String, ZipEntry>	entries	= createEntries();
			
			// Recursively create files for entries.
			this.entryfiles	= new HashMap<String, JarAsDirectory>();
			this.entries	= createFiles("/", entries);
			
		}
//		System.out.println("refresh: "+entry+", "+changed);
		return changed;
	}
	
	/**
	 * 
	 */
	public MultiCollection<String, ZipEntry> createEntries()
	{
		MultiCollection<String, ZipEntry>	entries	= new MultiCollection<String, ZipEntry>();
		
//		return entries;
		
		Set<String>	contained	= new HashSet<String>();
		final String mypath = getAbsolutePath();
		
		JarFile jar = null;
		try
		{
			// todo:
			// JarURLConnection does not support nested jar file access :-(
			// Workaround is to copy jar in temp dir and extract it
			// Then treat extracted file as top level jar again.
			// http://www.javakb.com/Uwe/Forum.aspx/java-programmer/45375/URL-to-nested-Jar-files-looking-for-workaround
			
			if(mypath.startsWith("\\jar:file") || mypath.startsWith("/jar:file") || mypath.startsWith("jar:file")
				|| mypath.startsWith("\\zip:file") || mypath.startsWith("/zip:file") || mypath.startsWith("zip:file"))
			{
				URL url = new URL(mypath);
				JarURLConnection conn = (JarURLConnection)url.openConnection();
				jar = conn.getJarFile();
			}
			else
			{
				jar = new JarFile(mypath);
			}
			
			Enumeration<JarEntry>	e	= jar.entries();
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
						entries.add(dir, entry);
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
//			if(mypath.indexOf("jar:file")!=mypath.lastIndexOf("jar:file") || mypath.indexOf("zip:file")!=mypath.lastIndexOf("zip:file"))
//			{
//				// todo: nested jar
//			}
//			else
//			{
				e.printStackTrace();
//			}
			System.out.println("Failed to open jar: "+e);
		}
		
		// Necessary, otherwise file cannot be deleted.
		// http://bugs.sun.com/view_bug.do?bug_id=4167874
		try
		{
			if(jar!=null)
				jar.close();
		}
		catch(Exception e)
		{
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
	public File[] createFiles(String key, MultiCollection<String, ZipEntry> entries)
	{
		Collection<ZipEntry> col	= entries.getCollection(key);
		JarAsDirectory[]	ret	= new JarAsDirectory[col.size()];
		Iterator<ZipEntry>	it	= col.iterator();
		for(int i=0; it.hasNext(); i++)
		{
			ZipEntry	entry	= (ZipEntry)it.next();
			ret[i]	= new JarAsDirectory(jarpath, entry);
			if(ret[i].isDirectory())
			{
				ret[i].entries	= createFiles(ret[i].getEntryName(), entries);
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
	public Map<String, JarAsDirectory> getEntryFiles()
	{
		return entryfiles;
	}
	
	/**
	 *  Test if this is the real jar (not a contained file).
	 */
	public boolean isRoot()
	{
		return getEntryName()==null;
	}

	/**
	 *  Get the lastmodified.
	 *  @return the lastmodified.
	 */
	public long getLastModified()
	{
		return lastmodified;
	}
	
	/**
	 * 
	 */
	public String getEntryName()
	{
		return entry!=null? entry.getName(): entrystr;
	}
	
	/**
	 *  Test if jar should refresh its content.
	 *  Normally only root jar reads entries and entries already contain their children.
	 */
	protected boolean isRefresh()
	{
		return refresh? true: getEntryName()==null;
	}
	
//	/**
//	 * 
//	 */
//	protected static String jarifyPath(String path, String file)
//	{
//		if(!path.startsWith("jar:file:"))
//			path = "jar:file:"+path;
//		if(!path.endsWith("!/") && !path.endsWith("!"))
//			path = path+"!/";
//		if(file!=null && !"/".equals(file))
//		{
//			path = path+file;
//		}
//		return path;
//	}
}