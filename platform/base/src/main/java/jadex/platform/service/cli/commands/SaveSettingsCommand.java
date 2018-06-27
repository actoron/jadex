package jadex.platform.service.cli.commands;

import java.util.Map;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Save setting a file.
 */
public class SaveSettingsCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"sas", "save", "savesettings"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Save the settings.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "sas : Save the platform settings.";
//		return "sas set.xml : Save the settings as set.xml";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<Void> ret = new Future<Void>();
		
		// todo: use filename
		final String filename = (String)args.get(null);
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		SServiceProvider.getService(comp, ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<ISettingsService, Void>(ret)
		{
			public void customResultAvailable(ISettingsService ss)
			{
				ss.saveProperties().addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		
		return ret;
	}
		
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo fn = new ArgumentInfo(null, String.class, null, null, null);
		return new ArgumentInfo[]{fn};
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
				return "Platform settings saved successfully.";
			}
		});
	}
}
