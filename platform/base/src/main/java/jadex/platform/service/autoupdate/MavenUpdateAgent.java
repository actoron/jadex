package jadex.platform.service.autoupdate;

import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.micro.annotation.Agent;

@Agent
public class MavenUpdateAgent extends UpdateAgent
{
	//-------- attributes --------
	
	/** The resource to update. */
	protected IResourceIdentifier rid = new ResourceIdentifier(null, new GlobalResourceIdentifier("org.activecomponents.jadex:jadex-platform-standalone:2.1", null, null));

//	/**
//	 *  Start the update check.
//	 */
//	protected IFuture<Void> startUpdating(final IResourceIdentifier rid)
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		agent.waitFor(interval, new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				checkForUpdate(rid).addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
//				{
//					public void customResultAvailable(IResourceIdentifier newrid)
//					{
//						performUpdate().addResultListener(new DelegationResultListener<Void>(ret));
//						
////						String d1s = rid.getGlobalIdentifier().getVersionInfo();
////						String d2s = rid.getGlobalIdentifier().getVersionInfo();
////						if(d1s!=null && d2s!=null)
////						{
////							try
////							{
////								Date d1 = new Date(new Long(d1s).longValue());
////								Date d2 = new Date(new Long(d2s).longValue());
////								if(d2.after(d1))
////								{
////									performUpdate().addResultListener(new DelegationResultListener<Void>(ret));
////								}
////							}
////							catch(Exception e)
////							{
////								e.printStackTrace();
////							}
////						}
////						else
////						{
////							ret.setResult(null);
////						}
//					}
//				});
//				return IFuture.DONE;
//			}
//		});
//		
//		return ret;
//	}
//	
//	/**
//	 *  Check if an update is available.
//	 */
//	protected IFuture<IResourceIdentifier> checkForUpdate(final IResourceIdentifier rid)
//	{
//		return getVersion(rid);
//	}
//	
//	/**
//	 *  Get the version for a resource.
//	 */
//	protected IFuture<IResourceIdentifier> getVersion(final IResourceIdentifier rid)
//	{
//		final Future<IResourceIdentifier> ret = new Future<IResourceIdentifier>();
//		IFuture<IDependencyService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getService("depser");
//		fut.addResultListener(new ExceptionDelegationResultListener<IDependencyService, IResourceIdentifier>(ret)
//		{
//			public void customResultAvailable(IDependencyService depser)
//			{
//				depser.loadDependencies(rid, false).addResultListener(new ExceptionDelegationResultListener<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>, IResourceIdentifier>(ret)
//				{
//					public void customResultAvailable(Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>> result)
//					{
//						IResourceIdentifier newrid = result.getFirstEntity();
////						System.out.println("versions: "+rid.getGlobalIdentifier().getVersionInfo()+" "+newrid.getGlobalIdentifier().getVersionInfo());
//						ret.setResult(newrid);
//					}
//				});
//			}
//		});
//		return ret;
//	}
}
