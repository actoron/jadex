package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.IRemoteFilter;
import jadex.commons.collection.SortedList;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SwingDefaultResultListener;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
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
	protected void	searchChildren()
	{
		listFiles().addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				Collection files = (Collection)result;
//				CollectionResultListener lis = new CollectionResultListener(files.size(), true, 
//					new DefaultResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						setChildren((List)result);
//					}
//				});
				
				List	nodes	= new SortedList(FILENODE_COMPARATOR, true);
//				List	nodes	= new ArrayList();
				for(Iterator it=files.iterator(); it.hasNext();)
				{
					File file = (File)it.next();
					ITreeNode node = getModel().getNode(file);
					if(node!=null)
					{
//						lis.resultAvailable(node);
						nodes.add(node);
					}
					else
					{
//						lis.resultAvailable(ModelTreePanel.createNode(DirNode.this, model, tree, file, iconcache, filter, null));
						nodes.add(ModelTreePanel.createNode(DirNode.this, model, tree, file, iconcache, filter, null));
					}
				}

				setChildren(nodes);
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
	
	/**
	 *  Comparator for filenodes.
	 */
	public static final Comparator FILENODE_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String	name1	= o1 instanceof FileNode ? ((FileNode)o1).getFile().getName() : ((RemoteFileNode)o1).getRemoteFile().getFilename();
			String	name2	= o2 instanceof FileNode ? ((FileNode)o2).getFile().getName() : ((RemoteFileNode)o2).getRemoteFile().getFilename();
			boolean	dir1	= o1 instanceof FileNode ? ((FileNode)o1).getFile().isDirectory() : ((RemoteFileNode)o1).getRemoteFile().isDirectory();
			boolean	dir2	= o2 instanceof FileNode ? ((FileNode)o2).getFile().isDirectory() : ((RemoteFileNode)o2).getRemoteFile().isDirectory();

			int	ret;
			if(dir1 && !dir2)
				ret	= -1;
			else if(!dir1 && dir2)
				ret	= 1;
			else
				ret	= name1.compareTo(name2);
			
			return ret;
		}
	};
}
