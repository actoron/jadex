package jadex.base.gui.modeltree;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.tree.TreeModel;

import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.filetree.DirNode;
import jadex.base.gui.filetree.IFileNode;
import jadex.base.gui.filetree.IIconCache;
import jadex.base.gui.filetree.JarNode;
import jadex.base.gui.filetree.RIDNode;
import jadex.base.gui.filetree.RemoteDirNode;
import jadex.base.gui.filetree.RemoteJarNode;
import jadex.base.gui.filetree.RootNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;

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
//		"src_folder", SGUI.makeIcon(ModelIconCache.class, "/jadex/base/gui/images/new_src_folder.png"),
		"src_folder", SGUI.makeIcon(ModelIconCache.class, "/jadex/base/gui/images/folder416.png"),
		"src_jar", SGUI.makeIcon(ModelIconCache.class, "/jadex/base/gui/images/jar16.png"),
//		"package", SGUI.makeIcon(ModelIconCache.class, "/jadex/base/gui/images/new_package.png")
		"package", SGUI.makeIcon(ModelIconCache.class, "/jadex/base/gui/images/package2.png")
	});
	
	//-------- attributes --------
	
	/** The icon cache. */
	protected final Map<Object, Icon>	myicons;
	
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
		this.myicons	= new HashMap<Object, Icon>();
		this.exta	= exta;
		this.tree	= tree;
	}
	
	//-------- methods --------
	
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final ISwingTreeNode node)
	{
		Icon	ret	= null;
		
		ret	= (Icon)myicons.get(node);
		
//		if(node instanceof IFileNode && ((IFileNode)node).getFileName().indexOf("Admin")!=-1)
//			System.out.println("file is: ");
		
		if(ret==null)
		{
			String type = null;
			
			if(node instanceof JarNode 
				|| (node instanceof RemoteJarNode && ((RemoteJarNode)node).isRoot())
				|| (node instanceof RIDNode && ((RIDNode)node).isJar()))
			{
				type = "src_jar";
			}
			else if(node instanceof DirNode || (node instanceof RemoteDirNode && !(node instanceof RemoteJarNode)) || node instanceof RIDNode
				|| (node instanceof RemoteJarNode && ((RemoteJarNode)node).isDirectory()))
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
//				System.out.println("getIcon: "+type);
				final String file = ((IFileNode)node).getFilePath(); 
				
				createResourceIdentifier(node).addResultListener(new SwingResultListener<IResourceIdentifier>(new IResultListener<IResourceIdentifier>()
				{
					public void resultAvailable(IResourceIdentifier rid)
					{
						SComponentFactory.getFileType(exta, file, rid)
							.addResultListener(new SwingResultListener<String>(new IResultListener<String>()
						{
							public void resultAvailable(final String type)
							{
								if(type!=null)
								{
									Icon icon = (Icon)myicons.get(type);
									if(icon==null)
									{
	//									System.out.println("deep: "+type+" "+file);
										SComponentFactory.getFileTypeIcon(exta, type)
											.addResultListener(new SwingResultListener<byte[]>(new IResultListener<byte[]>()
										{
											public void resultAvailable(byte[] result)
											{
												if(result!=null)	// Corner case on platform shutdown!?
												{
													Icon	icon	= new ImageIcon(result); 
													myicons.put(node, icon);
													myicons.put(type, icon);
													refresh(node);
												}
											}
											
											public void exceptionOccurred(Exception exception)
											{
												// ignore...
											}
										}));
									}
									else
									{
										myicons.put(node, icon);
										refresh(node);
									}
								}					
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// ignore...
							}
						}));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// ignore...
					}
				}));
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void refresh(ISwingTreeNode node)
	{
		TreeModel model	= tree.getModel();
		if(model instanceof AsyncSwingTreeModel)
		{
			((AsyncSwingTreeModel)model).fireNodeChanged(node);
		}
		else
		{
			tree.repaint();
		}
	}
	
//	/**
//	 *  Create a resource identifier.
//	 */
//	public IResourceIdentifier createResourceIdentifier(ISwingTreeNode node)
//	{
//		// Get the first child of selection path as url
//		ISwingTreeNode root = node;
//		while(root.getParent()!=null && root.getParent().getParent()!=null)
//			root = root.getParent();
//		
//		Tuple2<IComponentIdentifier, URL> lid = null;
//		if(root instanceof IFileNode)
//		{
//			URL url = SUtil.toURL(((IFileNode)root).getFilePath());
//			IComponentIdentifier plat = exta.getComponentIdentifier().getRoot();
//			lid = new Tuple2<IComponentIdentifier, URL>(plat, url);
//		}
//		// todo: construct global identifier
//		ResourceIdentifier rid = new ResourceIdentifier(lid, null);
//		return rid;
//	}
	
	/**
	 *  Create a resource identifier.
	 */
	public IFuture<IResourceIdentifier> createResourceIdentifier(ISwingTreeNode node)
	{
		// Get the first child of selection path as url
		ISwingTreeNode root = node;
		while(root.getParent()!=null && root.getParent().getParent()!=null)
			root = root.getParent();
		
		return ModelTreePanel.createResourceIdentifier(exta, ((IFileNode)root).getFilePath());
		
////		Tuple2<IComponentIdentifier, URL> lid = null;
////		if(root instanceof IFileNode)
////		{
//		final URL url = SUtil.toURL(((IFileNode)root).getFilePath());
////			IComponentIdentifier plat = exta.getComponentIdentifier().getRoot();
////			lid = new Tuple2<IComponentIdentifier, URL>(plat, url);
////		}
//		
//		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
//		exta.getServiceProvider().searchService( new ServiceQuery<>( IDependencyService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//			.addResultListener(new ExceptionDelegationResultListener<IDependencyService, IResourceIdentifier>(ret)
//		{
//			public void customResultAvailable(IDependencyService deps)
//			{
//				deps.getResourceIdentifier(url).addResultListener(new DelegationResultListener<IResourceIdentifier>(ret));
//			}
//		});
//		
//		return ret;
////		// todo: construct global identifier
////		ResourceIdentifier rid = new ResourceIdentifier(lid, null);
////		return rid;
	}
}
