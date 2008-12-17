package jadex.tools.common.modeltree;

import java.io.File;

/**
 *  A jar node represents a jar file. 
 */
public class JarNode extends DirNode
{
	//-------- constructors --------

	/**
	 *  Create a jar node.
	 *  Bean constructor.
	 */
	public JarNode()
	{
	}
	
	/**
	 *  Create a jar node.
	 */
	public JarNode(IExplorerTreeNode parent, String jar)
	{
		super(parent, new JarAsDirectory(jar));
	}

	//-------- methods --------
	
	/**
	 *  Get the file represented by this node.
	 */
	public File getFile()
	{
		if(file==null)
		{
			String	absolute	= new File(relative).getAbsolutePath();
			file	= new JarAsDirectory(absolute);
		}
		return this.file;
	}
}
