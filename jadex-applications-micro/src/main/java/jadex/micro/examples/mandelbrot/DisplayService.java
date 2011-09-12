package jadex.micro.examples.mandelbrot;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IFuture;

/**
 *  The service allows displaying results in the frame
 *  managed by the service providing agent.
 */
@Service
public class DisplayService implements IDisplayService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected DisplayAgent agent;
	
	//-------- IDisplayService interface --------

	/**
	 *  Display the result of a calculation.
	 */
	public IFuture<Void> displayResult(AreaData result)
	{
//		System.out.println("displayRes");
		agent.getPanel().setResults(result);
		return IFuture.DONE;
	}


	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayIntermediateResult(ProgressData progress)
	{
//		System.out.println("displayInRes");
		agent.getPanel().addProgress(progress);
		return IFuture.DONE;
	}
}
