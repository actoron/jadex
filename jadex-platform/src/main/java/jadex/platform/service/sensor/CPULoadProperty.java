package jadex.platform.service.sensor;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;

import java.util.Properties;

/**
 * 
 */
public class CPULoadProperty extends AbstractNFProperty<Double, Void>
{
	/** The current cpu load. */
	protected double load;
	
	/**
	 *  Create a new property.
	 */
	public CPULoadProperty(String name)
	{
		super(new NFPropertyMetaInfo(name, double.class, null, true, -1));
		
		Properties props = System.getProperties();
		for (Object key : props.keySet())
		{
			System.out.println(System.getProperty((String) key));
		}
		
	}

	/**
	 *  Get the value.
	 */
	public Double getValue(Class<Double> type, Class<Void> unit)
	{
		return new Double(load);
	}

	/**
	 *  Set the load.
	 *  @param load The load to set.
	 */
	public void setLoad(double load)
	{
		this.load = load;
	}
}

