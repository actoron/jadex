package jadex.micro.examples.mandelbrot;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.appstore.AppMetaInfo;
import jadex.bridge.service.types.appstore.IAppProviderService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public class AppProviderService implements IAppProviderService<IMandelbrotService>
{
	/** The app meta info. */
	protected AppMetaInfo ami;
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	public AppProviderService()
	{
		this.ami = new AppMetaInfo("Mandelbrot", "Jadex", "Allows to render fractal images", "1.0", 
			null, null);
	}
	
	/**
	 * 
	 */
	public IFuture<AppMetaInfo> getAppMetaInfo()
	{
		return new Future<AppMetaInfo>(ami);
	}

	/**
	 * 
	 */
	public IFuture<IMandelbrotService> getApplication()
	{
		IMandelbrotService ms = (IMandelbrotService)agent.getFeature(IProvidedServicesFeature.class).getProvidedServices(IMandelbrotService.class)[0];
		return new Future<IMandelbrotService>(ms);
	}
}
