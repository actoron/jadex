package jadex.backup.job.management;

import jadex.backup.swing.JobsPanel;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  Agent that is responsible for processing a job.
 */
@Agent
@Arguments(@Argument(name="cmdargs", clazz=String[].class))
@ProvidedServices(@ProvidedService(type=IJobManagementService.class, implementation=@Implementation(JobManagementService.class)))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
  binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class JobManagementAgent
{	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The gui frame. */
	protected JFrame gui;
	
	/** The cmd line args. */
	@AgentArgument
	protected String[] cmdargs;
	
	//-------- constructors --------
	
	/**
	 *  Called on startup.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		final Future<Void>	ret	= new Future<Void>();
		final IExternalAccess	ea	= agent.getExternalAccess();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui = JobsPanel.createFrame(ea);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@AgentKilled
	public IFuture<Void>	stop()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui.dispose();
				ret.setResult(null);
			}
		});
		
		return ret;
	}
}
