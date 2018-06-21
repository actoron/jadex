package jadex.rules.rulesystem.rete.nodes;

import jadex.rules.state.OAVObjectType;


/**
 *  A type node for filtering by type.
 */
public class TypeNode extends AlphaNode
{
	//-------- attributes --------
	
	/** The object type. */
	protected OAVObjectType type;

	//-------- constructors --------
	
	/**
	 *  Create a new node.
	 */
	public TypeNode(int nodeid, OAVObjectType type)
	{
		super(nodeid, null); // Need no constraint check as this is already done in the rete node

		this.type = type;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the object type.
	 *  @return The type.
	 */
	public OAVObjectType getObjectType()
	{
		return type;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return toString(", type="+type);
	}
}
