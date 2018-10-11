package jadex.platform.service.cli.commands;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Start or stop the JCC/gui.
 */
public class JCCCommand extends CreateComponentCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"openjcc", "startjcc", "jcc", "gui"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Open the Jadex Control Center.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "startjcc: start the jcc";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final String start = (String)args.get(null);
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();

		if(start==null || "start".equals(start.toLowerCase()))
		{
			Map<String, Object> newargs = new HashMap<String, Object>();
			newargs.put("-model", "jadex/tools/jcc/JCCAgent.class");
			return super.invokeCommand(context, newargs);
		}
		else
		{
			final Future<String> ret = new Future<String>();
			comp.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					final Future<Void> ret = new Future<Void>();
			
//					final IComponentManagementService cms = ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
//					IComponentDescription adesc = new CMSComponentDescription(null, null, false, false, false, false, false, null, "jadex.tools.jcc.JCC", null, null, -1, null, false);
					IComponentDescription adesc = new CMSComponentDescription().setModelName("jadex.tools.jcc.JCC");//null, null, false, false, false, false, false, null, "jadex.tools.jcc.JCC", null, null, -1, null, false);
					
//					cms.searchComponents(adesc, null, false).addResultListener(new SwingDefaultResultListener<IComponentDescription[]>()
					ia.searchComponents(adesc, null).addResultListener(new SwingDefaultResultListener<IComponentDescription[]>()
					{
						public void customResultAvailable(IComponentDescription[] descs)
						{
							CounterResultListener<Map<String, Object>> lis = new CounterResultListener<Map<String, Object>>(descs.length, new DelegationResultListener<Void>(ret));
							for(IComponentDescription desc: descs)
							{
								ia.getExternalAccess(desc.getName()).killComponent().addResultListener(lis);
							}
						}
					});
					return ret;
				}
			}).addResultListener(new ExceptionDelegationResultListener<Void, String>(ret)
			{
				public void customResultAvailable(Void result) throws Exception
				{
					ret.setResult("JCC killed");
				}
			});
			
			return ret;
		}
	}
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo startstop = new ArgumentInfo(null, String.class, null, "Start or stop the JCC.", null);
		return new ArgumentInfo[]{startstop};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, final Map<String, Object> args)
	{
		return new ResultInfo(String.class, "The current working directory.", null);
	}
}
