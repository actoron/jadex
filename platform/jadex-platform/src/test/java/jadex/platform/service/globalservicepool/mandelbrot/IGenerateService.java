package jadex.platform.service.globalservicepool.mandelbrot;

import jadex.commons.future.IFuture;

/**
 *  Service for generating a specific area.
 */
public interface IGenerateService
{
	/**
	 *  Generate a specific area using a defined x and y size.
	 */
	public IFuture<AreaData> generateArea(AreaData data);
}
