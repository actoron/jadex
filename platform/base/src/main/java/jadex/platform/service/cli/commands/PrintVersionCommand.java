package jadex.platform.service.cli.commands;

import java.util.Map;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.VersionInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Print the Jadex version on the console.
 */
public class PrintVersionCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"v", "version", "printversion"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Print the Jadex version on the console.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "version : print the Jadex version";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(CliContext context, Map<String, Object> args)
	{
		final Future<VersionInfo> ret = new Future<VersionInfo>();
		ret.setResult(VersionInfo.getInstance());
		return ret;
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, final Map<String, Object> args)
	{
		return new ResultInfo(String.class, "Success of the command.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				return ""+val;
			}
		});
	}
}
