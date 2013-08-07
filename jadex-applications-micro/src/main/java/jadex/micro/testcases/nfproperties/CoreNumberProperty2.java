package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Properties;

public class CoreNumberProperty2 extends AbstractNFProperty<Integer, Void>
{
	protected int cores;
	
	public CoreNumberProperty2()
	{
		super(new NFPropertyMetaInfo("componentcores", int.class, null, false, -1, null));
		cores = Runtime.getRuntime().availableProcessors();
		
		Properties props = System.getProperties();
		for (Object key : props.keySet())
		{
			System.out.println(System.getProperty((String) key));
		}
	}

	public IFuture<Integer> getValue(Class<Void> unit)
	{
		return new Future<Integer>(cores);
	}
}
