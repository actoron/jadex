package jadex.backup.browser;

import jadex.backup.resource.FileMetaInfo;
import jadex.backup.resource.IResourceService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.ThreadSuspendable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *  Dynamic view of network resources.
 */
public class ResourceTreeModel	implements TreeModel
{
	//-------- constants --------
	
	/** Dummy object for the tree root. */
	protected static String	ROOT	= "root";
	
	//-------- attributes --------

	/** The external access. */
	protected IExternalAccess	ea;

	/** The cached tree contents. */
	protected Map<Object, List<Object>>	contents;
	
	/** The tree listeners. */
	protected List<TreeModelListener>	listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new tree model.
	 */
	public ResourceTreeModel(IExternalAccess ea)
	{
		this.ea	= ea;
	}
	
	//-------- node mgmt. --------
	
	/**
	 *  Get the root of the tree.
	 */
	public Object getRoot()
	{
		return ROOT;
	}
	
	/**
	 *  Get a child of a node.
	 */
	public Object getChild(Object parent, int index)
	{
		List<Object>	children	= fetchChildren(parent);
		return children.get(index);
	}
	
	/**
	 *  Get the number of children.
	 */
	public int getChildCount(Object parent)
	{
		List<Object>	children	= fetchChildren(parent);
		return children.size();
	}
	
	/**
	 *  Get the index of a child.
	 */
	public int getIndexOfChild(Object parent, Object child)
	{
		List<Object>	children	= fetchChildren(parent);
		return children.indexOf(child);
	}
	
	/**
	 *  Check if a node is a leaf.
	 */
	public boolean isLeaf(Object node)
	{
		return node instanceof Tuple2 && !((FileMetaInfo)((Tuple2<?,?>)node).getFirstEntity()).getData().isDirectory();
	}
	
	/**
	 *  Called when a node is set.
	 */
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		throw new UnsupportedOperationException();
	}
	
	//-------- listeners --------
	
	/**
	 *  Add a tree model listener.
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		if(listeners==null)
		{
			listeners	= new ArrayList<TreeModelListener>();
		}
		listeners.add(l);
	}
	
	/**
	 *  Remove a tree model listener.
	 */
	public void removeTreeModelListener(TreeModelListener l)
	{
		if(listeners!=null)
		{
			listeners.remove(l);
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Fetch the children of a node.
	 *  @param node The node.
	 */
	protected List<Object>	fetchChildren(Object node)
	{
		List<Object>	ret	= contents!=null ? contents.get(node) : null;
		
		if(ret==null)
		{
			// To find top-level nodes start global resource search.
			if(ROOT.equals(node))
			{
				final List<Tuple2<FileMetaInfo, IResourceService>>	found	= new ArrayList<Tuple2<FileMetaInfo, IResourceService>>();
				IIntermediateFuture<IResourceService>	fut	= SServiceProvider.getServices(ea.getServiceProvider(), IResourceService.class, RequiredServiceInfo.SCOPE_GLOBAL);
				fut.addResultListener(new IntermediateDefaultResultListener<IResourceService>()
				{
					public void intermediateResultAvailable(final IResourceService remote)
					{
						remote.getFileInfo("/").addResultListener(new DefaultResultListener<FileMetaInfo>()
						{
							public void resultAvailable(FileMetaInfo fi)
							{
								synchronized(found)
								{
									found.add(new Tuple2<FileMetaInfo, IResourceService>(fi, remote));
								}
							}
						});
					}
				});
				try
				{
					// Hack!!! Block swing thread until results are available
					fut.get(new ThreadSuspendable(), 3000);
				}
				catch(TimeoutException e)
				{
				}
				
				synchronized(found)
				{
					ret	= new ArrayList<Object>(found);
				}			
			}
				
			// Subnodes are a tuple of file info and resource service.
			else if(node instanceof Tuple2)
			{
				ret	= new ArrayList<Object>();
				final Tuple2<?,?>	res	= (Tuple2<?,?>)node;
				FileMetaInfo	fi	= (FileMetaInfo)res.getFirstEntity();
				IResourceService	remote	= (IResourceService)res.getSecondEntity();
				try
				{
					// Hack!!! Block swing thread until results are available
					if(fi.getData().isDirectory())
					{
						Collection<FileMetaInfo>	list	= remote.getDirectoryContents(fi).get(new ThreadSuspendable(), 3000);
						for(FileMetaInfo tmp : list)
						{
							if(tmp.isExisting())
							{
								ret.add(new Tuple2<FileMetaInfo, IResourceService>(tmp, remote));
							}
						}
					}
				}
				catch(TimeoutException e)
				{
				}
			}
			
			if(contents==null)
			{
				contents	= new HashMap<Object, List<Object>>();
			}
			contents.put(node, ret);
		}
		
		return ret;
	}
}
