package jadex.bdi.planlib.protocols;


/**
 *  Interface for checking the acceptance of proposals.
 */
public interface ISelector
{
	/**
	 *  Select winner proposals.
	 *  @param proposals All available proposals.
	 *  @return The selected winner proposal(s) or none.
	 */
	public Object[] select(Object[] proposals);
}
