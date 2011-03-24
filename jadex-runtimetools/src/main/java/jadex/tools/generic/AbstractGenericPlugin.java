package jadex.tools.generic;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.Properties;
import jadex.commons.future.IFuture;

import javax.swing.JComponent;
import javax.swing.UIDefaults;


/**
 *  Abstract base plugin that allows to look at viewable components or service.
 */
public abstract class AbstractGenericPlugin extends AbstractJCCPlugin
{	
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
	});
	
	/** The selector panel. */
	protected AbstractSelectorPanel selectorpanel;
	
	//-------- methods --------
	
//	/**
//	 *  Create a panel for a component identifier.
//	 */
//	public abstract IFuture createPanel(Object element);
//
//	/**
//	 *  Refresh the combo box.
//	 */
//	public abstract void refreshCombo();
//	
//	/**
//	 *  Convert object to string for property saving.
//	 */
//	public abstract String convertToString(Object element);
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
	{		
		this.selectorpanel = createSelectorPanel();
		selectorpanel.refreshCombo();
		return selectorpanel;
	}
	
	/**
	 *  Create the selector panel.
	 */
	public abstract AbstractSelectorPanel createSelectorPanel();
	
	/**
	 *  Set properties loaded from project.
	 */
	public IFuture setProperties(final Properties props)
	{
		return selectorpanel.setProperties(props);
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture getProperties()
	{
		return selectorpanel.getProperties();
	}

	/**
	 *  Get the selector panel.
	 */
	public AbstractSelectorPanel getSelectorPanel()
	{
		return selectorpanel;
	}
	
	/** 
	 *  Shutdown the plugin.
	 */
	public void shutdown()
	{
		selectorpanel.shutdown();
	}
	
	/**
	 *  Get the help id.
	 */
	public String getHelpID()
	{
		return "tools."+getName();
	}
	
}
