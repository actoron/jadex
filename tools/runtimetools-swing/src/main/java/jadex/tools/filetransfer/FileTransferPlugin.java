package jadex.tools.filetransfer;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.Properties;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

/**
 *  The library plugin.
 */
public class FileTransferPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"deployer",	SGUI.makeIcon(FileTransferPlugin.class, "/jadex/tools/common/images/deployer.png"),
		"deployer_sel", SGUI.makeIcon(FileTransferPlugin.class, "/jadex/tools/common/images/deployer_sel.png"),
	});

	//-------- attributes --------
	
	/** The panel. */
	protected FileTransferPanel deployerpanel;
	
	//-------- methods --------
	
	/**
	 * @return "Deployer Tool"
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
		return selected? icons.getIcon("deployer_sel"): icons.getIcon("deployer");
	}

	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		deployerpanel = new FileTransferPanel(getJCC());
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
}
