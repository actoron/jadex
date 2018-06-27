package jadex.bdi.examples.hunterprey_classic;


/**
 *  Java class for concept CurrentVision of hunterprey_beans ontology.
 */
public class CurrentVision
{
	//-------- attributes ----------

	/** The creature. */
	protected Creature creature;

	/** The current vision of the creature. */
	protected Vision vision;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>CurrentVision</code>.
	 */
	public CurrentVision()
	{
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new CurrentVision.<br>
	 *  Initializes the object with required attributes.
	 * @param creature
	 * @param vision
	 */
	public CurrentVision(Creature creature, Vision vision)
	{
		this();
		setCreature(creature);
		setVision(vision);
	}

	//-------- accessor methods --------

	/**
	 *  Get the creature of this CurrentVision.
	 *  The creature.
	 * @return creature
	 */
	public Creature getCreature()
	{
		return this.creature;
	}

	/**
	 *  Set the creature of this CurrentVision.
	 *  The creature.
	 * @param creature the value to be set
	 */
	public void setCreature(Creature creature)
	{
		this.creature = creature;
	}

	/**
	 *  Get the vision of this CurrentVision.
	 *  The current vision of the creature.
	 * @return vision
	 */
	public Vision getVision()
	{
		return this.vision;
	}

	/**
	 *  Set the vision of this CurrentVision.
	 *  The current vision of the creature.
	 * @param vision the value to be set
	 */
	public void setVision(Vision vision)
	{
		this.vision = vision;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this CurrentVision.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CurrentVision(" + "creature=" + getCreature() + ", vision=" + getVision() + ")";
	}
}
