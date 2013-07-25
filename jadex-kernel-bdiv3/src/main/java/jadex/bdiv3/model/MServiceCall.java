package jadex.bdiv3.model;

/**
 * 
 */
public class MServiceCall extends MProcessableElement
{
	/** The method info. */
	protected MethodInfo methodinfo;
	
	/**
	 *  Create a new service call.
	 */
	public MServiceCall(String name, String target, boolean posttoall, boolean randomselection, String excludemode, MethodInfo methodinfo)
	{
		super(name, posttoall, randomselection, excludemode);
		this.methodinfo = methodinfo;
	}
}
