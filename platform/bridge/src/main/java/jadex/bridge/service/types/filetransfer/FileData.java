package jadex.bridge.service.types.filetransfer;

import java.io.File;

import jadex.commons.SUtil;


/**
 *  A file data represents a java.io.File that
 *  can be transferred to remote address spaces.
 *  Does only transfer file information, not the
 *  binary data itself.
 *  
 *  This class is necessary, because java.io.File
 *  makes too many assumptions about the underlying OS
 *  (slashed, root directories), such that File objects
 *  are not portable. I.e. a Windows path produces a
 *  corrupted File, when instantiated under Linux and vice versa.
 */
public class FileData
{
	//-------- attributes --------
	
	/** The file name. */
	protected String filename;
	
	/** The path. */
	protected String path;
	
	/** The boolean for directory. */
	protected boolean directory;
	
	/** The boolean for existance. */
	protected boolean exists;
	
	/** The display name. */
	protected String displayname;

	/** The last modified date. */
	protected long lastmodified;
	
	/** The separator char. */
	protected char separator;

	/** The prefix length. */
	protected int prefix;
	
	/** The file size. */
	protected long filesize;
	
	//-------- constructors --------

	/**
	 *  Create a new remote file.
	 */
	public FileData()
	{
		// Needed for bean creation.
	}

	/**
	 *  Create a new remote file.
	 */
	public FileData(String filename, String path, boolean directory, boolean exists,
		String displayname, long lastmodified, char separator, int prefix, long filesize)
	{
		this.filename = filename;
		this.path = path;
		this.directory = directory;
		this.exists	= exists;
		this.displayname = displayname;
		this.lastmodified = lastmodified;
		this.separator = separator;
		this.prefix = prefix;
		this.filesize = filesize;
	}
	
	/**
	 *  Create a new remote file.
	 */
	public FileData(File file)
	{
		boolean	floppy	= SUtil.isFloppyDrive(file);
		
		this.filename = file.getName();
		this.path = file.getPath();
		this.directory = floppy || file.isDirectory();	// Hack to avoid access to floppy disk.
		this.exists	= floppy || file.exists();	// Hack to avoid access to floppy disk.
		this.displayname = getDisplayName(file);
		this.lastmodified = floppy ? 0 : file.lastModified();	// Hack to avoid access to floppy disk.
//		this.root = SUtil.arrayToSet(file.listRoots()).contains(file);
		this.separator = File.separatorChar;
		this.prefix = SUtil.getPrefixLength(file);
		this.filesize = directory ? 0 : file.length();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the filename.
	 *  @return the filename.
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 *  Set the filename.
	 *  @param filename The filename to set.
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	/**
	 *  Get the path.
	 *  @return the path.
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 *  Set the path.
	 *  @param path The path to set.
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 *  Get the directory.
	 *  @return the directory.
	 */
	public boolean isDirectory()
	{
		return directory;
	}

	/**
	 *  Set the directory.
	 *  @param directory The directory to set.
	 */
	public void setDirectory(boolean directory)
	{
		this.directory = directory;
	}

	/**
	 *  Get the exists flag.
	 *  @return the exists flag.
	 */
	// Hack!!! Strange method name to conform to bean spec.
	public boolean isExists()
	{
		return exists;
	}

	/**
	 *  Set the exists flag.
	 *  @param exists The exists flag to set.
	 */
	public void setExists(boolean exists)
	{
		this.exists = exists;
	}

	/**
	 *  Get the displayname.
	 *  @return the displayname.
	 */
	public String getDisplayName()
	{
		return displayname;
	}

	/**
	 *  Set the displayname.
	 *  @param displayname The displayname to set.
	 */
	public void setDisplayName(String displayname)
	{
		this.displayname = displayname;
	}
	
	/**
	 *  Get the display name for a file.
	 */
	public static String getDisplayName(File file)
	{
		String	ret	= SUtil.isFloppyDrive(file) ? null : SUtil.getDisplayName(file);	// Hack to avoid access to floppy disk.
		
		if(ret==null || ret.length()==0)
			ret = file.getName();
		if(ret==null || ret.length()==0)
			ret = file.getPath();
		return ret;
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
	 *  Set the lastmodified.
	 *  @param lastmodified The lastmodified to set.
	 */
	public void setLastModified(long lastmodified)
	{
		this.lastmodified = lastmodified;
	}
	
	/**
	 *  Convert files to remote files.
	 */
	public static FileData[] convertToRemoteFiles(File[] files)
	{
		FileData[] ret = files==null? new FileData[0]: new FileData[files.length];
		for(int i=0; i<ret.length; i++)
		{
			ret[i] = new FileData(files[i]);
		}
		return ret;
	}
	
	/**
	 *  Get the separator char.
	 *  @return the separator char.
	 */
	public char getSeparatorChar()
	{
		return separator;
	}

	/**
	 *  Set the separator char.
	 *  @param separator The separator char to set.
	 */
	public void setSeparatorChar(char separator)
	{
		this.separator = separator;
	}
	
	/**
	 *  Get the prefix.
	 *  @return the prefix.
	 */
	public int getPrefixLength()
	{
		return prefix;
	}

	/**
	 *  Set the prefix.
	 *  @param prefix The prefix to set.
	 */
	public void setPrefixLength(int prefix)
	{
		this.prefix = prefix;
	}
	
	/**
	 *  Get the filesize.
	 *  @return The filesize.
	 */
	public long getFileSize()
	{
		return filesize;
	}

	/**
	 *  Set the filesize.
	 *  @param filesize The filesize to set.
	 */
	public void setFileSize(long filesize)
	{
		this.filesize = filesize;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "FileData(filename=" + filename + ", path=" + path
			+ ", directory=" + directory + ", displayname=" + displayname+ ")";
	}
}
