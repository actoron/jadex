package jadex.micro.examples.mandelbrot;

import jadex.commons.IFuture;
import jadex.commons.service.IService;

/**
 *  Service for displaying the result of a calculation. 
 */
public interface IDisplayService	extends IService
{
	/**
	 *  Display the result of a calculation.
	 */
	public IFuture displayResult(AreaData result);

	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture displayIntermediateResult(ProgressData progress);
}
