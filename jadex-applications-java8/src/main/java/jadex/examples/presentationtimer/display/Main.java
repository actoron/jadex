package jadex.examples.presentationtimer.display;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Main {
	
	public static boolean	startedWithMain;
	
	public static void main(String[] args) {
		
		startedWithMain = true;
		
		Main main = new Main();
		main.init();
		
		new Thread() {
			Robot robot = null;
			public void run() {
				while (true) {
					try {
						if (robot == null) {
							robot = new Robot();
						}
						Thread.sleep(60*1000);
						robot.keyPress(KeyEvent.VK_SHIFT);
						robot.keyRelease(KeyEvent.VK_SHIFT);
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (AWTException e) {
						e.printStackTrace();
					}
					
				}
			};
		}.start();
		
	}
	
	
	public void init() {
		
//		public static final String KERNEL_COMPONENT = "component";
//		public static final String KERNEL_MICRO = "micro";
//		public static final String KERNEL_BPMN = "bpmn";
//		public static final String KERNEL_BDIV3 = "v3";
//		public static final String KERNEL_BDI = "bdi";
//		public static final String KERNEL_BDIBPMN = "bdibpmn";
		
		HashMap<String, String> jadexArgs = new HashMap<String, String>();
		jadexArgs.put("platformname", "presentationtimer-*");
		jadexArgs.put("gui", "false");
		jadexArgs.put("welcome", "false");
		jadexArgs.put("cli", "false");
		jadexArgs.put("extensions", "null");
		jadexArgs.put("cli", "false");
		jadexArgs.put("awareness", "true");
		jadexArgs.put("conf", "jadex.platform.PlatformAgent");
		jadexArgs.put("chat", "false");
		jadexArgs.put("relaysecurity", "true");
//		jadexArgs.put("binarymessages", "true");
//		jadexArgs.put("kernels", "\"micro, bdiv3, component\"");
//		jadexArgs.put("relayaddress", "\"http://relay1.activecomponents.org/\"");
//		jadexArgs.put("relayaddress", "\"http://www0.activecomponents.org/relay\"");
//		jadexArgs.put("relayaddress", "\"http://localhost:8080/\"");
		jadexArgs.put("awamechanisms", "\"relay\"");
		jadexArgs.put("logging", "false");
		
//		jadexArgs.put("ssltcptransport", "true");
		jadexArgs.put("relaysecurity", "false");
		jadexArgs.put("networkname", "jadexnetwork");
		jadexArgs.put("networkpass", "laxlax");


		IFuture<IExternalAccess> fut = Starter.createPlatform(jadexArgs);

		fut.addResultListener(access -> {
			IComponentManagementService cms = getCMS(access).get();
			ITuple2Future<IComponentIdentifier, Map<String, Object>> fut2 = cms.createComponent("CDDisplay", CountdownAgent.class.getName()+".class", null);
			fut2.addTuple2ResultListener((IComponentIdentifier created) -> {
				System.out.println("CDDisplay Component created.");
			}, (Map<String,Object> terminated) -> {
				System.out.println("CDDisplay Component terminated!");
			});
		});

	}

	private IFuture<IComponentManagementService> getCMS(IExternalAccess access) {
		return access.scheduleStep(ia -> SServiceProvider.getService(access,
						  IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		
//		return access.scheduleStep(ia -> SServiceProvider.getService(ia,
//			  IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
	}
}
