package sodekovs.old.bikesharing.zeit;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

public class ZeitAgent extends MicroAgent
{
	private volatile int _zeit = 0;
	private IComponentStep r;
	
	@Override
	public IFuture<Void> executeBody()
	{
		System.out.println("Starte Zeitagent");
		r = new IComponentStep() {

			@Override
			public IFuture<Void> execute(IInternalAccess arg0)
			{
				_zeit++;
				Zeitverwaltung.gibInstanz().setzZeit( _zeit );
				waitForTick(this);
				return IFuture.DONE;
			}
		};
		waitForTick(r);
		return IFuture.DONE;
		
	}
}
