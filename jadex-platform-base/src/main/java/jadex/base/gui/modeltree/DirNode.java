package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.IFuture;
import jadex.commons.IRemoteFilter;
import jadex.commons.IntermediateFuture;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;

/**
 *  Node object representing a service container.
 */
public class DirNode extends FileNode
{
	//-------- attributes --------
	
	/** The filter. */
	protected IRemoteFilter filter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public DirNode(ITreeNode parent, AsyncTreeModel model, JTree tree, File file, ModelIconCache iconcache, IRemoteFilter filter)
	{
		super(parent, model, tree, file, iconcache);
		assert file.isDirectory();
		this.filter = filter;
//		System.out.println("node: "+getClass()+" "+desc.getName());
	}
	
	//-------- AbstractComponentTreeNode methods --------
	
	/**
	 *  Asynchronously search for children.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren(boolean force)
	{
		listFiles().addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				Collection files = (Collection)result;
				CollectionResultListener lis = new CollectionResultListener(files.size(), true, 
					new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						setChildren((List)result);
					}
				});
				
				for(Iterator it=files.iterator(); it.hasNext();)
				{
					File file = (File)it.next();
					ITreeNode node = getModel().getNode(file);
					if(node!=null)
					{
						lis.resultAvailable(node);
					}
					else
					{
						ModelTreePanel.createNode(DirNode.this, model, tree, file, iconcache, filter, null)
							.addResultListener(lis);
					}
				}
			}
		});
	}
	
	/**
	 *	Get a file filter according to current file type settings. 
	 */
	protected IFuture listFiles()
	{
		final IntermediateFuture ret = new IntermediateFuture();
		final File[] files = file.listFiles();
		final CollectionResultListener lis = new CollectionResultListener(files.length, true, new DelegationResultListener(ret));
		
		for(int i=0; i<files.length; i++)
		{
			if(filter==null)
			{
				lis.resultAvailable(files[i]);
			}
			else
			{
				final File file = files[i];
				filter.filter(files[i]).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						if(((Boolean)result).booleanValue())
							lis.resultAvailable(file);
						else
							lis.exceptionOccurred(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						lis.exceptionOccurred(null);
					}
				});
			}
		}
		return ret;
	}
	
}
