package jadex.base.gui.asynctree.adapter;

import java.util.HashMap;
import java.util.Map;

import jadex.base.gui.asynctree.AsyncTreeModelEvent;

import javax.swing.event.TreeModelListener;

/**
 * Wraps a java.swing.event.TreeModelListener to implement jadex.base.gui.asynctree.TreeModelListener
 */
public class TreeModelListenerWrapper implements jadex.base.gui.asynctree.TreeModelListener
{

	private static Map<javax.swing.event.TreeModelListener, TreeModelListenerWrapper> adapters;
	private javax.swing.event.TreeModelListener swingListener;

	static {
		adapters = new HashMap<TreeModelListener, TreeModelListenerWrapper>();
	}
	
	private TreeModelListenerWrapper(javax.swing.event.TreeModelListener swingListener)
	{
		this.swingListener = swingListener;
	}

	@Override
	public void treeStructureChanged(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeStructureChanged(new TreeModelEventWrapper(treeModelEvent));
	}

	@Override
	public void treeNodesChanged(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeNodesChanged(new TreeModelEventWrapper(treeModelEvent));
	}

	@Override
	public void treeNodesRemoved(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeNodesRemoved(new TreeModelEventWrapper(treeModelEvent));
	}

	@Override
	public void treeNodesInserted(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeNodesInserted(new TreeModelEventWrapper(treeModelEvent));
	}

	/**
	 * Wraps a given Swing TreeModelListener and returns the wrapped object.
	 * The wrapped Object is cached, so that two calls with the same TreeModelListener 
	 * will return the same wrapped object.
	 * @param swingListener
	 * @return TreeModelListenerWrapper
	 */
	public static TreeModelListenerWrapper getWrapperFor(javax.swing.event.TreeModelListener swingListener)
	{
		TreeModelListenerWrapper result = adapters.get(swingListener);
		if (result == null)
		{
			result = new TreeModelListenerWrapper(swingListener);
			adapters.put(swingListener, result);
		}
		return result;
	}

	/**
	 * Deletes a wrapped Object from cache.
	 * @param swingListener
	 */
	public static void deleteWrapperFor(javax.swing.event.TreeModelListener swingListener)
	{
		adapters.remove(swingListener);
	}

}
