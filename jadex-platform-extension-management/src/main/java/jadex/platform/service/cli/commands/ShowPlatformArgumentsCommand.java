package jadex.platform.service.cli.commands;

import java.util.HashMap;
import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Show the platform arguments or a single argument.
 */
public class ShowPlatformArgumentsCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"showargs", "sa"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "List current directory.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "sp awamechanisms : list the content of the argument named awamechanisms";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		final String argname = (String)args.get(null);
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		Map<String, Object> params = (Map<String, Object>)Starter.getPlatformValue(comp.getComponentIdentifier().getRoot(), IPlatformConfiguration.PLATFORMARGS);

		Map<String, Object> res = new HashMap<String, Object>();
		
		if(argname!=null)
		{
//			if(params.containsKey(argname))
//			{
				Object val = params.get(argname);
				res.put(argname, val);
//			}
//			else
//			{
//				comp.scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						ia.get
//						return null;
//					}
//				});
//			}
		}
		else
		{
			res.putAll(params);
		}
		
		ret.setResult(res);
		
		return ret;
	}
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo argname = new ArgumentInfo(null, String.class, null, "The argument name.", null);
		return new ArgumentInfo[]{argname};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(final CliContext clicontext, final Map<String, Object> args)
	{
		return new ResultInfo(Map.class, "The result.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				
				Map<String, Object> pargs = (Map<String, Object>)val;
				
				final String argname = (String)args.get(null);
				
				if(argname!=null)
				{
					buf.append("Argument value for ").append(argname).append(" is: ").append(pargs.get(argname)).append(SUtil.LF);
				}
				else
				{
					buf.append("All arguments values held by the platform: ").append(SUtil.LF);
					for(String name: pargs.keySet())
					{
						buf.append(name).append(": ").append(pargs.get(name)).append(SUtil.LF);
					}
				}
				
				return buf.toString();
			}
		});
	}
	
	
}
