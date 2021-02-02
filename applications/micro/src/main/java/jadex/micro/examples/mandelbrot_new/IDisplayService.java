package jadex.micro.examples.mandelbrot_new;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service for displaying the result of a calculation. 
 */
@Service
public interface IDisplayService
{
	/**
	 *  Display the result of a calculation.
	 */
	public IFuture<Void> displayResult(AreaData result);

	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayIntermediateResult(ProgressData progress);
	
	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayIntermediateResult(PartDataChunk progress);
	
	/**
	 *  Subscribe to display events.
	 */
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<Object> subscribeToDisplayUpdates(String displayid);
}
