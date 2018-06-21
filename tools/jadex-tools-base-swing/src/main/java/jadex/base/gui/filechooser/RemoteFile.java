package jadex.base.gui.filechooser;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

import jadex.bridge.service.types.filetransfer.FileData;


/**
 *  File wrapper for remote files.
 *  Is necessary because JFileChooser works
 *  with File objects only.
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

	/**
	 *  Test if is a directory.
	 *  @return True, if directory.
	 */
	public boolean isDirectory()
	{
//		System.out.println("isDir: "+getName()+" "+filedata.isDirectory());
		return filedata.isDirectory();
	}
	
	// These methods are based on list(). No need to overwrite.
	public File[] listFiles(FileFilter filter)
	{
		return super.listFiles(filter);
	}
	
	public File[] listFiles(FilenameFilter filter)
	{
		return super.listFiles(filter);		
	}
	
	public File[] listFiles()
	{
		return super.listFiles();		
	}
	
	/**
	 *  Get the last modified date.
	 */
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
		return filedata.isExists();
	}
	
	/**
	 *  Get the file name.
	 *  @return The file name.
	 */
    public String getName() 
    {
    	return filedata.getFilename();
    }
    
    /**
     *  Get the path.
     */
    public String getAbsolutePath()
    {
    	return filedata.getPath();
    }
    
    /**
     *  Get the path.
     */
    public String getPath()
    {
    	return filedata.getPath();
    }
    
    /**
     *  Get the parent name.
     *  @return The parent name.
     */
    public String getParent() 
    {
    	String ret = null;
    	int index = getPath().lastIndexOf(filedata.getSeparatorChar());
    	if(index < filedata.getPrefixLength()) 
    	{
    	    if((filedata.getPrefixLength() > 0) && (getPath().length() > filedata.getPrefixLength()))
    	    	ret =  getPath().substring(0, filedata.getPrefixLength());
    	}
    	else
    	{
    		ret = getPath().substring(0, index);
    	}
//    	return path.substring(0, index);
//    	super.getParent()
//    	String ret = null;
//    	String oldpath = getPath();
//    	if(oldpath!=null)
//    	{
//      		int findex = oldpath.indexOf(filedata.getSeparatorChar(), 1);
//      		int lindex = oldpath.lastIndexOf(filedata.getSeparatorChar());
//      		if(findex!=-1 && lindex!=0 && lindex!=oldpath.length()-1)
//    		{
//      			boolean last = findex==lindex || findex==lindex+1;
//    			ret = oldpath.substring(0, last? lindex+1: lindex);
//    		}
//    	}
    	
//    	System.out.println("getPa: "+getPath()+" "+ret);
    	return ret;
    }

    /**
     *  Get the parent file.
     */
    public File getParentFile() 
    {
//    	String p = this.getParent();
//    	if(p == null) return null;
//    	return new File(p, this.prefixLength);
    	
    	RemoteFile ret = null;
    	
    	String pa = getParent();
    	if(pa!=null)
    	{
    		String name = "";
    		String path = pa;
    		int findex = pa.indexOf(filedata.getSeparatorChar(), 1);
      		int lindex = pa.lastIndexOf(filedata.getSeparatorChar());
     		if(findex!=-1 && lindex!=0 && lindex!=pa.length())
    		{
    			name = pa.substring(lindex+1);
    		}
    		ret = new RemoteFile(new FileData(name, path, true, true, name, filedata.getLastModified(), 
    			filedata.getSeparatorChar(), filedata.getPrefixLength(), 0));
    	}
//    	System.out.println("getPaF: "+getPath()+" "+pa);
    	return ret;
    }

    /**
     *  Test if absolute.
     *  @return Treu, if absolute.
     */
    public boolean isAbsolute() 
    {
    	return true;
    }

//    public String getAbsolutePath() 
//    {
//    	return "."; // filedata.getPath();
//    }

//    public File getAbsoluteFile() 
//    {
//    	return super.getAbsoluteFile();
//    }

    // Java bug
    // http://bugs.sun.com/view_bug.do?bug_id=6691325
    public String getCanonicalPath() throws IOException 
    {
    	return new File(".").getCanonicalPath();	// Hack!!! Give JFileChooser something that exists.
//    	return getPath();	// Does not work because, JFileChooser tests if path exists locally. GRRR!
    }

    /**
     *  Get the canonical name.
     *  Needs to return this because JFileCooser uses this method to check isTraverable(),
     *  i.e. if a dir can be looked into. Hence, the dir property must be kept in returned file.
     */
    public File getCanonicalFile() throws IOException 
    {
    	return this;
    }
   
    
//    public boolean canRead() 
//    {
//    	return super.canRead();
//    }
//
//    public boolean canWrite() 
//    {
//    	return super.canRead();
//    }

    /**
     *  Test if is a file.
     *  @return True, if is a file.
     */
    public boolean isFile() 
    {
    	return !filedata.isDirectory();
    }

    /**
     *  Test if file is hidden.
     *  @return True, if hidden.
     */
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

    /**
     *  List contained filed.
     *  JFileChooser uses the FileSystemView.
     */
    public String[] list() 
    {
    	return null;
    };
    
    /**
     *  Get the string representation.
     *  @return The string representation.
     */
	public String toString()
	{
		return "RemoteFile(filedata="+filedata+")";
	}
	
	/**
	 *  Convert remote files to files.
	 */
	public static RemoteFile[] convertToFiles(FileData[] remfiles)
	{
		RemoteFile[] ret = remfiles==null? new RemoteFile[0]: new RemoteFile[remfiles.length];
		for(int i=0; i<ret.length; i++)
		{
			ret[i] = new RemoteFile(remfiles[i]);
		}
		return ret;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args) throws Exception
	{
//		System.out.println(getParent("C:\\"));
//		System.out.println(getParent("C:\\\\"));
//		System.out.println(getParent("C:\\projects\\jadex"));
		File f = new File("C:\\projects\\jadex\\jadex-commons\\pom.xml");
		System.out.println(f.getName()+" "+f.getPath()+" "+f.getAbsolutePath()+" "+f.getCanonicalPath());
	}
}
