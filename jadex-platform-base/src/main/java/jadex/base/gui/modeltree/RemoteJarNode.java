package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.IRemoteFilter;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.xml.annotation.XMLClassname;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;

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
//				final File[] files = jad.listFiles();
				
				Map rjfentries = new MultiCollection();
				Map zipentries = jad.createEntries();
				for(Iterator it=zipentries.keySet().iterator(); it.hasNext(); )
				{
					String name = (String)it.next();
					Collection childs = (Collection)zipentries.get(name);
//					System.out.println("childs: "+childs);
					for(Iterator it2=childs.iterator(); it2.hasNext(); )
					{
						ZipEntry child = (ZipEntry)it2.next();
						RemoteJarFile tmp = new RemoteJarFile(child.getName(), name+"/"+child.getName(), child.isDirectory(), rjfentries, name);
						rjfentries.put(name, tmp);
					}
				}
				
				RemoteJarFile rjf = new RemoteJarFile(jad.getName(), jad.getAbsolutePath(), true, rjfentries, "/");
				
				Collection files = rjf.listFiles();
				
				ret.setResult(files);
				
//				if(files!=null)
//				{
//					final CollectionResultListener lis = new CollectionResultListener(files.length, 
//						true, new DelegationResultListener(ret));
//					
//					for(int i=0; i<files.length; i++)
//					{
//						if(myfilter==null)
//						{
//							lis.resultAvailable(files[i]);
//						}
//						else
//						{
//							final File file = files[i];
//							myfilter.filter(files[i]).addResultListener(new IResultListener()
//							{
//								public void resultAvailable(Object result)
//								{
//									if(((Boolean)result).booleanValue())
//									{
//										lis.resultAvailable(new RemoteFile(file.getName(), file.getAbsolutePath(), file.isDirectory()));
//									}
//									else
//									{
//										lis.exceptionOccurred(null);
//									}
//								}
//								
//								public void exceptionOccurred(Exception exception)
//								{
//									lis.exceptionOccurred(null);
//								}
//							});
//						}
//					}
//				}
//				else
//				{
//					ret.setResult(Collections.EMPTY_LIST);
//				}
				
				return ret;
			}
		}).addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
}
