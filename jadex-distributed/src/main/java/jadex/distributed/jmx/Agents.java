package jadex.distributed.jmx;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.ISuspendable;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.IResultListener;

public class Agents implements AgentsMBean {
	
	private IComponentManagementService _platform;
	
	public Agents(IComponentManagementService platform) {
		super(); // well, the java compiler automatically inserts this already,  but OK to write it out for clarity
		this._platform = platform;
		/*this._current = null; // unnecessary
		this._countListener = new CountListener();*/
	}
	
	@Override
	public int getAgentCount() {
		//this._platform.getComponentIdentifiers(this._countListener);
		IFuture future = this._platform.getComponentDescriptions();
		ISuspendable caller = new ThreadSuspendable(new Object()); // I don't know what you think, but I think something really weird is going on here and that the IFuture+ISuspendable mechanism is in some way total broke
		IComponentDescription[] desc = (IComponentDescription[]) future.get(caller);
		return desc.length;
	};
	
	/*
	private IComponentIdentifier[] _resultAgentCount;
	private Thread _current;
	private IResultListener _countListener;
	// There is a problem: a call to IComponentManagementService.
	@Override
	public int getAgentCount() {
		// background thread called getComponentDescriptions, dieser thread hat callback, und hier wird .join() an diesem thread aufgerufen um das Ergebniss abzugreifen
		Thread t = new ResultThread(this._platform, Thread.currentThread());
		t.start();
		try {
			t.join();
		} catch (InterruptedException e1) { // never happens
			System.out.println("This never happens"); e1.printStackTrace();
		} // ResultThread finished, so the result is now available
		
		// I want the information NOW, so I transform the non-blocking call back way into a blocking version
		// block until count is availble; then return the value
		try {
			Thread.sleep(Long.MAX_VALUE); // sleep and wait for a interrupt(); Long.MAX_VALUE = 2^63-1 = 9*10^15 Sekunden; das sollte reichen
			// TODO welcher Thread führt eigentlich getAgentCount() aus? hoffentlich legt der obere sleep() nicht das gesamte System lahm
			// getAgentCount() wird wohl vom MBeanServer-Thread oder vom JMXAgent-Thread ausgeführt; platform sollte also nicht blockiert werden
		} catch (InterruptedException e) {
			System.out.println("AGENTS jemand rufte interrupt() an mir auf, Ergebnis ist verfügbar");
		}
		
		return _resultAgentCount.length;
	}*/
	
	/*private class ResultThread extends Thread implements IResultListener {
		
		private IComponentManagementService _platform;
		private Thread _thread;
		
		ResultThread(IComponentManagementService platform, Thread thread) {
			this._platform = platform;
			this._thread = thread;
		}
		
		@Override
		public void run() {
			// TODO wieso ist _platform hier null? etwa weil kein final? oder Referenz per Konstruktor zu übergeben?
			//this._platform.getComponentDescriptions(this); // call back mechanism
			//TODO this._platform.getComponentIdentifiers(this); // call back mechanism
			// irgendwann wird resultAvailable() von einem unbekannten Thread ausgerufen
			// Thread muss nicht bekannst sein; wichtig ist nur, dass dieser den oberen thread interrupted
		}

		@Override
		public void resultAvailable(Object source, Object result) { // called by IComponentManagementService when result is available 
			// the source can only be the IComponentManagementService, so no instanceof check is available; and btw: this hurts duck-typing, which is not supported by Java...btw...instead we have to deal with interface-iditis, a terrible disease
			_resultAgentCount = (IComponentIdentifier[]) result;
			_thread.interrupt(); // interrupt() thread to signal him that the result is available
		}
		
		// For IResultListener: exceptionOccured and resultAvailable
		@Override
		public void exceptionOccurred(Object source, Exception exception) {
			
		}

	}*/
	
	/*
	private class CountListener implements IResultListener {

		@Override
		public void exceptionOccurred(Object source, Exception exception) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void resultAvailable(Object source, Object result) {
			// TODO Auto-generated method stub
			
		}
		
	}
	*/
}
