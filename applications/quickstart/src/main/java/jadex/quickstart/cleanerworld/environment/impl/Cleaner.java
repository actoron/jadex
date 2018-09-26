package jadex.quickstart.cleanerworld.environment.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.quickstart.cleanerworld.environment.ICleaner;


/**
 *  Cleaner object represents knowledge about a cleaner robot.
 */
public class Cleaner extends LocationObject	implements ICleaner
{
	//-------- attributes ----------

	/** The agent that is controlling the cleaner. */
	private IComponentIdentifier	cid;
	
	/** Charge state of the battery (0.0-1.0). */
	private double chargestate;

	/** The carried waste, if any */
	private Waste carriedwaste;

	/** The distance that this cleaner is able to see. */
	private double visionrange;

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
	public Cleaner(IComponentIdentifier cid, Location location, Waste carriedwaste, double vision, double chargestate)
	{
		super(cid.getName(), location);
		this.cid	= cid;
		setCarriedWaste(carriedwaste);
		setVisionRange(vision);
		setChargestate(chargestate);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the associated agent.
	 *  @return The id of the associated agent.
	 */
	public IComponentIdentifier	getAgentIdentifier()
	{
		return cid;
	}

	/**
	 *  Set the associated agent.
	 */
	public void	setAgentIdentifier(IComponentIdentifier cid)
	{
		this.cid	= cid;
	}

	/**
	 *  Get the chargestate of this Cleaner.
	 * @return Charge state of the battery (0.0-1.0).
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
	 * @return The carried waste, if any.
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
	 * @return The distance that this cleaner is able to see.
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
	 *  Update this cleaner.
	 */
	public void update(Cleaner cl)
	{
		assert this.getId().equals(cl.getId());
		super.update(cl);
		
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
		return "Cleaner(" + "id=" + getId() + ", location=" + getLocation() + ")";
	}

	//-------- custom code --------

	/**
	 *  Clone the object.
	 */
	public Cleaner clone()
	{
		Cleaner clone = (Cleaner)super.clone();
		if(getCarriedWaste() != null)
			clone.setCarriedWaste((Waste)getCarriedWaste().clone());
		return clone;
	}
}
