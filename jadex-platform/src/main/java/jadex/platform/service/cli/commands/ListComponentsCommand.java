package jadex.platform.service.cli.commands;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
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
public class ListComponentsCommand extends ACliCommand
{
	/**
	 *  Get the command names.
	 */
	public String[] getNames()
	{
		return new String[]{"lc", "listcomponents"};
	}
	
	/**
	 *  Get the command description.
	 */
	public String getDescription()
	{
		return "List all components running on the platform.";
	}
	
	/**
	 * 
	 * @param context
	 * @param args
	 */
	public Object invokeCommand(CliContext context, Map<String, Object> args)
	{
		final Future<IComponentDescription[]> ret = new Future<IComponentDescription[]>();
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		SServiceProvider.getService(comp.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentDescription[]>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getComponentDescriptions().addResultListener(new DelegationResultListener<IComponentDescription[]>(ret));
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
		return new ResultInfo(Map.class, "List of running components", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				
				buf.append("running components: ").append(SUtil.LF);
				
				if(val!=null)
				{
					IComponentDescription[] res = (IComponentDescription[])val;
					for(IComponentDescription desc: res)
					{
						buf.append(desc.getName().getLocalName()).append(" (").append(desc.getModelName()).append(")").append(SUtil.LF);
					}
				}
				
				return buf.toString();
			}
		});
	}
}
