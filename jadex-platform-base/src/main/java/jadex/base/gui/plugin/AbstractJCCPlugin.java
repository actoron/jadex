package jadex.base.gui.plugin;


import jadex.base.gui.asynctree.INodeHandler;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ProxyComponentTreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.Properties;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;

/**
 *  Template class for control center plugins.
 */
public abstract class AbstractJCCPlugin implements IControlCenterPlugin
{
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
	 *  Initialize the plugin.
	 */
	public void init(IControlCenter jcc)
	{
		this.jcc = jcc;
		this.main_panel = createView();
		this.menu_bar = createMenuBar();
		this.tool_bar = createToolBar();
	}
	
	/** 
	 *  Shutdown the plugin.
	 */
	public void shutdown()
	{
		
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
	public IFuture setProperties(Properties ps)
	{
		return IFuture.DONE;
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture getProperties()
	{
		return new Future(null);
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
	
	/**
	 *  Find the class loader for a component.
	 *  Use component class loader for local components
	 *  and current platform class loader for remote components.
	 *  @param cid	The component id.
	 *  @return	The class loader.
	 */
	public static IFuture getClassLoader(final IComponentIdentifier cid, final IControlCenter jcc)
	{
		final Future	ret	= new Future();
		
		// Local component when platform name is same as JCC platform name
		if(cid.getPlatformName().equals(jcc.getJCCAccess().getComponentIdentifier().getPlatformName()))
		{
			SServiceProvider.getService(jcc.getJCCAccess().getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					IComponentManagementService	cms	= (IComponentManagementService)result;
					cms.getExternalAccess(cid).addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							IExternalAccess	ea	= (IExternalAccess)result;
							ret.setResult(ea.getModel().getClassLoader());
						}
					});
				}
			});
		}
		
		// Remote component
		else
		{
			SServiceProvider.getService(jcc.getJCCAccess().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					ILibraryService	ls	= (ILibraryService)result;
					ret.setResult(ls.getClassLoader());
				}
			});
		}
		return ret;
	}
	
	//-------- helper classes --------
	
	/**
	 *  A node handler allowing to spawn new control center views
	 *  for remote platforms displayed in component tree using proxy nodes.
	 */
	public static class ShowRemoteControlCenterHandler	implements INodeHandler
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
		public Action getDefaultAction(ITreeNode node)
		{
			return null;
		}
		
		/**
		 *  Get the overlay for a node if any.
		 */
		public Icon getOverlay(ITreeNode node)
		{
			return null;
		}
		
		/**
		 *  Get the popup actions available for all of the given nodes, if any.
		 */
		public Action[] getPopupActions(final ITreeNode[] nodes)
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
					new AbstractAction("Open Control Center")
					{
						public void actionPerformed(ActionEvent e)
						{
							for(int i=0; i<nodes.length; i++)
							{
								final IComponentIdentifier	cid	= ((ProxyComponentTreeNode)nodes[i]).getComponentIdentifier();
								SServiceProvider.getService(jcc.getPlatformAccess().getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(new SwingDefaultResultListener(panel)
								{
									public void customResultAvailable(Object result)
									{
										((IComponentManagementService)result).getExternalAccess(cid)
											.addResultListener(new SwingDefaultResultListener(panel)
										{
											public void customResultAvailable(Object result)
											{
												jcc.showPlatform((IExternalAccess)result);
											}
										});
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