package jadex.platform.service.cli.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Command for executing service queries.
 */
public class ServiceQueryCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"q", "query"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Execute a service query on the platform.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "q ";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		comp.scheduleStep(new IComponentStep<Collection<IService>>()
		{
			public IFuture<Collection<IService>> execute(IInternalAccess ia)
			{
				final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
				
				final String type = (String)args.get("-type");
				final String scope = (String)args.get("-scope");
				IComponentIdentifier owner = (IComponentIdentifier)args.get("-owner");
				final IComponentIdentifier provider = (IComponentIdentifier)args.get("-provider");
				final String[] tags = (String[])args.get("-tags");
				
				if(owner==null)
					owner = ia.getId().getRoot();
				
				ServiceQuery<IService> q = new ServiceQuery<IService>(type==null? null: new ClassInfo(type), scope, owner).setProvider(provider);
				if(tags!=null)
					q.setServiceTags(tags);
				
				//SServiceProvider.getServices(ia, q, false);
				ia.getFeature(IRequiredServicesFeature.class).searchServices(q)
					.addIntermediateResultListener(new IntermediateDelegationResultListener<IService>(ret));
				
				return ret;
			}
		}).addResultListener(new IntermediateDelegationResultListener<IService>(ret));
		
		ret.addResultListener(new IResultListener<Collection<IService>>()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			public void resultAvailable(Collection<IService> result)
			{
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
		ArgumentInfo type = new ArgumentInfo("-type", String.class, null, "The type.", null);
		ArgumentInfo scope = new ArgumentInfo("-scope", String.class, null, "The scope.", null);
		ArgumentInfo owner = new ArgumentInfo("-owner", IComponentIdentifier.class, null, "The owner.", DestroyComponentCommand.CID_CONVERTER);
		ArgumentInfo provider = new ArgumentInfo("-provider", IComponentIdentifier.class, null, "The provider.", DestroyComponentCommand.CID_CONVERTER);
		ArgumentInfo tags = new ArgumentInfo("-tags", String[].class, null, "The resource identifier.", new IStringObjectConverter()
		{
			public Object convertString(String val, Object context) throws Exception
			{
				List<String> ret = new ArrayList<String>();
				StringTokenizer stok = new StringTokenizer(val, ",");
				while(stok.hasMoreTokens())
				{
					ret.add(stok.nextToken());
				}
				return ret.toArray(new String[ret.size()]);
			}
		});
		
		return new ArgumentInfo[]{type, scope, owner, provider, tags};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args)
	{
		return new ResultInfo(Collection.class, "The creation result.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				Collection<IService> c = (Collection<IService>)val;
				
				buf.append("Found the following services, ").append("size="+c!=null?c.size():0).append(SUtil.LF);
				
				if(c!=null)
				{
					for(IService s: c)
					{
						buf.append(s.getId()).append(SUtil.LF);
					}
				}
				
				return buf.toString();
			}
		});
	}
}
