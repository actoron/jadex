package jadex.base.gui.filetree;

import jadex.base.RemoteJarFile;
import jadex.base.SRemoteGui;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.deployment.BunchFileData;
import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.collection.SortedList;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.util.Collection;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 *  The remote dir node.
 */
public class RemoteDirNode extends RemoteFileNode
{
	//-------- attributes --------
	
	// hack: should belong to the model
	/** The factory. */
	protected INodeFactory factory;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public RemoteDirNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, FileData file, 
		IIconCache iconcache, IExternalAccess exta, INodeFactory factory)
	{
		super(parent, model, tree, file, iconcache, exta);
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
//		System.out.println("searchChildren: "+file.getFilename());
		
		final List nodes = new SortedList(DirNode.FILENODE_COMPARATOR, true);
		listFiles().addResultListener(new SwingIntermediateResultListener<FileData>(new IIntermediateResultListener<FileData>()
		{
			public void resultAvailable(Collection<FileData> result)
			{
				for(FileData file: result)
				{
					intermediateResultAvailable(file);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				// use logger to print warning?
			}
			
			public void intermediateResultAvailable(FileData file)
			{
				if(file instanceof BunchFileData)
				{
					BunchFileData dat = (BunchFileData)file;
					
					for(FileData d: dat.getEntries())
					{
						ISwingTreeNode node = getModel().getNode(d.toString());//.getAbsolutePath());
						if(node!=null)
						{
	//						lis.resultAvailable(node);
							if(!nodes.contains(node))
							{
								nodes.add(node);
							}
						}
						else
						{
							nodes.add(factory.createNode(RemoteDirNode.this, getModel(), tree, d, iconcache, exta, factory));
						}
					}
					setChildren(nodes);
				}
				else
				{
					List nodes = new SortedList(DirNode.FILENODE_COMPARATOR, true);
					ISwingTreeNode node = getModel().getNode(file.toString());//.getAbsolutePath());
					if(node!=null)
					{
//						lis.resultAvailable(node);
						if(!nodes.contains(node))
						{
							nodes.add(node);
						}
					}
					else
					{
						nodes.add(factory.createNode(RemoteDirNode.this, getModel(), tree, file, iconcache, exta, factory));
					}
					setChildren(nodes);
				}
			}
			
		    public void finished()
		    {
		    	setChildren(nodes);
		    }
		}));
		
//		listFiles().addResultListener(new SwingResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				Collection files = (Collection)result;
////				System.out.println("received results: "+files.size());
////				CollectionResultListener lis = new CollectionResultListener(files==null? 0: files.size(), true, 
////					new DefaultResultListener()
////				{
////					public void resultAvailable(Object result)
////					{
////						setChildren((List)result);
////					}
////				});
////				
////				if(files!=null)
////				{
////					for(Iterator it=files.iterator(); it.hasNext();)
////					{
////						FileData file = (FileData)it.next();
////						ISwingTreeNode node = getModel().getNode(file.toString());
////						if(node!=null)
////						{
////							lis.resultAvailable(node);
////						}
////						else
////						{
////							lis.resultAvailable(factory.createNode(RemoteDirNode.this, model, tree, 
////								file, iconcache, exta, factory));
////						}
////					}
////				}
//				
//				List	nodes	= new SortedList(DirNode.FILENODE_COMPARATOR, true);
////				List	nodes	= new ArrayList();
//				for(Iterator it=files.iterator(); it.hasNext();)
//				{
//					FileData file = (FileData)it.next();
//					ISwingTreeNode node = getModel().getNode(file.toString());//.getAbsolutePath());
//					if(node!=null)
//					{
////						lis.resultAvailable(node);
//						if(!nodes.contains(node))
//						{
//							nodes.add(node);
//						}
//					}
//					else
//					{
////						lis.resultAvailable(ModelTreePanel.createNode(DirNode.this, model, tree, file, iconcache, filter, null));
//						nodes.add(factory.createNode(RemoteDirNode.this, getModel(), tree, file, iconcache, exta, factory));
//					}
//				}
//
//				setChildren(nodes);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				// use logger to print warning?
//			}
//		}));
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
	 *	Get a file filter according to current file type settings. 
	 */
	protected ISubscriptionIntermediateFuture<FileData> listFiles()
	{
		final SubscriptionIntermediateFuture<FileData> ret = new SubscriptionIntermediateFuture<FileData>();
		
		if(file instanceof RemoteJarFile)
		{
			RemoteJarFile myfile = (RemoteJarFile)file;
			Collection<FileData> files = myfile.listFiles();
			ret.setResult(files);			
		}
		else
		{
			SRemoteGui.listFiles(file, factory.getFileFilter(), exta)
				.addResultListener(new IntermediateDelegationResultListener<FileData>(ret));
		}
		
		return ret;
	}
	
}
