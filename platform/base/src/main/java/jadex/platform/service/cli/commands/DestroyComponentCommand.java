package jadex.platform.service.cli.commands;

import java.util.Iterator;
import java.util.Map;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *
 */
public class DestroyComponentCommand extends ACliCommand
{
	public static final IStringObjectConverter CID_CONVERTER = new IStringObjectConverter()
	{
		public Object convertString(String val, Object context) throws Exception
		{
			IExternalAccess comp = (IExternalAccess)((CliContext)context).getUserContext();
			String pfn = comp.getId().getPlatformName();
			if(val.indexOf("@")==-1 && !val.equals(pfn))
			{
				val += "@"+pfn;
			}
			return new BasicComponentIdentifier(val);
		}
	};
	
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"dc", "destroycomponent", "kc", "killcomponent"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Destroy a component on the platform.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "dc chat : destroy component named chat at current platform";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
		
		final IComponentIdentifier cid = (IComponentIdentifier)args.get(null);
		if(cid==null)
		{
			ret.setException(new RuntimeException("No component id given."));
		}
		else
		{
			final IExternalAccess comp = (IExternalAccess)context.getUserContext();
			
			comp.scheduleStep(new IComponentStep<Map<String, Object>>()
			{
				public IFuture<Map<String, Object>> execute(IInternalAccess ia)
				{
					final Future<Map<String, Object>> ret = new Future<Map<String, Object>>();
			
					comp.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
						.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
					{
						public void customResultAvailable(IComponentManagementService cms)
						{
							cms.destroyComponent(cid).addResultListener(new DelegationResultListener<Map<String,Object>>(ret));
						}
					});
					
					return ret;
				}
			}).addResultListener(new DelegationResultListener<Map<String, Object>>(ret));
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
		ArgumentInfo ai = new ArgumentInfo(null, IComponentIdentifier.class, null, "The component to destroy.", CID_CONVERTER);
		return new ArgumentInfo[]{ai};
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
				
				buf.append("component successfully destroyed.").append(SUtil.LF);
				
				if(val!=null)
				{
					Map<String, Object> res = (Map<String, Object>)val;
					for(Iterator<String> it = res.keySet().iterator(); it.hasNext(); )
					{
						String key = it.next();
						Object v = res.get(key);
						buf.append(key).append(" = ");
						if(v!=null)
						{
							buf.append(v.toString());
						}
						else
						{
							buf.append("null");
						}
					}
				}
				
				return buf.toString();
			}
		});
	}
}
