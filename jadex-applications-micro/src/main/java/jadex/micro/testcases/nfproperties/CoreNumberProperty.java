package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Non-functional property reporting the CPU core count.
 *
 */
public class CoreNumberProperty extends AbstractNFProperty<Integer, Void>
{
	/** CPU core count. */
	protected int cores;
	
	/**
	 *  Create the property.
	 */
	public CoreNumberProperty()
	{
		super(new NFPropertyMetaInfo("cores", int.class, null, false, -1, false, null));
		cores = Runtime.getRuntime().availableProcessors();
		
//		Properties props = System.getProperties();
//		for (Object key : props.keySet())
//		{
//			System.out.println(System.getProperty((String) key));
//		}
	}

//	public IFuture<Integer> getValue(Class<Void> unit)
	/**
	 *  Returns the value.
	 */
	public IFuture<Integer> getValue(Void unit)
	{
		return new Future<Integer>(cores);
	}
}
