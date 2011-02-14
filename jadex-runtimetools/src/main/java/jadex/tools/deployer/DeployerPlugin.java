package jadex.tools.deployer;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.Properties;
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
	
	/** The panel. */
	protected DeployerPanel deployerpanel;
	
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
		deployerpanel = new DeployerPanel(getJCC());
		return deployerpanel;
	}

	/**
	 *  Set properties loaded from project.
	 */
	public IFuture setProperties(Properties props)
	{
		return deployerpanel.setProperties(props);
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture getProperties()
	{
		return deployerpanel.getProperties();
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
