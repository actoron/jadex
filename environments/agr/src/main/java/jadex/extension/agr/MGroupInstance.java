package jadex.extension.agr;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.SReflect;

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
	public MPosition[]	getMPositions()
	{
		return positions==null ? null : (MPosition[])positions.toArray(new MPosition[positions.size()]);
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
	 */
	public MGroupType getGroupType(MAGRSpaceType spacetype)
	{
		return spacetype.getGroupType(getTypeName());
	}
	
	/**
	 *  Get a string representation of this AGR group instance.
	 *  @return A string representation of this AGR group instance.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(getName());
		sbuf.append(", typename=");
		sbuf.append(getTypeName());
		if(positions!=null)
		{
			sbuf.append(", positions=");
			sbuf.append(positions);
		}
		sbuf.append(")");
		return sbuf.toString();
	}
}
