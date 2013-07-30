package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;

import java.util.Properties;

public class CoreNumberProperty extends AbstractNFProperty<Integer, Void>
{
	protected int cores;
	
	public CoreNumberProperty(String name)
	{
		super(new NFPropertyMetaInfo(name, int.class, null, false, -1));
		cores = Runtime.getRuntime().availableProcessors();
		
		Properties props = System.getProperties();
		for (Object key : props.keySet())
		{
			System.out.println(System.getProperty((String) key));
		}
		
	}

	public Integer getValue(Class<Integer> type, Class<Void> unit)
	{
		return cores;
	}
}
