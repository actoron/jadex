package jadex.base.gui;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
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
	public static IFuture<Tuple2<ProvidedServiceInfo[], RequiredServiceInfo<?>[]>>	getServiceInfos(IExternalAccess ea)
	{
		return ea.scheduleImmediate(new IComponentStep<Tuple2<ProvidedServiceInfo[], RequiredServiceInfo<?>[]>>()
		{
			@XMLClassname("getServiceInfos")
			public IFuture<Tuple2<ProvidedServiceInfo[], RequiredServiceInfo<?>[]>> execute(IInternalAccess ia)
			{
				final Future<Tuple2<ProvidedServiceInfo[], RequiredServiceInfo<?>[]>>	ret	= new Future<Tuple2<ProvidedServiceInfo[], RequiredServiceInfo<?>[]>>();
				final RequiredServiceInfo<?>[]	ris	= ia.getServiceContainer().getRequiredServiceInfos();
				IIntermediateFuture<IService>	ds	= SServiceProvider.getDeclaredServices(ia.getServiceContainer());
				ds.addResultListener(new ExceptionDelegationResultListener<Collection<IService>, Tuple2<ProvidedServiceInfo[], RequiredServiceInfo<?>[]>>(ret)
				{
					public void customResultAvailable(Collection<IService> result)
					{
						ProvidedServiceInfo[]	pis	= new ProvidedServiceInfo[result.size()];
						Iterator<IService>	it	= result.iterator();
						for(int i=0; i<pis.length; i++)
						{
							IService	service	= it.next();
							// todo: implementation?
							pis[i]	= new ProvidedServiceInfo(service.getServiceIdentifier().getServiceName(), 
								service.getServiceIdentifier().getServiceType(), null, null);
						}
						
						ret.setResult(new Tuple2<ProvidedServiceInfo[], RequiredServiceInfo<?>[]>(pis, ris));
					}
				});
				return ret;
			}
		});		
	}
}
