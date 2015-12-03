package jadex.tools.generic;

import javax.swing.JComponent;
import javax.swing.UIDefaults;

import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.Properties;
import jadex.commons.future.IFuture;


/**
 *  Abstract base plugin that allows to look at viewable components or service.
 */
public abstract class AbstractGenericPlugin<E> extends AbstractJCCPlugin
{	
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
	});
	
	/** The selector panel. */
	protected AbstractSelectorPanel<E> selectorpanel;
	
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
		assert selectorpanel==null;
		this.selectorpanel = createSelectorPanel();
		selectorpanel.refreshCombo();
		return selectorpanel;
	}
	
	/**
	 *  Create the selector panel.
	 */
	public abstract AbstractSelectorPanel<E> createSelectorPanel();
	
	/**
	 *  Set properties loaded from project.
	 */
	public IFuture<Void> setProperties(final Properties props)
	{
		return selectorpanel.setProperties(props);
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture<Properties> getProperties()
	{
		return selectorpanel.getProperties();
	}

	/**
	 *  Get the selector panel.
	 */
	public AbstractSelectorPanel<E> getSelectorPanel()
	{
		return selectorpanel;
	}
	
	/** 
	 *  Shutdown the plugin.
	 */
	public IFuture<Void> shutdown()
	{
		selectorpanel.shutdown();
		return super.shutdown();
	}
	
	/**
	 *  Get the help id.
	 */
	public String getHelpID()
	{
		return "tools."+getName();
	}
	
}
