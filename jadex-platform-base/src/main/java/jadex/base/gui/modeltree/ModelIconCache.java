package jadex.base.gui.modeltree;

import jadex.base.SComponentFactory;
import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.DirNode;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.filetree.IIconCache;
import jadex.base.gui.filetree.JarNode;
import jadex.base.gui.filetree.RemoteDirNode;
import jadex.base.gui.filetree.RemoteJarNode;
import jadex.base.gui.filetree.RootNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.library.LibraryService;
import jadex.commons.Tuple2;
import jadex.commons.gui.SGUI;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

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
		
		ret	= (Icon)myicons.get(node);
		
		if(ret==null)
		{
			String type = null;
			if(node instanceof JarNode || node instanceof RemoteJarNode)
			{
				type = "src_jar";
			}
			else if(node instanceof DirNode || node instanceof RemoteDirNode)
			{
				if(node.getParent() instanceof RootNode)
				{
					type = "src_folder";
				}
				else
				{
					type = "package";
				}
			}
			if(type!=null)
				ret = (Icon)icons.get(type);
		}
		
		if(ret==null)
		{
			if(node instanceof IFileNode && exta!=null)
			{
				// Todo: remember ongoing searches for efficiency?
	//			System.out.println("getIcon: "+type);
				final String file = ((IFileNode)node).getFilePath(); 
				
				SComponentFactory.getFileType(exta, file, createResourceIdentifier(node))
					.addResultListener(new SwingDefaultResultListener(tree)
				{
					public void customResultAvailable(Object result)
					{
						final String type = (String)result;
						if(type!=null)
						{
							Icon icon = (Icon)myicons.get(type);
							if(icon==null)
							{
//								System.out.println("deep: "+type+" "+file);
								SComponentFactory.getFileTypeIcon(exta, type)
									.addResultListener(new SwingDefaultResultListener(tree)
								{
									public void customResultAvailable(Object result)
									{
										myicons.put(node, (Icon)result);
										myicons.put(type, (Icon)result);
										refresh(node);
									}
								});
							}
							else
							{
								myicons.put(node, icon);
								refresh(node);
							}
						}					
					}
				});
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void refresh(ITreeNode node)
	{
		TreeModel model	= tree.getModel();
		if(model instanceof AsyncTreeModel)
		{
			((AsyncTreeModel)model).fireNodeChanged(node);
		}
		else
		{
			tree.repaint();
		}
	}
	
	/**
	 *  Create a resource identifier.
	 */
	public IResourceIdentifier createResourceIdentifier(ITreeNode node)
	{
		// Get the first child of selection path as url
		ITreeNode root = node;
		while(root.getParent()!=null && root.getParent().getParent()!=null)
			root = root.getParent();
		
		Tuple2<IComponentIdentifier, URL> lid = null;
		if(root instanceof IFileNode)
		{
			URL url = LibraryService.toURL(((IFileNode)root).getFilePath());
			IComponentIdentifier plat = exta.getComponentIdentifier().getRoot();
			lid = new Tuple2<IComponentIdentifier, URL>(plat, url);
		}
		// todo: construct global identifier
		ResourceIdentifier rid = new ResourceIdentifier(lid, null);
		return rid;
	}
}
