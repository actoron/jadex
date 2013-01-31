package jadex.agentkeeper.game.state.buildings;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.Vector2Int;


/**
 * Simple dataholding of the Buildungs in the Scene for use in State-related
 * stuff
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class Building
{
	private int			owner;

	private Vector2Int	location;

	private String		type;

	private SpaceObject	spaceobject;

	/**
	 * @return the owner
	 */
	public int getOwner()
	{
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(int owner)
	{
		this.owner = owner;
	}

	/**
	 * @return the location
	 */
	public Vector2Int getLocation()
	{
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Vector2Int location)
	{
		this.location = location;
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

	/**
	 * @return the spaceobject
	 */
	public SpaceObject getSpaceobject()
	{
		return spaceobject;
	}

	/**
	 * @param spaceobject the spaceobject to set
	 */
	public void setSpaceobject(SpaceObject spaceobject)
	{
		this.spaceobject = spaceobject;
	}

}
