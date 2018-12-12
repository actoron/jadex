package jadex.commons.gui;

import javax.swing.JMenuItem;

/**
 *  Interface for getting/creating (dynamic) menu item (structures).
 */
public interface IMenuItemConstructor
{
	/**
	 *  Get or create a new menu item (struture).
	 *  @return The menu item (structure).
	 */
	public JMenuItem getMenuItem();
}
