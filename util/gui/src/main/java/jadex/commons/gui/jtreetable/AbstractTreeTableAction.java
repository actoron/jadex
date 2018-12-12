package jadex.commons.gui.jtreetable;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;


/**
 *  Default implementation of tree table action.
 */
public abstract class AbstractTreeTableAction	extends AbstractAction	implements TreeTableAction
{
	//-------- constructors --------

	/**
	 *  Create a new AbstractTreeTableAction.
	 */
	public AbstractTreeTableAction()
	{
		super();
	}

	/**
	 *  Create a new AbstractTreeTableAction.
	 */
	public AbstractTreeTableAction(String name)
	{
		super(name);
	}

	/**
	 *  Create a new AbstractTreeTableAction.
	 */
	public AbstractTreeTableAction(String name, Icon icon)
	{
		super(name, icon);
	}

	//-------- TreeTableAction interface --------
	
	/**
	 * @return true if the checkbox should be selected.
	 */
	public abstract boolean	isSelected();

	//-------- ActionListener interface --------

	/**
	 *  Invoked when an action occurs.
	 */
	public abstract void actionPerformed(ActionEvent e);
}
