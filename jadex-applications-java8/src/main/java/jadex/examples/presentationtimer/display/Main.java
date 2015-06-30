package jadex.examples.presentationtimer.display;

import jadex.base.PlatformConfiguration;
import jadex.base.RootComponentConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
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
		PlatformConfiguration config = PlatformConfiguration.getMinimalRelayAwareness();
		RootComponentConfiguration rootConfig = config.getRootConfig();

		config.setPlatformName("presentationtimer-*");
		
		rootConfig.setNetworkName("jadexnetwork");
		rootConfig.setNetworkPass("laxlax");
		
//		config.setDht(true);
		
		IFuture<IExternalAccess> fut = Starter.createPlatform(config);

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
