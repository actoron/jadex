package jadex.examples.presentationtimer.remotecontrol;

import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.SResultListener;


public class ClientMain  {
	
	public static boolean	startedWithMain;
	
	public static void main(String[] args) {
		Future.DEBUG = true;
		ClientMain countDownClient = new ClientMain();
		countDownClient.setUp();
		startedWithMain = true;
	}
	
	public void setUp()
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getMinimalComm();

		config.setPlatformName("presentationtimer-*");
		config.setNetworkNames(new String[] { "jadexnetwork" });
		config.setNetworkSecrets(new String[] { "laxlax" });
//		config.setDht(true);

		IFuture<IExternalAccess> fut = Starter.createPlatform(config);

		fut.addResultListener(access -> {

			access.scheduleStep(ia -> {
				ITuple2Future<IComponentIdentifier, Map<String, Object>> fut2 = access.createComponent(null, new CreationInfo().setName("CDClient").setFilename(ClientAgent.class.getName() + ".class"));
				fut2.addTuple2ResultListener(cid -> System.out.println("Client Agent created"), SResultListener.ignoreResults());
				return Future.getEmptyFuture();
			});
		});

	}

}
