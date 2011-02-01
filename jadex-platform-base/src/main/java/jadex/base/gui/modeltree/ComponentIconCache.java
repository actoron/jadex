package jadex.base.gui.modeltree;

import jadex.base.SComponentFactory;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IExternalAccess;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.tree.TreeModel;

/**
 *  Cache for component icons.
 *  Asynchronously loads icons and updates tree.
 */
public class ComponentIconCache
{
	/**
	 * The image icons.
	 */
	protected final UIDefaults icons = new UIDefaults(new Object[]
	{
//		"scanning_on",	SGUI.makeIcon(DefaultNodeFunctionality.class, "/jadex/base/gui/images/new_refresh_anim.gif"),
		"src_folder", SGUI.makeIcon(ComponentIconCache.class, "/jadex/base/gui/images/new_src_folder.png"),
		"src_jar", SGUI.makeIcon(ComponentIconCache.class, "/jadex/base/gui/images/new_src_jar.png"),
		"package", SGUI.makeIcon(ComponentIconCache.class, "/jadex/base/gui/images/new_package.png")
	});
	
	//-------- attributes --------
	
	/** The icon cache. */
//	private final Map	icons;
	
	/** The service provider. */
	protected final IExternalAccess exta;
	
	/** The tree. */
	protected final JTree tree;
	
	//-------- constructors --------
	
	/**
	 *  Create an icon cache.
	 */
	public ComponentIconCache(IExternalAccess exta, JTree tree)
	{
//		this.icons	= new HashMap();
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
		else if(node instanceof JarNode)
		{
			ret = (Icon)icons.get("src_jar");
		}
		else if(node instanceof DirNode)
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
		else if(node instanceof FileNode && exta!=null)
		{
			// Todo: remember ongoing searches for efficiency?
//			System.out.println("getIcon: "+type);
			final String file = ((FileNode)node).getFile().getAbsolutePath();
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
										icons.put(node, result);
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
