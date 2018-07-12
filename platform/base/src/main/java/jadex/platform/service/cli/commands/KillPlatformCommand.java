/**
 * 
 */
package jadex.platform.service.cli.commands;

import java.util.Map;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *
 */
public class KillPlatformCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"kp", "killplatform"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Destroy the platform.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "kp : kill the current platform";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		comp.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.destroyComponent(comp.getId().getRoot())
					.addResultListener(new DelegationResultListener<Map<String,Object>>(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args)
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
