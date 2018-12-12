package jadex.bdiv3.examples.cleanerworld.world;

import jadex.commons.SUtil;


/**
 *  Editable Java class for concept Cleaner of cleaner-generated ontology.
 */
public class Cleaner extends LocationObject
{
	//-------- attributes ----------

	/** Attribute for slot chargestate. */
	protected double chargestate;

	/** Attribute for slot carried-waste. */
	protected Waste carriedwaste;

	/** Attribute for slot vision-range. */
	protected double visionrange;

	/** Attribute for slot name. */
	protected String name;

	//-------- constructors --------

	/**
	 *  Create a new Cleaner.
	 */
	public Cleaner()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new Cleaner.
	 */
	public Cleaner(Location location, String name, Waste carriedwaste, double vision, double chargestate)
	{
		setLocation(location);
		setId(name);
		setName(name);
		setCarriedWaste(carriedwaste);
		setVisionRange(vision);
		setChargestate(chargestate);
	}

	/**
	 *  Get the chargestate of this Cleaner.
	 * @return chargestate
	 */
	public double getChargestate()
	{
		return this.chargestate;
	}

	/**
	 *  Set the chargestate of this Cleaner.
	 * @param chargestate the value to be set
	 */
	public void setChargestate(double chargestate)
	{
		this.chargestate = chargestate;
	}

	/**
	 *  Get the carried-waste of this Cleaner.
	 * @return carried-waste
	 */
	public Waste getCarriedWaste()
	{
		return this.carriedwaste;
	}

	/**
	 *  Set the carried-waste of this Cleaner.
	 * @param carriedwaste the value to be set
	 */
	public void setCarriedWaste(Waste carriedwaste)
	{
		this.carriedwaste = carriedwaste;
	}

	/**
	 *  Get the vision-range of this Cleaner.
	 * @return vision-range
	 */
	public double getVisionRange()
	{
		return this.visionrange;
	}

	/**
	 *  Set the vision-range of this Cleaner.
	 * @param visionrange the value to be set
	 */
	public void setVisionRange(double visionrange)
	{
		this.visionrange = visionrange;
	}

	/**
	 *  Get the name of this Cleaner.
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name of this Cleaner.
	 * @param name the value to be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Update this wastebin.
	 */
	public void update(Cleaner cl)
	{
		assert this.getId().equals(cl.getId());
		
		setChargestate(cl.getChargestate());
		setVisionRange(cl.getVisionRange());
		
		if(SUtil.equals(getCarriedWaste(), cl.getCarriedWaste()))
			setCarriedWaste(cl.getCarriedWaste());
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this Cleaner.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Cleaner(" + "id=" + getId() + ", location=" + getLocation() + ", name=" + getName() + ")";
	}

	//-------- custom code --------

	/**
	 *  Clone the object.
	 */
	public Object clone()
	{
		Cleaner clone = (Cleaner)super.clone();
		if(getCarriedWaste() != null)
			clone.setCarriedWaste((Waste)getCarriedWaste().clone());
		return clone;
	}
}
