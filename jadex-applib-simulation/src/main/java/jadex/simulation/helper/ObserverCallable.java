package jadex.simulation.helper;

import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bdi.examples.cleanerworld.MoveTask;
import jadex.bridge.IComponentIdentifier;
import jadex.simulation.model.ObservedEvent;
import jadex.simulation.model.Observer;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ObserverCallable implements Callable<ArrayList> {

	// private String res;
	// private final String a;

	private AbstractEnvironmentSpace space;
	private IComponentIdentifier identifier;
	private String componentType;
	long timestamp;
	public boolean threadSuspended = true;
	private SynchObject monitor;
	private ArrayList<Observer> observerList;
	private ArrayList<String> resultList = new ArrayList<String>();
	private int counter = 0;
	private IComponentIdentifier[] identifierArray;

	// space, identifier, componentType, timestamp
	ObserverCallable(AbstractEnvironmentSpace space, IComponentIdentifier identifier, String componentType, long timestamp, Object monitor) {
		this.space = space;
		this.identifier = identifier;
		this.componentType = componentType;
		this.timestamp = timestamp;
		this.monitor = new SynchObject();
	}
	
	ObserverCallable(AbstractEnvironmentSpace space, long timestamp, ArrayList<Observer> observerList, IComponentIdentifier[] identifierArray) {
		this.space = space;
		this.identifier = identifier;
		this.componentType = componentType;
		this.timestamp = timestamp;
		this.monitor = new SynchObject();
		this.observerList = observerList;
		this.identifierArray = identifierArray;
	}


	public ArrayList call() {
		
		
//		IComponentIdentifier[] id = space.getAgents();
		
		for(Observer obs : observerList){
		
		
		
		if (obs.getData().getObjectSource().getType().equals(Constants.BDI_AGENT)) {
			String agentType = obs.getData().getObjectSource().getName();
			
//			for(IComponentIdentifier agentIdentifier : space.getAgents()){
			for(IComponentIdentifier agentIdentifier : identifierArray){
				if(space.getContext().getComponentType(agentIdentifier).equals(agentType)){
					//TODO: Apply / Check if filter has been set on this observer data
					System.out.println("#DeltaTime4Exec# Starting ObserverHelper. " + counter);
					counter++;
					
					ObserveBDIAgentThread thread = new ObserveBDIAgentThread(space,  agentIdentifier,  Constants.BDI_AGENT,  timestamp, monitor, this);
//					thread.start();
					thread.run();
					
					
//					observedEvents.add(ObserverHelper.observeComponent(space, agentIdentifier, Constants.BDI_AGENT,  myTime, executor));
//					String tmpRes = ObserverHelper.observeComponent(space, agentIdentifier, Constants.BDI_AGENT,  clockservice.getTime());
//					System.out.println("#DeltaTime4Exec# received result from ObserverHelper.");					
				}
					
			}
			
		}
		
		
		
		}
		
		
		
		
		
		
//		for(int i=0; i < 10; i++){
//			System.out.println(timestamp  + " - i: "  + i);			
//			}
//			ObserveBDIAgentThread thread = new ObserveBDIAgentThread(space,  identifier,  componentType,  timestamp, monitor);
//			thread.start();
//			
			synchronized (monitor) {
				try {
					monitor.wait();
				} catch (InterruptedException  e) {

				}
			}
			System.out.println("Callable over");
			return new ArrayList();
		}

//	public synchronized void addResult(String s){
//		this.resultList.add(s);
//		System.out.println("Added res: " + resultList.size());
//		if(counter == resultList.size())
//		monitor.notify();
//	}
	
//	 private void observeAgent(){
//	 IServiceContainer plat = space.getContext().getServiceContainer();
//	 try
//	 {
//	 // SyncResultListener lis = new SyncResultListener();
//	 // exta.invokeLater(new Runnable() {
//	 ((IComponentExecutionService)plat.getService(IComponentExecutionService.class)).getExternalAccess(identifier, new IResultListener() {
//					
//	 @Override
//	 public void resultAvailable(Object source, Object result) {
//	 // TODO Auto-generated method stub
//	 ExternalAccessFlyweight exta = (ExternalAccessFlyweight) result;
//	 System.out.println("#IResList#---> " + exta.getAgentName());
//	 threadSuspended = false;
//	 // return "ergebnis_da";
//	 }
//					
//	 @Override
//	 public void exceptionOccurred(Object source, Exception exception) {
//	 // TODO Auto-generated method stub
//						
//	 }
//	 });
//	 // Object ret = lis.waitForResult();
//	 // getParameter("result").setValue(ret);
//	 }
//	 catch(Exception e)
//	 {
//	 e.printStackTrace();
//	 // fail(e); // Do not show exception on console.
//	 }
//			
////	 return "durchlaufen";
//	 
//	 }
}