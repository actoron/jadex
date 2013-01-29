package jadex.base.gui.asynctree.adapter;

import jadex.base.gui.asynctree.AsyncTreeModelEvent;

/**
 * Wraps a Jadex AsyncTreeModelEvent in a Swing TreeModelEvent.
 */
public class TreeModelEventWrapper extends javax.swing.event.TreeModelEvent
{

	public TreeModelEventWrapper(AsyncTreeModelEvent jadexEvent)
	{
		super(jadexEvent.getModel(), jadexEvent.getPath(), jadexEvent.getIndices(), jadexEvent.getChildren());
	}
	

}
