package jadex.base.gui.filetree;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import jadex.base.JarAsDirectory;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.filechooser.RemoteFile;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.commons.SUtil;

/**
 *  Cache for component icons.
 *  Asynchronously loads icons and updates tree.
 */
public class DefaultIconCache implements IIconCache
{
	//-------- constants --------

	/** The default folder icon name. */
	public static final String DEFAULT_FOLDER = "default_folder";
	
	//-------- attributes --------
	
	/** The icon map. */
	protected Map icons = new HashMap();
	
	//-------- methods --------
	
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final ISwingTreeNode node)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		Icon	ret	= null;
		
		if(node instanceof FileNode)
		{
			File file = ((FileNode)node).getFile();
			boolean isroot = SUtil.arrayToSet(File.listRoots()).contains(file);
			
			File tmp = null;
			try
			{
				String suffix = "";
				if(!isroot)
				{
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
					else if(file.isDirectory())
					{
						ret = (Icon)icons.get(DEFAULT_FOLDER);
					}
				}
					
				
				if(ret==null)
				{
					// Case dir in Jar
					if(file instanceof JarAsDirectory && suffix.length()==0)// && !((JarAsDirectory)file).isRoot())
					{
						tmp = new RemoteFile(new FileData(file.getName(), "", true, true, FileData.getDisplayName(file), 
							file.lastModified(), File.separatorChar, SUtil.getPrefixLength(file), 0));
						ret = FileSystemView.getFileSystemView().getSystemIcon(tmp);  
					}
					else
					{
						// Case normal file or root drive
						if(isroot || (file.exists() && file.canRead()) || SUtil.arrayToSet(File.listRoots()).contains(file))
//						if(suffix.length()>0 || SUtil.arrayToSet(file.listRoots()).contains(file))
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
				
				if(ret!=null)
				{
					if(suffix.length()==0 && !isroot && file.isDirectory())
						suffix = DEFAULT_FOLDER;
					if(suffix.length()>0 && !icons.containsKey(suffix))
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
			FileData file = ((RemoteFileNode)node).getRemoteFile();

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
			else if(file.isDirectory())
			{
				ret = (Icon)icons.get(DEFAULT_FOLDER);
			}
			
			if(ret==null)
			{
				File tmp = null;
				try
				{
					// Case dir and dir in Jar
					if(suffix.length()==0)// && !((JarAsDirectory)file).isRoot())
					{
						ret = (Icon)icons.get(DEFAULT_FOLDER);
						if(ret==null)
						{
							tmp = new RemoteFile(new FileData(file.getFilename(), "", 
								true, true, file.getDisplayName(), file.getLastModified(), File.separatorChar, file.getPrefixLength(), 0));
							ret = FileSystemView.getFileSystemView().getSystemIcon(tmp); 
						}
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
				
				if(ret!=null)
				{
					if(suffix.length()==0)
						suffix = DEFAULT_FOLDER;
					if(suffix.length()>0 && !icons.containsKey(suffix))
						icons.put(suffix, ret);
				}
			}
			
//			file = new MyFile(rf.getFilename(), rf.getPath(), rf.isDirectory());
		}
		
		return ret;
	}
}
