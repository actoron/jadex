package jadex.commons.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import jadex.commons.SUtil;

/**
 *  The popup builder. Constructs a (non-nested) popup menu from actions.
 */
public class PopupBuilder
{
	/** The menu-items and actions. */
	protected List rawmenu;

	/**
	 *  Create the popup builder.
	 *  @param rawmenu The raw menu.
	 */
	public PopupBuilder()
	{
		this.rawmenu = new ArrayList();
	}
	
	/**
	 *  Create the popup builder.
	 *  @param rawmenu The raw menu.
	 */
	public PopupBuilder(List rawmenu)
	{
		this.rawmenu = rawmenu;
	}

	/**
	 *  Create the popup builder.
	 *  @param rawmenu The raw menu.
	 */
	public PopupBuilder(Object[] rawmenu)
	{
		this.rawmenu = SUtil.arrayToList(rawmenu);
	}

	/**
	 *  Build the popup menu.
	 *  @return The popup menu.
	 */
	public JPopupMenu buildPopupMenu()
	{
		JPopupMenu pop = new JPopupMenu();

		for(int i=0; i<rawmenu.size(); i++)
		{
			Object tmp = rawmenu.get(i);
			if(tmp instanceof Action)
			{
				if(((Action)tmp).isEnabled())
				{
					pop.add((Action)tmp);
				}
			}
			else if(tmp instanceof IMenuItemConstructor)
			{
				JMenuItem item = ((IMenuItemConstructor)tmp).getMenuItem();
				if(item!=null)
					pop.add(item);
			}
		}

		return pop;
	}
	
	/**
	 *  Add an item.
	 *  @param item The item (Action or IMenuItemConstructor).
	 */
	public void addItem(Object item)
	{
		rawmenu.add(item);
	}
	
	/**
	 *  Remove an item.
	 *  @param item The item (Action or IMenuItemConstructor).
	 */
	public void removeItem(Object item)
	{
		rawmenu.remove(item);
	}

}