package jadex.base.gui.filetree;

import java.io.File;

import javax.swing.JTree;

import jadex.base.JarAsDirectory;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;

/**
 *  Node for jar file.
 */
public class JarNode extends DirNode
{
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public JarNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, File file, IIconCache iconcache, INodeFactory factory)
	{
		super(parent, model, tree, file instanceof JarAsDirectory? file: new JarAsDirectory(file.getPath()), iconcache, factory);
//		System.out.println("node: "+getClass()+" "+desc.getName());
	}
	
	//-------- AbstractComponentTreeNode methods --------
	
	/**
	 *  Asynchronously search for children.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		((JarAsDirectory)getFile()).refresh();
		super.searchChildren();
	}
	
	//-------- methods --------
	
//	/**
//	 *  Get the file represented by this node.
//	 */
//	public File getFile()
//	{
//		assert file!=null;
//		return this.file;
//	}
}
