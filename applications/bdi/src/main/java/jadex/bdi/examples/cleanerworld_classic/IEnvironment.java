package jadex.bdi.examples.cleanerworld_classic;


/**
 *  The possible actions that can be performed
 *  in the environment.
 */
public interface IEnvironment
{
	//-------- cleaner actions --------

	/**
	 *  Get the current vision.
	 *  @param cleaner The cleaner.
	 *  @return The current vision.
	 */
	public Vision getVision(Cleaner cleaner);

	/**
	 *  Try to pick up some piece of waste.
	 *  @param waste The waste.
	 *  @return True if the waste could be picked up.
	 */
	public boolean pickUpWaste(Waste waste);

	/**
	 *  Drop a piece of waste.
	 *  @param waste The piece of waste.
	 *  @param wastebin The waste bin.
	 */
	public boolean dropWasteInWastebin(Waste waste, Wastebin wastebin);
	
}