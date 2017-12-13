package jadex.micro.testcases.semiautomatic.features;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.AbstractComponentFeature;

/**
 * 
 */
public class CustomFeature extends AbstractComponentFeature implements ICustomFeature
{
	/**
	 *  Factory method constructor for instance level.
	 */
	public CustomFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Test method.
	 */
	public String someMethod()
	{
		return "hello from new agent feature";
	}
}
