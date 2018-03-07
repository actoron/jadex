package jadex.commons.future;


/**
 *  Additional interface for result listeners that are 
 *  able to process commands from the source future.
 */
public interface IFutureCommandListener
{
	/**
	 *  Called when a command is available.
	 */
	public void commandAvailable(Object command);
}
