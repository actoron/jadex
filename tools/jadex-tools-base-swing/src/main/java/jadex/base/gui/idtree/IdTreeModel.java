package jadex.base.gui.idtree;


import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;


/**
 *  Tree model that allows looking up nodes per id.
 */
public class IdTreeModel<T> extends DefaultTreeModel
{
	//-------- attributes --------
	
	/** The id map (id -> node). */
	protected Map<String, IdTreeNode<T>> nodes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new tree model.
	 */
	public IdTreeModel()
	{
		super(null, false);
		
		assert SwingUtilities.isEventDispatchThread();
		
		this.nodes = new HashMap<String, IdTreeNode<T>>();
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new node.
	 *  @param node The node.
	 */
	public void addNode(IdTreeNode<T> node)
	{
		assert SwingUtilities.isEventDispatchThread();
		
//		nodes.put(node.getId(), node);
		registerAll(node);
		
//		System.out.println("current nodes: "+nodes.keySet());
	}
	
	/**
	 *  Remove a node.
	 *  @param node The node.
	 */
	public boolean removeNode(IdTreeNode<T> node)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		return deregisterAll(node);
	}
	
	/**
	 *  Register a node and all its children.
	 *  @param node The node.
	 */
	protected void registerAll(IdTreeNode<T> node)
	{
		Object tst = nodes.get(node.getId());
		if(tst!=null && tst!=node)
			throw new RuntimeException("Node already contained: "+node+" "+nodes.get(node.getId()));
			
		nodes.put(node.getId(), node);
		
		for(int i=node.getChildCount()-1; i>=0; i--)
		{
			registerAll((IdTreeNode<T>)node.getChildAt(i));
		}
	}
	
	/**
	 *  Deregister a node and all its children.
	 *  @param node The node.
	 */
	protected boolean deregisterAll(IdTreeNode<T> node)
	{
		boolean ret = nodes.remove(node.getId())!=null;
		
		if(ret)
		{
			for(int i=node.getChildCount()-1; i>=0; i--)
			{
				deregisterAll((IdTreeNode<T>)node.getChildAt(i));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get a node per id.
	 *  @param id The node id.
	 *  @return The node.
	 */
	public IdTreeNode<T> getNode(String id)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		return nodes.get(id);
	}
	
	/**
	 *  Get a node per id.
	 *  @param id The node id.
	 *  @return The node.
	 */
	public Map<String, IdTreeNode<T>> getNodes()
	{
		assert SwingUtilities.isEventDispatchThread();
		
		return nodes;
	}
}
