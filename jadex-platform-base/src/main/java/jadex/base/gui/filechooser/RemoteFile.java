package jadex.base.gui.filechooser;

import jadex.base.gui.filetree.FileData;

import java.io.File;
import java.io.IOException;


/**
 * 
 */
public class RemoteFile extends File
{
	//-------- attributes --------
	
	/** The file data. */
	protected FileData filedata;
	
	//-------- constructors -------- 
	
	/**
	 *  Create a directory representation of a jar file.
	 */
	public RemoteFile(FileData filedata)
	{
		super(filedata.getPath());
		this.filedata = filedata;
	}
	
	//-------- File methods --------
	
	/**
	 *  Get the filedata.
	 *  @return the filedata.
	 */
	public FileData getFiledata()
	{
		return filedata;
	}

	public boolean isDirectory()
	{
		return filedata.isDirectory();
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
    	return filedata.getFilename();
    }

    public static String getParent(String oldpath) 
    {
//    	String oldpath = getPath();
    	String ret = null;
    	if(oldpath!=null)
    	{
      		int findex = oldpath.indexOf(separatorChar, 1);
      		int lindex = oldpath.lastIndexOf(separatorChar);
      		if(findex!=-1 && lindex!=0 && lindex!=oldpath.length()-1)
    		{
      			boolean last = findex==lindex || findex==lindex+1;
    			ret = oldpath.substring(0, last? lindex+1: lindex);
    		}
    	}
//    	System.out.println("getPa: "+oldpath+" "+ret);
    	return ret;
    }

    public File getParentFile() 
    {
    	RemoteFile ret = null;
    	
    	String pa = getParent();
    	if(pa!=null)
    	{
    		String name = "";
    		String path = pa;
    		int findex = pa.indexOf(separatorChar, 1);
      		int lindex = pa.lastIndexOf(separatorChar);
     		if(findex!=-1 && lindex!=0 && lindex!=pa.length())
    		{
    			name = pa.substring(lindex+1);
    		}
    		ret = new RemoteFile(new FileData(name, path, true, name));
    	}
//    	System.out.println("getPaF: "+getPath()+" "+pa);
    	return ret;
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
    // Java bug
    // http://bugs.sun.com/view_bug.do;jsessionid=34602d7fb6f3f5408b98e914c3b?bug_id=6691325
    public String getCanonicalPath() throws IOException 
    {
//    	return ".";
    	return getPath();
    }

    public File getCanonicalFile() throws IOException 
    {
    	return this;
    }
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
    	return !filedata.isDirectory();
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

    public String[] list() 
    {
    	return null;
    };
    
	public String toString()
	{
		return "RemoteFile(filedata="+filedata+")";
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		System.out.println(getParent("C:\\"));
		System.out.println(getParent("C:\\\\"));
		System.out.println(getParent("C:\\projects\\jadex"));
//		File f = new File("C:\\");
//		System.out.println(SUtil.arrayToString(f.listFiles()));
	}
}
