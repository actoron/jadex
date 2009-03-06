package jadex.adapter.base.agr;

import jadex.adapter.base.appdescriptor.MAgentType;

import java.util.ArrayList;
import java.util.List;

/**
 *  Group instance representation.
 */
public class MGroupInstance
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The type name. */
	protected String typename;
	
	/** The positions. */
	protected List positions;
	
	//-------- methods --------
	
	/**
	 *  Set the name of the group type.
	 *  @param name	The name of the group type.
	 */
	public void	setName(String name)
	{
		this.name	= name;
	}
	
	/**
	 *  Get the name of the group type.
	 *  @return The name of the group type.
	 */
	public String	getName()
	{
		return this.name;
	}

	/**
	 *  Get the type name.
	 *  @return The typename.
	 */
	public String getTypeName()
	{
		return this.typename;
	}

	/**
	 *  Set the type name.
	 *  @param typename The typename to set.
	 */
	public void setTypeName(String typename)
	{
		this.typename = typename;
	}
	
	/**
	 *  Get the positions.
	 */
	public List getMPositions()
	{
		return positions;
	}
	
	/**
	 *  Add a position.
	 *  @param position The position. 
	 */
	public void addMPosition(MPosition position)
	{
		if(positions==null)
			positions = new ArrayList();
		positions.add(position);
	}
	
	/**
	 *  Get the group type.
	 * /
	public MGroupType getType(MAGRSpaceType)
	{
		MAgentType ret = null;
		List agenttypes = apptype.getMAgentTypes();
		for(int i=0; ret==null && i<agenttypes.size(); i++)
		{
			MAgentType at = (MAgentType)agenttypes.get(i);
			if(at.getName().equals(getTypeName()))
				ret = at;
		}
		return ret;
	}*/
}
