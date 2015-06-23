package jadex.examples.presentationtimer.remotecontrol;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.SResultListener;

import java.util.HashMap;
import java.util.Map;


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
		// public static final String KERNEL_COMPONENT = "component";
		// public static final String KERNEL_MICRO = "micro";
		// public static final String KERNEL_BPMN = "bpmn";
		// public static final String KERNEL_BDIV3 = "v3";
		// public static final String KERNEL_BDI = "bdi";
		// public static final String KERNEL_BDIBPMN = "bdibpmn";

		HashMap<String, String> jadexArgs = new HashMap<String, String>();
		jadexArgs.put("platformname", "presentationclient-*");
		jadexArgs.put("gui", "false");
		jadexArgs.put("welcome", "false");
		jadexArgs.put("cli", "false");
		jadexArgs.put("extensions", "null");
		jadexArgs.put("cli", "false");
		jadexArgs.put("awareness", "true");
		jadexArgs.put("conf", "jadex.platform.PlatformAgent");
		jadexArgs.put("chat", "false");
		jadexArgs.put("relaysecurity", "true");
		// jadexArgs.put("binarymessages", "true");
		// jadexArgs.put("kernels", "\"micro, bdiv3, component\"");
		// jadexArgs.put("relayaddress",
		// "\"http://relay1.activecomponents.org/\"");
		// jadexArgs.put("relayaddress",
		// "\"http://www0.activecomponents.org/relay\"");
		// jadexArgs.put("relayaddress", "\"http://localhost:8080/\"");
		jadexArgs.put("awamechanisms", "\"relay\"");
		jadexArgs.put("logging", "false");
		jadexArgs.put("networkname", "jadexnetwork");
		jadexArgs.put("networkpass", "laxlax");

		// jadexArgs.put("ssltcptransport", "true");
		jadexArgs.put("relaysecurity", "false");

		IFuture<IExternalAccess> fut = Starter.createPlatform(jadexArgs);

		fut.addResultListener(access -> {

			access.scheduleStep(ia -> {
				System.out.println("Got external platform access");
				IComponentManagementService cms = getCMS(access).get();
				System.out.println("Got cms");
				ITuple2Future<IComponentIdentifier, Map<String, Object>> fut2 = cms.createComponent("CDClient", ClientAgent.class.getName() + ".class", null);
				fut2.addTuple2ResultListener(cid -> System.out.println("Client Agent created"), SResultListener.ignoreResults());
				return Future.getEmptyFuture();
			});
		});

	}

	private IFuture<IComponentManagementService> getCMS(IExternalAccess access)
	{
		return access.scheduleStep(ia -> SServiceProvider.getService(access, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
	}

}
