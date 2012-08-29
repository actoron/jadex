package jadex.platform.service.cli.commands;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class DestroyComponentCommand extends ACliCommand
{
	public static IStringObjectConverter CID_CONVERTER = new IStringObjectConverter()
	{
		public Object convertString(String val, Object context) throws Exception
		{
			IExternalAccess comp = (IExternalAccess)((CliContext)context).getUserContext();
			if(val.indexOf("@")==-1)
			{
				val += "@"+comp.getComponentIdentifier().getPlatformName();
			}
			return new ComponentIdentifier(val);
		}
	};
	
	/**
	 *  Get the command names.
	 */
	public String[] getNames()
	{
		return new String[]{"dc", "destroycomponent", "kc", "killcomponent"};
	}
	
	/**
	 *  Get the command description.
	 */
	public String getDescription()
	{
		return "Destroy a component on the platform.";
	}
	
	/**
	 * 
	 * @param context
	 * @param args
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
			
			SServiceProvider.getService(comp.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Map<String, Object>>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					cms.destroyComponent(cid).addResultListener(new DelegationResultListener<Map<String,Object>>(ret));
				}
			});
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param context
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo ai = new ArgumentInfo(null, IComponentIdentifier.class, null, "The component to destroy.", CID_CONVERTER);
		return new ArgumentInfo[]{ai};
	}
	
	/**
	 * 
	 * @param context
	 */
	public ResultInfo getResultInfo(CliContext context)
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
