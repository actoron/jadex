package jadex.platform.service.daemon;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.daemon.IDaemonService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.MessageType;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 * Daemon agent provides functionalities for managing platforms.
 */
@Description("This agent offers the daemon service.")
@GuiClassName("jadex.tools.daemon.DaemonViewerPanel")
@RequiredServices(@RequiredService(name = "libservice", type = ILibraryService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
@ProvidedServices(@ProvidedService(type = IDaemonService.class, implementation = @Implementation(DaemonService.class)))
@Agent
public class DaemonAgent
{
	//-------- attributes --------
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Handle messages from responder agents.
	 */
	@AgentMessageArrived
	public void messageArrived(Map<String, Object> msg, MessageType mt)
	{
		if(SFipa.FIPA_MESSAGE_TYPE.equals(mt))
		{
			DaemonService	ds	= (DaemonService)agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedServiceRawImpl(IDaemonService.class);
			ds.messageReceived((IComponentIdentifier)msg.get(SFipa.SENDER), (String)msg.get(SFipa.CONTENT));
		}
	}
}
