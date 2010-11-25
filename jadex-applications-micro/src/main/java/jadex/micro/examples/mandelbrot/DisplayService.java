package jadex.micro.examples.mandelbrot;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;

/**
 *  The service allows displaying results in the frame
 *  managed by the service providing agent.
 */
public class DisplayService extends BasicService implements IDisplayService
{
	//-------- attributes --------
	
	/** The agent. */
	protected DisplayAgent agent;
	
	//-------- constructors --------

	/**
	 *  Create a new display service.
	 */
	public DisplayService(DisplayAgent agent)
	{
		super(agent.getServiceProvider().getId(), IDisplayService.class, null);
		this.agent	= agent;
	}
	
	//-------- IDisplayService interface --------

	/**
	 *  Display the result of a calculation.
	 */
	public IFuture displayResult(AreaData result)
	{
		agent.getPanel().setResults(result);
		return new Future(null);
	}


	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture displayIntermediateResult(ProgressData progress)
	{
		agent.getPanel().addProgress(progress);
		return new Future(null);
	}
}
