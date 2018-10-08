package jadex.quickstart.cleanerworld.environment;

/**
 *  (Knowledge about) a waste bin.
 */
public interface IWastebin	extends ILocationObject
{
	/**
	 *  Get the id (or name) of this object.
	 *  @return The id.
	 */
	public String getId();

	/**
	 *  Get the location of this object.
	 * @return The location of the object.
	 */
	public ILocation getLocation();
	
	/**
	 *  Get the wastes of this Wastebin.
	 * @return wastes
	 */
	public IWaste[] getWastes();

	/**
	 *  Get an wastes of this Wastebin.
	 *  @param idx The index.
	 *  @return wastes
	 */
	public IWaste getWaste(int idx);

	/**
	 *  Get the capacity of this Wastebin.
	 * @return The maximum number of waste objects to fit in this waste bin.
	 */
	public int getCapacity();

	/**
	 *  Test is the wastebin is full.
	 *  @return True, when wastebin is full.
	 */
	public boolean isFull();

	/**
	 *  Test is the waste is in the waste bin.
	 *  @return True, when wastebin contains the waste.
	 */
	public boolean contains(IWaste waste);
}
