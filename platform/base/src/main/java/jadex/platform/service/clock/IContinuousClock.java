package jadex.platform.service.clock;

import jadex.bridge.service.types.clock.IClock;



/**
 *  Continuous clock interface extends a normal clock
 *  by adding a method getting the dilation.
 */
public interface IContinuousClock extends IClock
{
	/**
	 *  Get the clocks dilation.
	 *  @return The clocks dilation.
	 */
	public double getDilation();
	
	/**
	 *  Set the clocks dilation.
	 *  @param dilation The clocks dilation.
	 */
	public void setDilation(double dilation);
}
