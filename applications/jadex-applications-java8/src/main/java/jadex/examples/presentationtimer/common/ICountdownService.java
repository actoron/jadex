package jadex.examples.presentationtimer.common;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;


public interface ICountdownService
{

	@Reference
	// geht nur so und nicht im parameter
	public interface ICountdownListener
	{
		void timeChanged(String timeString);

		void stateChanged(State state);
	}

	public IFuture<Void> addListener(ICountdownListener l);

	public ISubscriptionIntermediateFuture<State> registerForState();

	public ISubscriptionIntermediateFuture<String> registerForTime();

	public IFuture<State> getState();

	public IFuture<String> getTime();

	public IFuture<Void> start();

	public IFuture<Void> stop();

	public IFuture<Void> reset();

}
