package jadex.backup.job;

import jadex.backup.swing.ObjectivesPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  Agent that is responsible for processing a job.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IJobService.class, implementation=@Implementation(JobService.class)))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
  binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@ComponentTypes(@ComponentType(name="sa", filename="jadex/backup/resource/ResourceProviderAgent.class"))
public class JobManagerAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/** The gui frame. */
	protected JFrame gui;
	
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
				gui = ObjectivesPanel.createFrame(ea);
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
