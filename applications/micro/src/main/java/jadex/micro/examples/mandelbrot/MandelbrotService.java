package jadex.micro.examples.mandelbrot;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public class MandelbrotService implements IMandelbrotService
{
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/**
	 *  Get the generate service.
	 *  @return The generate service.
	 */
	public IFuture<IGenerateService> getGenerateService()
	{
		IGenerateService ser = (IGenerateService)agent.getFeature(IProvidedServicesFeature.class).getProvidedServices(IGenerateService.class)[0];
		return new Future<IGenerateService>(ser);
	}
	
	/**
	 *  Get the display service.
	 *  @return The display service.
	 */
	public IFuture<IDisplayService> getDisplayService()
	{
		IDisplayService ser = (IDisplayService)agent.getFeature(IProvidedServicesFeature.class).getProvidedServices(IDisplayService.class)[0];
		return new Future<IDisplayService>(ser);
	}
}
