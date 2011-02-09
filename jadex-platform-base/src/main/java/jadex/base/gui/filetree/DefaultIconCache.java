package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filechooser.MyFile;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 *  Cache for component icons.
 *  Asynchronously loads icons and updates tree.
 */
public class DefaultIconCache implements IIconCache
{
	//-------- methods --------
	
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final ITreeNode node)
	{
		Icon	ret	= null;
		
		File file = null;
		
		if(node instanceof FileNode)
		{
			file = ((FileNode)node).getFile();
		}
		else if(node instanceof RemoteFileNode)
		{
			RemoteFile rf = ((RemoteFileNode)node).getRemoteFile();
			file = new MyFile(rf.getFilename(), rf.getPath(), rf.isDirectory());
		}
		
		File tmp = null;
		try
		{
			if(file instanceof JarAsDirectory)
			{
				String suffix = file.getName();
				int idx = suffix.lastIndexOf(".");
				if(idx!=-1)
				{
					suffix = suffix.substring(idx);
					tmp = File.createTempFile("icon", suffix);  
				}
				else
				{
					tmp = new MyFile(file.getName(), "", true);
				}
				ret = FileSystemView.getFileSystemView().getSystemIcon(tmp);  
			}
			else
			{
				ret = FileSystemView.getFileSystemView().getSystemIcon(file);  
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(tmp!=null)
				tmp.delete();
		}

		return ret;
	}
}
