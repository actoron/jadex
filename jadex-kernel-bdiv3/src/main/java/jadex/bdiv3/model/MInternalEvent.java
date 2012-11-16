package jadex.bdiv3.model;

/**
 * 
 */
public class MInternalEvent extends MClassBasedElement
{
	/**
	 *  Create a new internal event.
	 */
	public MInternalEvent(String target)//, boolean posttoall, boolean randomselection, String excludemode)
	{
		super(target, true, false, null);
	}
}
