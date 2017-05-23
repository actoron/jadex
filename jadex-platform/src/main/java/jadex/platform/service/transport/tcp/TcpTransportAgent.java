package jadex.platform.service.transport.tcp;

import java.util.HashMap;
import java.util.Map;

import jadex.base.PlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.service.types.address.TransportAddressBook;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;

@Agent(autoprovide=true)
public class TcpTransportAgent implements ITransportService
{
	//-------- constants --------
	
	/** The protocol name (key for transport address book). */
	public static final String	TRANSPORT_NAME	= "tcp";
	
	//-------- arguments --------
	
	/** The default priority, when choosing a transport to communicate with a specific platform. */
	@AgentArgument
	// TODO: need second priority for indirect communication (over relay platform)?
	protected int	priority	= 1000;
	
	/** The port, the transport should listen to (-1: don't listen, 0: choose random port, >0: use given port). */
	@AgentArgument
	protected int	port	= 0;
	
	/**
	 *  The keep-alive (group), i.e. an address, to which the transport should stay connected
	 *  or a group of addresses (comma separated), where the transport should stay connected one of the group.
	 *  If the connection fails, the transport will try to reconnect, possibly after a timeout. 
	 */
	@AgentArgument
	// TODO: not yet implemented... required for message relaying
	protected String	keepalivegroup	= null;
	
	//-------- internal attributes --------
	
	/** The open connections (target platform -> connection). */
	protected Map<IComponentIdentifier, TcpTransportConnection>	connections	= new HashMap<IComponentIdentifier, TcpTransportConnection>();
	
	//-------- ITransportService interface --------
	
	/**
	 *  Checks if the transport is ready.
	 * 
	 *  @param header Message header.
	 *  @return Transport priority, when ready
	 */
	public IFuture<Integer> isReady(Map<String, Object> header)
	{
		
		final Future<Integer>	ret	= new Future<Integer>();
		getConnection(header).addResultListener(new ExceptionDelegationResultListener<TcpTransportConnection, Integer>(ret)
		{
			@Override
			public void customResultAvailable(TcpTransportConnection con) throws Exception
			{
				if(con!=null)
				{
					ret.setResult(priority); // con.isRelay() ? prio_relay : prio_default...
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Send a message.
	 *  
	 *  @param header Message header.
	 *  @param body Message body.
	 *  @return Done, when sent, failure otherwise.
	 */
	public IFuture<Void> sendMessage(Map<String, Object> header, byte[] body)
	{
		// HACK: todo...
		return IFuture.DONE;
	}
	
	//-------- helper methods --------
		
	/**
	 *  Get a connection to a given platform.
	 */
	protected IFuture<TcpTransportConnection>	getConnection(Map<String, Object> header)
	{
		Future<TcpTransportConnection>	ret	= new Future<TcpTransportConnection>();
		
		// Check existing connection.
		IComponentIdentifier	rec	= (IComponentIdentifier)header.get(MessageComponentFeature.RECEIVER);
		TcpTransportConnection	con	= connections.get(rec);
		if(con!=null)
		{
			ret.setResult(con);
		}
		else
		{
			// Check applicability
			TransportAddressBook	book	= (TransportAddressBook)PlatformConfiguration.getPlatformValue(rec, PlatformConfiguration.DATA_ADDRESSBOOK);
			String[]	addresses	= book.getPlatformAddresses(rec, TRANSPORT_NAME);
			
			if(addresses!=null)
			{
				
			}
		}

		return ret;
	}
}
