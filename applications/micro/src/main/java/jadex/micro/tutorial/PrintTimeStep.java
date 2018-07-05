package jadex.micro.tutorial;

import java.util.Date;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

/**
 *  Component step that prints the time.
 */
public class PrintTimeStep implements IComponentStep<Void>
{
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> execute(IInternalAccess ia)
	{
		IFuture<IClockService> fut = ia.getFeature(IRequiredServicesFeature.class).getService("clockservice");
		fut.addResultListener(new DefaultResultListener<IClockService>()
		{
			public void resultAvailable(IClockService cs)
			{
//				IClockService cs = (IClockService)result;
				System.out.println("Time for a chat buddy: "+new Date(cs.getTime()));
			}
		});
		return IFuture.DONE;
	}
}
