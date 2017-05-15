package jadex.platform.service.message.websockettransport;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

/**
 * Test web socket connections.
 */
public class NVClientExample
{
	public static void main(String[] args)	throws IOException
	{
		WebSocketFactory factory = new WebSocketFactory(); //.setConnectionTimeout(5000);
		WebSocket ws = factory.createSocket("ws://echo.websocket.org");
		
		ws.connectAsynchronously();
		
		ws.addListener(new WebSocketAdapter()
		{
			@Override
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
			{
				websocket.sendText("Hello.");
			}
			
			@Override
			public void onTextMessage(WebSocket websocket, String text) throws Exception
			{
				System.out.println("Received Text: "+text);
				
				websocket.disconnect();
			}
		});
	}
}
