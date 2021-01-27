package jadex.micro.examples.mandelbrot_new;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Service for generating a specific area.
 */
@Service
public interface IGenerateService
{
	/**
	 *  Generate a specific area using a defined x and y size.
	 */
	public IFuture<AreaData> generateArea(AreaData data);
	
	/**
	 *  Calculate and display the default image from current settings.
	 */
	public IFuture<Void> calcDefaultImage();
}
