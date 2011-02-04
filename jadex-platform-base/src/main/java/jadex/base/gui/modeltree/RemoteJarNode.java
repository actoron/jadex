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
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.xml.annotation.XMLClassname;

import java.io.File;
import java.util.Collections;

import javax.swing.JTree;

/**
 *  Node for remote jar file.
 */
public class RemoteJarNode extends RemoteDirNode
{
	//-------- constructors --------
	
	/**
	 *  Create a new jar node.
	 */
	public RemoteJarNode(ITreeNode parent, AsyncTreeModel model, JTree tree, RemoteFile file, 
		ModelIconCache iconcache, IRemoteFilter filter, IExternalAccess exta)
	{
		super(parent, model, tree, file, iconcache, filter, exta);
//		System.out.println("node: "+getClass()+" "+desc.getName());
	}
	
	//-------- methods --------
	
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
				
				JarAsDirectory jad = new JarAsDirectory(myfile.getPath());
				jad.refresh();
				
				final File[] files = jad.listFiles();
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
