package jadex.tools.appstore;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.tools.deployer.DeployerPlugin;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

/**
 *  The app store plugin.
 */
public class AppStorePlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"deployer",	SGUI.makeIcon(DeployerPlugin.class, "/jadex/tools/common/images/deployer.png"),
		"deployer_sel", SGUI.makeIcon(DeployerPlugin.class, "/jadex/tools/common/images/deployer_sel.png"),
	});

	//-------- attributes --------
	
	/** The panel. */
	protected AppStorePanel apppanel;
	
	//-------- methods --------
	
	/**
	 * @return "Deployer Tool"
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "App Store Tool";
	}

	/**
	 * @return the conversation icon
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("deployer_sel"): icons.getIcon("deployer");
	}

	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		apppanel = new AppStorePanel(getJCC().getJCCAccess());
		return apppanel;
	}

	/**
	 *  Set properties loaded from project.
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		return IFuture.DONE;
//		return apppanel.setProperties(props);
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture<Properties> getProperties()
	{
		return new Future<Properties>(new Properties());
//		return apppanel.getProperties();
	}
}
