package jadex.rules.test.state;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test creating and dropping objects.
 */
public class ReferenceManagementTest extends TestCase
{
	//-------- OAV type definitions --------
	
	/** The node type model. */
	public static final OAVTypeModel node_type_model;
	
	/** The node type. */
	public static final OAVObjectType node_type;
	
	/** A node has a name. */
	public static final OAVAttributeType node_has_name;

	/** A node has subnodes. */
	public static final OAVAttributeType node_has_subnodes;

	static
	{
		node_type_model = new OAVTypeModel("node_type_model");
		node_type_model.addTypeModel(OAVJavaType.java_type_model);
		
		// node type
		node_type = node_type_model.createType("node");
		node_has_name = node_type.createAttributeType("node_has_name", OAVJavaType.java_string_type);
		node_has_subnodes = node_type.createAttributeType("node_has_subnodes", node_type, OAVAttributeType.LIST);
	}

	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** Root node. */
	protected Object	root;
	
	/** Intermediate node. */
	protected Object	intermediate;
	
	/** 1st leaf node. */
	protected Object	leaf1;
	
	/** 2nd leaf node. */
	protected Object	leaf2;
	
	/** Nodes, which have been removed. */
	protected Set	removed;
	
	//-------- methods --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{
		// todo: make state impl customizable
		
		this.state = OAVStateFactory.createOAVState(node_type_model);
		
		this.root	= state.createRootObject(node_type);
		state.setAttributeValue(root, node_has_name, "root");
		
		this.intermediate	= state.createObject(node_type);
		state.setAttributeValue(intermediate, node_has_name, "intermediate");
		
		this.leaf1	= state.createObject(node_type);
		state.setAttributeValue(leaf1, node_has_name, "leaf1");
		
		this.leaf2	= state.createObject(node_type);
		state.setAttributeValue(leaf2, node_has_name, "leaf2");
		
		state.addAttributeValue(root, node_has_subnodes, intermediate);
		state.addAttributeValue(intermediate, node_has_subnodes, leaf1);
		state.addAttributeValue(intermediate, node_has_subnodes, leaf2);
		
		state.notifyEventListeners();
		
		// Add state listener to store node names of removed nodes.
		removed	= new HashSet();
		state.addStateListener(new IOAVStateListener()
		{
			public void objectAdded(Object id, OAVObjectType type, boolean root)
			{
			}
			public void objectModified(Object id, OAVObjectType type,
					OAVAttributeType attr, Object oldvalue, Object newvalue)
			{
			}
			public void objectRemoved(Object id, OAVObjectType type)
			{
				removed.add(state.getAttributeValue(id, node_has_name));
			}
		}, true);
		
	}
	
	/**
	 *  Test removing a leaf node.
	 */
	public void testRemoveLeaf()
	{
		state.removeAttributeValue(intermediate, node_has_subnodes, leaf1);
		state.notifyEventListeners();
		
		Set	test	= new HashSet(Arrays.asList(new String[]{"leaf1"}));
		assertEquals(test, removed);
		checkAccess(leaf1, false);
	}

	/**
	 *  Test removing a leaf node, which is externally referenced.
	 */
	public void testRemoveLeafExternal()
	{
		state.addExternalObjectUsage(leaf1, this);
		state.removeAttributeValue(intermediate, node_has_subnodes, leaf1);
		state.notifyEventListeners();
		
		Set test	= new HashSet(Arrays.asList(new String[]{"leaf1"}));
		assertEquals(test, removed);
		checkAccess(leaf1, true);

		removed.clear();
		state.removeExternalObjectUsage(leaf1, this);
		state.notifyEventListeners();
		
		test	= Collections.EMPTY_SET;
		assertEquals(test, removed);
		checkAccess(leaf1, false);
	}
	
	/**
	 *  Test removing an intermediate node.
	 */
	public void testRemoveIntermediate()
	{
		state.removeAttributeValue(root, node_has_subnodes, intermediate);
		state.notifyEventListeners();
		
		Set	test	= new HashSet(Arrays.asList(new String[]{"leaf1", "leaf2", "intermediate"}));
		assertEquals(test, removed);
		checkAccess(intermediate, false);
		checkAccess(leaf1, false);
		checkAccess(leaf2, false);
	}

	/**
	 *  Test removing an intermediate node, which is externally referenced.
	 */
	public void testRemoveIntermediateExternal()
	{
		state.addExternalObjectUsage(intermediate, this);
		state.removeAttributeValue(root, node_has_subnodes, intermediate);
		state.notifyEventListeners();
		
		Set test	= new HashSet(Arrays.asList(new String[]{"leaf1", "leaf2", "intermediate"}));
		assertEquals(test, removed);
		checkAccess(intermediate, true);
		checkAccess(leaf1, true);
		checkAccess(leaf2, true);

		removed.clear();
		state.removeExternalObjectUsage(intermediate, this);
		state.notifyEventListeners();
		
		test	= Collections.EMPTY_SET;
		assertEquals(test, removed);
		checkAccess(intermediate, false);
		checkAccess(leaf1, false);
		checkAccess(leaf2, false);
	}
	
	/**
	 *  Test removing an intermediate node with an externally referenced leaf node.
	 */
	public void testRemoveIntermediateExternalLeaf()
	{
		state.addExternalObjectUsage(leaf1, this);
		state.removeAttributeValue(root, node_has_subnodes, intermediate);
		state.notifyEventListeners();
		
		Set test	= new HashSet(Arrays.asList(new String[]{"leaf1", "leaf2", "intermediate"}));
		assertEquals(test, removed);
		checkAccess(intermediate, false);
		checkAccess(leaf1, true);
		checkAccess(leaf2, false);

		removed.clear();
		state.removeExternalObjectUsage(leaf1, this);
		state.notifyEventListeners();
		
		test	= Collections.EMPTY_SET;
		assertEquals(test, removed);
		checkAccess(intermediate, false);
		checkAccess(leaf1, false);
		checkAccess(leaf2, false);
	}
	
	/**
	 *  Test removing an intermediate node, which is externally referenced and with an externally referenced leaf node.
	 */
	public void testRemoveIntermediateMultipleExternals()
	{
		state.addExternalObjectUsage(leaf1, this);
		state.addExternalObjectUsage(intermediate, this);
		state.removeAttributeValue(root, node_has_subnodes, intermediate);
		state.notifyEventListeners();
		
		Set test	= new HashSet(Arrays.asList(new String[]{"leaf1", "leaf2", "intermediate"}));
		assertEquals(test, removed);
		checkAccess(intermediate, true);
		checkAccess(leaf1, true);
		checkAccess(leaf2, true);

		removed.clear();
		state.removeExternalObjectUsage(intermediate, this);
		state.notifyEventListeners();
		
		test	= Collections.EMPTY_SET;
		assertEquals(test, removed);
		checkAccess(intermediate, false);
		checkAccess(leaf1, true);
		checkAccess(leaf2, false);

		removed.clear();
		state.removeExternalObjectUsage(leaf1, this);
		state.notifyEventListeners();
		
		test	= Collections.EMPTY_SET;
		assertEquals(test, removed);
		checkAccess(intermediate, false);
		checkAccess(leaf1, false);
		checkAccess(leaf2, false);
	}
	
	/**
	 *  Test removing two intermediate nodes.
	 */
	public void testRemoveMultipleIntermediatesExternal()
	{
		Object	intermediate2	= state.createObject(node_type);
		state.setAttributeValue(intermediate2, node_has_name, "intermediate2");
		state.addAttributeValue(intermediate2, node_has_subnodes, leaf1);
		state.addAttributeValue(root, node_has_subnodes, intermediate2);
		
		state.addExternalObjectUsage(intermediate2, this);
		state.addExternalObjectUsage(intermediate, this);
		state.removeAttributeValue(root, node_has_subnodes, intermediate);
		state.notifyEventListeners();
		
		Set test	= new HashSet(Arrays.asList(new String[]{"leaf2", "intermediate"}));
		assertEquals(test, removed);
		checkAccess(intermediate, true);
		checkAccess(intermediate2, true);
		checkAccess(leaf1, true);
		checkAccess(leaf2, true);

		removed.clear();
		state.removeAttributeValue(root, node_has_subnodes, intermediate2);
		state.notifyEventListeners();
		
		test	= new HashSet(Arrays.asList(new String[]{"leaf1", "intermediate2"}));
		assertEquals(test, removed);
		checkAccess(intermediate, true);
		checkAccess(intermediate2, true);
		checkAccess(leaf1, true);
		checkAccess(leaf2, true);

		removed.clear();
		state.removeExternalObjectUsage(intermediate, this);
		state.notifyEventListeners();
		
		test	= Collections.EMPTY_SET;
		assertEquals(test, removed);
		checkAccess(intermediate, false);
		checkAccess(intermediate2, true);
		checkAccess(leaf1, true);
		checkAccess(leaf2, false);

		removed.clear();
		state.removeExternalObjectUsage(intermediate2, this);
		state.notifyEventListeners();
		
		test	= Collections.EMPTY_SET;
		assertEquals(test, removed);
		checkAccess(intermediate, false);
		checkAccess(intermediate2, false);
		checkAccess(leaf1, false);
		checkAccess(leaf2, false);
	}
	
	//-------- helper methods --------

	/**
	 *  Check, if access to an object is allowed or forbidden.
	 */
	protected void checkAccess(Object node, boolean allowed)
	{
		try
		{
			state.getAttributeValue(node, node_has_name);
			if(!allowed)
				fail("Object could be accessed");
		}
		catch(Throwable e)
		{
			if(allowed)
				fail("Object could not be accessed");
		}
	}
}
