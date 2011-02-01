package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IRemoteFilter;
import jadex.commons.concurrent.CollectionResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.xml.annotation.XMLClassname;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;

/**
 * 
 */
public class RemoteDirNode extends RemoteFileNode
{
	//-------- attributes --------
	
	/** The filter. */
	protected IRemoteFilter filter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public RemoteDirNode(ITreeNode parent, AsyncTreeModel model, JTree tree, RemoteFile file, 
		ComponentIconCache iconcache, IRemoteFilter filter, IExternalAccess exta)
	{
		super(parent, model, tree, file, iconcache, exta);
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
					ModelTreePanel.createNode(RemoteDirNode.this, model, tree, 
						it.next(), iconcache, filter, exta).addResultListener(lis);
				}
			}
		});
	}
	
	/**
	 *	Get a file filter according to current file type settings. 
	 */
	protected IFuture listFiles()
	{
		final Future ret = new Future();
		
		final RemoteFile myfile = file;
		final IRemoteFilter myfilter = filter;
		exta.scheduleStep(new IComponentStep()
		{
			@XMLClassname("listFiles")
			public Object execute(IInternalAccess ia)
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
										lis.resultAvailable(new RemoteFile(file.getName(), file.getAbsolutePath(), file.isDirectory()));
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
		
		return ret;
	}
	
}
