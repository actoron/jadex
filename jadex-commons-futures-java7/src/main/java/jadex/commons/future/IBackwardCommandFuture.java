package jadex.commons.future;

/**
 * 
 */
public interface IBackwardCommandFuture
{
	/**
	 *  Send a backward command in direction of the source.
	 *  @param info The command info.
	 */
	public void sendBackwardCommand(Object info);
}
