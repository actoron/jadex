/**
 * 
 */
package jadex.platform.service.cli.commands;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class ListPlatformsCommand extends ACliCommand
{
	/**
	 *  Get the command names.
	 */
	public String[] getNames()
	{
		return new String[]{"lp", "listplatforms"};
	}
	
	/**
	 *  Get the command description.
	 */
	public String getDescription()
	{
		return "List all currently known platforms.";
	}
	
	/**
	 * 
	 * @param context
	 * @param args
	 */
	public Object invokeCommand(CliContext context, Map<String, Object> args)
	{
		final Future<Collection<DiscoveryInfo>> ret = new Future<Collection<DiscoveryInfo>>();
		IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		SServiceProvider.getService(comp.getServiceProvider(), IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IAwarenessManagementService, Collection<DiscoveryInfo>>(ret)
		{
			public void customResultAvailable(IAwarenessManagementService awas)
			{
				awas.getKnownPlatforms().addResultListener(new DelegationResultListener<Collection<DiscoveryInfo>>(ret));
			}
		});
		return ret;
	}
	
	/**
	 * 
	 * @param context
	 */
	public ResultInfo getResultInfo(CliContext context)
	{
		return new ResultInfo(Collection.class, "The list of platforms.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				Collection<DiscoveryInfo> res = (Collection<DiscoveryInfo>)val;
				if(res!=null)
				{
					Iterator<DiscoveryInfo> it = res.iterator();
					for(int i=0; it.hasNext(); i++)
					{
						buf.append("(").append(i).append(") ").append(it.next().getComponentIdentifier()).append(SUtil.LF);
					}
				}
				return buf.toString();
			}
		});
	}
}
