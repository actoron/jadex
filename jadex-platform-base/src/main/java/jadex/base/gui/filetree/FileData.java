package jadex.base.gui.filetree;

import jadex.base.gui.filechooser.RemoteFile;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

/**
 *  A file data represents a java.io.File that
 *  can be transferred to remote address spaces.
 *  Does only transfer file information, not the
 *  binary data itself.
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
	
	/** The display name. */
	protected String displayname;

//	/** The flag if is root dir. */
//	protected boolean root;
	
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
	public FileData(String filename, String path, boolean directory, String displayname)//, boolean root)
	{
		this.filename = filename;
		this.path = path;
		this.directory = directory;
		this.displayname = displayname;
//		this.root = root;
	}
	
	/**
	 *  Create a new remote file.
	 */
	public FileData(File file)
	{
		this.filename = file.getName();
		this.path = file.getAbsolutePath();
		this.directory = file.isDirectory();
		this.displayname = getDisplayName(file);
//		this.root = SUtil.arrayToSet(file.listRoots()).contains(file);
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
		String ret = FileSystemView.getFileSystemView().getSystemDisplayName(file);
		if(ret==null || ret.length()==0)
			ret = file.getName();
		if(ret==null || ret.length()==0)
			ret = file.getPath();
		return ret;
	}
	
//	/**
//	 *  Get the root.
//	 *  @return the root.
//	 */
//	public boolean isRoot()
//	{
//		return root;
//	}
//
//	/**
//	 *  Set the root.
//	 *  @param root The root to set.
//	 */
//	public void setRoot(boolean root)
//	{
//		this.root = root;
//	}

	/**
	 *  Convert remote files to files.
	 */
	public static RemoteFile[] convertToFiles(FileData[] remfiles)
	{
		RemoteFile[] ret = remfiles==null? new RemoteFile[0]: new RemoteFile[remfiles.length];
		for(int i=0; i<ret.length; i++)
		{
			ret[i] = new RemoteFile(remfiles[i].getFilename(), remfiles[i].getPath(), remfiles[i].isDirectory());
		}
		return ret;
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
}
