package jadex.base.gui.plugin;

import jadex.commons.Properties;
import jadex.commons.future.IFuture;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;


/**
 *  Interface for control center plugins.
 */
public interface IControlCenterPlugin
{
	/**
	 *  Test if this plugin should be initialized lazily.
	 *  @return True, if lazy.
	 */
	public boolean isLazy();
	
	/**
	 *  This initializes a plugin and is done in context of a swing thread.
	 */
	public void init(IControlCenter main);

	/**
	 *  Informs the plugin that it should stop all its computation
	 */
	public void shutdown();
	
	/**
	 *  Should reset the plugin to an initial state.
	 */
	public void reset();
	
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
	 *  Return the id for the help system
	 *  This method may be called before init().
	 */
	public String getHelpID();

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
	 *  Advices the the plugin to restore its properties from the argument
	 */
	public IFuture setProperties(Properties ps);

	/**
	 *  Advices the plugin to store its properties in the argument This is done
	 *  on project close or save.
	 */
	public IFuture getProperties();

}