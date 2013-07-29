package jadex.micro.testcases.nfproperties;

import java.util.Properties;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;

public class CoreNumberProperty extends AbstractNFProperty<Integer, Void>
{
	protected int cores;
	
	protected NFPropertyMetaInfo metainfo;
	
	public CoreNumberProperty(String name)
	{
		super(name);
		metainfo = new NFPropertyMetaInfo(name, int.class, null, false, -1);
		cores = Runtime.getRuntime().availableProcessors();
		
		Properties props = System.getProperties();
		for (Object key : props.keySet())
		{
			System.out.println(System.getProperty((String) key));
		}
		
	}

	@Override
	public INFPropertyMetaInfo getMetaInfo()
	{
		return metainfo;
	}

	@Override
	public Integer getValue(Class<Integer> type, Class<Void> unit)
	{
		return cores;
	}
}
