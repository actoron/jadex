package jadex.agentkeeper.init.map.process;

import jadex.extension.envsupport.math.Vector2Int;

public class SimpleMapType
{
	/**
	 * Simple Helper Class for Map generation
	 * 
	 * @author Philip Willuweit p.willuweit@gmx.de
	 */

	private Vector2Int position;
	private String type;
	
	
	
	public SimpleMapType(Vector2Int position, String type)
	{
		this.position = position;
		this.type = type;
	}
	
	
	/**
	 * @return the position
	 */
	public Vector2Int getPosition()
	{
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector2Int position)
	{
		this.position = position;
	}
	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	
}
