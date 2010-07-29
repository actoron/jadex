package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.SwingDefaultResultListener;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.help.UnsupportedOperationException;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *  Tree model, which dynamically represents running components.
 */
public class ComponentTreeModel implements TreeModel
{
	//-------- attributes --------
	
	/** The component management service. */
	private final IComponentManagementService	cms;
	
	/** The root component. */
	private final IComponentDescription	root;
	
	/** The UI component used for displaying error messages. */
	// Todo: status bar for longer lasting actions?
	private final Component	ui;
	
	/** The component descriptions (cid -> desc). */
	private final Map	descriptions;

	/** The map of children for each component (desc -> list). */
	private final Map	children;

	/** The tree listeners. */
	private final List	listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a component tree model.
	 */
	public ComponentTreeModel(IComponentManagementService cms, IComponentDescription root, Component ui)
	{
		this.cms = cms;
		this.root = root;
		this.ui	= ui;
		this.descriptions = new HashMap();
		this.children = new HashMap();
		this.listeners	= new ArrayList();
		
		descriptions.put(root.getName(), root);

		cms.addComponentListener(null, new IComponentListener()
		{
			public void componentRemoved(final IComponentDescription desc, Map results)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						descriptions.remove(desc.getName());
						if(desc.getParent()!=null)
						{
							IComponentDescription	parent	= (IComponentDescription)descriptions.get(desc.getParent());
							List	cs	= (List)children.get(parent);
							int index	= cs.indexOf(desc);
							cs.remove(desc);
							fireNodeRemoved(parent, desc, index);
						}
					}
				});
			}
			
			public void componentChanged(IComponentDescription desc)
			{
			}
			
			public void componentAdded(final IComponentDescription desc)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						descriptions.put(desc.getName(), desc);
						if(desc.getParent()!=null)
						{
							IComponentDescription	parent	= (IComponentDescription)descriptions.get(desc.getParent());
							List	cs	= (List)children.get(parent);
							int index	= cs.size();
							cs.add(desc);
							fireNodeAdded(parent, desc, index);
						}
					}
				});
			}
		});
	}
	
	//-------- TreeModel interface --------
	
	/**
	 *  Get the root node.
	 */
	public Object getRoot()
	{
		return root;
	}
	
	/**
	 *  Get the given child of a node.
	 */
	public Object getChild(Object parent, int index)
	{
		Object ret;
		if(children.containsKey(parent))
		{
			ret = ((List)children.get(parent)).get(index);
		}
		else
		{
			searchChildren(parent);
			throw new IndexOutOfBoundsException("No child at " + index
					+ " for " + parent);
		}
		// System.out.println("getChild: "+parent+", "+index+", "+ret);
		return ret;
	}
	
	/**
	 *  Get the number of children of a node.
	 */
	public int getChildCount(Object parent)
	{
		int ret = 0;
		if(children.containsKey(parent))
		{
			ret = ((List)children.get(parent)).size();
		}
		else
		{
			searchChildren(parent);
		}
		// System.err.println("getChildCount: "+parent+", "+ret);
		return ret;
	}
	
	/**
	 *  Get the index of a child.
	 */
	public int getIndexOfChild(Object parent, Object child)
	{
		int ret = -1;
		if(children.containsKey(parent))
		{
			ret = ((List)children.get(parent)).indexOf(child);
		}
		else
		{
			searchChildren(parent);
		}
		// System.err.println("getIndexOfChild: "+parent+", "+child+", "+ret);
		return ret;
	}
	
	/**
	 *  Test if the node is a leaf.
	 */
	public boolean isLeaf(Object node)
	{
		boolean ret = getChildCount(node) == 0;
		// System.out.println("isLeaf: "+node+", "+ret);
		return ret;
	}
	
	/**
	 *  Edit the value of a node.
	 */
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		throw new UnsupportedOperationException(
				"Component Tree is not editable.");
	}
	
	/**
	 *  Add a listener.
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		this.listeners.add(l);
	}
	
	/**
	 *  Remove a listener.
	 */
	public void removeTreeModelListener(TreeModelListener l)
	{
		this.listeners.remove(l);
	}
	
	//-------- helper methods --------

	/**
	 *  Asynchronously search for children of the given node.
	 *  Tree will be updated later. 
	 */
	protected void searchChildren(Object node)
	{
		// todo: services (service container node?)

		if(node instanceof IComponentDescription)
		{
			// Todo: futurize getChildren call.
			final IComponentDescription desc = (IComponentDescription)node;
			final IComponentIdentifier[] achildren = cms.getChildren(desc.getName());
			if(achildren.length > 0)
			{
				final IComponentDescription[] dchildren = new IComponentDescription[achildren.length];
				for(int i = 0; i < achildren.length; i++)
				{
					final int index = i;
					cms.getComponentDescription(achildren[i]).addResultListener(new SwingDefaultResultListener(ui)
					{
						public void customResultAvailable(Object source, Object result)
						{
							dchildren[index] = (IComponentDescription)result;

							// Last child? -> inform listeners
							if(index == achildren.length - 1)
							{
								for(int i = 0; i < dchildren.length; i++)
									descriptions.put(dchildren[i].getName(), dchildren[i]);
								children.put(desc, new ArrayList(Arrays.asList(dchildren)));
								// System.out.println("children found: "+desc);
								fireTreeChanged(desc);
							}
						}
					});
				}
			}
			else
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						children.put(desc, new ArrayList());
						// System.out.println("no children found: "+desc);
						fireTreeChanged(desc);
					}
				});
			}
		}
	}

    /**
     *  Inform listeners that tree has changed from given node on.
     */
	protected void fireTreeChanged(IComponentDescription desc)
	{
		List path = buildTreePath(desc);
		
//		System.out.println("Path changed: "+path);
		
		for(int i=0; i<listeners.size(); i++)
		{
			((TreeModelListener)listeners.get(i)).treeStructureChanged(new TreeModelEvent(cms, path.toArray()));
		}
	}

    /**
     *  Inform listeners that a node has changed.
     */
	protected void fireNodeChanged(IComponentDescription desc)
	{
		int[]	indices;
		Object[]	nodes;
		List	path;
		if(desc.getParent()!=null)
		{
			IComponentDescription	parent	= (IComponentDescription)descriptions.get(desc.getParent());
			indices	= new int[]{((List)children.get(parent)).indexOf(desc)};
			nodes	= new Object[]{desc};
			path = buildTreePath(parent);	
		}
		else
		{
			indices	= null;
			nodes	= null;
			path = buildTreePath(desc);	
			
		}
		
//		System.out.println("Path changed: "+path);
		
		for(int i=0; i<listeners.size(); i++)
		{
			((TreeModelListener)listeners.get(i)).treeNodesChanged(new TreeModelEvent(cms, path.toArray(), indices, nodes));
		}
	}

    /**
     *  Inform listeners that a node has been removed
     */
	protected void fireNodeRemoved(IComponentDescription parent, IComponentDescription child, int index)
	{
		List path = buildTreePath(parent);
		
//		System.out.println("Node removed: "+child+", "+index+", "+path);
		
		for(int i=0; i<listeners.size(); i++)
		{
			((TreeModelListener)listeners.get(i)).treeNodesRemoved(new TreeModelEvent(cms, path.toArray(), new int[]{index}, new Object[]{child}));
		}
	}

    /**
     *  Inform listeners that a node has been added
     */
	protected void fireNodeAdded(IComponentDescription parent, IComponentDescription child, int index)
	{
		List path = buildTreePath(parent);
		
//		System.out.println("Node added: "+child+", "+index+", "+path);
		
		for(int i=0; i<listeners.size(); i++)
		{
			((TreeModelListener)listeners.get(i)).treeNodesInserted(new TreeModelEvent(cms, path.toArray(), new int[]{index}, new Object[]{child}));
		}
	}
	
	/**
	 *  Build a tree path to the given node.
	 *  @param desc The node.
	 *  @return The path items.
	 */
	protected List buildTreePath(IComponentDescription desc)
	{
		List	path	= new LinkedList();
		IComponentDescription	pdesc	= desc;
		while(pdesc!=null)
		{
			path.add(0, pdesc);
			if(pdesc.getParent()!=null)
			{
				pdesc	= (IComponentDescription)descriptions.get(pdesc.getParent());
			}
			else
			{
				pdesc	= null;
			}
		}
		return path;
	}
}
