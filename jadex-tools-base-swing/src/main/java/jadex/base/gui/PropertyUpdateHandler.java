package jadex.base.gui;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * 
 */
public class PropertyUpdateHandler
{
	//-------- attributes --------

	/** The local external access. ggg */
	protected IExternalAccess access;
	
	/** The local listeners (cid+":"+propname->commands). */
//	protected Map<String, List<ICommand<IMonitoringEvent>>> commands;
	protected List<ICommand<IMonitoringEvent>> commands;
	
	/** The subscription. */
	protected ISubscriptionIntermediateFuture<IMonitoringEvent> subscription;
	
	//-------- constructors --------
	
	/**
	 *  Create a CMS update handler.
	 */
	public PropertyUpdateHandler(IExternalAccess access)
	{
		this.access	= access;
	
		SServiceProvider.getService(access.getServiceProvider(), IMonitoringService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<IMonitoringService>()
		{
			public void resultAvailable(IMonitoringService monser)
			{
				subscription = monser.subscribeToEvents(new IFilter<IMonitoringEvent>()
				{
					public boolean filter(IMonitoringEvent ev)
					{
						return IMonitoringEvent.TYPE_PROPERTY_ADDED.equals(ev.getType())
							|| IMonitoringEvent.TYPE_PROPERTY_REMOVED.equals(ev.getType());
					}
				});
				
				subscription.addResultListener(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent ev)
					{
						if(IMonitoringEvent.TYPE_PROPERTY_REMOVED.equals(ev.getType()))
						{
//							String propname = (String)ev.getProperty("propname");
//							String key = ev.getSourceIdentifier().getName()+":"+propname;
//							String key = (String)ev.getProperty("id");
//							System.out.println("prop removed: "+propname);
							
							if(commands!=null)
							{
								for(ICommand<IMonitoringEvent> cmd: commands)
								{
									cmd.execute(ev);
								}
							}
						}
						else if(IMonitoringEvent.TYPE_PROPERTY_ADDED.equals(ev.getType()))
						{
//							String propname = (String)ev.getProperty("propname");
//							String key = (String)ev.getProperty("id");
//							System.out.println("prop added: "+propname);
							
							if(commands!=null)
							{
								for(ICommand<IMonitoringEvent> cmd: commands)
								{
									cmd.execute(ev);
								}
							}
						}
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
	}
	
	/**
	 *  Add a command.
	 */
	public void addPropertyCommand(ICommand<IMonitoringEvent> cmd)
	{
//		System.out.println("add prop handler: "+cmd);
		
		if(commands==null)
			commands = new ArrayList<ICommand<IMonitoringEvent>>();
		commands.add(cmd);
	}
	
	/**
	 *  Remove a command.
	 */
	public void removePropertyCommand(ICommand<IMonitoringEvent> cmd)
	{
//		System.out.println("rem prop handler: "+cmd);
		
		if(commands!=null)
		{
			commands.remove(cmd);
		}
	}
	
//	/**
//	 *  Add a command.
//	 */
//	public void addPropertyCommand(IComponentIdentifier cid, String propname, ICommand<IMonitoringEvent> cmd)
//	{
//		System.out.println("add prop handler: "+cid+" "+propname+" "+hashCode());
//		
//		if(commands==null)
//			commands = new HashMap<String, List<ICommand<IMonitoringEvent>>>();
//		String key = cid.getName()+":"+propname;
//		List<ICommand<IMonitoringEvent>> cmds = commands.get(key);
//		if(cmds==null)
//		{
//			cmds = new ArrayList<ICommand<IMonitoringEvent>>();
//			commands.put(key, cmds);
//		}
//		cmds.add(cmd);
//	}
//	
//	/**
//	 *  Remove a command.
//	 */
//	public void removePropertyCommand(IComponentIdentifier cid, String propname, ICommand<IMonitoringEvent> cmd)
//	{
//		System.out.println("rem prop handler: "+cid+" "+propname+" "+hashCode());
//		
//		if(commands!=null)
//		{
//			String key = cid.getName()+":"+propname;
//			List<ICommand<IMonitoringEvent>> cmds = commands.get(key);
//			if(cmds!=null)
//			{
//				cmds.remove(cmd);
//			}
//		}
//	}
	
	/**
	 *  Dispose the handler.
	 */
	public void dispose()
	{
		assert SwingUtilities.isEventDispatchThread();// ||  Starter.isShutdown();
		
		subscription.terminate();
	}
}
