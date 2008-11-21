package jadex.tools.common.modeltree;

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
}
