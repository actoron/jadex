package jadex.micro.examples.mandelbrot;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

/**
 *  Service for generating a specific area.
 */
public interface IGenerateService	extends IService
{
	/**
	 *  Generate a specific area using a defined x and y size.
	 */
	public IFuture generateArea(AreaData data);
}
