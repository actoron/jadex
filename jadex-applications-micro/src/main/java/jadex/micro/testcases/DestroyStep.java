package jadex.micro.testcases;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;

/**
 *  Behavior of the component result test.
 */
public class DestroyStep implements IComponentStep
{
	/**
	 *  Execute the test.
	 */
	public Object execute(final IInternalAccess ia)
	{
		ia.killComponent();
		return null;
	}
}
