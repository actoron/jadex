package jadex.platform.service.message;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;

/**
 *  Test that messages can be sent without direct connection but through intermediary platforms.
 */
public class RelayTransportTest
{
	/**
	 *  Test message sending between two web socket clients through a shared server.
	 */
	@Test
	public void	testWithWebsocketServer()	throws IOException
	{
		// Find free port for web socket server
		ServerSocket	s	= new ServerSocket(0);
		int port	= s.getLocalPort();
		s.close();
		
		// Start platform with published status agent gui
		IPlatformConfiguration	config	= STest.getDefaultTestConfig(getClass())
			.setValue("intravmawareness", false)
			.setValue("intravm", false)
			.setSuperpeer(true)
			.setValue("ws", true)
			.setValue("ws.port", -1)	// no server, just client
			.setValue("catalogawareness", true);
		
		// Start server platform at given port port
		IExternalAccess	platform	= Starter.createPlatform(config.setValue("ws.port", port)).get();
		platform.killComponent().get();
	}
}
