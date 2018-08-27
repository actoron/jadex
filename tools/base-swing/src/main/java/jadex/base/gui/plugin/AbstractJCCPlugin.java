package jadex.base.gui.plugin;


import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ProxyComponentTreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Properties;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  Template class for control center plugins.
 */
public abstract class AbstractJCCPlugin implements IControlCenterPlugin
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"openjcc",	SGUI.makeIcon(AbstractJCCPlugin.class, "/jadex/base/gui/images/openjcc.png")
	});
	
	//-------- attributes --------
	
	/** The jcc. */
	protected IControlCenter	jcc;

	/** The menu bar. */
	private JMenu[] menu_bar;
	
	/** The tool bar. */
	private JComponent[] tool_bar;
	
	/** The main view. */
	private JComponent main_panel;
	
	//-------- constructors --------
	
	/**
	 *  Lazy plugins are inited on first access.
	 */
	public boolean	isLazy()
	{
		return true;
	}
	
	/** 
	 *  Initialize the plugin.
	 */
	public IFuture<Void> init(final IControlCenter jcc)
	{
		final Future<Void> ret = new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					AbstractJCCPlugin.this.jcc = jcc;
					AbstractJCCPlugin.this.main_panel = createView();
					AbstractJCCPlugin.this.menu_bar = createMenuBar();
					AbstractJCCPlugin.this.tool_bar = createToolBar();
					ret.setResult(null);
				}
				catch(Exception e)
				{
//					e.printStackTrace();
					ret.setException(e);
				}
			}
		});
		return ret;
	}
	
	/** 
	 *  Shutdown the plugin.
	 */
	public IFuture<Void> shutdown()
	{
		return IFuture.DONE;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the jcc.
	 */
	public IControlCenter	getJCC()
	{
		return this.jcc;
	}

	//-------- empty methods --------
	
	/** 
	 *  Create the tool bar (if any).
	 *  @return The tool bar.
	 */
	public JComponent[] getToolBar()
	{
		return tool_bar;
	}
	
	/** 
	 *  Get the menu bar (if any).
	 *  @return The menu bar.
	 */
	public JMenu[] getMenuBar()
	{
		return menu_bar;
	}

	/**
	 *  Get the main view.
	 *  @return The main view.
	 */
	public JComponent getView()
	{
		return main_panel;
	}
	
	/**
	 *  Set properties loaded from project.
	 */
	public IFuture<Void> setProperties(Properties ps)
	{
		return IFuture.DONE;
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture<Properties> getProperties()
	{
		return Future.getEmptyFuture();
	}
	
	/**
	 *  Store settings if any in platform settings service.
	 */
	public IFuture<Void> pushPlatformSettings()
	{
		return IFuture.DONE;
	}
	
	//-------- internal create methods --------
	
	/**
	 *  Create tool bar.
	 *  @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		return null;
	}
	
	/**
	 *  Create menu bar.
	 *  @return The menu bar.
	 */
	public JMenu[] createMenuBar()
	{
		return null;
	}
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		return null;
	}

	//-------- helper methods --------
	
//	/**
//	 *  Find the class loader for a component.
//	 *  Use component class loader for local components
//	 *  and current platform class loader for remote components.
//	 *  @param cid	The component id.
//	 *  @return	The class loader.
//	 */
//	public static IFuture getClassLoader(final IComponentIdentifier cid, final IControlCenter jcc)
//	{
//		final Future	ret	= new Future();
//		
//		// Local component when platform name is same as JCC platform name
//		if(cid.getPlatformName().equals(jcc.getJCCAccess().getComponentIdentifier().getPlatformName()))
//		{
//			jcc.getJCCAccess().getServiceProvider().searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//				.addResultListener(new DelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//					IComponentManagementService	cms	= (IComponentManagementService)result;
//					cms.getExternalAccess(cid).addResultListener(new DelegationResultListener(ret)
//					{
//						public void customResultAvailable(Object result)
//						{
//							IExternalAccess	ea	= (IExternalAccess)result;
//							ret.setResult(ea.getModel().getClassLoader());
//						}
//					});
//				}
//			});
//		}
//		
//		// Remote component
//		else
//		{
//			jcc.getJCCAccess().getServiceProvider().searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//				.addResultListener(new DelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//					ILibraryService	ls	= (ILibraryService)result;
//					ret.setResult(ls.getClassLoader());
//				}
//			});
//		}
//		return ret;
//	}
	
	/**
	 *  Find the class loader for a component.
	 *  Use component class loader for local components
	 *  and current platform class loader for remote components.
	 *  @param cid	The component id.
	 *  @return	The class loader.
	 */
	public static IFuture<ClassLoader> getClassLoader(final IComponentIdentifier cid, final IControlCenter jcc)
	{
		final Future<ClassLoader>	ret	= new Future<ClassLoader>();
		
		jcc.getJCCAccess().getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, ClassLoader>(ret)
		{
			public void customResultAvailable(final IExternalAccess exta)
			{
				jcc.getJCCAccess().searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, ClassLoader>(ret)
				{
					public void customResultAvailable(final ILibraryService libservice)
					{
						libservice.getClassLoader(exta.getModel().getResourceIdentifier())
							.addResultListener(new DelegationResultListener<ClassLoader>(ret));
					}
				});
			}
		});
		
		return ret;
	}
	
	//-------- helper classes --------
	
	/**
	 *  A node handler allowing to spawn new control center views
	 *  for remote platforms displayed in component tree using proxy nodes.
	 */
	public static class ShowRemoteControlCenterHandler	implements ISwingNodeHandler
	{
		//-------- attributes --------
		
		/** The control center. */
		protected IControlCenter	jcc;
		
		/** The outer panel. */
		protected Component	panel;
		
		//-------- constructors --------
		
		/**
		 *  Create a new handler.
		 */
		public ShowRemoteControlCenterHandler(IControlCenter jcc, Component panel)
		{
			this.jcc	= jcc;
			this.panel	= panel;
		}
		
		//-------- INodeHandler interface --------
		
		/**
		 *  Get the default action to be performed after a double click.
		 */
		public Action getDefaultAction(ISwingTreeNode node)
		{
			return null;
		}
		
		/**
		 *  Get the overlay for a node if any.
		 */
		public Icon getSwingOverlay(ISwingTreeNode node)
		{
			return null;
		}
		
		public byte[] getOverlay(ITreeNode node)
		{
			return null;
		}

		/**
		 *  Get the popup actions available for all of the given nodes, if any.
		 */
		public Action[] getPopupActions(final ISwingTreeNode[] nodes)
		{
			Action[]	ret;
			boolean	allproxy	= true;
			for(int i=0; allproxy && i<nodes.length; i++)
			{
				allproxy	= nodes[i] instanceof ProxyComponentTreeNode
					&& ((ProxyComponentTreeNode)nodes[i]).getComponentIdentifier()!=null;
			}
			
			if(allproxy)
			{
				ret	= new Action[]
				{
					new ToolTipAction("Open Control Center", (Icon)icons.get("openjcc"), "Click to open new Control Center tab for platform")
					{
						public void actionPerformed(ActionEvent e)
						{
							for(int i=0; i<nodes.length; i++)
							{
								final IComponentIdentifier	cid	= ((ProxyComponentTreeNode)nodes[i]).getComponentIdentifier();
								jcc.getPlatformAccess().getExternalAccess(cid)
									.addResultListener(new SwingDefaultResultListener<IExternalAccess>(panel)
								{
									public void customResultAvailable(IExternalAccess ea)
									{
										jcc.showPlatform(ea);
									}
								});
							}
						}
					}
				};
			}
			else
			{
				ret	= new Action[0];
			}
			
			return ret;
		}
	}
}