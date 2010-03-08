package deco4mas.examples.V2.tspaces;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;


/**
 * This Plan is used to update the environment. Right now it is used as an
 * observer to show observed values of agents / the application.
 * 
 * 
 */
@SuppressWarnings("serial")
public class SenderActivityPlan extends Plan {

	private int counter;

	public void body() {

		waitFor(1000);		
		
//		OAVBDIFetcher fetcher = new OAVBDIFetcher(this.getState(), this.getRCapability()); 
//		Object tmpRes = SJavaParser.evaluateExpression("$beliefbase.testBelief > -5", fetcher);
		
//		OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rcapa);
//		OAVBDIFetcher fetcher = new OAVBDIFetcher(this.getState(), this.getExternalAccess());
//		SJavaParser.evaluateExpression("$beliefbase.testBelief > 4", fetcher);
//		ExpressionFlyweight express = ExpressionFlyweight.getExpressionFlyweight(this.getState(), this.getScope(), new String("$beliefbase.testBelief > 5"));
//		Object val = express.getValue();
//		
//		if ( val instanceof Boolean) {
//			Boolean bval = (Boolean) val;
//
//			System.out.println(this.getAgentName() + " : Evaluated Expression: " + bval);
//		}
//		
		
		
		
		counter = 0;
		while (counter < 5) {
			System.out.println("***** Executing CounterPlan: " + counter);
//			getBeliefbase().getBelief("testBelief").setFact(counter);
			getBeliefbase().getBeliefSet("testBelief").addFact(counter);
			
			
			IInternalEvent ie = this.getEventbase().createInternalEvent("testEvent");
			this.getEventbase().dispatchInternalEvent(ie);
			
			
			waitFor(2500);
			counter++;
			
//			getBeliefbase().getBelief("forListenerTest").setFact(counter);
//			
//			this.getExternalAccess().getBeliefbase().getBelief("forListenerTest").addBeliefListener(new IBeliefListener() {
//				public void beliefChanged(AgentEvent ae) {
//					System.out.println("## value: " + getBeliefbase().getBelief("forListenerTest").getFact());					
//				}
//			});
			
			
			
//			if(counter == 0){
//				IGoal goal = createGoal("testGoal");
//				dispatchTopLevelGoal(goal);
//			}
			
			//create new internal events
//			IInternalEvent ie = this.getEventbase().createInternalEvent("servicePlan");
//			ie.getParameter("increment").setValue(counter);
//			this.getEventbase().dispatchInternalEvent(ie);
			
		}
//		CoordinationSpace mySpace = (CoordinationSpace) getBeliefbase().getBelief("env").getFact();
////		String tt =  mySpace.getProperty("src//main//java//jadex//bdi//examples//antworld//decoMAS//belief_set.dynamics.xml").toString();
//		System.out.println("--->" + mySpace.getProperty("dynamics_configuration").toString());
//		IApplicationContext applicationContext = this.getExternalAccess().getApplicationContext();
//		IAMS ams = ((IAMS) applicationContext.getPlatform().getService(IAMS.class));
//		ams.getExternalAccess(getAgentIdentifier(), new IResultListener() {
//			public void exceptionOccurred(Exception exception) {
//			System.out.println("exception!");
//				exception.printStackTrace();
//			}
//
//			public void resultAvailable(Object result) {
//				IExternalAccess exta = (IExternalAccess) result;
//				System.out.println("1888 " + exta.toString());
////				behObserver = new BDIBehaviorObservationComponent(exta);
//				System.out.println("1999");
//			}
//		});
	}
}
