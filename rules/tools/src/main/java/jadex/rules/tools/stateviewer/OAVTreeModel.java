package jadex.rules.tools.stateviewer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;


/**
 *  Swing Tree model for an OAV state. Enables displaying an 
 *  oav state in a swing tree.
 */
public class OAVTreeModel implements TreeModel
{

	/**
	 * flag to indicate if java objects should be inspectable in the tree
	 * TO DO: make configurable via GUI?
	 */
	protected final static boolean enableObjectInspection = true;
	
	//-------- static part --------
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		// Tab icons.
		"object", SGUI.makeIcon(OAVTreeModel.class, "/jadex/rules/tools/stateviewer/images/object.png"),
		"attribute", SGUI.makeIcon(OAVTreeModel.class, "/jadex/rules/tools/stateviewer/images/attribute.png"),
		"value", SGUI.makeIcon(OAVTreeModel.class, "/jadex/rules/tools/stateviewer/images/value.png"),
		"javaobject", SGUI.makeIcon(OAVTreeModel.class, "/jadex/rules/tools/stateviewer/images/bean.png"),
		"javaattribute", SGUI.makeIcon(OAVTreeModel.class, "/jadex/rules/tools/stateviewer/images/javaattribute.png"),
		"javavalue", SGUI.makeIcon(OAVTreeModel.class, "/jadex/rules/tools/stateviewer/images/value.png")
	});
	
	/**
	 * The list of timers to update the object inspector tree nodes
	 */
	protected static List timerList;

	
	//-------- attributes --------
	
	/** The root node. */
	protected RootNode	root;

	/** The local copy of the state (synchronized to swing thread). */
	protected CopyState	copy;
	
	/** The listeners. */
	protected Set	listeners;

	/** The pending notification flag. */
	protected boolean	notify;
	
	/** Nodes for objects to allow fine-tuned tree redraw (object-id -> {node1, node2, ...}).
	 *  Because the state is a (possibly cyclic) graph, there may be more than one node for a single object! */
	protected Map	nodes;
	
	/** list for all created Attribute inspector nodes */
	protected List inspectors;
	
//	/** Random to generate unique(?) IDs*/
//	protected Random rng;
	
	/** UUID counter */
	private int uuidcounter;

	
	//-------- constructors --------

	/**
	 *  Create new OAV tree model.
	 *  @param id	The root object id.
	 *  @param state	The OAV state.
	 *  @param showempty	Flag, if empty attributes should be shown.
	 */
	public OAVTreeModel(IOAVState state)
	{
		// use identity hash for different (java) objects being equal (e.g. empty list).
		// todo: mixed identity map like used in state?
		this.nodes	= new IdentityHashMap();
		this.root	= new RootNode();
		
		this.inspectors = new ArrayList();
//		this.rng = new Random(System.currentTimeMillis());
		this.uuidcounter = 0;
		
		Timer refreshTimer = new Timer(5000, new ObjectInspectorRefreshAction(this));
		refreshTimer.start();
		OAVTreeModel.addRefreshTimer(refreshTimer);
		
		// Todo: create copy state on state thread.
		this.copy	= new CopyState(state, new SwingSynchronizator());
		
		// could this anonymous inner class result in a cyclic reference  that prevents the TreeModel 
		// to be removed from gc when Introspector plugin is closed?
		// model -> copystate -> listener -> model$this0
		// 				|-> state -> listener -> copystate-lister$this0
		//					  ^-<- Agent
		// IMHO the OAVTreeModel for the Introspector plugin will be removed only
		// if the Agent that was introspected is removed too
		copy.addStateListener(new IOAVStateListener()
		{
			/**
			 *  Notification when an attribute value of an object has been set.
			 *  @param id The object id.
			 *  @param attr The attribute type.
			 *  @param oldvalue The oldvalue.
			 *  @param newvalue The newvalue.
			 */
			public void objectModified(Object id, OAVObjectType type,  
				final OAVAttributeType attr, Object oldvalue, Object newvalue)
			{
//				System.out.println("modified: "+id+"."+attr.getName()+": "+oldvalue+" -> "+newvalue);
				Object	tmp	= nodes.get(id);
				if(tmp instanceof ObjectNode)
				{
					// Find object and attribute node.
					ObjectNode	node	= (ObjectNode)tmp;
					List	children	= node.getChildren();
					AttributeNode	attrnode	= null;
					for(int i=0; attrnode==null && i<children.size(); i++)
					{
						if(((AttributeNode)children.get(i)).attribute==attr)
							attrnode = (AttributeNode)children.get(i);
					}
					
					// Handle updates

					// Add new attribute node.
					if(attrnode==null && newvalue!=null)
					{
						// Rebuild children to create and find index of new node (hack???) 
						node.children	= null;
						List	newchildren	= node.getChildren();
						node.children	= children;	// Keep old children, as there may be expanded subtrees.
						for(int i=0; attrnode==null && i<newchildren.size(); i++)
						{
							if(((AttributeNode)newchildren.get(i)).attribute==attr)
								attrnode	= (AttributeNode)newchildren.get(i);
						}
						int index	= newchildren.indexOf(attrnode);
						children.add(index, attrnode);	// insert new node into old children. 
						
						if(listeners!=null)
						{
							TreeModelEvent	event	= new TreeModelEvent(this, node.getPath(), new int[]{index}, new Object[]{attrnode});
							TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
							for(int i=0; i<alisteners.length; i++)
							{
								alisteners[i].treeNodesInserted(event);
							}
						}
					}
					else if(attrnode!=null && OAVAttributeType.NONE.equals(attr.getMultiplicity()))
					{
						// Update existing node with new value
						if(newvalue!=null)
						{
							
							attrnode.drop();
							attrnode.children	= null;
							
							if(listeners!=null)
							{
								TreeModelEvent	event	= new TreeModelEvent(this, attrnode.getPath());
								TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
								for(int i=0; i<alisteners.length; i++)
								{
									alisteners[i].treeStructureChanged(event);
								}
							}
						}
						
						// Remove existing node
						else
						{
							int index	= children.indexOf(attrnode);
							children.remove(attrnode);
							attrnode.drop();

							if(listeners!=null)
							{
								TreeModelEvent	event	= new TreeModelEvent(this, node.getPath(), new int[]{index}, new Object[]{attrnode});
								TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
								for(int i=0; i<alisteners.length; i++)
								{
									alisteners[i].treeNodesRemoved(event);
								}
							}
						}
					}
					else if(attrnode!=null)
					{
						if(attrnode.children!=null)	// Otherwise node is not shown and doesn't need update.
						{
							assert oldvalue!=newvalue;
							
							// Add new value (at the end of children)
							if(newvalue!=null)
							{
								Object	child	= newvalue;
								if(!(attr.getType() instanceof OAVJavaType))
									child = new ObjectNode(attrnode, child);
								else if(isInspectable(newvalue))
									// objectInspector Node
									child = new ObjectInspectorNode(attrnode, newvalue.getClass(), null, newvalue);
								// else use plain value
	
								attrnode.children.add(child);

								if(listeners!=null)
								{
									TreeModelEvent	event	= new TreeModelEvent(this, attrnode.getPath(), new int[]{attrnode.children.size()-1}, new Object[]{child});
									TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
									for(int i=0; i<alisteners.length; i++)
									{
										alisteners[i].treeNodesInserted(event);
									}
								}
							}
							
							// Remove child of attribute node.
							else if(attrnode.children.size()>1)
							{
								Object	child	= oldvalue;
								if(!(attr.getType() instanceof OAVJavaType))
									child = new ObjectNode(attrnode, child);
								else if(isInspectable(child))
									// objectInspector Node
									child = new ObjectInspectorNode(attrnode, child.getClass(), null, child);
								// else use plain value

//									int index	= attrnode.children.indexOf(child);
								int index	= getIndexForChild(attrnode.children, child);
//								System.out.println("modified removing: "+child+", "+index);
								
								if(attrnode.children.get(index) instanceof ObjectNode)
									((ObjectNode)attrnode.children.get(index)).drop();
								if(attrnode.children.get(index) instanceof AbstractInspectorNode)
									((AbstractInspectorNode)attrnode.children.get(index)).drop();
								attrnode.children.remove(index);
								
								if(listeners!=null)
								{
									TreeModelEvent	event	= new TreeModelEvent(this, attrnode.getPath(), new int[]{index}, new Object[]{child});
									TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
									for(int i=0; i<alisteners.length; i++)
									{
										alisteners[i].treeNodesRemoved(event);
									}
								}
							}

							// Remove attribute node when last value is removed.
							else
							{
								int index	= children.indexOf(attrnode);
								children.remove(attrnode);
								attrnode.drop();

								if(listeners!=null)
								{
									TreeModelEvent	event	= new TreeModelEvent(this, node.getPath(), new int[]{index}, new Object[]{attrnode});
									TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
									for(int i=0; i<alisteners.length; i++)
									{
										alisteners[i].treeNodesRemoved(event);
									}
								}
							}
						}
					}
				}
				else if(tmp instanceof ObjectInspectorNode)
				{
					// todo? Or does it automatically handles changes?
				}
			}
					
			/**
			 *  Notification when an object has been added to the state.
			 *  @param child The object id.
			 *  @param type The object type.
			 */
			public void objectAdded(Object id, OAVObjectType type, boolean root)
			{
//				System.out.println("added: "+id+", "+type+", "+root);
				if(root)
				{
					assert !nodes.containsKey(id): "Node already contained: "+id+", "+nodes.get(id);

					List	children	= OAVTreeModel.this.root.getChildren();
					Object	child	= id;
					if(!(copy.getType(child) instanceof OAVJavaType))
					{
//						child = new ObjectNode(this, child);
						child = new ObjectNode(getRoot(), child);
					}
					else if(isInspectable(child))
					{
						// objectInspector Node
//						child = new ObjectInspectorNode(this, child.getClass(), null, child);
						child = new ObjectInspectorNode(getRoot(), child.getClass(), null, child);
					}
					children.add(child);
					
					if(listeners!=null)
					{
						TreeModelEvent	event	= new TreeModelEvent(this, new Object[]{OAVTreeModel.this.root}, new int[]{children.size()-1}, new Object[]{child});
						TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
						for(int i=0; i<alisteners.length; i++)
						{
							alisteners[i].treeNodesInserted(event);
						}
					}
				}
			}
			
			/**
			 *  Notification when an object has been removed from state.
			 *  @param id The object id.
			 *  @param type The object type.
			 */
			public void objectRemoved(Object id, OAVObjectType type)
			{
//				System.out.println("removed: "+id+", "+type);
				
				Object node	= nodes.get(id);
				if(node instanceof ObjectNode)
				{
					ObjectNode	onode	= (ObjectNode)node;
					int index	= getIndexOfChild(onode.parent, onode);
					Object[]	path	= null;
					if(onode.parent==OAVTreeModel.this.root)
					{
						((RootNode)onode.parent).children.remove(index);
						path	= new Object[]{OAVTreeModel.this.root};
					}
					else if(onode.parent instanceof AttributeNode)
					{
						((AttributeNode)onode.parent).children.remove(index);
						path	= ((AttributeNode)onode.parent).getPath();
					}
					onode.drop();
					
					if(listeners!=null && path!=null)
					{
						TreeModelEvent	event	= new TreeModelEvent(this, path, new int[]{index}, new Object[]{node});
						TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
						for(int i=0; i<alisteners.length; i++)
						{
							alisteners[i].treeNodesRemoved(event);
						}
					}
				}
				else if(node instanceof ObjectInspectorNode)
				{
					ObjectInspectorNode onode = (ObjectInspectorNode)node;
					int index	= getIndexOfChild(onode.parent, onode);
					Object[]	path	= null;
					if(onode.parent==OAVTreeModel.this.root)
					{
						((RootNode)onode.parent).children.remove(index);
						path	= new Object[]{OAVTreeModel.this.root};
					}
					else if(onode.parent instanceof AttributeNode)
					{
//						try
//						{
							((AttributeNode)onode.parent).children.remove(index);
							path	= ((AttributeNode)onode.parent).getPath();
//						}
//						catch(Exception e)
//						{
//							e.printStackTrace();
//						}
					}
					onode.drop();
					
					if(listeners!=null && path!=null)
					{
						TreeModelEvent	event	= new TreeModelEvent(this, path, new int[]{index}, new Object[]{node});
						TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
						for(int i=0; i<alisteners.length; i++)
						{
							alisteners[i].treeNodesRemoved(event);
						}
					}
				}
			}
		}, false);
	}
	
	

	//-------- TreeModel interface --------

	/**
	 *  Get the root node of the tree.
	 */
	public Object getRoot()
	{
		return root;
	}
	
	/**
	 *  Get the number of children of the given node.
	 */
	public int getChildCount(Object parent)
	{
//		System.out.println("getChildCount: "+parent);
		int	count;
		
		// Node for an attribute.
		if(parent instanceof AttributeNode)
		{
			count	= ((AttributeNode)parent).getChildren().size();
		}
		
		// Node for an OAV object.
		else if(parent instanceof ObjectNode)
		{
			count	= ((ObjectNode)parent).getChildren().size();
		}
		
		// Root node.
		else if(parent instanceof RootNode)
		{
			count	= ((RootNode)parent).getChildren().size();
		}
		
		// Node for an ObjectInspector object.
		else if(parent instanceof ObjectInspectorNode)
		{
			count	= ((ObjectInspectorNode)parent).getChildren().size();
		}
		
		// Node for an ObjectAttributeInspector object.
		else if(parent instanceof ObjectInspectorAttributeNode)
		{
			count	= ((ObjectInspectorAttributeNode)parent).getChildren().size();
		}
		
		// Node is value.
		else
		{
			count	= 0;
		}
		
		return count;
	}

	/**
	 *  Get the given child of a node.
	 */
	public Object getChild(Object parent, int index)
	{
//		System.out.println("getChild: "+parent+", "+index);
		Object ret;
		
		// Node for an attribute.
		if(parent instanceof AttributeNode)
		{
			ret	= ((AttributeNode)parent).getChildren().get(index);
		}
		
		// Node for an object.
		else if(parent instanceof ObjectNode)
		{
			ret	= ((ObjectNode)parent).getChildren().get(index);
		}
		
		// Root node.
		else if(parent instanceof RootNode)
		{
			ret	= ((RootNode)parent).getChildren().get(index);
		}
		
		// Node for an ObjectInspector object.
		else if(parent instanceof ObjectInspectorNode)
		{
			ret	= ((ObjectInspectorNode)parent).getChildren().get(index);
		}
		
		// Node for an ObjectAttributeInspector object.
		else if(parent instanceof ObjectInspectorAttributeNode)
		{
			ret	= ((ObjectInspectorAttributeNode)parent).getChildren().get(index);
		}

		// Value node has no children.
		else
		{
			throw new IllegalArgumentException("Node has no children: "+parent);
		}
		
		return ret;
	}

	/**
	 *  Get the index of a given child.
	 */
	public int getIndexOfChild(Object parent, Object child)
	{
		int	index	= -1;
		// Hack!!! Inefficient implementation!?
		int	count	= getChildCount(parent);
		for(int i=0; index==-1 && i<count; i++)
		{
			if(getChild(parent, i).equals(child))
				index	= i;
		}
		return index;
	}

	/**
	 *  Check if a node is a leaf node.
	 */
	public boolean isLeaf(Object node)
	{
//		System.out.println("isLeaf: "+getChildCount(node)==0+" "+node);
		return getChildCount(node)==0;
	}

	/**
	 *  Add a listener to the model.
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		if(listeners==null)
			listeners	= new HashSet();
		listeners.add(l);
	}

	/**
	 *  Remove a listener from the model.
	 */
	public void removeTreeModelListener(TreeModelListener l)
	{
		if(listeners!=null)
			listeners.remove(l);
	}

	/**
	 *  Called by user changes (when the tree is editable).
	 */
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		// ignored...
	}
	
	// --- helper methods to update the tree ---
	
	/**
	 * Generate a unique id for ObjectNode's
	 * @return synchronized call to System.currentTimeMillis();
	 */
	protected synchronized int getNextNodeUUID() 
	{
		//return rng.nextInt();
		return uuidcounter += 1;
		//return new Long(System.currentTimeMillis());
	}
	
	/**
	 * Regenerate subtree if a node was replaced
	 * @param treePath The Path to the node that was changed
	 */
	protected void fireTreeStructureChanged(Object[] treePath)
	{
		TreeModelEvent event = 
			new TreeModelEvent(this, treePath);
		if (listeners != null)
		{
			TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
			for(int i=0; i<alisteners.length; i++)
			{
				alisteners[i].treeStructureChanged(event);
			}
//			System.out.println("Structure: " + event);
		}
	}

	/**
	 * Tests if two lists of Inspector nodes (e.g. children) 
	 * are semantically equals
	 * @param l1 list to test
	 * @param l2 list to test
	 * @return true if the lists are the same or contains the semantically same objects in the same order
	 */
	protected boolean testInspectorNodesListEquals(List l1, List l2)
	{
		if (l1 == l2)
		    return true;
		if (l1==null || l2==null)
		    return false;

		ListIterator e1 = l1.listIterator();
		ListIterator e2 = l2.listIterator();
		while(e1.hasNext() && e2.hasNext()) 
		{
		    Object o1 = e1.next();
		    Object o2 = e2.next();
		    try 
		    {
			    if (!(o1==null ? o2==null : ((AbstractInspectorNode)o1).equals(o2, false)))
			    	return false;
		    }
		    catch (ClassCastException e)
		    {
		    	return false;
		    }
		}
		return !(e1.hasNext() || e2.hasNext());

	}
	
	/**
	 * Get the index for a child in children list, beginning with index=0
	 * @see #getIndexForChild(List children, Object child, int start)
	 */
	protected int getIndexForChild(List children, Object child)
	{
		return getIndexForChild(children, child, 0);
	}
	
	/** 
	 * Returns the FIRST occurrence of child in given children list. 
	 * Be sure to remove the child from the list or to save the index 
	 * to find additional occurrences of the child in later searches.
	 * <p>
	 * This method is using the "equals(Object o, boolean checkUID)" method 
	 * to find a child with the checkUID parameter set to false and can be used
	 * to find a semantically equal child.
	 * <p>
	 * @param children List to search for the child.
	 * @param child The child to search for.
	 * @return the FIRST index for the child in the children list
	 */
	protected int getIndexForChild(List children, Object child, int start)
	{
		assert children != null; 
		int index	= -1;
		
		for(int i=start; index==-1 && i<children.size(); i++)
//		for(int i=children.size()-1; i>=0 && index == -1; i--)
		{
			if((children.get(i) instanceof ObjectNode) 
				&& ((ObjectNode)children.get(i)).equals(child, false))
			{
				index = i;
			}
			else if ((children.get(i) instanceof ObjectInspectorNode) 
					&& ((ObjectInspectorNode)children.get(i)).equals(child, false))
			{
				index = i;
			}
			else if ((children.get(i) instanceof ObjectInspectorAttributeNode) 
					&& ((ObjectInspectorAttributeNode)children.get(i)).equals(child, false))
			{
				index = i;
			}
			else if ((children.get(i) instanceof ObjectInspectorValueNode) 
					&& ((ObjectInspectorValueNode)children.get(i)).equals(child, false))
			{
				index = i;
			}
			else if (children.get(i).equals(child))
			{
				index = i;
			}
			
		}
		
		return index;
	}

	/**
	 * Refresh all displayed attributes
	 * @param oldRoot
	 */
	protected void refreshInspectorNodes()
	{
//			System.out.println("refresh called");
		
		TreeModelEvent event = null;
//			Object[] listener = listenerList.getListenerList();
		
		Object[] inspectorNodes = inspectors.toArray(new Object[inspectors.size()]);
		// the array contains all attribute nodes ordered in the path from root to leaf.
		// e.g. a parent node will have a lower index than his children
		//
		// we could loop from leafs to root, to avoid drawing problems when updating a node that was
		// already removed due to a parent object change 
		// OR 
		// null all (grand) children from a dropped node in the array to avoid later redrawing what will
		// cause a drawing error / problem
		//
		// TO DO: decide which implementation is more efficient
		
		for (int inspectorIndex = inspectorNodes.length-1; inspectorIndex >= 0; inspectorIndex--)
//			for (int inspectorIndex = 0; inspectorIndex < inspectorNodes.length; inspectorIndex++)
		{
			if (inspectorNodes[inspectorIndex] instanceof ObjectInspectorAttributeNode)
			{
				ObjectInspectorAttributeNode node = (ObjectInspectorAttributeNode) inspectorNodes[inspectorIndex];
				
				if (node.children!=null) // if childrens are not displayed, no update is needed
				{
					// Regenerate children and fire change event for changed values
					// keep and restore old children as there may be expanded subtrees
					List oldchildren = node.children;
					node.children = null;
					List newchildren = node.getChildren();
					node.children = oldchildren;
					
					if (node.isArrayNode())
					{
						// if we have a simple type we don't have to check subtrees, 
						// simply remove old and add new children
						if (!isInspectable(node.type.getComponentType(), true))
						{
							//if (!oldchildren.equals(newchildren))
							if (!testInspectorNodesListEquals(oldchildren, newchildren))
							{
								node.children = newchildren;
								fireTreeStructureChanged(node.getPath());
							}
						}
						// some array fields can be inspected, so assume that there may be a
						// expanded subtree.
						else
						{
							// List to save already selected children from newchildren
							// Needed to avoid double select of the same value in an array
							List prevSelectedChildren = new ArrayList();
						
							// Handle removed children
							Map removedChildren = new TreeMap();
							for (int i = 0; i < oldchildren.size(); i++)
							{
								Object oldchild = oldchildren.get(i);
								
								// use only not previous selected children 
								int index = getIndexForChild(newchildren, oldchild, 0);
								while (index != -1 && prevSelectedChildren.contains(newchildren.get(index)))
								{
									prevSelectedChildren.add(newchildren.get(index));
									index = getIndexForChild(newchildren, oldchild, index+1);
								}
								
								//if (!newchildren.contains(oldchild))
								// value was removed
								if (index == -1)
								{
									// add it to removedChildren
									removedChildren.put(Integer.valueOf(i), oldchild);
									// don't remove child from old children here! This will change 
									// index of other child's as well
								}
							}
							// clear selected children after use
							prevSelectedChildren.clear();

							// remove the removed from oldchildren an store 
							// index and value in change event arrays
							Object[] removed = removedChildren.entrySet().toArray();
							int[] indexes = new int[removed.length];
							Object[] childs = new Object[removed.length];
							for (int i = 0; i < removed.length; i++)
							{
								Map.Entry entry = (Map.Entry) removed[i];
								indexes[i] = ((Integer) entry.getKey()).intValue();
								childs[i] = entry.getValue();
								
								// drop child if it is an inspector node
								if (entry.getValue() instanceof ObjectInspectorNode)
									((ObjectInspectorNode) entry.getValue()).drop();
								
								// remove from children list
								oldchildren.remove(entry.getValue());
								//oldchildren.remove(getIndexForChild(oldchildren, entry.getValue()));
							}
							
							// Handle inserted children
							// replace the rest of old children at their position in new children
							// save the not replaced children as added nodes in change event arrays
							Map insertedChildren = new TreeMap();
							for (int i = 0; i < newchildren.size(); i++)
							{
								Object newchild = newchildren.get(i);
								
								// use only not previous selected children 
								int index = getIndexForChild(oldchildren, newchild, 0);
								while (index != -1 && prevSelectedChildren.contains(newchildren.get(index)))
								{
									prevSelectedChildren.add(oldchildren.get(index));
									index = getIndexForChild(oldchildren, newchild, index+1);
								}
								
								// replace new with old as there may be expanded sub trees
								//if (oldchildren.contains(newchild))
								if (index != -1)
								{
									//Object oldchild = oldchildren.get(oldchildren.indexOf(newchild));
									Object oldchild = oldchildren.get(index);
									// remove oldchild from oldchildren 
									//(needed to support same object twice in arrays)
									oldchildren.remove(index);
									newchildren.remove(i);
									newchildren.add(i, oldchild);
									// drop newchild, it was replaced by the old one
									if (newchild instanceof ObjectInspectorNode)
										((ObjectInspectorNode) newchild).drop();
								}
								// value was added
								else
								{
									// register children for change event
									insertedChildren.put(Integer.valueOf(i), newchild);
								}
							}
							// clear selected children after use
							prevSelectedChildren.clear();
							
							// update the name prefix for children
							for (int i = 0; i < newchildren.size(); i++)
							{
								Object obj = newchildren.get(i);
								if (obj instanceof ObjectInspectorNode)
								{
									((ObjectInspectorNode) obj).namePrefix = "["+i+"] ";
								}
								else if (obj instanceof ObjectInspectorValueNode)
								{
									((ObjectInspectorValueNode) obj).namePrefix = "["+i+"] ";
								}
							}
							
							// create the array for nodes inserted change event 
							Object[] inserted = insertedChildren.entrySet().toArray();
							int[] insertedIndexes = new int[inserted.length];
							Object[] insertedChilds = new Object[inserted.length];
							for (int insertedIndex = 0; insertedIndex < inserted.length; insertedIndex++)
							{
								Map.Entry entry = (Map.Entry) inserted[insertedIndex];
								insertedIndexes[insertedIndex] = ((Integer) entry.getKey()).intValue();
								insertedChilds[insertedIndex] = entry.getValue();
							}
							
							// add the new children as node children
							node.children = newchildren;
							
							TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
							
							// create and fire event for removed children
							if (removedChildren.size() > 0)
							{
								event = new TreeModelEvent(this, node.getPath(), indexes, childs);
								for(int i=0; i<alisteners.length; i++)
								{
									alisteners[i].treeNodesRemoved(event);
								}
								//System.out.println("Removed: " + event);
							}
							
							// create and fire event for inserted children
							if (insertedChildren.size() > 0)
							{
								event = new TreeModelEvent(this, node.getPath(), insertedIndexes, insertedChilds);
								for(int i=0; i<alisteners.length; i++)
								{
									alisteners[i].treeNodesInserted(event);
								}
								//System.out.println("Inserted: " + event);
							}
						}
					}
					// we have no array, so expect only one child for the attribute node
					else
					{
						
						// Number of children and index of a child can't change at runtime, expect of arrays
						assert oldchildren.size() == newchildren.size(): node;
						// An attribute field can only have one value
						assert newchildren.size() == 1 : node;
						
						//if ( !(oldchildren.get(0).equals(newchildren.get(0))) )
						if ( getIndexForChild(oldchildren, newchildren.get(0)) == -1 )
						{
							// replace old children with new children and drop old
							node.children = newchildren;
							if (oldchildren.get(0) instanceof ObjectInspectorNode)
							{
								((ObjectInspectorNode) oldchildren.get(0)).drop();
							}
							
							// create and fire event
							if (oldchildren.get(0) instanceof ObjectInspectorNode || newchildren.get(0) instanceof ObjectInspectorNode)
							{
								// regenerate tree if inspector object node has changed
								fireTreeStructureChanged((Object[]) SUtil.joinArrays(node.getPath(), new Object[]{oldchildren.get(0)}));
							}
							else
							{
								// only redraw node if we have a inspector value
								event = new TreeModelEvent(this, node.getPath(), new int[]{0}, new Object[]{oldchildren.get(0)});
//									System.out.println("Changed: " + event);
								TreeModelListener[]	alisteners	= (TreeModelListener[])listeners.toArray(new TreeModelListener[listeners.size()]);
								for(int i=0; i<alisteners.length; i++)
								{
									alisteners[i].treeNodesChanged(event);
									
								}
							}
							
							
						}
						else if (newchildren.get(0) instanceof ObjectInspectorNode)
						{
							// no difference between children, drop new children if it is an ObjectInspectorNode
							((ObjectInspectorNode) newchildren.get(0)).drop();
						}
					}
				}
			}
			else 
			{
				if (inspectorNodes[inspectorIndex] != null)
					System.err.println("Error in ObjectInspectorTreeModel, unknown inspector node type: " + inspectorNodes[inspectorIndex]);
			}
		}
	}
	
	/**
	 *  Dispose the model and remove all listeners.
	 */
	public void	dispose()
	{
		this.copy.dispose();
	}
	
	//-------- static part --------

	/**
	 *  Decide if java object should be inspectable.
	 */
	protected static boolean	isInspectable(Object obj)
	{
		boolean	ret	= enableObjectInspection && obj!=null;
		if(ret)
		{
			ret	= ret && !(obj instanceof String);
			ret	= ret && !(obj instanceof Number);
			ret	= ret && !(obj instanceof Character);
			ret	= ret && !(obj instanceof Boolean);
			ret	= ret && obj.getClass()!=Object.class;
		}
		return ret;
	}
	
	/**
	 *  Decide if java object should be inspectable.
	 *  This method test if clazz.isAssignableFrom([String|Number|Character|])
	 */
	protected static boolean	isInspectable(Class clazz, boolean inspectObjectClass)
	{
		boolean	ret	= enableObjectInspection && clazz!=null;
		if(ret)
		{
		
			ret	= ret && (clazz.isAssignableFrom(String.class));
			ret	= ret && (clazz.isAssignableFrom(Number.class));
			ret	= ret && (clazz.isAssignableFrom(Character.class));
			ret	= ret && (clazz.isAssignableFrom(Boolean.class));
			if (!inspectObjectClass)
				ret	= ret && clazz != Object.class;

		}
		return ret;
	}
	
	
	/**
	 * Sets the refresh delay for the ObjectInspector refresh
	 * A value equal or lower to 0 disables the refresh and stop the timers
	 */
	public static void setRefreshDelay(int millis)
	{
//		System.out.println("Set OAVTreeModel refresh delay to " + millis);
		if (timerList != null)
		{
			if (millis > 0)
				for (Iterator timers = timerList.iterator(); timers.hasNext();)
				{
					Timer timer = (Timer) timers.next();
					timer.setDelay(millis);
					if (!timer.isRunning())
						timer.start();
				}
			else
				for (Iterator timers = timerList.iterator(); timers.hasNext();)
				{
					Timer timer = (Timer) timers.next();
					if (timer.isRunning())
						timer.stop();
				}
		}
	}
	
	/**
	 * Add a timer to the static refresh timer list 
	 * @param t
	 */
	protected synchronized static void addRefreshTimer(Timer t)
	{
		if(timerList==null)
		{
			timerList = new ArrayList();
		}
		
		timerList.add(t);
	}
	
	/**
	 * Remove a timer from the static refresh timer list
	 * @param t
	 */
	protected static void removeRefreshTimer(Timer t)
	{
		if(t!=null)
		{
			t.stop();
			if(timerList!=null)
			{
				timerList.remove(t);
			}
		}
	}

	/** A abstract node for this model */ 
	abstract class AbstractInspectorNode
	{
		/** The parent node */
		protected Object	parent;
		
		/** The children of this node (cached)*/
		protected List 	children;

		/** The path from the root node to this node. */
		protected Object[]	path;
		
		/** A unique id for this node */
		protected int 	nodeUUID;
		
		// --- constructor ---
		
		protected AbstractInspectorNode()
		{
			nodeUUID = getNextNodeUUID();
		}
		
		// --- abstract methods ---
		
		public abstract List getChildren();
		
		public abstract Object[] getPath();
		
		protected abstract boolean equals(Object obj, boolean checkUUID);
		
		// --- methods ---
		
		public void drop() { };
		
		public int hashCode()
		{		
			return nodeUUID;
		}
		
	}
	
	/**
	 *  A node representing an attribute value.
	 */
	public class AttributeNode
	{
		//-------- attributes --------
		
		/** The attribute. */
		protected OAVAttributeType	attribute;
		
		/** The object node. */
		protected ObjectNode	parent;

		/** The children. */
		protected List	children;
		
		/** The path from the root node to this node. */
		protected Object[]	path;
		
		/** A unique id for this node */
		protected int nodeUUID;

		//-------- constructors --------
		
		/**
		 *  Create a new attribute node.
		 *  @param parent	The parent.
		 *  @param attribute	The attribute.
		 */
		public AttributeNode(ObjectNode parent, OAVAttributeType attribute)
		{
			this.parent	= parent;
			this.attribute	= attribute;
			
			this.nodeUUID = getNextNodeUUID();
		}
		
		//-------- methods --------
		
		/**
		 *  Get the children of this node.
		 */
		public List	getChildren()
		{
			if(children==null)
			{
				children 	= new ArrayList();
				if(OAVAttributeType.NONE.equals(attribute.getMultiplicity()))
				{
					Object	child	= copy.getAttributeValue(parent.object, attribute);
					if(!(attribute.getType() instanceof OAVJavaType))
						child = new ObjectNode(this, child);
					else if(isInspectable(child))
						// objectInspector Node
						child = new ObjectInspectorNode(this, child.getClass(), null, child);
					// else use plain value
					
					children.add(child);
				}
				else
				{
					Collection	coll	= copy.getAttributeValues(parent.object, attribute);
					if(coll!=null)
					{
						for(Iterator it=coll.iterator(); it.hasNext(); )
						{
							Object	child	= it.next();
							if(!(attribute.getType() instanceof OAVJavaType))
								child = new ObjectNode(this, child);
							else if(isInspectable(child))
								// objectInspector Node
								child = new ObjectInspectorNode(this, child.getClass(), null, child);
							else
								// else use wrapped plain value, as JTree does not allow duplicates.
								child	= new ObjectInspectorValueNode(this, null, child);
							
							children.add(child);
						}
					}
				}
			}
			return children;
		}
		
		/**
		 *  Get the path of this node (inclusive) starting from the root node.
		 */
		public Object[]	getPath()
		{
			if(path==null)
			{
				if(parent!=null)
				{
					path	= (Object[])SUtil.joinArrays(parent.getPath(), new Object[]{this});
				}
				else
				{
					path	= new Object[]{this};
				}
			}
			
			return path;
		}
		
		/**
		 *  Unregister a node and its subnodes.
		 */
		public void	drop()
		{
			if(children!=null)
			{
				for(int i=0; i<children.size(); i++)
				{
					if(children.get(i) instanceof ObjectNode)
						((ObjectNode)children.get(i)).drop();
				}
			}
		}

		/**
		 *  Create a string representation of the attribute node.
		 *  @return A string representation of the attribute node.
		 */
		public String	toString()
		{
			String name	= attribute.getName();
			int idx	= name.indexOf("has_");
			if(idx!=-1)
				name	= name.substring(idx+4);
			return name; //+" (attribute)";
		}
		
		protected boolean equals(Object obj, boolean checkUUID)
		{
			boolean ret = 
				obj instanceof AttributeNode
				&& ((AttributeNode)obj).parent==parent 
				&& ((AttributeNode)obj).attribute==attribute;
			
			if (checkUUID && ret)
				ret = ret && ((AttributeNode)obj).nodeUUID==nodeUUID;
			
			return ret;
		}
		
		public boolean equals(Object obj)
		{
//			return obj instanceof AttributeNode
//				&& ((AttributeNode)obj).parent==parent 
//				&& ((AttributeNode)obj).attribute==attribute;
			
			return equals(obj, true);
		}
		
		public int hashCode()
		{
//			int	ret	= 31 + parent.hashCode();
//			ret	= ret*31 + attribute.hashCode();
//			return ret;
			
			return nodeUUID;
		}		
	}
	
	/**
	 *  A node representing an object.
	 */
	public class ObjectNode
	{
		//-------- attributes --------
		
		/** The object. */
		protected Object	object;
		
		/** The parent node (attribute or root node). */
		protected Object	parent;

		/** The children. */
		protected List	children;

		/** The path from the root node to this node. */
		protected Object[]	path;
		
		/** A unique id for this node */
		protected int nodeUUID;
		
		//-------- constructors --------
		
		/**
		 *  Create a new object node.
		 *  @param parent	The parent node (if not root node).
		 *  @param object	The object.
		 *  @param attribute	The attribute.
		 */
		public ObjectNode(Object parent, Object object)
		{
			this.parent	= parent;
			this.object	= object;
			this.nodeUUID = getNextNodeUUID();
			nodes.put(object, this);
		}
		
		//-------- methods --------
		
		/**
		 *  Get the children of this node.
		 */
		public List	getChildren()
		{
			if(children==null)
			{
				children 	= new ArrayList();
				OAVObjectType	type	= copy.getType(object);
				while(type!=null)
				{
					Iterator	it	= type.getDeclaredAttributeTypes().iterator();
					while(it.hasNext())
					{
						OAVAttributeType	attr	= (OAVAttributeType)it.next();
						if(OAVAttributeType.NONE.equals(attr.getMultiplicity()))
						{
							if(copy.getAttributeValue(object, attr)!=null)
								children.add(new AttributeNode(this, attr));
						}
						else
						{
							if(copy.getAttributeValues(object, attr)!=null)
								children.add(new AttributeNode(this, attr));
						}
					}
					type	= type.getSupertype();
				}
			}
			return children;
		}
		
		/**
		 *  Get the path of this node (inclusive) starting from the root node.
		 */
		public Object[]	getPath()
		{
			if(path==null)
			{
				if(parent instanceof AttributeNode)
				{
					path	= (Object[])SUtil.joinArrays(((AttributeNode)parent).getPath(), new Object[]{this});
				}
				else
				{
					path	= new Object[]{parent, this};
				}
			}
			
			return path;
		}
		
		/**
		 *  Unregister a node and its subnodes.
		 */
		public void	drop()
		{
			nodes.remove(object);
			if(children!=null)
			{
				for(int i=0; i<children.size(); i++)
					((AttributeNode)children.get(i)).drop();
			}
		}
		
		/**
		 *  Create a string representation of the attribute node.
		 *  @return A string representation of the attribute node.
		 */
		public String	toString()
		{
			String ret = ""+object;
			try
			{
				// Hack!!! configure name slot?
				OAVObjectType	type	= copy.getType(object);
				OAVAttributeType name = null;
				try
				{
					name = type.getAttributeType("element_has_name");
				}
				catch(Exception e)
				{
					name	= null;
				}
				if(name!=null)
				{
					Object val	= copy.getAttributeValue(object, name);
					if(val!=null)
						ret = val.toString() + " (id="+ret+")";
				}
			}
			catch(Exception e)
			{
				System.err.println("no name for "+object+", "+(object!=null?object.getClass().toString():"null"));
				e.printStackTrace();
			}
			
			return ret;
		}
		
		/**
		 * This method can be used to do a sematically equals check.
		 * E.g. check only the fields, not the unique identifier.
		 * @param obj Object to test for equals
		 * @param checkUUID flag to check the unique Identifier for the node. <br><code>true</code>=do a compete equals check<br><code>false</code>=do a sematically equals check
		 */
		protected boolean equals(Object obj, boolean checkUUID) 
		{
			boolean ret =  obj instanceof ObjectNode
					&& ((ObjectNode)obj).object==object;
			
			if (checkUUID && ret)
				ret = ret && ((ObjectNode)obj).nodeUUID==nodeUUID;
			
			return ret;
		}
		
		public boolean equals(Object obj)
		{
			return equals(obj, true);
		}
		
		public int hashCode()
		{
//			int ret = 31 + object.hashCode();
//				//ret = ret*31 + nodeUUID;
//				//ret = ret*31	+ (nodeUUID != null ? nodeUUID.hashCode() : 0);
//			return ret;
			
			return nodeUUID;
		}
	}
	
	/**
	 *  The root node containing the nodes for the root objects of the state.
	 */
	public class RootNode 
	{
		//-------- attributes --------
		
		/** The children. */
		protected List	children;
		
		//-------- constructors --------
		
		/**
		 *  Create a new object node.
		 */
		public RootNode()
		{
		}
		
		//-------- methods --------
		
		/**
		 *  Get the children of this node.
		 */
		public List	getChildren()
		{
			if(children==null)
			{
				children 	= new ArrayList();
				for(Iterator it=copy.getRootObjects(); it.hasNext(); )
				{
					Object	child	= it.next();
					if(!(copy.getType(child) instanceof OAVJavaType))
						child = new ObjectNode(this, child);
					else if(isInspectable(child))
						// objectInspector Node
						child = new ObjectInspectorNode(this, child.getClass(), null, child);
					children.add(child);
				}
			}
			return children;
		}		
	}
	
	/**
	 * TreeModel node for java object inspection
	 * @author claas
	 *
	 */
	public class ObjectInspectorNode extends AbstractInspectorNode
	{
		/** The Class type for this node */
		protected Class type;
		
		/** The object represented by this node */
		protected Object nodeObject;
		
		/** The list of fields of the represented object*/
		protected List fields;
		
		/** The name for this node e.g. the objects name */
		protected String name;
		
		/** A prefix to display with name, e.g. for arrays "[index]" */
		protected String namePrefix;

		// ---- constructors ----
		
		/**
		 * Create a ObjectInspectorNode
		 * 
		 * @param type Class type for this object (e.g. myObject.class)
		 * @param name for this node
		 * @param object to inspect
		 */
		public ObjectInspectorNode(Object parent, Class type, String name, Object object)
		{
			this(parent, type, null, name, object);
		}
		
		/**
		 * Create a ObjectInspectorNode
		 * 
		 * @param type Class type for this object (e.g. myObject.class)
		 * @param name for this node
		 * @param object to inspect
		 */
		public ObjectInspectorNode(Object parent, Class type, String namePrefix, String name, Object object)
		{
			this.parent = parent;
			this.type = type;
			this.name = name;
			this.namePrefix = namePrefix;
			this.nodeObject = object;
			
			getFields();
			
			nodes.put(object, this);
		}

		// --- methods ----
		
		/**
		 * Generate and return a List of all fields for the 
		 * object represented by this node.
		 * @return List of accessible node object fields
		 */
		public List getFields()
		{
			
			// create list of fields only once, 
			//number or type of attributes can't change at runtime
			if (fields==null)
			{	
				this.fields = new ArrayList();
			
				// find all fields for a class expect strings and null values
				if (!type.isPrimitive() && !type.isArray() && !type.equals(String.class) && nodeObject != null)
				{
					// iterate over fields from the class and superclasses
					for (Class clazz = nodeObject.getClass(); clazz != null; clazz = clazz.getSuperclass())
					{
						Field[] f = clazz.getDeclaredFields();
						AccessibleObject.setAccessible(f, true);
	
						for (int i = 0; i < f.length; i++)
						{
							// get only nonstatic fields
							if(!Modifier.isStatic(f[i].getModifiers()))
							{
								fields.add(f[i]);
							}
							// TO-DO: Filter other fields as well?
						}
					}
				}
			}
			return fields;
		}
		
		/**
		 *  Get the children of this node.
		 */
		public List	getChildren()
		{
			if(children==null)
			{
				children = new ArrayList();
				Iterator	it	= fields.iterator();
				while(it.hasNext())
				{
					Field f = (Field) it.next();
					try
					{
						children.add(new ObjectInspectorAttributeNode(this, f, null));
					} 
					catch (IllegalAccessException e)
					{
						// Field not accessible - ignore for children ?
						children.add("-ERROR- Exception occurred: " +e);
					}
				}
			}
			return children;
		}
		
		/**
		 *  Get the path of this node (inclusive) starting from the root node.
		 */
		public Object[]	getPath()
		{
			if(path==null)
			{
				if (parent != null)
				{
					if(parent instanceof AttributeNode)
					{
						path	= (Object[])SUtil.joinArrays(((AttributeNode)parent).getPath(), new Object[]{this});
					}
					else if(parent instanceof ObjectInspectorAttributeNode)
					{
						path	= (Object[])SUtil.joinArrays(((ObjectInspectorAttributeNode)parent).getPath(), new Object[]{this});
					}
					else 
					{
						path	= new Object[]{parent, this};
					}
				}
				else
				{
					path	= new Object[]{this};
				}
			}
			
			return path;
		}
		
		
		/**
		 *  Unregister a node and its subnodes.
		 */
		public void	drop()
		{
			nodes.remove(nodeObject);
			
			if(children!=null)
			{
				for(int i=0; i<children.size(); i++)
				{
					((ObjectInspectorAttributeNode)children.get(i)).drop();
				}
			}
		}
		
		/** 
		 * Access the object represented by this node
		 * @return the nodeObject Attribute
		 */
		protected Object getNodeObject()
		{
//			if (isInpsectionRootNode())
				return nodeObject;
//			else
//			{
//				try
//				{
//					Object obj = null;
//					if (((ObjectInspectorAttributeNode)parent).isArrayNode())
//					{
//						// parent is an array attribute, use the attribute value for this node
//						obj = ((ObjectInspectorAttributeNode)parent).getArrayValue();
//					}
//					else
//					{
//						// parent is an normal attribute, use the attribute value for this node
//						obj = ((ObjectInspectorAttributeNode)parent).getFieldValue();
//					}
//					
//					if (obj == nodeObject || (obj != null && obj.equals(nodeObject)))
//					{
//						// ignore, its the same object
//					}
//					else
//					{
//						// replace nodeObject
//						nodeObject = obj;
//						fields = null;
//					}
//					
//					return nodeObject;
//					
//				} catch (Exception e)
//				{
//					return "-ERROR- Exception occurred: " + e; 
//					//e.printStackTrace();
//				}
//			}
		}
		
		protected boolean isInpsectionRootNode()
		{
			return (!(parent instanceof ObjectInspectorAttributeNode));
		}

		/**
		 * This method can be used to do a sematically equals check.
		 * E.g. check only the fields, not the unique identifier.
		 * @param obj Object to test for equals
		 * @param checkUUID flag to check the unique Identifier for the node. <br><code>true</code>=do a compete equals check<br><code>false</code>=do a sematically equals check
		 */
		protected boolean equals(Object obj, boolean checkUUID)
		{
			boolean ret = obj instanceof ObjectInspectorNode
				&& ((ObjectInspectorNode)obj).parent==parent
				&& ((ObjectInspectorNode)obj).type==type
				&& SUtil.equals(((ObjectInspectorNode)obj).name, name)
				&& (fields != null && fields.equals(((ObjectInspectorNode)obj).fields))
				&& (nodeObject != null && nodeObject.equals(((ObjectInspectorNode) obj).nodeObject));
			
			if (checkUUID && ret)
				ret = ret && ((ObjectInspectorNode)obj).nodeUUID==nodeUUID;
			
			return ret;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			return equals(obj, true);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{		
//			int ret = 31 + (parent != null ? parent.hashCode() : 0);
//				ret = ret*31	+ (type!=null ? type.hashCode() : 0);
//				ret = ret*31	+ (name!=null ? name.hashCode() : 0);
//				ret = ret*31	+ (fields != null ? fields.hashCode() : 0);
//				ret = ret*31	+ (nodeObject != null ? nodeObject.hashCode() : 0);
//				//ret = ret*31	+ nodeUUID;
//				//ret = ret*31	+ (nodeUUID != null ? nodeUUID.hashCode() : 0);
//			return ret;
			
			return nodeUUID;
			
		}

		/**
		 * Get a string representation of this node
		 */
		public String toString()
		{
			getNodeObject();
			return  (namePrefix != null ? namePrefix : "") 
					+ (name!=null ? name : "") 
//					+ (type.isPrimitive() ? " = " : "") 
					+ (nodeObject!=null ? nodeObject.toString() : "null") ;
		}

		

	} // class ObjectInspectorNode

	
	
	/**
	 * Node for an Java object attribute
	 * @author claas
	 *
	 */
	class ObjectInspectorAttributeNode extends AbstractInspectorNode
	{
		
		/** The field represented by this node */
		protected Field field;
		
		/** The Class type for this attribute node */
		protected Class type;

		/** The name for this node e.g. the objects name */
		protected String name;
		
		/** 
		 * The attribute value represented by this node <p> 
		 * This value will be updated every time getChildren() creates a list of children
		 */
		protected Object attributeValue;


		// ---- constructor -----
		
		/**
		 * Create a ObjectInspectorAttributeNode with a dynamic 
		 * reference to the inspectable Field
		 * @param objectInspectorNode parent Node
		 * @param f the field to get from object parameter
		 * @param namePrefix the object to get the field from
		 */
		public ObjectInspectorAttributeNode(ObjectInspectorNode parent, Field f, String name) 
		throws IllegalAccessException
		{
			this.parent = parent;
			this.field = f;
			this.type = f.getType();
			this.name = (name!=null?name:f.getName());

			inspectors.add(this);
		}

		// ---- methods ----
		
		/**
		 *  Get the children of this node.
		 */
		public List	getChildren()
		{
			if(children==null)
			{
				this.children = new ArrayList();
				
				//this.attributeValue = field.get(attributeObject);
				
				// get value for this node
				// the parent Array-Field if this is an array, else the value itself
				//Object value = null;
				//if (type.isArray())
				if (isArrayNode())
				{
					try
					{
//						attributeValue = getArrayValue();
						attributeValue = getFieldValue();
						
						if (attributeValue == null)
						{
//							children.add("null");
							
							// IGNORE !
							
							// don't add children for null valued arrays
							//children.add(new ObjectInspectorValueNode(this, null, attributeValue));
						}
						else 
						{
							for (int i = 0; i < Array.getLength(attributeValue); i++)
							{
								
								Object obj = Array.get(attributeValue, i);
								
								if (OAVTreeModel.isInspectable(obj))
								{
									children.add(new ObjectInspectorNode(this, obj.getClass(), "["+i+"] ", null, obj));
								}
								else
								{
//									children.add("["+i+"] "+obj);
									children.add(new ObjectInspectorValueNode(this, "["+i+"] ", obj));
								}
								
							}
						}
						
					} catch (Exception e)
					{
						e.printStackTrace();
						children.add("-ERROR- Exception occurred: " +e);
						
					}
				}
				else
				{
					this.attributeValue = getFieldValue();
					
					// create a new object inspector node for inspectable attribute
					if (OAVTreeModel.isInspectable(attributeValue))
					{
						children.add(new ObjectInspectorNode(this, type, name, attributeValue));	
					}
					// else add a simple value node 
					else
					{
						//children.add((attributeValue!=null ? attributeValue : "null"));
						children.add(new ObjectInspectorValueNode(this, null, attributeValue));
					}
					
				}
			}
			return children;
		}
		
		protected boolean isArrayNode()
		{
			attributeValue = getFieldValue();
			return type.isArray() || (attributeValue != null && attributeValue.getClass().isArray());
		}
		
		/**
		 * returns the object that is represented by this attribute node as is.
		 * e.g. the field of the parent node object.
		 * If this object is an array, the array itself is returned. If you need access to the value
		 * of the index that is represented with this node, use getArrayValue() instead.
		 */
		protected Object getFieldValue()
		{
			try
			{
				return field.get(((ObjectInspectorNode)parent).getNodeObject());
			} catch (Exception e)
			{
				//e.printStackTrace();
				return "-ERROR- Exception occurred: " + e;
			}
		}
		
		/**
		 * Returns the value for the represented field from the parent object inspector node
		 * If this field is an array, the value from the array index represented 
		 * by this node is returned. When access to the array is needed, e.g. to 
		 * create the the node children, use getFieldValue() instead. 
		 * @return
		 */
		protected Object getArrayValue()
		{
			try
			{
				if (isArrayNode())
				{
					return Array.get(field.get(((ObjectInspectorNode)parent).getNodeObject()), ((ObjectInspectorNode)parent).getChildren().indexOf(this)/*-1*/);
				}
				else
					return "-ERROR- getArrayValue called on a non array type";
			} catch (Exception e)
			{
				//e.printStackTrace();
				return "-ERROR- Exception occurred: " + e;
			}
		}
		
		/**
		 *  Unregister a node and its subnodes.
		 */
		public void	drop()
		{
			inspectors.remove(this);
			
			if(children!=null)
			{
				for(int i=0; i<children.size(); i++)
				{
					if(children.get(i) instanceof ObjectInspectorNode)
						((ObjectInspectorNode)children.get(i)).drop();
				}
			}
			
		}
		
		/**
		 *  Get the path of this node (inclusive) starting from the root node.
		 */
		public Object[]	getPath()
		{
			if(path==null)
			{
				if(parent!=null)
				{
					path	= (Object[])SUtil.joinArrays(((ObjectInspectorNode)parent).getPath(), new Object[]{this});
				}
				else
				{
					path	= new Object[]{this};
				}
			}
			
			return path;
		}

		/**
		 * This method can be used to do a sematically equals check.
		 * E.g. check only the fields, not the unique identifier.
		 * @param obj Object to test for equals
		 * @param checkUUID flag to check the unique Identifier for the node. <br><code>true</code>=do a compete equals check<br><code>false</code>=do a sematically equals check
		 */
		protected boolean equals(Object obj, boolean checkUUID)
		{
			boolean ret = obj instanceof ObjectInspectorAttributeNode
						&& ((ObjectInspectorAttributeNode) obj).parent == parent
						&& ((ObjectInspectorAttributeNode) obj).field == field
						&& SUtil.equals(((ObjectInspectorAttributeNode)obj).name, name);

			if (ret) 
			{
				Object objValue = ((ObjectInspectorAttributeNode) obj).attributeValue;
				// test if attribute values are equal if type is primitiv
				if (type.isPrimitive())
					ret = (attributeValue==null ? objValue==null : attributeValue.equals(objValue));
				// else test reference
				else
					ret = (attributeValue==objValue);
			}
			
			if (checkUUID && ret)
				ret = ret && ((ObjectInspectorAttributeNode)obj).nodeUUID==nodeUUID;

			return ret;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			return equals(obj, true);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
//			int ret = 31 + (parent != null ? parent.hashCode() : 0);
//				ret = ret*31	+ (field!=null ? field.hashCode() : 0);
//				ret = ret*31	+ (name!=null ? name.hashCode() : 0);
//				ret = ret*31	+ (attributeValue != null ? attributeValue.hashCode() : 0);
//				//ret = ret*31	+ nodeUUID;
//				
//			return ret;
			
			return nodeUUID;
			
		}

		/**
		 * Get a string representation of this node
		 */
		public String toString()
		{	
			String len = "";
			try
			{
				//if (type.isArray())
				if (isArrayNode())
				{
					Object arrayValue = getFieldValue();
					len = (arrayValue !=null ? ""+ Array.getLength(arrayValue) : "null");
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				len = "-ERROR-";
			}
			
			return (name != null ? name : "?? INVALID NODE NAME ??") 
				//+ (type.isArray()  ? "["+len+"]" : "")
				+ (isArrayNode()  ? "["+len+"]" : "")
				//+ (attributeValue!=null? " = "+attributeValue: " (null)")
				;
		}

	} // class ObjectInspectorAttributeNode
	
	
	/**
	 * This class represents a simple value for ObjectInspectorAtttributeNode values.
	 * It is needed to display a prefix [index] for arrays
	 */
	class ObjectInspectorValueNode extends AbstractInspectorNode
	{
		// ---- attributes ----
		
		/** A simple value node can have a displayed name prefix e.g. for Arrays */
		protected String namePrefix;
		
		/** The simple Object represented by this node */
		protected Object value;

		// --- constructor ----
		
		/** create a simple value node */
		public ObjectInspectorValueNode(Object parent, String namePrefix, Object value)
		{
			super.parent = parent;
			
			this.namePrefix = namePrefix;
			this.value = value;
			
		}
		
		// --- methods ---
		
		public List getChildren()
		{
			return null;
		}
		
		public Object[] getPath()
		{
			return (Object[]) SUtil.joinArrays(((AbstractInspectorNode)super.parent).getPath(), new Object[]{this});
		}
		
		/**
		 * This method can be used to do a sematically equals check.
		 * E.g. check only the fields, not the unique identifier.
		 * @param obj Object to test for equals
		 * @param checkUUID flag to check the unique Identifier for the node. <br><code>true</code>=do a compete equals check<br><code>false</code>=do a sematically equals check
		 */
		protected boolean equals(Object obj, boolean checkUUID)
		{
			boolean ret = (obj instanceof ObjectInspectorValueNode)
				&& ((ObjectInspectorValueNode)obj).parent == parent
				&& (value == null ? ((ObjectInspectorValueNode)obj).value == null : value.equals(((ObjectInspectorValueNode)obj).value));
			if (checkUUID && ret)
				ret = ret && ((ObjectInspectorValueNode)obj).nodeUUID==nodeUUID;
			
			return ret;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			return equals(obj, true);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
//			int ret = 31	+ (parent != null ? parent.hashCode() : 0);
//				ret = ret*31	+ (value!=null ? value.hashCode() : 0);
//				//ret = ret*31	+ nodeUUID;
//				//ret = ret*31	+ (nodeUUID != null ? nodeUUID.hashCode() : 0);
//			return ret;
			
			return nodeUUID;
				
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			String ret = (namePrefix != null ? namePrefix : "") + (value != null ? value : "null");
//			ret = "[ValueNode: " + ret + "]";
			return ret;
		}
		
	} // class ObjectInspectorValueNode
	
	/**
	 *  OAV tree cell renderer displays right icons.
	 */
	public static class OAVTreeCellRenderer extends DefaultTreeCellRenderer
	{
		/**
		 *  Get the tree cell renderer component.
		 */
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
			boolean expanded, boolean leaf, int row, boolean hasFocus) 
		{
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
//			System.out.println(value+" "+value.getClass());
			if(value instanceof ObjectNode)
				setIcon(icons.getIcon("object"));
			else if(value instanceof AttributeNode)
				setIcon(icons.getIcon("attribute"));
			else if (value instanceof ObjectInspectorNode)
				setIcon(icons.getIcon("javaobject"));
			else if (value instanceof ObjectInspectorAttributeNode)
				setIcon(icons.getIcon("javaattribute"));
			else if (value instanceof ObjectInspectorValueNode)
				setIcon(icons.getIcon("javavalue"));
			else
				setIcon(icons.getIcon("value"));
			return this;
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.DefaultTreeCellRenderer#getPreferredSize()
		 */
		public Dimension getPreferredSize()
		{
			// change prefered size to disable the swing 
			// "..." bug in tree display
			Dimension d = new Dimension(super.getPreferredSize());
			d.setSize(d.getWidth()+10, d.getHeight());
			return d;
		}	
	}
}

/**
 * Action class to update the tree model e.g. with a timer
 */
class ObjectInspectorRefreshAction implements ActionListener
{
	/** 
	 * A WeakReference to the tree model to allow the JVM to remove the 
	 * model from heap when introspector plugin is closed 
	 */
	WeakReference treeModel;
	
	/** 
	 * Create a ActionListener with a weak reference to the OAVTreeModel to update 
	 */
	public ObjectInspectorRefreshAction(OAVTreeModel treeModel)
	{
		this.treeModel = new WeakReference(treeModel);
	}

	/**
	 * Perform OAVTreeModel refresh if TreeModel reference exist else remove 
	 * Timer from Timer list
	 */
	public void actionPerformed(ActionEvent e)
	{		
		OAVTreeModel model = (OAVTreeModel) treeModel.get();
		if (model != null)
		{
			model.refreshInspectorNodes();
			//System.gc();
		}
		else
		{
			Object obj = e.getSource();
			if (obj instanceof Timer)
				OAVTreeModel.removeRefreshTimer((Timer) obj);
			
//			System.err.println("removed timer! - " + obj);
		}
	}
	
}