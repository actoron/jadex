package jadex.adapter.standalone.transport.localmtp;

import jadex.adapter.base.IMessageService;
import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.adapter.standalone.transport.ITransport;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatform;
import jadex.commons.collection.SCollection;

import java.util.List;
import java.util.Map;

/**
 *  The local transport for sending messages on the
 *  same platform (just calls the local deliver method).
 */
public class LocalTransport implements ITransport
{
	//-------- constants --------
	
	/** The schema name. */
	public final static String SCHEMA = "local-mtp://";
	
	//-------- attributes --------
	
	/** The message service. */
//	protected IMessageService msgservice;
	
	/** The addresses. */
	protected String[] addresses;
	
	/** The platform name. */
//	protected String platformname;
	
	/** The platform. */
	protected IPlatform platform;
	
	//-------- constructors --------
	
	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public LocalTransport(IPlatform platform)
	{
//		this.msgservice = (IMessageService)platform.getService(IMessageService.class, SFipa.MESSAGE_SERVICE);
		this.platform = platform;
		this.addresses = new String[0];
//		this.platformname = platform.getName();
	}

	/**
	 *  Start the transport.
	 */
	public void start()
	{
		// nothing to do.
	}
	
	/**
	 *  Perform cleanup operations (if any).
	 */
	public void shutdown()
	{
		// nothing to do.
	}
	
	//-------- methods --------
	
	/**
	 *  Send a message.
	 *  @param message The message to send.
	 *  @return The agent identifiers of the agents 
	 *  the message could not be sent to.
	 */
	public AgentIdentifier[] sendMessage(Map message, String msgtype, IAgentIdentifier[] recs)
	{
		List todeliver = SCollection.createArrayList();
		List undelivered = SCollection.createArrayList();
		
//		IAgentIdentifier[] recs = message.getReceivers();
//		String hap = Configuration.getConfiguration().getProperty(Configuration.PLATFORMNAME);
		
		for(int i=0; i<recs.length; i++)
		{
			if(recs[i].getPlatformName().equals(platform.getName()))
				todeliver.add(recs[i]);
			else
				undelivered.add(recs[i]);
		}
		if(todeliver.size()>0)
		{
			((IMessageService)platform.getService(IMessageService.class)).deliverMessage(message, msgtype, (IAgentIdentifier[])todeliver
				.toArray(new IAgentIdentifier[todeliver.size()]));
		}
		
		return (AgentIdentifier[])undelivered.toArray(new AgentIdentifier[undelivered.size()]);
	}
	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String getServiceSchema()
	{
		return "local:";
	}
	
	/**
	 *  Get the adresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses()
	{
		return addresses;
	}

}
