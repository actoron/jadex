package jadex.bdi.examples.hunterprey_classic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import jadex.bridge.IComponentIdentifier;


/**
 *  Editable Java class for concept Creature of hunterprey ontology.
 */
public abstract class Creature extends WorldObject
{
//	IInternalAccess	$component	= null;
//	Object o	= 					((IDF)$component.getFeature(jadex.bridge.service.component.IRequiredServicesFeature.class).searchLocalService(new ServiceQuery( IDF.class, ServiceScope.PLATFORM))).createDFComponentDescription(
//		$component.getId(), ((IDF)$component.getFeature(jadex.bridge.service.component.IRequiredServicesFeature.class).searchLocalService(new ServiceQuery( IDF.class, ServiceScope.PLATFORM)))
//		.createDFServiceDescription("environment-service", "hunter-prey environment", "University of Hamburg",
//				new String[]{"JADEX_XML"}, new String[]{"hunterprey"}, new String[]{"fipa-request"}, null));
	
	//-------- constants --------

	/** All possible directions. */
	public static final String[] alldirs = new String[]{RequestMove.DIRECTION_UP, RequestMove.DIRECTION_RIGHT, RequestMove.DIRECTION_DOWN, RequestMove.DIRECTION_LEFT};

	//-------- attributes ----------

	/** The age of the creature (in simulation steps). */
	protected int age;

	/** Attribute for slot points. */
	protected int points;

	/** Unique name for this creature. */
	protected String name;

	/** Agent identifier of the creature. */
	protected transient IComponentIdentifier aid;

	/** The distance a creature is able to see. */
	protected int visionrange;

	/** The width of the world. */
	protected int worldwidth;

	/** The height of the world. */
	protected int worldheight;

	/** The number of simulation ticks, this creature is considered alive. */
	protected int leaseticks;

	//-------- constructors --------

	/**
	 *  Create a new Creature.
	 */
	public Creature()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Get the age of this Creature.
	 *  The age of the creature (in simulation steps).
	 * @return age
	 */
	public int getAge()
	{
		return this.age;
	}

	/**
	 *  Set the age of this Creature.
	 *  The age of the creature (in simulation steps).
	 * @param age the value to be set
	 */
	public void setAge(int age)
	{
		this.age = age;
	}

	/**
	 *  Get the points of this Creature.
	 * @return points
	 */
	public int getPoints()
	{
		return this.points;
	}

	/**
	 *  Set the points of this Creature.
	 * @param points the value to be set
	 */
	public void setPoints(int points)
	{
		this.points = points;
	}

	/**
	 *  Get the name of this Creature.
	 *  Unique name for this creature.
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name of this Creature.
	 *  Unique name for this creature.
	 * @param name the value to be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the aid of this Creature.
	 *  Agent identifier of the creature.
	 * @return aid
	 */
	public IComponentIdentifier getAID()
	{
		return this.aid;
	}

	/**
	 *  Set the aid of this Creature.
	 *  Agent identifier of the creature.
	 * @param aid the value to be set
	 */
	public void setAID(IComponentIdentifier aid)
	{
		this.aid = aid;
	}

	/**
	 *  Get the visionrange of this Creature.
	 *  The distance a creature is able to see.
	 * @return visionrange
	 */
	public int getVisionRange()
	{
		return this.visionrange;
	}

	/**
	 *  Set the visionrange of this Creature.
	 *  The distance a creature is able to see.
	 * @param visionrange the value to be set
	 */
	public void setVisionRange(int visionrange)
	{
		this.visionrange = visionrange;
	}

	/**
	 *  Get the world-width of this Creature.
	 *  The width of the world.
	 * @return world-width
	 */
	public int getWorldWidth()
	{
		return this.worldwidth;
	}

	/**
	 *  Set the world-width of this Creature.
	 *  The width of the world.
	 * @param worldwidth the value to be set
	 */
	public void setWorldWidth(int worldwidth)
	{
		this.worldwidth = worldwidth;
	}

	/**
	 *  Get the world-height of this Creature.
	 *  The height of the world.
	 * @return world-height
	 */
	public int getWorldHeight()
	{
		return this.worldheight;
	}

	/**
	 *  Set the world-height of this Creature.
	 *  The height of the world.
	 * @param worldheight the value to be set
	 */
	public void setWorldHeight(int worldheight)
	{
		this.worldheight = worldheight;
	}

	/**
	 *  Get the leaseticks of this Creature.
	 *  The number of simulation ticks, this creature is considered alive.
	 * @return leaseticks
	 */
	public int getLeaseticks()
	{
		return this.leaseticks;
	}

	/**
	 *  Set the leaseticks of this Creature.
	 *  The number of simulation ticks, this creature is considered alive.
	 * @param leaseticks the value to be set
	 */
	public void setLeaseticks(int leaseticks)
	{
		this.leaseticks = leaseticks;
	}

	//-------- custom code --------

	/**
	 *  Test if two creatures are equal.
	 */
	public boolean equals(Object o)
	{
		return o instanceof Creature && ((Creature)o).getName().equals(getName());
	}

	/**
	 *  Get the hash code of the creature.
	 */
	public int hashCode()
	{
		return getName().hashCode();
	}

	/**
	 *  Clone the creature.
	 */
	public Object clone()
	{
		Creature ret = null;
		try
		{
			ret = (Creature)getClass().newInstance();
			ret.setName(this.getName());
			ret.setAge(this.getAge());
			ret.setPoints(this.getPoints());
			ret.setAID(this.getAID());
			ret.setLocation(this.getLocation());
			ret.setLeaseticks(this.getLeaseticks());
		}
		catch(InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		return ret;
	}

	//-------- map helper methods --------

	/**
	 *  Create a location.
	 *  @param dir The direction.
	 *  @return The new location.
	 */
	public Location createLocation(String dir)
	{
		return createLocation(this.getLocation(), dir);
	}

	/**
	 *  Create a location.
	 *  @param loc The location.
	 *  @param dir The direction.
	 *  @return The new location.
	 */
	public Location createLocation(Location loc, String dir)
	{
		int x = loc.getX();
		int y = loc.getY();
		int width = getWorldWidth();
		int height = getWorldHeight();

		if(RequestMove.DIRECTION_UP.equals(dir))
		{
			y = (height + y - 1) % height;
		}
		else if(RequestMove.DIRECTION_DOWN.equals(dir))
		{
			y = (y + 1) % height;
		}
		else if(RequestMove.DIRECTION_LEFT.equals(dir))
		{
			x = (width + x - 1) % width;
		}
		else if(RequestMove.DIRECTION_RIGHT.equals(dir))
		{
			x = (x + 1) % width;
		}

		return new Location(x, y);
	}

	/**
	 *  Get the distance between me and an object.
	 *  @return The number of moves required to move between the objects
	 */
	public int getDistance(WorldObject a)
	{
		return getLocationDistance(getLocation(), a.getLocation());
	}

	/**
	 *  Get the distance between two objects
	 *  @return The number of moves required to move between the objects
	 */
	public int getDistance(WorldObject a, WorldObject b)
	{
		return getLocationDistance(a.getLocation(), b.getLocation());
	}

	/**
	 *  Get the distance between two locations.
	 *  @return The number of moves required to move between the locations.
	 */
	public int getLocationDistance(Location a, Location b)
	{
		int dx = Math.abs(a.getX() - b.getX());
		int dy = Math.abs(a.getY() - b.getY());
		if(dx > getWorldWidth() / 2)
			dx = getWorldWidth() - dx;
		if(dy > getWorldHeight() / 2)
			dy = getWorldHeight() - dy;
		return dx + dy; // Assume no diagonal movement.
	}

	/**
	 *  Test if two locations are near
	 *  Range is in all direction (including diagonals).
	 */
	public boolean isNear(Location a, Location b, int range)
	{
		int dx = Math.abs(a.getX() - b.getX());
		int dy = Math.abs(a.getY() - b.getY());
		if(dx > getWorldWidth() / 2)
			dx = getWorldWidth() - dx;
		if(dy > getWorldHeight() / 2)
			dy = getWorldHeight() - dy;
		return dx <= range && dy <= range;
	}

	/**
	 *  Test if a location is in my vision range.
	 */
	public boolean isInVisionRange(Location a)
	{
		int dx = Math.abs(a.getX() - getLocation().getX());
		int dy = Math.abs(a.getY() - getLocation().getY());
		if(dx > getWorldWidth() / 2)
			dx = getWorldWidth() - dx;
		if(dy > getWorldHeight() / 2)
			dy = getWorldHeight() - dy;
		return dx <= getVisionRange() && dy <= getVisionRange();
	}

	/**
	 *  Get the directions between me and an object.
	 *  @return The possible directions to move nearer to the 2nd object.
	 */
	public String[] getDirections(WorldObject a)
	{
		return getDirections(getLocation(), a.getLocation());
	}

	/**
	 *  Get the directions between two objects.
	 *  @return The possible directions to move nearer to the 2nd object.
	 */
	public String[] getDirections(WorldObject a, WorldObject b)
	{
		return getDirections(a.getLocation(), b.getLocation());
	}

	/**
	 *  Get the directions between two locations.
	 *  @return The possible directions to move nearer to the 2nd location.
	 */
	public String[] getDirections(Location a, Location b)
	{
		int distance = getLocationDistance(a, b);
		ArrayList directions = new ArrayList();
		directions.add(RequestMove.DIRECTION_UP);
		directions.add(RequestMove.DIRECTION_DOWN);
		directions.add(RequestMove.DIRECTION_LEFT);
		directions.add(RequestMove.DIRECTION_RIGHT);
		for(Iterator i = directions.iterator(); i.hasNext();)
		{
			// Remove, if direction is not towards second location.
			if(getLocationDistance(createLocation(a, (String)i.next()), b) >= distance)
			{
				i.remove();
			}
		}
		return (String[])directions.toArray(new String[directions.size()]);
	}

	/**
	 *  Sort objects by distance.
	 */
	public void sortByDistance(WorldObject[] objects)
	{
		sortByDistance(objects, getLocation());
	}

	/**
	 *  Sort objects by distance.
	 */
	public void sortByDistance(WorldObject[] objects, final Location loc)
	{
		Arrays.sort(objects, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return getLocationDistance(loc, ((WorldObject)o1).getLocation()) - getLocationDistance(loc, ((WorldObject)o2).getLocation());
			}
		});
	}

	/**
	 *  Get a world object at a specified location.
	 *  @param loc The location.
	 *  @return The object at the location.
	 */
	public WorldObject getObject(Location loc, WorldObject[] objects)
	{
		WorldObject ret = null;
		for(int i = 0; i < objects.length; i++)
		{
			if(objects[i].getLocation().equals(loc))
				ret = objects[i];
		}
		return ret;
	}

	/**
	 *  Get all possible directions to move.
	 *  @param objects The objects near.
	 *  @return The objects one can move to.
	 */
	public String[] getPossibleDirections(WorldObject[] objects)
	{
		List posdirs = new ArrayList();
		for(int i = 0; i < alldirs.length; i++)
		{
			if(!(getObject(createLocation(Creature.alldirs[i]), objects) instanceof Obstacle))
				posdirs.add(alldirs[i]);
		}
		return (String[])posdirs.toArray(new String[posdirs.size()]);
	}

	/**
	 *  Update the creature.
	 */
	public void update(Creature creature)
	{
		if(getAge()!=creature.getAge())
			setAge(creature.getAge());
		if(getPoints()!=creature.getPoints())
			setPoints(creature.getPoints());
		if(!getName().equals(creature.getName()))
			setName(creature.getName());
		if(getVisionRange()!=creature.getVisionRange())
			setVisionRange(creature.getVisionRange());
		if(getWorldHeight()!=creature.getWorldHeight())
			setWorldHeight(creature.getWorldHeight());
		if(getWorldWidth()!=creature.getWorldWidth())
			setWorldWidth(creature.getWorldWidth());
		if(getLeaseticks()!=creature.getLeaseticks())
			setWorldWidth(creature.getLeaseticks());
		if(!getLocation().equals(creature.getLocation()))
			setLocation(creature.getLocation());
	}
}
