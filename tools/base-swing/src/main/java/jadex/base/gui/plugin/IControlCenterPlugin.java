package jadex.base.gui.plugin;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;

import jadex.commons.IPropertiesProvider;
import jadex.commons.future.IFuture;


/**
 *  Interface for control center plugins.
 */
public interface IControlCenterPlugin extends IPropertiesProvider
{	
	/**
	 *  Lazy plugins are inited on first access.
	 */
	public boolean	isLazy();

	/**
	 *  This initializes a plugin and is done in context of a swing thread.
	 */
	public IFuture<Void> init(IControlCenter main);

	/**
	 *  Informs the plugin that it should stop all its computation
	 */
	public IFuture<Void> shutdown();
		
	/**
	 *  Return the unique name of this plugin.
	 *  This method may be called before init().
	 *  Used e.g. to store properties of each plugin.
	 */
	public String getName();

	/**
	 *  Return the icon representing this plugin.
	 *  This method may be called before init().
	 */
	public Icon getToolIcon(boolean selected);

	/**
	 *  Return the panel that is shown in the center of the JCC design.
	 *  This is called in swing thread context.
	 */
	public JComponent getView();

	/**
	 *  Get the menu bar containing the menus that should be added to the JCC menu bar. 
	 */
	public JMenu[] getMenuBar();

	/**
	 *  Create a tool bar containing the items that should be added to the JCC tool bar. 
	 */
	public JComponent[] getToolBar();

	/**
	 *  Store settings if any in platform settings service.
	 */
	public IFuture<Void> pushPlatformSettings();
}