package jadex.base.gui.filetree;

import jadex.base.SRemoteGui;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.future.IIntermediateFuture;

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
	public RemoteJarNode(ITreeNode parent, AsyncTreeModel model, JTree tree, FileData file, 
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
		return SRemoteGui.listJarFileEntries(file, factory.getFileFilter(), exta);
	}
}
