package jadex.micro.testcases;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

/**
 *  Behavior of the component result test.
 */
public class DestroyStep implements IComponentStep<Void>
{
	/**
	 *  Execute the test.
	 */
	public IFuture<Void> execute(final IInternalAccess ia)
	{
		ia.killComponent();
		return IFuture.DONE;
	}
}
