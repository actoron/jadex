package jadex.base.gui.modeltree;

import jadex.base.SComponentFactory;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.DirNode;
import jadex.base.gui.filetree.FileNode;
import jadex.base.gui.filetree.IIconCache;
import jadex.base.gui.filetree.JarNode;
import jadex.base.gui.filetree.RemoteDirNode;
import jadex.base.gui.filetree.RemoteFileNode;
import jadex.base.gui.filetree.RemoteJarNode;
import jadex.base.gui.filetree.RootNode;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.gui.SGUI;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.tree.TreeModel;

/**
 *  Cache for component icons.
 *  Asynchronously loads icons and updates tree.
 */
public class ModelIconCache implements IIconCache
{
	//-------- constants --------
	
	/**
	 * The image icons.
	 */
	protected final UIDefaults icons = new UIDefaults(new Object[]
	{
//		"scanning_on",	SGUI.makeIcon(DefaultNodeFunctionality.class, "/jadex/base/gui/images/new_refresh_anim.gif"),
		"src_folder", SGUI.makeIcon(ModelIconCache.class, "/jadex/base/gui/images/new_src_folder.png"),
		"src_jar", SGUI.makeIcon(ModelIconCache.class, "/jadex/base/gui/images/new_src_jar.png"),
		"package", SGUI.makeIcon(ModelIconCache.class, "/jadex/base/gui/images/new_package.png")
	});
	
	//-------- attributes --------
	
	/** The icon cache. */
	protected final Map	myicons;
	
	/** The service provider. */
	protected final IExternalAccess exta;
	
	/** The tree. */
	protected final JTree tree;
	
	//-------- constructors --------
	
	/**
	 *  Create an icon cache.
	 */
	public ModelIconCache(IExternalAccess exta, JTree tree)
	{
		this.myicons	= new HashMap();
		this.exta	= exta;
		this.tree	= tree;
	}
	
	//-------- methods --------
	
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final ITreeNode node)
	{
		Icon	ret	= null;
		
		if(icons.containsKey(node))
		{
			ret	= (Icon)icons.get(node);
		}
		else if(myicons.containsKey(node))
		{
			ret	= (Icon)myicons.get(node);
		}
		else if(node instanceof JarNode || node instanceof RemoteJarNode)
		{
			ret = (Icon)icons.get("src_jar");
		}
		else if(node instanceof DirNode || node instanceof RemoteDirNode)
		{
			if(node.getParent() instanceof RootNode)
			{
				ret = (Icon)icons.get("src_folder");
			}
			else
			{
				ret = (Icon)icons.get("package");
			}
		}
		else if((node instanceof FileNode || node instanceof RemoteFileNode) && exta!=null)
		{
			// Todo: remember ongoing searches for efficiency?
//			System.out.println("getIcon: "+type);
			final String file = node instanceof FileNode? 
				((FileNode)node).getFile().getAbsolutePath():
				((RemoteFileNode)node).getRemoteFile().getPath();
			
			SComponentFactory.getFileType(exta, file)
				.addResultListener(new SwingDefaultResultListener(tree)
			{
				public void customResultAvailable(Object result)
				{
					SComponentFactory.getFileType(exta, file)
						.addResultListener(new SwingDefaultResultListener(tree)
					{
						public void customResultAvailable(Object result)
						{
							final String type = (String)result;
							if(type!=null)
							{
								SComponentFactory.getFileTypeIcon(exta, type)
									.addResultListener(new SwingDefaultResultListener(tree)
								{
									public void customResultAvailable(Object result)
									{
										myicons.put(node, result);
										TreeModel	model	= tree.getModel();
										if(model instanceof AsyncTreeModel)
										{
											((AsyncTreeModel)model).fireNodeChanged(node);
										}
										else
										{
											tree.repaint();
										}
									}
								});
							}					
						}
					});
				}
			});
		}
		
		return ret;
	}
}
