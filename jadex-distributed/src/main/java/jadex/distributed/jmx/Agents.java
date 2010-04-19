package jadex.distributed.jmx;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;
import jadex.standalone.service.ComponentManagementService;

public class Agents implements AgentsMBean {
	
	private IComponentManagementService _platform;
	
	private Thread _current;
	
	private int agentCount;
	
	public Agents(IComponentManagementService platform) {
		super(); // javac automatically inserts this, but ok
		this._platform = platform;
		this._current = null; // unnecessary
	}
	
	@Override
	public int getAgentCount() {
		// background thread called getComponentDescriptions, dieser thread hat callback, und hier wird .join() an diesem thread aufgerufen um das Ergebniss abzugreifen
		IComponentIdentifier[] identifiers = null; // gets set by ResultThread
		Thread t = new ResultThread(identifiers);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e1) { // never happens
			System.out.println("This never happens"); e1.printStackTrace();
		} // ResultThread finished, so the result is now available
		
		// I want the information NOW, so I transform the non-blocking call back way into a blocking version
		// block until count is availble; then return the value
		this._current = Thread.currentThread();
		try {
			this._current.wait(); // current thread gets notified by resultAvailable when the result from the IComponentManagementService is available
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return this.agentCount;
	}
	
	
	private class ResultThread extends Thread implements IResultListener {
		private IComponentIdentifier[] _identifiers;
		
		ResultThread(IComponentIdentifier[] identifiers) {
			this._identifiers = identifiers;
		}
		
		@Override
		public void run() {
			_platform.getComponentDescriptions(this); // call back mechanism
		}

		/*** For IResultListener: exceptionOccured and resultAvailable ***/
		@Override
		public void exceptionOccurred(Object source, Exception exception) {
			
		}

		@Override
		public void resultAvailable(Object source, Object result) { // called by IComponentManagementService when result is available 
			// the source can only be the IComponentManagementService, so no instanceof check is available; and btw: this hurts duck-typing, which is not supported by Java...btw...instead we have to deal with interface-iditis, a terrible disease
			this._identifiers = (IComponentIdentifier[]) result;
		}
	};
}
