package jadex.base.gui.filetree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.SwingUtilities;

import jadex.base.RemoteJarFile;
import jadex.base.SRemoteGui;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.filetransfer.BunchFileData;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.commons.Tuple2;
import jadex.commons.collection.SortedList;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.gui.future.SwingIntermediateResultListener;

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
			Map<String, Collection<FileData>> rjfentries = new LinkedHashMap<String, Collection<FileData>>();
			List<FileData> entries = new ArrayList<FileData>();
			
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
					
					for(Tuple2<String, RemoteJarFile> tmp: dat.getEntries())
					{
						Collection<FileData> dir = rjfentries.get(tmp.getFirstEntity());
						if(dir==null)
						{
							dir	= new ArrayList<FileData>();
							rjfentries.put(tmp.getFirstEntity(), dir);
						}
						RemoteJarFile rjn = tmp.getSecondEntity();
						rjn.setJarEntries(rjfentries);
						dir.add(rjn);
					}
					
//					System.out.println("rjfentries size: "+rjfentries.size());
				}
				else
				{
					entries.add(file);
				}
			}
			
		    public void finished()
		    {
		    	Collection<FileData> files = null;
		    	
		    	if(rjfentries.size()>0)
		    	{
		    		if(rjfentries.keySet().size()==1)
		    		{
		    			files = new ArrayList<FileData>();
		    			String v = rjfentries.keySet().iterator().next();
			    		for(FileData jf: rjfentries.get(v))
			    		{
			    			files.add(jf);
			    		}
		    		}
		    		else
		    		{
//			    		RemoteJarFile rjf = new RemoteJarFile(jad.getName(), jad.getAbsolutePath(), true, 
//							FileData.getDisplayName(jad), rjfentries, "/", jad.getLastModified(), File.separatorChar, SUtil.getPrefixLength(jad), jad.length());
			    		RemoteJarFile rjf = new RemoteJarFile(file.getFilename(), null, true, 
							null, rjfentries, "/", 0, File.separatorChar, 0, rjfentries.size());
						files = rjf.listFiles();
		    		}
		    		rjfentries = null;
//					System.out.println("size is: "+files.size());
		    	}
		    	else if(entries.size()>0)
		    	{
		    		files = entries;
		    	}
		    	
		    	if(files!=null)
		    	{
			    	for(FileData d: files)
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
		    	}
		    	
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
