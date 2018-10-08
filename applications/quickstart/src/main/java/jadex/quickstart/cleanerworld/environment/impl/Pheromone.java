package jadex.quickstart.cleanerworld.environment.impl;

import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.quickstart.cleanerworld.environment.IPheromone;

/**
 *  Environment representation of a pheromone.
 */
public class Pheromone	extends LocationObject	implements IPheromone
{
	//-------- static attributes --------

	/** Counter for unique IDs. */
	private static int instancecnt;
	
	/**
	 *  Get an instance number.
	 */
	private static synchronized int	getNumber()
	{
		return ++instancecnt;
	}
	
	//-------- attributes --------
	
	/** The pheromone type (arbitrary string). */
	private String	type;
	
	/** The creation time of the pheromone, used to calculate its strength. */
	private long creation;
	
	/** The clock service, used to determine age of phermone for calculating its strength. */
	private IClockService	clock;
	
	//-------- constructors --------

	/**
	 *  Create a new pheromone.
	 */
	public Pheromone()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new pheromone.
	 *  @param location	The location.
	 */
	public Pheromone(Location location, String type)
	{
		super("Pheromone_#"+getNumber(), location);
		this.type	= type;
		if(ExecutionComponentFeature.LOCAL.get()==null)
		{
			throw new IllegalStateException("Pheromone needs to be created on agent thread.");
		}
		this.clock	= ExecutionComponentFeature.LOCAL.get().getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IClockService.class));
		this.creation	= clock.getTime();
	}

	//-------- methods --------
	
	/**
	 *  Get a string representation of this object.
	 *  @return The string representation.
	 */
	public String toString() {
		return "Pheromone(" + "type="+getType()
			+ ", strength="+getStrength()
			+ ", location="+getLocation() + ")";
	}
	
	/**
	 *  Copy the object.
	 */
	@Override
	public Pheromone clone()
	{
		return (Pheromone)super.clone();
	}

	/**
	 *  Get the type of the pheromone.
	 *  @return The pheromone type as string.
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 *  Set the type of the pheromone.
	 */
	public void setType(String type)
	{
		this.type	= type;
	}
	
	/**
	 *  Get the clock.
	 */
	public IClockService getClock()
	{
		return clock;
	}
	
	/**
	 *  Set the clock.
	 */
	public void setClock(IClockService clock)
	{
		this.clock	= clock;
	}
	
	/**
	 *  Get the strength of the pheromone (0..1).
	 *  @return The current strength of the pheromone.
	 */
	public double getStrength()
	{
		// age of pheromone in seconds
		double	age	= (clock.getTime() - creation) / 1000.0;
		
		// Here the evaporation rate is calculated.
		// Change for, e.g., exponential evaporation.
		return Math.max(0.0, 1.0 - age*EVAPORATION_RATE);
	}
}

