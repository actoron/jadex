package jadex.standalone.service;

import jadex.base.fipa.SFipa;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.bridge.MessageType;
import jadex.bridge.RemoteMethodInvocationInfo;
import jadex.bridge.RemoteMethodResultInfo;
import jadex.micro.MicroAgent;

import java.util.Map;

/**
 *  Remote service management service that hosts the corresponding
 *  service. It basically has the task to forward messages from
 *  remote service management components on other platforms to its service.
 */
public class RemoteServiceManagementAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The remote management service. */
	protected IRemoteServiceManagementService rms;
	
	//-------- constructors --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		rms = new RemoteServiceManagementService(getExternalAccess());
		addService(IRemoteServiceManagementService.class, rms);
		startServiceProvider();
	}
	
	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(Map msg, MessageType mt)
	{
		if(SFipa.MESSAGE_TYPE_NAME_FIPA.equals(mt.getName()))
		{
			Object content = msg.get(SFipa.CONTENT);
			
			if(content instanceof RemoteMethodInvocationInfo)
			{
				IComponentIdentifier rrms = (IComponentIdentifier)msg.get(SFipa.SENDER);
				String convid = (String)msg.get(SFipa.CONVERSATION_ID);
				RemoteMethodInvocationInfo rmii = (RemoteMethodInvocationInfo)msg.get(SFipa.CONTENT);
				rms.remoteInvocationReceived(rrms, rmii, convid);
				
			}
			else if(content instanceof RemoteMethodResultInfo)
			{
				String convid = (String)msg.get(SFipa.CONVERSATION_ID);
				RemoteMethodResultInfo rmri = (RemoteMethodResultInfo)msg.get(SFipa.CONTENT);
				rms.remoteResultReceived(rmri, convid);
			}
			else
			{
				System.out.println("Unexpected message: "+msg);
			}
		}
	}
}
