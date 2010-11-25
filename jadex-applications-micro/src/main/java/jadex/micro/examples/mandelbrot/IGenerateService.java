package jadex.micro.examples.mandelbrot;

import jadex.commons.IFuture;
import jadex.commons.service.IService;

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
