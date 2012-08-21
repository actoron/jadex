package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.IRemoteFilter;
import jadex.commons.collection.SortedList;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.annotations.Classname;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
	public RemoteDirNode(ITreeNode parent, AsyncTreeModel model, JTree tree, FileData file, 
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
		listFiles().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				Collection files = (Collection)result;
//				CollectionResultListener lis = new CollectionResultListener(files==null? 0: files.size(), true, 
//					new DefaultResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						setChildren((List)result);
//					}
//				});
//				
//				if(files!=null)
//				{
//					for(Iterator it=files.iterator(); it.hasNext();)
//					{
//						FileData file = (FileData)it.next();
//						ITreeNode node = getModel().getNode(file.toString());
//						if(node!=null)
//						{
//							lis.resultAvailable(node);
//						}
//						else
//						{
//							lis.resultAvailable(factory.createNode(RemoteDirNode.this, model, tree, 
//								file, iconcache, exta, factory));
//						}
//					}
//				}
				
				List	nodes	= new SortedList(DirNode.FILENODE_COMPARATOR, true);
//				List	nodes	= new ArrayList();
				for(Iterator it=files.iterator(); it.hasNext();)
				{
					FileData file = (FileData)it.next();
					ITreeNode node = getModel().getNode(file.toString());//.getAbsolutePath());
					if(node!=null)
					{
//						lis.resultAvailable(node);
						nodes.add(node);
					}
					else
					{
//						lis.resultAvailable(ModelTreePanel.createNode(DirNode.this, model, tree, file, iconcache, filter, null));
						nodes.add(factory.createNode(RemoteDirNode.this, model, tree, file, iconcache, exta, factory));
					}
				}

				setChildren(nodes);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// use logger to print warning?
			}
		});
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
	protected IFuture listFiles()
	{
		final Future ret = new Future();
		
		if(file instanceof RemoteJarFile)
		{
			RemoteJarFile myfile = (RemoteJarFile)file;
			Collection files = myfile.listFiles();
			
			ret.setResult(files);
			
//			for(int i=0; i<files.length; i++)
//			{
//				final CollectionResultListener lis = new CollectionResultListener(files.length, 
//					true, new DelegationResultListener(ret));
//				final RemoteJarFile ff = (RemoteJarFile)files[i];
//				filter.filter(files[i]).addResultListener(new IResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						if(((Boolean)result).booleanValue())
//							lis.resultAvailable(ff);
//						else
//							lis.exceptionOccurred(null);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						lis.exceptionOccurred(null);
//					}
//				});
//			}
		}
		else
		{
			final FileData myfile = file;
			final IRemoteFilter myfilter = factory.getFileFilter();
			exta.scheduleStep(new IComponentStep<Collection>()
			{
				@Classname("listFiles")
				public IFuture<Collection> execute(IInternalAccess ia)
				{
					Future ret = new Future();
					
					File f = new File(myfile.getPath());
					final File[] files = f.listFiles();
					if(files!=null)
					{
						final CollectionResultListener lis = new CollectionResultListener(files.length, 
							true, new DelegationResultListener(ret));
						
						for(int i=0; i<files.length; i++)
						{
							if(myfilter==null)
							{
								lis.resultAvailable(files[i]);
							}
							else
							{
								final File file = files[i];
								myfilter.filter(files[i]).addResultListener(new IResultListener()
								{
									public void resultAvailable(Object result)
									{
										if(((Boolean)result).booleanValue())
											lis.resultAvailable(new FileData(file));
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
					}
					else
					{
						ret.setResult(Collections.EMPTY_LIST);
					}
					
					return ret;
				}
			}).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
}
