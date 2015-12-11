package jadex.base.gui.filetree;

import java.io.File;

import javax.swing.JTree;

import jadex.base.RemoteJarFile;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.commons.SUtil;

/** 
 *  Default factory for creating nodes.
 */
public abstract class DefaultNodeFactory implements INodeFactory
{
	//-------- methods --------
	
	/**
	 *  Create a new component node.
	 */
	public ISwingTreeNode createNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree,
		Object value, IIconCache iconcache, IExternalAccess exta, INodeFactory factory)
	{
		ISwingTreeNode ret = null;
		
		if(value instanceof File)
		{
			File file = (File)value;
			if(SUtil.arrayToSet(File.listRoots()).contains(file) || file.isDirectory())
			{
				ret = new DirNode(parent, model, tree, file, iconcache, factory);
			}
			else if(file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))
			{
				ret = new JarNode(parent, model, tree, file, iconcache, factory);
			}
			else
			{
				ret = new FileNode(parent, model, tree, file, iconcache);
			}
		}
		else if(value instanceof FileData)
		{
			FileData file = (FileData)value;
			if(file instanceof RemoteJarFile //&& ((RemoteJarFile)file).isDirectory()) 
				|| file.getFilename().endsWith(".jar") || file.getFilename().endsWith(".zip"))
			{
				ret = new RemoteJarNode(parent, model, tree, file, iconcache, exta, factory);
			}
			else if(file.isDirectory())
			{
				ret = new RemoteDirNode(parent, model, tree, file, iconcache, exta, factory);
			}
			else
			{
				ret = new RemoteFileNode(parent, model, tree, file, iconcache, exta);
			}
		}
		// todo: remote
		// todo: move to modeltree factory
		else if(value instanceof IResourceIdentifier)
		{
			ret = new RIDNode(parent, model, tree, (IResourceIdentifier)value, iconcache, factory);
		}
		
		if(ret==null)
			throw new IllegalArgumentException("Unknown value: "+value);
		
		return ret;
	}
}
