package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentProperties;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DefaultResultListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;

/**
 *  Node object representing a service container.
 */
public class DirNode	extends AbstractTreeNode
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
	public DirNode(ITreeNode parent, AsyncTreeModel model, JTree tree, File file)
	{
		super(parent, model, tree);
		
		assert file!=null && file.isDirectory();
		
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
		File[] files = file.listFiles(); // explorer.getFileFilter()
		final List children = new ArrayList();
		
		CollectionResultListener lis = new CollectionResultListener(files.length, true, 
			new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				setChildren((List)result);
			}
		});
		
		for(int i=0; i<files.length; i++)
		{
			ModelTreePanel.createNode(this, model, tree, files[i]).addResultListener(lis);
		}
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
