package jadex.tools.common;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;


/** 
 * SelectAction
 */
public abstract class SelectAction
{
	/** <code>flag</code>: the flag for this action */
	protected boolean flag = false;

	/** <code>action</code>: an action */
	protected final AbstractAction action;

	/** 
	 * Constructor: <code>SelectAction</code>.
	 * @param name
	 */
	public SelectAction(String name)
	{
		this(name, null);
	}

	/** 
	 * Constructor: <code>SelectAction</code>.
	 * @param name
	 * @param icon
	 */
	public SelectAction(String name, Icon icon)
	{
		action = new AbstractAction(name, icon)
		{
			/** 
			 * @param e 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e)
			{
				AbstractButton cb = (AbstractButton)e.getSource();
				setSelected(cb.isSelected());
			}
		};
	}

	/**  
	 * @param b
	 */
	public void setSelected(boolean b)
	{
		if(flag != b)
		{
			synchronized(this)
			{
				flag = b;
				int i = buttons.size();
				while(i-- > 0)
				{
					AbstractButton ab = (AbstractButton)buttons.get(i);
					if(ab.isSelected() != flag)
					{
						ab.setSelected(flag);
					}
				}
			}

			flagChanged(flag);
		}
	}

	/** 
	 * @return true if this action is selected
	 */
	public boolean isSelected()
	{
		return flag;
	}

	/** 
	 * no op. called when the flag has changed
	 * @param flag
	 */
	public abstract void flagChanged(boolean flag);

	/** <code>buttons</code>: stores the buttons to be notified */
	protected final ArrayList buttons = new ArrayList();

	/** adds a button 
	 * @param ab
	 * @return the argument 
	 */
	public synchronized AbstractButton add(AbstractButton ab)
	{
		if(!buttons.contains(ab))
		{
			buttons.add(ab);
		}
		ab.setAction(action);
		return ab;
	}

	/**
	 * @return a new fresh JCheckBoxMenuItem
	 */
	public JCheckBoxMenuItem getCBItem()
	{
		return (JCheckBoxMenuItem)add(new JCheckBoxMenuItem());
	}

}
