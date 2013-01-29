package jadex.base.gui.asynctree.adapter;

import jadex.base.gui.asynctree.AsyncTreeModelEvent;

public class TreeModelEventAdapter extends javax.swing.event.TreeModelEvent
{

	public TreeModelEventAdapter(AsyncTreeModelEvent jadexEvent)
	{
		super(jadexEvent.getModel(), jadexEvent.getPath(), jadexEvent.getIndices(), jadexEvent.getChildren());
	}
	

}
