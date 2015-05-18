package jadex.bdiv3.model;

/**
 * 
 */
public class MServiceCall extends MProcessableElement
{
	/** The method info. */
//	protected MethodInfo methodinfo;
	
	/**
	 *  Create a new service call.
	 */
//	public MServiceCall(String name, boolean posttoall, boolean randomselection, String excludemode, MethodInfo methodinfo)
	public MServiceCall(String name, boolean posttoall, boolean randomselection, ExcludeMode excludemode)
	{
		super(name, posttoall, randomselection, excludemode);
//		this.methodinfo = methodinfo;
	}
}
