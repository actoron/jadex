package jadex.platform.service.message.websockettransport;

import com.neovisionaries.ws.client.WebSocketFactory;

import jadex.bridge.service.component.IInternalRequiredServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Autostart;
import jadex.platform.service.transport.AbstractTransportAgent;
import jadex.platform.service.transport.ITransport;

/**
 *  Agent implementing the web socket transport.
 *
 */
@Agent(autostart=@Autostart(value=Boolean3.TRUE, name="ws", predecessors="jadex.platform.service.address.TransportAddressAgent"))
public class WebSocketTransportAgent extends AbstractTransportAgent<IWebSocketConnection>
{
	/** Maximum size of websocket frame payloads. */
	@AgentArgument
	protected int maxpayload = 4096;
	
	/** Idle connection timeout. */
	@AgentArgument
	protected int idletimeout = 5000;	// TODO: support higher values
	
	/** Timeout on trying to connect. */
	@AgentArgument
	protected long connecttimeout = 8000;
	
	/** Daemon thread pool service. */
	protected IDaemonThreadPoolService threadpoolsrv;
	
	protected WebSocketFactory websocketfactory;
	
	/**
	 *  Creates the agent.
	 */
	public WebSocketTransportAgent()
	{
	}
	
	/**
	 *  Initializes the agent.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		websocketfactory = new WebSocketFactory(); //.setConnectionTimeout(5000);
		websocketfactory.setConnectionTimeout((int) connecttimeout);
		threadpoolsrv = ((IInternalRequiredServicesFeature)agent.getFeature(IRequiredServicesFeature.class)).getRawService(IDaemonThreadPoolService.class);
//		threadpoolsrv = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IDaemonThreadPoolService.class));
		return super.init();
	}
	
	@AgentKilled
	public void stop()
	{
		super.shutdown();
		
		synchronized(this)
		{
			if (candidates != null)
			{
				for (IWebSocketConnection con : candidates.keySet())
				{
					con.forceClose();
				}
			}
		}
	}
	
 	/**
 	 *  Get the transport implementation
 	 */
 	public ITransport<IWebSocketConnection> createTransportImpl()
 	{
 		return new WebSocketTransport();
 	}
 	
 	/**
 	 *  Gets the maximum size of websocket frame payloads.
 	 * 
 	 *  @return Maximum size of websocket frame payloads. 
 	 */
 	public int getMaximumPayloadSize()
 	{
 		return maxpayload;
 	}
 	
 	/**
 	 *  Gets the maximum message size of websocket messages.
 	 * 
 	 *  @return Maximum message size of websocket messages. 
 	 */
 	public int getMaximumMessageSize()
 	{
 		return maxmsgsize;
 	}
 	
 	/**
 	 *  Gets the idle timeout.
 	 * 
 	 *  @return The idle timeout. 
 	 */
 	public int getIdleTimeout()
 	{
 		return idletimeout;
 	}
 	
 	/**
 	 *  Gets the connect timeout.
 	 * 
 	 *  @return The connect timeout. 
 	 */
 	public long getConnectTimeout()
	{
		return connecttimeout;
	}
 	
 	/**
 	 *  Returns the thread pool service.
 	 * 
 	 *  @return The thread pool service.
 	 */
 	public IDaemonThreadPoolService getThreadPoolService()
 	{
 		return threadpoolsrv;
 	}
 	
 	public WebSocketFactory getWebSocketFactory()
	{
		return websocketfactory;
	}
}
