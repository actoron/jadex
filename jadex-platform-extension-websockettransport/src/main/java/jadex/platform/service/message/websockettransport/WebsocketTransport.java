package jadex.platform.service.message.websockettransport;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;


/**
 * Test web socket connections.
 */
public class WebsocketTransport
{
	public static void main(String[] args)
	{
		try
		{
			final CountDownLatch	latch	= new CountDownLatch(1);
			ClientEndpointConfig	config	= ClientEndpointConfig.Builder.create().build();
			ClientManager client = ClientManager.createClient();
			URI	serveruri	=  new URI("ws://echo.websocket.org");
			
//			client.setDefaultMaxSessionIdleTimeout(...);
			
			Future<Session>	sesfut	= client.asyncConnectToServer(new Endpoint()
			{
				@Override
				public void onOpen(Session session, EndpointConfig config)
				{
					session.addMessageHandler(new MessageHandler.Whole<String>()
					{
						@Override
						public void onMessage(String message)
						{
							System.out.println("Received message: " + message);
							latch.countDown();
						}
					});
					try
					{
						session.getBasicRemote().sendText("xxx");
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}, config, serveruri);
			
			sesfut.get();
			latch.await();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
