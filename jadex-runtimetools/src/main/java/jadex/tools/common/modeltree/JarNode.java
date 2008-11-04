package jadex.tools.common.modeltree;


import javax.swing.tree.TreeNode;


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
	public JarNode(TreeNode parent, String jar, INodeFunctionality nof)
	{
		super(parent, new JarAsDirectory(jar), nof);
	}
	
	/**
	 *  Create a jar node.
	 */
	public JarNode(TreeNode parent, String jar, INodeFunctionality nof, boolean valid)
	{
		super(parent, new JarAsDirectory(jar), nof, valid);
	}
}
