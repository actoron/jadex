package jadex.base.gui.filechooser;

import java.io.File;

/**
 * 
 */
public class MyFile extends File
{
	//-------- attributes --------
	
	/** The external access. */
	//protected IExternalAccess exta;
	
	/** The name. */
	protected String name;
	
	/** The boolean for directory. */
	protected boolean dir;
	
	//-------- constructors -------- 
	
	/**
	 *  Create a directory representation of a jar file.
	 */
	public MyFile(String name, String path, boolean dir)//, IExternalAccess exta)
	{
		super(path);
		this.name = name;
		this.dir = dir;
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
}