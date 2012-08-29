package jadex.tools.email;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

/**
 *  Plugin for starting components.
 */
public class EmailClientPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"starter",	SGUI.makeIcon(EmailClientPlugin.class, "/jadex/tools/common/images/new_starter.png"),
		"starter_sel",	SGUI.makeIcon(EmailClientPlugin.class, "/jadex/tools/common/images/new_starter_sel.png"),
	});

	//-------- methods --------
	
	
	/**
	 *  Return the unique name of this plugin.
	 *  This method may be called before init().
	 *  Used e.g. to store properties of each plugin.
	 */
	public String getName()
	{
		return "Email Client";
	}
	
	/**
	 *  Get the icon.
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected? icons.getIcon("starter_sel"): icons.getIcon("starter");
	}
	
	public JComponent createView()
	{
		return new EmailClientPluginPanel(getJCC());
	}
	
	/**
	 *  Shutdown the plugin.
	 */
	public IFuture<Void> shutdown()
	{
		((EmailClientPluginPanel)getView()).dispose();
		return super.shutdown();
	}
}


