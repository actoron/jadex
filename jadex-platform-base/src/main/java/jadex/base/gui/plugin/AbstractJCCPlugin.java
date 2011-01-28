package jadex.base.gui.plugin;


import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.library.ILibraryService;
import jadex.xml.annotation.XMLClassname;

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
	 *  Test if this plugin should be initialized lazily.
	 *  @return True, if lazy.
	 */
	public boolean isLazy()
	{
		return true;
	}
	
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
	public void setProperties(Properties ps)
	{
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public Properties	getProperties()
	{
		return null;
	}

	/**
	 *  Reset the plugin.
	 */
	public void reset()
	{
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
		if(cid.getPlatformName().equals(jcc.getComponentIdentifier().getPlatformName()))
		{
			jcc.getExternalAccess().scheduleStep(new IComponentStep()
			{
				@XMLClassname("get-classloader")
				public Object execute(IInternalAccess ia)
				{
					ia.getRequiredService("cms").addResultListener(new DelegationResultListener(ret)
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
					return null;
				}
			});
		}
		
		// Remote component
		else
		{
			jcc.getExternalAccess().scheduleStep(new IComponentStep()
			{
				@XMLClassname("get-libraryservice")
				public Object execute(IInternalAccess ia)
				{
					ia.getRequiredService("libservice").addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							ILibraryService	ls	= (ILibraryService)result;
							ret.setResult(ls.getClassLoader());
						}
					});
					return null;
				}
			});
		}
		return ret;
	}
	
	/**
	 *  Add a subproperties to a properties.
	 */
	public static void	addSubproperties(Properties props, String type, Properties subproperties)
	{
		if(subproperties!=null)
		{
			if(subproperties.getType()!=null && !subproperties.getType().equals(type))
				throw new RuntimeException("Incompatible types: "+subproperties.getType()+", "+type);
			
			subproperties.setType(type);
			props.addSubproperties(subproperties);
		}
	}
}