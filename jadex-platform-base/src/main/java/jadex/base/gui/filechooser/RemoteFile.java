package jadex.base.gui.filechooser;

import java.io.File;
import java.io.*;


/**
 * 
 */
public class RemoteFile extends File
{
	//-------- attributes --------
	
	/** The external access. */
	//protected IExternalAccess exta;
	
	/** The name. */
	protected String name;
	
	/** The path. */
	protected String path;
	
	/** The boolean for directory. */
	protected boolean dir;
	
	/** The prefix length. */
	protected int prelength;
	
	//-------- constructors -------- 
	
	/**
	 *  Create a directory representation of a jar file.
	 */
	public RemoteFile(String name, String path, boolean dir)//, IExternalAccess exta)
	{
		this(name, path, dir, 3);
	}
	
	/**
	 *  Create a directory representation of a jar file.
	 */
	public RemoteFile(String name, String path, boolean dir, int prelength)//, IExternalAccess exta)
	{
		super(path+separatorChar+name);
		this.name = name;
		this.path = path;
		this.dir = dir;
		this.prelength = prelength;
	}
	
	//-------- File methods --------
	
	public boolean isDirectory()
	{
		return dir;
	}
	
//	public File[] listFiles(FileFilter filter)
//	{
//		
//	}
//	
//	public File[] listFiles(FilenameFilter filter)
//	{
//		
//	}
//	
//	public File[] listFiles()
//	{
//		
//	}
	
//	public String getAbsolutePath()
//	{
//		return path;
//	}
	
	public long lastModified()
	{
		return System.currentTimeMillis();
	}
	
	//-------- extra methods --------

	/**
	 *  Check if the file exists.
	 */
	public boolean exists()
	{
		return true;	// hack???
	}
	
    public String getName() 
    {
    	return name;
    }

    public String getParent() 
    {
    	String ret = null;
    	int index = getPath().lastIndexOf(separatorChar);
    	if (index<prelength) 
    	{
    	    if((prelength>0) && (getPath().length() > prelength))
    	    	ret =  getPath().substring(0, prelength);
    	}
    	else
    	{
    		ret = getPath().substring(0, index);
    	}
    	System.out.println("getPa: "+name+" "+ret);
    	return ret;
    }

    public File getParentFile() 
    {
    	String p = getParent();
    	String name = "unknown";
    	if(p!=null)
    	{
    		int index = p.lastIndexOf(separatorChar);
    		if(index>0) 
    			name  =  getPath().substring(0, index);
    	}
    	return p==null? null: new RemoteFile(name, p, true, prelength);
    }

    public boolean isAbsolute() 
    {
    	return true;
    }

//    public String getAbsolutePath() 
//    {
//    	return getPath();
//    }

//    public File getAbsoluteFile() 
//    {
//    	return null;
//    }
//
//    public String getCanonicalPath() throws IOException 
//    {
//    	return getPath();
//    }
//
//    public File getCanonicalFile() throws IOException 
//    {
//    	return this;
//    }
//
//    public boolean canRead() 
//    {
//    	return true;
//    }
//
//    public boolean canWrite() 
//    {
//    	return true;
//    }
//
    public boolean isFile() 
    {
    	return !dir;
    }

    public boolean isHidden() 
    {
    	return false;
    }
//
//    public long length() 
//    {
//    	return 0;
//    }
//
//    public boolean createNewFile() throws IOException 
//    {
//    	return false;
//    }
//
//    public boolean delete() 
//    {
//    	return false;
//    }
//
//    public void deleteOnExit() 
//    {
//    }
//
//    public boolean mkdir() 
//    {
//    	return false;
//    }
//
//    public boolean mkdirs() 
//    {
//    	return false;
//    }
//
//    public boolean renameTo(File dest) 
//    {
//    	return false;
//    }
//
//    public boolean setLastModified(long time) 
//    {
//    	return false;
//    }
//
//    public boolean setReadOnly() 
//    {
//    	return false;
//    }
//
//    public boolean setWritable(boolean writable, boolean ownerOnly) 
//    {
//    	return false;
//    }
//
//    public boolean setReadable(boolean readable, boolean ownerOnly) 
//    {
//    	return false;
//    }
//
//    public boolean setExecutable(boolean executable, boolean ownerOnly) 
//    {
//    	return false;
//    }
//
//    public boolean canExecute() 
//    {
//    	return true;
//    }

    public static File[] listRoots() 
    {
    	return new RemoteFile[]{new RemoteFile("root", "z:", true)};
    }

//    public long getTotalSpace() 
//    {
//    	return 0;
//    }
//
//    public long getFreeSpace() 
//    {
//    	return 0;
//    }
//    
//    public long getUsableSpace() 
//    {
//    	return 0;
//    }

	public String toString()
	{
		return "RemoteFile(name=" + name + ", dir=" + dir + ", path="+getPath()+")";
	}
}
