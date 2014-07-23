package jadex.base.gui.filetree;

import java.util.Collection;

import jadex.base.SRemoteGui;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;

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
	public RemoteJarNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, FileData file, 
		IIconCache iconcache, IExternalAccess exta, INodeFactory factory)
	{
		super(parent, model, tree, file, iconcache, exta, factory);
//		System.out.println("node: "+getClass()+" "+desc.getName());
	}
	
	//-------- methods --------
	
	/**
	 *	Get a file filter according to current file type settings. 
	 */
	protected IIntermediateFuture<FileData> listFiles()
	{
		System.out.println("ListFiles started");
		final long start = System.currentTimeMillis();
		IIntermediateFuture<FileData> ret = SRemoteGui.listJarFileEntries(file, factory.getFileFilter(), exta);
		
		ret.addResultListener(new IResultListener<Collection<FileData>>()
		{
			public void resultAvailable(Collection<FileData> result)
			{
				long dur = System.currentTimeMillis()-start;
				System.out.println("ListFiles needed: "+dur/1000);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				long dur = System.currentTimeMillis()-start;
				System.out.println("ListFiles needed: "+dur/1000);
			}
		});
		
		return ret;
	}
}
