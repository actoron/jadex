package jadex.micro.examples.mandelbrot;

import jadex.bridge.service.annotation.GuiClass;
import jadex.commons.future.IFuture;

/**
 * 
 */
@GuiClass(MandelbrotPanel.class)
public interface IMandelbrotService
{
	/**
	 *  Get the generate service.
	 *  @return The generate service.
	 */
	public IFuture<IGenerateService> getGenerateService();
	
	/**
	 *  Get the display service.
	 *  @return The display service.
	 */
	public IFuture<IDisplayService> getDisplayService();
}
