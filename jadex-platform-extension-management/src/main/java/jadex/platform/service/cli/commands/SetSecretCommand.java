/**
 * 
 */
package jadex.platform.service.cli.commands;

import java.util.Map;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

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
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("ss -pn hans -p secrethans").append(" : sets password for platform hans to secrethans").append(SUtil.LF);
		buf.append("ss -nn net -p secretnet").append(" : sets password for network net to secretnet").append(SUtil.LF);
		buf.append("ss -pn hans").append(" : removes platform password for platform hans").append(SUtil.LF);
		buf.append("ss -p mysec").append(" : sets own platform password to mysec").append(SUtil.LF);
		return buf.toString();
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
		
		if(plat==null && net==null && args.get("-p")==null)
		{
			ret.setException(new RuntimeException("no parameters given."));
		}
		else
		{
			final IExternalAccess comp = (IExternalAccess)context.getUserContext();
			
			SServiceProvider.getService(comp, ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<ISecurityService, Void>(ret)
			{
				public void customResultAvailable(final ISecurityService ss)
				{
					if(plat!=null)
					{
						ss.setPlatformPassword(new BasicComponentIdentifier(plat), pass)
							.addResultListener(new DelegationResultListener<Void>(ret));
					}
					else if(net!=null)
					{
						ss.setNetworkPassword(net, pass)
							.addResultListener(new DelegationResultListener<Void>(ret));
					}
					else
					{
						ss.setLocalPassword(pass).addResultListener(new DelegationResultListener<Void>(ret));
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
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args)
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
