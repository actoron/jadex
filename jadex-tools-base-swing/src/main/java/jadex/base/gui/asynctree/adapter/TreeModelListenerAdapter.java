package jadex.base.gui.asynctree.adapter;

import java.util.HashMap;
import java.util.Map;

import jadex.base.gui.asynctree.AsyncTreeModelEvent;

import javax.swing.event.TreeModelListener;

/**
 * Wraps a java.swing.event.TreeModelListener to implement jadex.base.gui.asynctree.TreeModelListener
 */
public class TreeModelListenerAdapter implements jadex.base.gui.asynctree.TreeModelListener
{

	private static Map<javax.swing.event.TreeModelListener, TreeModelListenerAdapter> adapters;
	private javax.swing.event.TreeModelListener swingListener;

	static {
		adapters = new HashMap<TreeModelListener, TreeModelListenerAdapter>();
	}
	
	private TreeModelListenerAdapter(javax.swing.event.TreeModelListener swingListener)
	{
		this.swingListener = swingListener;
	}

	@Override
	public void treeStructureChanged(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeStructureChanged(new TreeModelEventAdapter(treeModelEvent));
	}

	@Override
	public void treeNodesChanged(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeNodesChanged(new TreeModelEventAdapter(treeModelEvent));
	}

	@Override
	public void treeNodesRemoved(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeNodesRemoved(new TreeModelEventAdapter(treeModelEvent));
	}

	@Override
	public void treeNodesInserted(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeNodesInserted(new TreeModelEventAdapter(treeModelEvent));
	}

	public static TreeModelListenerAdapter getAdapterFor(javax.swing.event.TreeModelListener swingListener)
	{
		TreeModelListenerAdapter result = adapters.get(swingListener);
		if (result == null)
		{
			result = new TreeModelListenerAdapter(swingListener);
			adapters.put(swingListener, result);
		}
		return result;
	}

	public static void deleteAdapterFor(javax.swing.event.TreeModelListener swingListener)
	{
		adapters.remove(swingListener);
	}

}
