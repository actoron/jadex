package jadex.simulation.helper;

import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bdi.runtime.impl.ExternalAccessFlyweight;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IServiceContainer;
import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.Observer;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ObserverHelper {
	
	
//	public static ObservedEvent observeComponent(AbstractEnvironmentSpace space, IComponentIdentifier identifier, String componentType, long timestamp, ExecutorService executor){
	public static ArrayList observeComponent(AbstractEnvironmentSpace space, long timestamp, ExecutorService executor, ArrayList<Observer> observerList, IComponentIdentifier[] identifierArray){

		// Needed to get synchronous call.
//		Callable<ObservedEvent> c = new ObserverCallable( space,  identifier,  componentType,  timestamp, new Object()); 				
		Callable<ArrayList> c = new ObserverCallable(space,  timestamp, observerList, identifierArray);
		Future<ArrayList> result = executor.submit( c );
		ArrayList observedEvent = null;
		try {
			System.out.println("#ObserverHelper# Just submitted ObserverCallable to start. " + timestamp);
			 observedEvent  = result.get();
			System.out.println("#ObserverHelper# Just received result from ObserverCallable. " + timestamp );
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return observedEvent;
	}
		
		
		
//		IServiceContainer plat = space.getContext().getServiceContainer();		
//		try
//		{
////			SyncResultListener lis = new SyncResultListener();
////			exta.invokeLater(new Runnable() {
//			((IComponentExecutionService)plat.getService(IComponentExecutionService.class)).getExternalAccess(identifier, new IResultListener() {
//				
//				@Override
//				public void resultAvailable(Object source, Object result) {
//					// TODO Auto-generated method stub
//					ExternalAccessFlyweight exta = (ExternalAccessFlyweight) result;
//					System.out.println("#IResList#---> "  + exta.getAgentName());
////					return "ergebnis_da";
//				}
//				
//				@Override
//				public void exceptionOccurred(Object source, Exception exception) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
////			Object ret = lis.waitForResult();
////			getParameter("result").setValue(ret);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
////			fail(e); // Do not show exception on console. 
//		}
//		
//		return "durchlaufen";
//	}

	
	
	
//	private static void callableTest(){
//		Callable<String> c = new TestCallable( new String("test") ); 
//		ExecutorService executor = Executors.newCachedThreadPool();
//		
//		Future<String> result = executor.submit( c );
//		try {
//			System.out.println("calling res:");
//			String myRes  = result.get();
//			System.out.println("got res:" + myRes);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
	
	
	
	
	
//	IAMS ams = (IAMS) ((IApplicationContext) space.getContext()).getPlatform().getService(IAMS.class);
//	ams.getExternalAccess(agent, new IResultListener() {
//		public void exceptionOccurred(Exception exception) {
//			// exception.printStackTrace();
//		}
//
//		public void resultAvailable(Object result) {
//			final IExternalAccess exta = (IExternalAccess) result;
}
