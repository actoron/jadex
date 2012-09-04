package jadex.platform.service.cli.commands;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cli.ICliService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.CliShell;
import jadex.platform.service.cli.CloseShellException;
import jadex.platform.service.cli.IInternalCliService;
import jadex.platform.service.cli.RemoteCliShell;
import jadex.platform.service.cli.ResultInfo;

import java.util.Collection;
import java.util.Map;

/**
 * 
 */
public class SwitchPlatformCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"sp", "switchplatform"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Switch to a platform.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("sp Hans-PC_664").append(" : switch to platform Hans-PC_664").append(SUtil.LF);
		buf.append("sp ..").append(" : switch back to platform you came from").append(SUtil.LF);
		return buf.toString();
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, Map<String, Object> args)
	{
		final Future<IInternalCliService> ret = new Future<IInternalCliService>();
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		if("..".equals(args.get(null)))
		{
			// cannot directly close shell because this has to be done by the one that survives
			ret.setException(new CloseShellException());
		}
		else
		{
			final IComponentIdentifier cid = new ComponentIdentifier((String)args.get(null));
			
			SServiceProvider.getServices(comp.getServiceProvider(), IInternalCliService.class, RequiredServiceInfo.SCOPE_GLOBAL)
				.addResultListener(new IIntermediateResultListener<IInternalCliService>()
			{
				boolean found = false;
				public void intermediateResultAvailable(final IInternalCliService cliser)
				{
					final IComponentIdentifier plat = ((IService)cliser).getServiceIdentifier().getProviderId().getRoot();
					if(plat.equals(cid) && !ret.isDone())
					{
						found = true;
						SServiceProvider.getService(comp.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IInternalCliService>(ret)
						{
							public void customResultAvailable(IComponentManagementService cms)
							{
								cms.getExternalAccess(plat).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IInternalCliService>(ret)
								{
									public void customResultAvailable(IExternalAccess exta)
									{
										Tuple2<String, Integer> osid = context.getShell().getSessionId();
										Tuple2<String, Integer> nsid = new Tuple2<String, Integer>(osid.getFirstEntity(), new Integer(osid.getSecondEntity().intValue()+1)); 
										context.getShell().addSubshell(new RemoteCliShell(cliser, nsid));
//										ret.setResult(cliser);
										ret.setResult(null);
									}
								});
							}
						});
					}
				}
				
				public void finished()
				{
					if(!found)
						ret.setExceptionIfUndone(new RuntimeException("Target not found: "+cid));
				}
				
				public void resultAvailable(Collection<IInternalCliService> result)
				{
					for(IInternalCliService ser: result)
						intermediateResultAvailable(ser);
					finished();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setExceptionIfUndone(new RuntimeException("Target not found: "+cid));
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
		ArgumentInfo name = new ArgumentInfo(null, String.class, null, "The platform name.", null);
		return new ArgumentInfo[]{name};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args)
	{
		return new ResultInfo(Collection.class, "The name of the new platform:", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				return val!=null? ((IService)val).getServiceIdentifier().getProviderId().getRoot().getName(): "";
			}
		});
	}
}
