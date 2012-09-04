/**
 * 
 */
package jadex.platform.service.cli.commands;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

import java.util.Map;

/**
 *  Command to set a password or network secret.
 */
public class SetSecretCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"ss", "setsecret"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Set a secret, i.e. a platform password or network.";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<Void> ret = new Future<Void>();
		
		final String plat = (String)args.get("-pn");
		final String net = (String)args.get("-nn");
		// If -p is given without value it will be set as empty
		final String pass = args.containsKey("-p") && args.get("-p")==null? "": (String)args.get("-p"); 
		
		if(plat==null && net==null)
		{
			ret.setException(new RuntimeException());
		}
		else
		{
			final IExternalAccess comp = (IExternalAccess)context.getUserContext();
			
			SServiceProvider.getService(comp.getServiceProvider(), ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<ISecurityService, Void>(ret)
			{
				public void customResultAvailable(final ISecurityService ss)
				{
					if(plat!=null)
					{
						ss.setPlatformPassword(new ComponentIdentifier(plat), pass)
							.addResultListener(new DelegationResultListener<Void>(ret));
					}
					else
					{
						ss.setNetworkPassword(net, pass)
							.addResultListener(new DelegationResultListener<Void>(ret));
					}
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo plat = new ArgumentInfo("-pn", String.class, null, "The platform name.", null);
		ArgumentInfo net = new ArgumentInfo("-nn", String.class, null, "The network name.", null);
		ArgumentInfo pass = new ArgumentInfo("-p", String.class, null, "The password.", null);
		return new ArgumentInfo[]{plat, net, pass};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context)
	{
		return new ResultInfo(Void.class, "The creation result.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				return "secret changed successfully.";
			}
		});
	}
}
