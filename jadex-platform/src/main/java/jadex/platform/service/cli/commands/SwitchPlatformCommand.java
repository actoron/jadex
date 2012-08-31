package jadex.platform.service.cli.commands;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

import java.util.Collection;
import java.util.Map;

/**
 * 
 */
public class SwitchPlatformCommand extends ACliCommand
{
	/**
	 *  Get the command names.
	 */
	public String[] getNames()
	{
		return new String[]{"sp", "switchplatform"};
	}
	
	/**
	 *  Get the command description.
	 */
	public String getDescription()
	{
		return "Switch to a platform.";
	}
	
	/**
	 * 
	 * @param context
	 * @param args
	 */
	public Object invokeCommand(final CliContext context, Map<String, Object> args)
	{
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		final IComponentIdentifier cid = new ComponentIdentifier((String)args.get(null));
		
		SServiceProvider.getService(comp.getServiceProvider(), IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IAwarenessManagementService, IExternalAccess>(ret)
		{
			public void customResultAvailable(IAwarenessManagementService awas)
			{
				awas.getKnownPlatforms().addResultListener(new ExceptionDelegationResultListener<Collection<DiscoveryInfo>, IExternalAccess>(ret)
				{
					public void customResultAvailable(Collection<DiscoveryInfo> results)
					{
						boolean found = false;
						for(DiscoveryInfo di: results)
						{
							final IComponentIdentifier fcid = di.getComponentIdentifier();
							if(fcid.equals(cid))
							{
								found = true;
								SServiceProvider.getService(comp.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret)
								{
									public void customResultAvailable(IComponentManagementService cms)
									{
										cms.getExternalAccess(fcid).addResultListener(new DelegationResultListener<IExternalAccess>(ret)
										{
											public void customResultAvailable(IExternalAccess result)
											{
												context.setUserContext(result);
												ret.setResult(result);
											}
										});
									}
								});
								break;
							}
						}
						
						if(!found)
						{
							ret.setException(new RuntimeException("Platform not found: "+cid));
						}
					}
				}); 
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 * @param context
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo name = new ArgumentInfo(null, String.class, null, "The platform name.", null);
		return new ArgumentInfo[]{name};
	}
	
	/**
	 * 
	 * @param context
	 */
	public ResultInfo getResultInfo(CliContext context)
	{
		return new ResultInfo(Collection.class, "The nameo of the new platform:", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				return ((IExternalAccess)val).getComponentIdentifier().getName();
			}
		});
	}
}
