package jadex.commons.gui.jtreetable;

import javax.swing.Action;


/**
 *  An action to add checkbox menu items to tree table popups.
 */
public interface TreeTableAction extends Action
{
	/**
	 *  Determine if the checkbox should be selected.
	 */
	public boolean isSelected();
}
