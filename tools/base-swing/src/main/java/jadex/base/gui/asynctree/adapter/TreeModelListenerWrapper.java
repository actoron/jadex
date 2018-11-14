package jadex.base.gui.asynctree.adapter;

import jadex.base.gui.asynctree.AsyncTreeModelEvent;

/**
 * Wraps a java.swing.event.TreeModelListener to implement jadex.base.gui.asynctree.TreeModelListener
 */
public class TreeModelListenerWrapper implements jadex.base.gui.asynctree.TreeModelListener
{
//	private static Map<javax.swing.event.TreeModelListener, TreeModelListenerWrapper> adapters;
	private javax.swing.event.TreeModelListener swingListener;

//	static
//	{
//		adapters = new HashMap<TreeModelListener, TreeModelListenerWrapper>();
//	}
	
	private TreeModelListenerWrapper(javax.swing.event.TreeModelListener swingListener)
	{
		this.swingListener = swingListener;
	}

	public void treeStructureChanged(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeStructureChanged(new TreeModelEventWrapper(treeModelEvent));
	}

	public void treeNodesChanged(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeNodesChanged(new TreeModelEventWrapper(treeModelEvent));
	}

	public void treeNodesRemoved(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeNodesRemoved(new TreeModelEventWrapper(treeModelEvent));
	}

	public void treeNodesInserted(AsyncTreeModelEvent treeModelEvent)
	{
		swingListener.treeNodesInserted(new TreeModelEventWrapper(treeModelEvent));
	}
	
	/**
	 *  Overridden such that a new wrapper for the same listener can be used to remove an old wrapper. 
	 */
	public int hashCode()
	{
		return 31 + swingListener.hashCode();
	}
	
	/**
	 *  Overridden such that a new wrapper for the same listener can be used to remove an old wrapper. 
	 */
	public boolean equals(Object o)
	{
		return o instanceof TreeModelListenerWrapper && ((TreeModelListenerWrapper)o).swingListener.equals(swingListener);
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
		return  new TreeModelListenerWrapper(swingListener);
//		TreeModelListenerWrapper result = adapters.get(swingListener);
//		if (result == null)
//		{
//			result = new TreeModelListenerWrapper(swingListener);
//			adapters.put(swingListener, result);
//		}
//		return result;
	}

//	/**
//	 * Deletes a wrapped Object from cache.
//	 * @param swingListener
//	 */
//	public static void deleteWrapperFor(javax.swing.event.TreeModelListener swingListener)
//	{
//		adapters.remove(swingListener);
//	}
}
