package jadex.bdi.examples.marsworld_classic;

import jadex.commons.SReflect;

/**
 *  The target is a map position
 */
public class Target extends LocationObject
{
	//-------- attributes --------

	/** The currently available ore amount. */
	protected int ore;

	/** The max. produceable ore amount. */
	protected int initialcapacity;

	/** The actual available capacity. */
	protected int capacity;

	/** Was the target visited. */
	protected boolean marked;

	/** A static instance count. */
	protected static int cnt;

	//-------- constructors --------

	/**
	 *  Create a new target.
	 *  Empty bean constructor.
	 */
	public Target()
	{
	}

	/**
	 *  Create a new target.
	 */
	public Target(Location loc, int initialcapacity)
	{
		setId("target#"+(cnt++));
		setLocation(loc);
		this.ore = 0;
		this.initialcapacity = initialcapacity;
		this.capacity = initialcapacity;
		this.marked = false;
	}

	/**
	 *  Change the amount of ore at the Location
	 *  @param ore The produced ore.
	 */
	public void produceOre(int ore)
	{
		if(capacity<ore)
		{
			throw new RuntimeException("This target cannot produce more ore!");
		}
		else
		{
			this.ore += ore;
			this.capacity = initialcapacity - this.ore;
		}
	}

	/**
	 *  Retrieve some ore amount.
	 *  @param amount The amount.
	 */
	public int retrieveOre(int amount)
	{
		int ret;
		if(amount>ore)
		{
			//throw new RuntimeException("Not that much ore available");
			ret = ore;
			ore = 0;
		}
		else
		{
			ret = amount;
			ore -= amount;
		}
		return ret;
	}

	/**
	 *  Get the capacity of ore.
	 *  @return The amount of Ore
	 */
	public int getOreCapacity()
	{
		return this.capacity;
	}

	/**
	 *  Get the amount of ore.
	 *  @return The amount of Ore
	 */
	public int getOre()
	{
		return this.ore;
	}

	/**
	 *  Mark this target as visited.
	 */
	public void setMarked()
	{
		this.marked = true;
	}

	/**
	 *  Test if target was visited.
	 *  @return True if already visited.
	 */
	public boolean isMarked()
	{
		return this.marked;
	}

	/**
	 *  Test if target can produce some ore.
	 *  @return true if target can produce some ore
	 */
	public boolean isOre()
	{
		return capacity>0;
	}

// bean setter and getter	
	
	/** Getter for capacity
	 * @return Returns capacity.
	 */
	public int getCapacity()
	{
		return this.capacity;
	}

	/** Setter for capacity.
	 * @param capacity The Target.java value to set
	 */
	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}

	/** Getter for initialcapacity
	 * @return Returns initialcapacity.
	 */
	public int getInitialcapacity()
	{
		return this.initialcapacity;
	}

	/** Setter for initialcapacity.
	 * @param initialcapacity The Target.java value to set
	 */
	public void setInitialcapacity(int initialcapacity)
	{
		this.initialcapacity = initialcapacity;
	}

	/** Setter for marked.
	 * @param marked The Target.java value to set
	 */
	public void setMarked(boolean marked)
	{
		this.marked = marked;
	}

	/** Setter for ore.
	 * @param ore The Target.java value to set
	 */
	public void setOre(int ore)
	{
		this.ore = ore;
	}
	
	/**
	 * Convert the Location to a string representation.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(getClass())+" name="+getId()+", inicap="+initialcapacity
			+", cap="+capacity+", ore="+ore+", marked="+marked+" loc="+location;
	}
}
