package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.IRemoteFilter;
import jadex.commons.collection.SortedList;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.TreeExpansionHandler;
import jadex.commons.gui.future.SwingDefaultResultListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

/**
 *  Node object representing a service container.
 */
public class DirNode extends FileNode
{
	//-------- attributes --------
	
	// hack: should belong to the model
	/** The factory. */
	protected INodeFactory factory;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public DirNode(ITreeNode parent, AsyncTreeModel model, JTree tree, File file, 
		IIconCache iconcache, INodeFactory factory)
	{
		super(parent, model, tree, file, iconcache);
		
//		assert file.isDirectory(): file;
		this.factory = factory;
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
					ITreeNode node = getModel().getNode(file);//.getAbsolutePath());
					if(node!=null)
					{
//						lis.resultAvailable(node);
						nodes.add(node);
					}
					else
					{
//						lis.resultAvailable(ModelTreePanel.createNode(DirNode.this, model, tree, file, iconcache, filter, null));
						nodes.add(factory.createNode(DirNode.this, model, tree, file, iconcache, null, factory));
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
		if(file==null)
			return new Future(new ArrayList());
		
		final Future ret = new Future();
		final File[] files = file.listFiles();
		final CollectionResultListener lis = new CollectionResultListener(files==null? 0: files.length, true, new DelegationResultListener(ret));
		
		if(files!=null)
		{
//			System.out.println("name: "+toString()+" files length: "+files.length);
			for(int i=0; i<files.length; i++)
			{
				IRemoteFilter	filter	= factory.getFileFilter();
				if(filter==null)
				{
					lis.resultAvailable(files[i]);
				}
				else
				{
					final File file = files[i];
//					System.out.println("in: "+file);
					filter.filter(files[i]).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
//							System.out.println("out: "+file);
							if(((Boolean)result).booleanValue())
								lis.resultAvailable(file);
							else
								lis.exceptionOccurred(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							System.out.println("out: "+file);
							lis.exceptionOccurred(null);
						}
					});
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Refresh the node.
	 */
	public void refresh(boolean recurse)
	{
		if(TreeExpansionHandler.isTreeExpanded(tree, new TreePath(model.buildTreePath(this).toArray())))
			super.refresh(recurse);
	}
	
	/**
	 *  Check if the node is a leaf.
	 */
	public boolean	isLeaf()
	{
		assert SwingUtilities.isEventDispatchThread();

		return false;
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
			boolean	dir1	= o1 instanceof FileNode ? ((FileNode)o1).getFile().isDirectory() && !(o1 instanceof JarNode) : ((RemoteFileNode)o1).getRemoteFile().isDirectory() && !(o1 instanceof RemoteJarNode);
			boolean	dir2	= o2 instanceof FileNode ? ((FileNode)o2).getFile().isDirectory() && !(o2 instanceof JarNode) : ((RemoteFileNode)o2).getRemoteFile().isDirectory() && !(o2 instanceof RemoteJarNode);

			int	ret;
			if(dir1 && !dir2)
				ret	= -1;
			else if(!dir1 && dir2)
				ret	= 1;
			else
				ret	= name1.compareTo(name2);
			
//			System.out.println("comp: "+ret+" "+name1+" "+name2);
			return ret;
		}
	};
}
