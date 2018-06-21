package jadex.base.gui.filetree;

import javax.swing.JTree;

import jadex.base.RemoteJarFile;
import jadex.base.SRemoteGui;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.commons.future.ISubscriptionIntermediateFuture;

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
	protected ISubscriptionIntermediateFuture<FileData> listFiles()
	{
//		System.out.println("ListFiles started");
		final long start = System.currentTimeMillis();
		
		ISubscriptionIntermediateFuture<FileData> ret = SRemoteGui.listJarFileEntries(file, factory.getFileFilter(), exta);
		
//		ret.addResultListener(new IIntermediateResultListener<FileData>()
//		{
//			public void intermediateResultAvailable(FileData result)
//			{
//				System.out.print(".");
//			}
//			
//			public void finished()
//			{
//				long dur = System.currentTimeMillis()-start;
//				System.out.println("ListFiles needed: "+dur/1000);
//			}
//				
//			public void resultAvailable(Collection<FileData> result)
//			{
//				long dur = System.currentTimeMillis()-start;
//				System.out.println("ListFiles needed: "+dur/1000);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				long dur = System.currentTimeMillis()-start;
//				System.out.println("ListFiles needed: "+dur/1000);
//			}
//		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	public boolean isRoot()
	{
		return file!=null && !(file instanceof RemoteJarFile);
	}
	
	/**
	 *  Get the file path.
	 */
	public String	getFilePath()
	{
		return file instanceof RemoteJarFile? super.getFilePath()+"!/"+((RemoteJarFile)file).getPathName()+"/"+((RemoteJarFile)file).getFilename(): super.getFilePath();
	}
}
