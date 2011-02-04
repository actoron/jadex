package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.IRemoteFilter;

import java.io.File;

import javax.swing.JTree;

/**
 *  Node for jar file.
 */
public class JarNode extends DirNode
{
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public JarNode(ITreeNode parent, AsyncTreeModel model, JTree tree, File file, ModelIconCache iconcache, IRemoteFilter filter)
	{
		super(parent, model, tree, new JarAsDirectory(file.getAbsolutePath()), iconcache, filter);
		assert file.isDirectory();
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
