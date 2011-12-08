package jadex.base.gui;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.xml.annotation.XMLClassname;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Helper class for GUI code to be executed on remote
 *  devices (e.g. android.).
 */
public class SRemoteGui
{
	//-------- methods --------
	
	/**
	 *  Get the service infos for a component.
	 *  @param ea	The component access.
	 *  @return	The provided and required service infos.
	 */
	public static IFuture<Object[]>	getServiceInfos(IExternalAccess ea)
	{
		return ea.scheduleImmediate(new IComponentStep<Object[]>()
		{
			@XMLClassname("getServiceInfos")
			public IFuture<Object[]> execute(IInternalAccess ia)
			{
				final Future<Object[]>	ret	= new Future<Object[]>();
				final RequiredServiceInfo[]	ris	= ia.getServiceContainer().getRequiredServiceInfos();
//				final IServiceIdentifier[] sid
				IIntermediateFuture<IService>	ds	= SServiceProvider.getDeclaredServices(ia.getServiceContainer());
				ds.addResultListener(new ExceptionDelegationResultListener<Collection<IService>, Object[]>(ret)
				{
					public void customResultAvailable(Collection<IService> result)
					{
						ProvidedServiceInfo[]	pis	= new ProvidedServiceInfo[result.size()];
						IServiceIdentifier[]	sis	= new IServiceIdentifier[result.size()];
						Iterator<IService>	it	= result.iterator();
						for(int i=0; i<pis.length; i++)
						{
							IService	service	= it.next();
							// todo: implementation?
							sis[i] = service.getServiceIdentifier();
							pis[i]	= new ProvidedServiceInfo(service.getServiceIdentifier().getServiceName(), 
//								service.getServiceIdentifier().getServiceType(), null, null);
								sis[i].getServiceType().getType(), null, null);
						}
						
						ret.setResult(new Object[]{pis, ris, sis});
					}
				});
				return ret;
			}
		});		
	}
}
