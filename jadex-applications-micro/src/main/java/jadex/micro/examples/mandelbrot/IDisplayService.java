package jadex.micro.examples.mandelbrot;

import jadex.commons.future.IFuture;

/**
 *  Service for displaying the result of a calculation. 
 */
public interface IDisplayService
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
