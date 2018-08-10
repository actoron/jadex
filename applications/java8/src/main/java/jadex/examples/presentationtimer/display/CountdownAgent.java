package jadex.examples.presentationtimer.display;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.examples.presentationtimer.common.ICountdownController;
import jadex.examples.presentationtimer.common.ICountdownGUIService;
import jadex.examples.presentationtimer.common.ICountdownService;
import jadex.examples.presentationtimer.common.State;
import jadex.examples.presentationtimer.display.ui.ConfigureFrame;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@ProvidedServices({
	@ProvidedService(type = ICountdownService.class),
	@ProvidedService(type = ICountdownGUIService.class)
}
)
@RequiredServices({
	@RequiredService(type= IComponentManagementService.class, name = "cms")
})
public class CountdownAgent implements ICountdownService, ICountdownGUIService {

	private Set<ICountdownListener> listeners;
	private ICountdownController controller;
	private Set<SubscriptionIntermediateFuture<State>> stateFutures;
	private Set<SubscriptionIntermediateFuture<String>> timeFutures;

	@AgentServiceSearch
	private IComponentManagementService cms;
	
	@Agent
	private IInternalAccess agent;
	private String lastTime = "00:00";
	private State lastState = new State();
	
	public CountdownAgent() {
		listeners = new HashSet<ICountdownService.ICountdownListener>();
		stateFutures = new HashSet<SubscriptionIntermediateFuture<State>>();
		timeFutures = new HashSet<SubscriptionIntermediateFuture<String>>();
	}

	@Override
	public IFuture<Void> addListener(@Reference ICountdownListener l) {
		System.out.println("Received addListener()");
		this.listeners.add(l);
		return Future.DONE;
	}
	
	@Override
	public ISubscriptionIntermediateFuture<State> registerForState() {
		System.out.println("Register for state");
//		IntermediateFuture<State> intermediateFuture = new IntermediateFuture<State>();
		SubscriptionIntermediateFuture<State> intermediateFuture = (SubscriptionIntermediateFuture<State>) SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		this.stateFutures.add(intermediateFuture);
		return intermediateFuture;
	}
	
	@Override
	public ISubscriptionIntermediateFuture<String> registerForTime() {
		System.out.println("Register for time");
		SubscriptionIntermediateFuture<String> intermediateFuture = (SubscriptionIntermediateFuture<String>) SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		this.timeFutures.add(intermediateFuture);
		return intermediateFuture;
	}
	
	
	@Override
	public IFuture<State> getState() {
		return new Future<State>(lastState);
	}

	@Override
	public IFuture<String> getTime() {
		return new Future<String>(lastTime);
	}

	@Override
	public IFuture<Void> start() {
		System.out.println("Received start()");
		controller.start();
		
//		IComponentManagementService cms = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IComponentManagementService.class)).get();
		System.out.println(cms);
		agent.createComponent(null, new CreationInfo().setFilename("jadex.examples.presentationtimer.display.CountdownAgent.class")).addTuple2ResultListener(
			t -> System.out.println(t),
			ex -> System.out.println(ex));
		return Future.DONE;
	}

	@Override
	public IFuture<Void> stop() {
		System.out.println("Received stop()");
		controller.stop();
		return Future.DONE;
	}

	@Override
	public IFuture<Void> reset() {
		System.out.println("Received reset()");
		return Future.DONE;
	}

	
	@Override
	public synchronized void informTimeUpdated(String timeString) {
		this.lastTime = timeString;
		for (ICountdownListener l : listeners) {
			l.timeChanged(timeString);
		}
		
		List<SubscriptionIntermediateFuture> terminated = new ArrayList<SubscriptionIntermediateFuture>();
		for (SubscriptionIntermediateFuture<String> fut : timeFutures) {
			if (!fut.isDone()) {
				fut.addIntermediateResult(timeString);
			} else {
				System.out.println("Found terminated future, removing");
				terminated.add(fut);
			}
		}
		
		for(SubscriptionIntermediateFuture terminableIntermediateFuture : terminated)
		{
			timeFutures.remove(terminableIntermediateFuture);
		}
	}

	@Override
	public synchronized void informStateUpdated(State state) {
		this.lastState = state;
		for (ICountdownListener l : listeners) {
			l.stateChanged(state);
		}
		
		List<TerminableIntermediateFuture> terminated = new ArrayList<TerminableIntermediateFuture>();
		for (TerminableIntermediateFuture<State> fut : stateFutures) {
			if (!fut.isDone()) {
				fut.addIntermediateResult(state);
			} else {
				terminated.add(fut);
			}
		}
		for(TerminableIntermediateFuture terminableIntermediateFuture : terminated)
		{
			stateFutures.remove(terminableIntermediateFuture);
		}
	}

	@Override
	public void setController(ICountdownController controller) {
		this.controller = controller;
	}
	
	@AgentCreated
	public void agentCreated() {
		System.out.println("Countdown agent created!");
		ConfigureFrame configureFrame = new ConfigureFrame(this);
		configureFrame.setLocationRelativeTo(null);
		configureFrame.setVisible(true);
	}

}
