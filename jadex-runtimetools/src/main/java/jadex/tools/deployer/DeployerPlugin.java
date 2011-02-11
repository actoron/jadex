package jadex.tools.deployer;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

/**
 *  The library plugin.
 */
public class DeployerPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"conversation",	SGUI.makeIcon(DeployerPlugin.class, "/jadex/tools/common/images/libcenter.png"),
		"conversation_sel", SGUI.makeIcon(DeployerPlugin.class, "/jadex/tools/common/images/libcenter_sel.png"),
	});

	//-------- attributes --------
	
	//-------- methods --------
	
	/**
	 *  Test if this plugin should be initialized lazily.
	 *  @return True, if lazy.
	 */
	public boolean isLazy()
	{
		return false;
	}
	
	/**
	 * @return "Library Tool"
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Deployer Tool";
	}

	/**
	 * @return the conversation icon
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("conversation_sel"): icons.getIcon("conversation");
	}

	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		return new DeployerPanel(getJCC());
	}

	/**
	 *  Set properties loaded from project.
	 */
	public IFuture setProperties(Properties props)
	{
		return new Future(null);
//		Property[] ps = props.getProperties("cp");
//		// Hack: todo!?
//		ILibraryService ls = (ILibraryService)SServiceProvider.getService(getJCC()
//			.getExternalAccess().getServiceProvider(), ILibraryService.class).get(new ThreadSuspendable());
//		for(int i=0; i<ps.length; i++)
//		{
//			try
//			{
//				// todo: make addURL return future
//				File	file = new File(URLDecoder.decode(ps[i].getValue(), Charset.defaultCharset().name()));
//				if(file.exists())
//				{
//					ls.addURL(file.toURI().toURL());
//				}
//				else
//				{
//					ls.addURL(new URL(ps[i].getValue()));
//				}
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				System.out.println("Classpath problem: "+ps[i].getValue());
//			}
//		}
//		
//		return new Future(null);
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture getProperties()
	{
		return new Future(null);
//		final Future ret = new Future();
//		
//		SServiceProvider.getService(getJCC()
//			.getExternalAccess().getServiceProvider(), ILibraryService.class)
//			.addResultListener(new SwingDelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				ILibraryService ls = (ILibraryService)result;
//				ls.getURLs().addResultListener(new SwingDelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result)
//					{
//						List urls = (List)result;
//						Properties props = new Properties();
//						
//						for(int i=0; i<urls.size(); i++)
//						{
//							URL	url	= (URL) urls.get(i);
//							String	urlstring;
//							if(url.getProtocol().equals("file"))
//							{
//								urlstring	= SUtil.convertPathToRelative(url.getPath());
//							}
//							else
//							{
//								urlstring	= url.toString();
//							}
//							
//							props.addProperty(new Property("cp", urlstring));
//						}
//						
//						ret.setResult(props);
//					}
//				});
//			}
//		});
//		
//		return ret;
	}

	/** 
	 * @return the help id of the perspective
	 * @see jadex.base.gui.plugin.AbstractJCCPlugin#getHelpID()
	 */
	public String getHelpID()
	{
		return "tools.deployertool";
	}
	
	/**
	 *  Reset the conversation center to an initial state
	 */
	public void	reset()
	{
	}
}
