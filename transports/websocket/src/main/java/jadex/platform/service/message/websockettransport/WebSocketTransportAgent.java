package jadex.platform.service.message.websockettransport;

import com.neovisionaries.ws.client.WebSocketFactory;

import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.platform.service.transport.AbstractTransportAgent;
import jadex.platform.service.transport.ITransport;

/**
 *  Agent implementing the web socket transport.
 *
 */
@Agent(name="ws", autostart=Boolean3.TRUE)
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
	
	/** WebSocket factory. */
	protected WebSocketFactory websocketfactory;
	
	/**
	 *  Creates the agent.
	 */
	public WebSocketTransportAgent()
	{
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
 	
 	/**
 	 *  Sets the thread pool service.
 	 * 
 	 *  @param tps The thread pool service.
 	 */
 	public void setThreadPoolService(IDaemonThreadPoolService tps)
 	{
 		threadpoolsrv = tps;
 	}
 	
 	/**
 	 *  Gets the WebSocket factory.
 	 *  @return The WebSocket factory.
 	 */
 	public WebSocketFactory getWebSocketFactory()
	{
		return websocketfactory;
	}
 	
 	/**
 	 *  Sets the WebSocket factory.
 	 *  @param websocketfactory The WebSocket factory.
 	 */
 	public void setWebsocketFactory(WebSocketFactory websocketfactory)
	{
		this.websocketfactory = websocketfactory;
	}
}
