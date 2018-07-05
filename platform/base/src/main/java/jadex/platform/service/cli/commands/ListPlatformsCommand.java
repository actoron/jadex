package jadex.platform.service.cli.commands;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.bridge.service.types.remote.IProxyAgentService.State;
import jadex.commons.SUtil;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

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
		StringBuffer buf = new StringBuffer();
		buf.append("lp : list all available platforms").append(SUtil.LF);
		buf.append("lp -s: list all available platforms with their connection state").append(SUtil.LF);
		return buf.toString();
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(CliContext context, Map<String, Object> args)
	{
//		final IntermediateFuture<IComponentIdentifier> ret = new IntermediateFuture<IComponentIdentifier>();
		final IntermediateFuture<String> ret = new IntermediateFuture<String>();
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		final boolean state = args.containsKey("-s");
		
		comp.searchServices( new ServiceQuery<>(IProxyAgentService.class, RequiredServiceInfo.SCOPE_PLATFORM))
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
					public void resultAvailable(final IComponentIdentifier cid)
					{
//						IComponentIdentifier key = ((IService)ser).getServiceIdentifier().getProviderId();
						
						if(state)
						{
							ser.getConnectionState().addResultListener(new SwingResultListener<IProxyAgentService.State>(new IResultListener<IProxyAgentService.State>()
							{
								public void resultAvailable(State result) 
								{
//									System.out.println("rec: "+cid.getName()+" ("+result+")");
									ret.addIntermediateResultIfUndone(cid.getName()+" ("+result+")");
									ongoing--;
									checkFinished();
								}
								
								public void exceptionOccurred(Exception exception) 
								{
//									System.out.println("ex: "+cid.getName()+" (UNCONNECTED)");
									ret.addIntermediateResultIfUndone(cid.getName()+" ("+State.UNCONNECTED+")");
									ongoing--;
									checkFinished();
								}
							}));
						}
						else
						{
							ret.addIntermediateResultIfUndone(cid.getName());
							ongoing--;
							checkFinished();
						}
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
		
		return ret;
	}
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo state = new ArgumentInfo("-s", String.class, null, "The platform connection state.", null);
		return new ArgumentInfo[]{state};
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
				Collection<String> res = (Collection<String>)val;
				if(res!=null)
				{
					Iterator<String> it = res.iterator();
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
