package jadex.bdi.examples.blocksworld;


import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;

import javax.swing.DefaultListModel;


/**
 *  A list model representing a collection of blocks
 *  on a table or in a bucket.
 */
public class BlocksListModel	extends DefaultListModel//<Block> // java 7
{
	/**
	 *  Create a list model for the given table.
	 *  @param table	The table.
	 */
	public BlocksListModel(Table table)
	{
		// Add initial blocks.
		for(int i=0; i<table.blocks.size(); i++)
			addElement(table.blocks.get(i));

		// Add listener top handle changes in content.
		table.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void	propertyChange(PropertyChangeEvent pce)
			{
				if(pce.getPropertyName().equals("blocks"))
				{
					if(pce.getOldValue()!=null)
					{
						removeElement(pce.getOldValue());
					}
					if(pce.getNewValue()!=null)
					{
						addElement((Block)pce.getNewValue());
					}
				}
			}
		});
	}
}

