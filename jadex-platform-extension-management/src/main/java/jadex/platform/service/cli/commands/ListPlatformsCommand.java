/**
 * 
 */
package jadex.platform.service.cli.commands;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.awareness.IAwarenessManagementService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;

/**
 *  List all currently known platforms.
 */
public class ListPlatformsCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"lp", "listplatforms"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "List all currently known platforms.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "lp : list all available platforms";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(CliContext context, Map<String, Object> args)
	{
		final IntermediateFuture<IComponentIdentifier> ret = new IntermediateFuture<IComponentIdentifier>();
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		SServiceProvider.getServices(comp.getServiceProvider(), IProxyAgentService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingIntermediateResultListener<IProxyAgentService>(new IIntermediateResultListener<IProxyAgentService>()
		{
			protected int ongoing = 0;
			protected boolean finished = false;
			public void intermediateResultAvailable(final IProxyAgentService ser)
			{
				ongoing++;
	//			System.out.println("found: "+result);
				ser.getRemoteComponentIdentifier().addResultListener(new SwingResultListener<IComponentIdentifier>(new IResultListener<IComponentIdentifier>()
				{
					public void resultAvailable(IComponentIdentifier cid)
					{
//						IComponentIdentifier key = ((IService)ser).getServiceIdentifier().getProviderId();
						ret.addIntermediateResultIfUndone(cid);
						ongoing--;
						checkFinished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ongoing--;
						checkFinished();
					}
				}));
			}
			
			public void finished()
			{
				finished = true;
				checkFinished();
	//			System.out.println("fini");
			}
			
			public void resultAvailable(Collection<IProxyAgentService> result)
			{
				if(result!=null)
				{
					for(IProxyAgentService ser: result)
					{
						intermediateResultAvailable(ser);
					}
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
			
			protected void checkFinished()
			{
				if(ongoing==0 && finished)
				{
					ret.setFinishedIfUndone();
				}
			}
		}));
		
//		SServiceProvider.getService(comp.getServiceProvider(), IAwarenessManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<IAwarenessManagementService, Collection<IComponentIdentifier>>(ret)
//		{
//			public void customResultAvailable(IAwarenessManagementService awas)
//			{
//				awas.getKnownPlatforms().addResultListener(new ExceptionDelegationResultListener<Collection<DiscoveryInfo>, Collection<IComponentIdentifier>>(ret)
//				{
//					public void customResultAvailable(Collection<DiscoveryInfo> result)
//					{
//						final List<IComponentIdentifier> res = new ArrayList<IComponentIdentifier>();
//						for(DiscoveryInfo dis: result)
//						{
//							res.add(dis.getComponentIdentifier());
//						}
//						
//						SServiceProvider.getService(comp.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//							.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Collection<IComponentIdentifier>>(ret)
//						{
//							public void customResultAvailable(IComponentManagementService cms)
//							{
//								cms.getChildrenDescriptions(comp.getComponentIdentifier().getRoot()).addResultListener(
//									new ExceptionDelegationResultListener<IComponentDescription[], Collection<IComponentIdentifier>>(ret)
//								{
//									public void customResultAvailable(IComponentDescription[] results) 
//									{
//										for(IComponentDescription desc: results)
//										{
//											// Hack, use constant?!o
//											if("jadex.platform.service.remote.Proxy".equals(desc.getModelName())
//												&& !res.contains(desc.getName()))
//											{
//												res.add(new ComponentIdentifier(desc.getName().getLocalName(), desc.getName().getAddresses()));
//											}
//										}
//										
//										ret.setResult(res);
//									}
//								});
//							}
//						});
//					}
//				});
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args)
	{
		return new ResultInfo(Collection.class, "The list of platforms.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				Collection<IComponentIdentifier> res = (Collection<IComponentIdentifier>)val;
				if(res!=null)
				{
					Iterator<IComponentIdentifier> it = res.iterator();
					for(int i=0; it.hasNext(); i++)
					{
//						buf.append("(").append(i).append(") ").append(it.next().getComponentIdentifier()).append(SUtil.LF);
						buf.append("* ").append(it.next()).append(SUtil.LF);
					}
				}
				return buf.toString();
			}
		});
	}
}
