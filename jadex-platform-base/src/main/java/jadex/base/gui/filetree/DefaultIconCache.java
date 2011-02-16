package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filechooser.MyFile;
import jadex.commons.SUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 *  Cache for component icons.
 *  Asynchronously loads icons and updates tree.
 */
public class DefaultIconCache implements IIconCache
{
	//-------- constants --------
	
	/** The icon map. */
	protected Map icons = new HashMap();
	
	//-------- methods --------
	
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final ITreeNode node)
	{
		Icon	ret	= null;
		
		if(node instanceof FileNode)
		{
			File file = ((FileNode)node).getFile();
			
			File tmp = null;
			try
			{
				String suffix = "";
				if(!file.isDirectory() || (file instanceof JarAsDirectory && ((JarAsDirectory)file).isRoot()))
				{
					String name = file.getName();
					int idx = name.lastIndexOf(".");
					if(idx!=-1)
					{
						suffix = name.substring(idx);
						ret = (Icon)icons.get(suffix);
					}
				}
				
				if(ret==null)
				{
					// Case dir in Jar
					if(file instanceof JarAsDirectory && suffix.length()==0)// && !((JarAsDirectory)file).isRoot())
					{
						tmp = new MyFile(file.getName(), "", true);
						ret = FileSystemView.getFileSystemView().getSystemIcon(tmp);  
					}
					else
					{
						// Case normal file 
						if((file.exists() && file.canRead()) || SUtil.arrayToSet(file.listRoots()).contains(file))
						{
							ret = FileSystemView.getFileSystemView().getSystemIcon(file);  
						}
						// Case virtual file with suffix
						else
						{
	//						tmp = new MyFile("icon", suffix, file.isDirectory());
							tmp = File.createTempFile("icon", suffix);
							ret = FileSystemView.getFileSystemView().getSystemIcon(tmp);  
						}
					}
				}
				
				if(ret!=null && suffix.length()>0)
				{
					icons.put(suffix, ret);
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
		}
		else if(node instanceof RemoteFileNode)
		{
			RemoteFile file = ((RemoteFileNode)node).getRemoteFile();

			String suffix = "";
			if(!file.isDirectory())
			{
				String name = file.getFilename();
				int idx = name.lastIndexOf(".");
				if(idx!=-1)
				{
					suffix = name.substring(idx);
					ret = (Icon)icons.get(suffix);
				}
			}
			
			if(ret==null)
			{
				File tmp = null;
				try
				{
					// Case dir and dir in Jar
					if(suffix.length()==0)// && !((JarAsDirectory)file).isRoot())
					{
						tmp = new MyFile(file.getFilename(), "", true);
						ret = FileSystemView.getFileSystemView().getSystemIcon(tmp);  
					}
					else
					{
						// Case virtual file with suffix
						tmp = File.createTempFile("icon", suffix);
						ret = FileSystemView.getFileSystemView().getSystemIcon(tmp);  
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
			}
			
//			file = new MyFile(rf.getFilename(), rf.getPath(), rf.isDirectory());
		}
		
		return ret;
	}
}
