package jadex.bridge.sensor.mac;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.NFRootProperty;
import jadex.commons.SUtil;

/**
 *  The (first) mac address.
 */
public class MacAddressProperty extends NFRootProperty<String, Void>
{
	/** The name of the property. */
	public static final String NAME = "mac address";
	
	/**
	 *  Create a new property.
	 */
	public MacAddressProperty(final IInternalAccess comp)
	{
		super(comp, new NFPropertyMetaInfo(NAME, String.class, null, false, -1, false, Target.Root));
	}
	
	/**
	 *  Measure the value.
	 */
	public String measureValue()
	{
		return SUtil.getMacAddress();
	}
}
