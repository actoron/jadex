package jadex.tools.generic;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.Properties;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *  The generic plugin for a specified component or service. 
 */
public class ComponentServicePlugin extends AbstractJCCPlugin
{
	//-------- constants --------

//	/** The image icons. */
//	protected static final UIDefaults icons = new UIDefaults(new Object[]
//	{
//		"conversation",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/libcenter.png"),
//		"conversation_sel", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/libcenter_sel.png"),
//		"help",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/help.gif"),
//	});

	//-------- attributes --------
	
	/** The service class. */
	protected Class servicetype;
	
	/** The component model name. */
	protected String modelname;
	
	//-------- methods --------
	
	/**
	 *  Test if this plugin should be initialized lazily.
	 *  @return True, if lazy.
	 */
	public boolean isLazy()
	{
		return true;
	}
	
	/**
	 * @return "Library Tool"
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "";
	}

	/**
	 * @return the conversation icon
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return null;//selected? icons.getIcon("conversation_sel"): icons.getIcon("conversation");
	}

	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{
		return new JPanel();
	}

	/**
	 *  Set properties loaded from project.
	 */
	public void setProperties(Properties props)
	{
		if(props.getProperty("gen_")!=null);
			((JSplitPane)getView()).setDividerLocation(props.getIntProperty("mainsplit_location"));

		
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
//		props.addProperty(new Property("cp", urlstring));
		return props;
	}

	/** 
	 * @return the help id of the perspective
	 * @see jadex.base.gui.plugin.AbstractJCCPlugin#getHelpID()
	 */
	public String getHelpID()
	{
		return "tools.librarytool";
	}
	
	/**
	 *  Reset the conversation center to an initial state
	 */
	public void	reset()
	{
		
	}
}

