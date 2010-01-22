package jadex.simulation.helper;

import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bdi.runtime.impl.ExternalAccessFlyweight;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;

public class ObserveBDIAgentThread extends Thread{

	private AbstractEnvironmentSpace space;
	private IComponentIdentifier identifier;
	private String componentType;
	long timestamp;
	// public boolean threadSuspended = true;
	private SynchObject monitor;
//	private Object localMonitor = new Object();
	private ObserverCallable callable;

	public ObserveBDIAgentThread(AbstractEnvironmentSpace space, IComponentIdentifier identifier, String componentType, long timestamp, SynchObject monitor, ObserverCallable callable) {
		this.monitor = monitor;
		this.space = space;
		this.identifier = identifier;
		this.componentType = componentType;
		this.timestamp = timestamp;
//		this.monitor = monitor;
		this.callable = callable;
	}

	public void run() {
		System.out.println("#ObserveBDIAgentThread#Started " + timestamp);
//		while (true) {
		
		getExternalAccess();
		
//		synchronized (localMonitor) {
//		synchronized (monitor) {
////			try {
////				localMonitor.wait();
//				monitor.notify();
////			} catch (InterruptedException  e) {
//
////			}
//		}
		
//			synchronized (monitor) {
//				for (int i = 0; i < 50; i++) {
//					System.out.println("iii: " + i);
//				}
//				monitor.notify();
//			}
			
//			try {
//	            Thread.sleep(3000);
//	         } catch (InterruptedException e) {
//	            //nichts
//	         }

//		}
			System.out.println("#ObserveBDIAgentThread#End" + timestamp);
	}

	
	private void getExternalAccess() {
		
		IServiceContainer plat = space.getContext().getServiceContainer();
		
		try {
			// SyncResultListener lis = new SyncResultListener();
			// exta.invokeLater(new Runnable() {
			((IComponentExecutionService) plat.getService(IComponentExecutionService.class)).getExternalAccess(identifier, new IResultListener() {

				@Override
				public void resultAvailable(Object source, Object result) {
					// TODO Auto-generated method stub
//					System.out.println("#ObserveBDIAgentThread# Got exta--->");
					ExternalAccessFlyweight exta = (ExternalAccessFlyweight) result;
					System.out.println("#ObserveBDIAgentThread# Got exta---> " + exta.getAgentName() + timestamp);
//					monitor.addResult2List(exta.getAgentName());
//					monitor.reduceCounter();
//					if(monitor.getCounter() == 0){
//						monitor.notify();
//					}
//					callable.addResult(exta.getAgentName());
//					synchronized (localMonitor) {
////						for (int i = 0; i < 50; i++) {
////							System.out.println("iii: " + i);
////						}
//						localMonitor.notify();
//					}
//					threadSuspended = false;
					// return "ergebnis_da";
				}

				@Override
				public void exceptionOccurred(Object source, Exception exception) {
					// TODO Auto-generated method stub

				}
			});
			// Object ret = lis.waitForResult();
			// getParameter("result").setValue(ret);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERRROR");
			// fail(e); // Do not show exception on console.
		}

		// return "durchlaufen";

	}
}