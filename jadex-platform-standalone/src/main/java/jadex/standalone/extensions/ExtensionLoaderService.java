package jadex.standalone.extensions;

import java.util.StringTokenizer;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

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
	public IFuture	start()
	{
		IFuture	ret	= IFuture.DONE;
		
		String	extensions	= component.getArguments()!=null
			? (String)component.getArguments().get("extensions") : null;
		
		if(extensions!=null)
		{
			final StringTokenizer	stok	= new StringTokenizer(extensions, ", ");
			if(stok.hasMoreTokens())
			{
				final Future	fut	= new Future();
				ret	= fut;
				component.getServiceContainer().getRequiredService("cms")
					.addResultListener(new DelegationResultListener(fut)
				{
					public void customResultAvailable(Object result)
					{
						if(stok.hasMoreTokens())
						{
							final String	model	= stok.nextToken();
							final IComponentManagementService	cms	= (IComponentManagementService)result;
							cms.createComponent(null, model, new CreationInfo(component.getComponentIdentifier()), null)
								.addResultListener(new IResultListener()
							{
								public void resultAvailable(Object result)
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
		else
		{
			component.killComponent();
		}
		
		return ret;
	}
}
