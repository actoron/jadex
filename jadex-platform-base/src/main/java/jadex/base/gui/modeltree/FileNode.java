package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentProperties;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;

/**
 *  Node object representing a service container.
 */
public class FileNode	extends AbstractTreeNode
{
	//-------- attributes --------
	
	/** The file. */
	protected File file;
	
	/** The properties component (if any). */
	protected ComponentProperties	propcomp;
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public FileNode(ITreeNode parent, AsyncTreeModel model, JTree tree, File file)
	{
		super(parent, model, tree);
		
		assert file!=null;
		
//		System.out.println("node: "+getClass()+" "+desc.getName());
		
		this.file = file;
		
		model.registerNode(this);
	}
	
	//-------- AbstractComponentTreeNode methods --------
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return file.toString();
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
	{
		return null;//iconcache.getIcon(this, desc.getType());
	}
	
	/**
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	public void refresh(boolean recurse, boolean force)
	{
//		cms.getComponentDescription(desc.getName()).addResultListener(new SwingDefaultResultListener()
//		{
//			public void customResultAvailable(Object result)
//			{
//				FileTreeNode.this.desc	= (IComponentDescription)result;
//				getModel().fireNodeChanged(FileTreeNode.this);
//			}
//			public void customExceptionOccurred(Exception exception)
//			{
//				// ignore
//			}
//		});

		super.refresh(recurse, force);
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren(boolean force)
	{
	}
	
	//-------- methods --------
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return file.getName();
	}

	/**
	 *  True, if the node has properties that can be displayed.
	 */
	public boolean	hasProperties()
	{
		return false;
//		return true;
	}
	
	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent()
	{
		return null;
//		if(propcomp==null)
//		{
//			propcomp	= new ComponentProperties();
//		}
//		propcomp.setDescription(desc);
//		return propcomp;
	}
}
