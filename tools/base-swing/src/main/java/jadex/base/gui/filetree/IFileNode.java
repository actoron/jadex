package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.ISwingTreeNode;

/**
 *  Common interface for all nodes of the file tree
 *  (except the invisible root node).
 *  Provides unified access to local and remote files.
 */
public interface IFileNode extends ISwingTreeNode
{
	/**
	 *  Get the file name.
	 */
	public String	getFileName();
	
	/**
	 *  Get the file path.
	 */
	public String	getFilePath();
	
	/**
	 *  Check if the file is a directory. 
	 */
	public boolean	isDirectory();
	
	/**
	 *  Get the file size.
	 */
	public long getFileSize();
}
