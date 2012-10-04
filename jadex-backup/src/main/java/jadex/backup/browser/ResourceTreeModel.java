package jadex.backup.browser;

import jadex.backup.resource.FileInfo;
import jadex.backup.resource.IResourceService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.Future;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.ThreadSuspendable;

import java.util.ArrayList;
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
		return node instanceof Tuple2 && ((Tuple2<?,?>)node).getFirstEntity()!=null && !((FileInfo)((Tuple2<?,?>)node).getFirstEntity()).isDirectory();
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
				final Map<String, List<IResourceService>>	found	= new HashMap<String, List<IResourceService>>();
				IIntermediateFuture<IResourceService>	fut	= SServiceProvider.getServices(ea.getServiceProvider(), IResourceService.class, RequiredServiceInfo.SCOPE_GLOBAL);
				fut.addResultListener(new IntermediateDefaultResultListener<IResourceService>()
				{
					public void intermediateResultAvailable(IResourceService result)
					{
						synchronized(found)
						{
							List<IResourceService>	list	= found.get(result.getResourceId());
							if(list==null)
							{
								list	= new ArrayList<IResourceService>();
								found.put(result.getResourceId(), list);
							}
							list.add(result);
						}
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
					ret	= new ArrayList<Object>();
					for(List<IResourceService> l: found.values())
					{
						FileInfo	fi	= new FileInfo();
						fi.setLocation("./");
						fi.setDirectory(true);
						ret.add(new Tuple2<FileInfo, List<IResourceService>>(fi, l));
					}
				}			
			}
				
			// Subnodes are a tuple of file info and resource service list.
			else if(node instanceof Tuple2)
			{
				final Tuple2<?,?>	res	= (Tuple2<?,?>)node;
				
				// For speed: Query all services in parallel and continue on first result.
				final Future<List<Object>>	fut	= new Future<List<Object>>();
				for(Object rs: (List<?>)res.getSecondEntity())
				{
					FileInfo	fi	= (FileInfo)res.getFirstEntity();
					((IResourceService)rs).getFiles(fi).addResultListener(new IResultListener<FileInfo[]>()
					{
						public void resultAvailable(FileInfo[] result)
						{
							List<Object>	ret	= new ArrayList<Object>();
							for(FileInfo info: result)
							{
								ret.add(new Tuple2<FileInfo, List<IResourceService>>(info, (List<IResourceService>)res.getSecondEntity()));
							}
							fut.setResultIfUndone(ret);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// ignored.
						}
					});
				}
				try
				{
					// Hack!!! Block swing thread until results are available
					ret	= fut.get(new ThreadSuspendable(), 3000);
				}
				catch(TimeoutException e)
				{
					ret	= new ArrayList<Object>();
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
