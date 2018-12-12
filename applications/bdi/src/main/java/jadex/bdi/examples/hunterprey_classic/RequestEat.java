package jadex.bdi.examples.hunterprey_classic;

import jadex.bridge.fipa.IComponentAction;


/**
 *  Java class for concept RequestEat of hunterprey_beans ontology.
 */
public class RequestEat implements IComponentAction
{
	//-------- attributes ----------

	/** The creature. */
	protected Creature creature;

	/** The object being eaten. */
	protected WorldObject object;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>RequestEat</code>.
	 */
	public RequestEat()
	{
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new RequestEat.<br>
	 *  Initializes the object with required attributes.
	 * @param creature
	 * @param object
	 */
	public RequestEat(Creature creature, WorldObject object)
	{
		this();
		setCreature(creature);
		setObject(object);
	}

	//-------- accessor methods --------

	/**
	 *  Get the creature of this RequestEat.
	 *  The creature.
	 * @return creature
	 */
	public Creature getCreature()
	{
		return this.creature;
	}

	/**
	 *  Set the creature of this RequestEat.
	 *  The creature.
	 * @param creature the value to be set
	 */
	public void setCreature(Creature creature)
	{
		this.creature = creature;
	}

	/**
	 *  Get the object of this RequestEat.
	 *  The object being eaten.
	 * @return object
	 */
	public WorldObject getObject()
	{
		return this.object;
	}

	/**
	 *  Set the object of this RequestEat.
	 *  The object being eaten.
	 * @param object the value to be set
	 */
	public void setObject(WorldObject object)
	{
		this.object = object;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this RequestEat.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "RequestEat(" + "creature=" + getCreature() + ", object=" + getObject() + ")";
	}
}
