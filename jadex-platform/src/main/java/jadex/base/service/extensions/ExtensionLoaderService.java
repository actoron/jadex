package jadex.base.service.extensions;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IExtensionLoaderService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.StringTokenizer;

/**
 *  Load extensions at startup.
 */
@Service
public class ExtensionLoaderService implements IExtensionLoaderService
{
	//-------- attributes --------
	
	/** The component providing the service. */
	@ServiceComponent
	protected IInternalAccess	component;
	
	//-------- methods --------
	
	/**
	 *  Start service.
	 */
	@ServiceStart
	public IFuture<Void>	start()
	{
		IFuture<Void>	ret	= IFuture.DONE;
		
		String	extensions	= component.getArguments()!=null
			? (String)component.getArguments().get("extensions") : null;
		
		if(extensions!=null)
		{
			final StringTokenizer	stok	= new StringTokenizer(extensions, ", ");
			if(stok.hasMoreTokens())
			{
				final Future<Void>	fut	= new Future<Void>();
				ret	= fut;
				IFuture<IComponentManagementService> rsfut = component.getServiceContainer().getRequiredService("cms");
				rsfut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(fut)
				{
					public void customResultAvailable(final IComponentManagementService cms)
					{
						if(stok.hasMoreTokens())
						{
							final String	model	= stok.nextToken();
//							final IComponentManagementService	cms	= (IComponentManagementService)result;
							cms.createComponent(null, model, new CreationInfo(component.getComponentIdentifier()), null)
								.addResultListener(new IResultListener<IComponentIdentifier>()
							{
								public void resultAvailable(IComponentIdentifier result)
								{
									customResultAvailable(cms);	// Continue with next token.
								}
								
								public void exceptionOccurred(Exception exception)
								{
									component.getLogger().warning("Extension '"+model+"' could not be loaded: "+exception);
									customResultAvailable(cms);	// Continue with next token.
								}
							});
						}
						else
						{
							fut.setResult(null);
						}
					}
				});
			}
		}
		
		return ret;
	}
}
