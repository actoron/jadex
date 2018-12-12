package jadex.tools.email;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

/**
 *  Plugin for starting components.
 */
public class EmailClientPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"email", SGUI.makeIcon(EmailClientPlugin.class, "/jadex/tools/email/images/email.png"),
		"email_sel", SGUI.makeIcon(EmailClientPlugin.class, "/jadex/tools/email/images/email_sel.png"),
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
		return selected? icons.getIcon("email_sel"): icons.getIcon("email");
	}
	
	/**
	 *  Create the view.
	 */
	public JComponent createView()
	{
		return new EmailClientPluginPanel(getJCC());
	}
	
	/**
	 *  Shutdown the plugin.
	 */
	public IFuture<Void> shutdown()
	{
		if(getView()!=null)
			((EmailClientPluginPanel)getView()).dispose();
		return super.shutdown();
	}
}


