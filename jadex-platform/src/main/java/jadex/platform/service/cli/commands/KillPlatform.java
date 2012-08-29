/**
 * 
 */
package jadex.platform.service.cli.commands;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

import java.util.Map;

/**
 *
 */
public class KillPlatform extends ACliCommand
{
	/**
	 *  Get the command names.
	 */
	public String[] getNames()
	{
		return new String[]{"kp", "killcomponent"};
	}
	
	/**
	 *  Get the command description.
	 */
	public String getDescription()
	{
		return "Destroy the platform.";
	}
	
	/**
	 * 
	 * @param context
	 * @param args
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		SServiceProvider.getService(comp.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.destroyComponent(comp.getComponentIdentifier().getRoot())
					.addResultListener(new DelegationResultListener<Map<String,Object>>(ret));
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
		return new ResultInfo(Map.class, "The termination result.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				
				buf.append("platform successfully destroyed.").append(SUtil.LF);
				
				return buf.toString();
			}
		});
	}
}
