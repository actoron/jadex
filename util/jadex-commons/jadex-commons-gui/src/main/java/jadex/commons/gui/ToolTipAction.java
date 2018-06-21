package jadex.commons.gui;

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 *  An action with associated tooltip text.
 */
public abstract class ToolTipAction extends AbstractAction
{
	/**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
	 * @param name The action name.
	 * @param icon The icon.
	 * @param tooltiptext The tool tip text.
     */
    public ToolTipAction(String name, Icon icon, String tooltiptext)
	{
		super(name, icon);
		if(tooltiptext!=null)
			putValue(SHORT_DESCRIPTION, tooltiptext);
	}
}
