package jadex.commons.future;

/**
 *  Send a backward command in direction of the source, i.e. the original future emitting value.
 */
public interface IBackwardCommandFuture
{
	/**
	 *  Send a backward command in direction of the source.
	 *  @param info The command info.
	 */
	public void sendBackwardCommand(Object info);
}
