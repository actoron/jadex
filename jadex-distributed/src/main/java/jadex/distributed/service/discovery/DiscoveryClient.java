package jadex.distributed.service.discovery;

import java.net.InetSocketAddress;
import java.util.Set;

public class DiscoveryClient {

	private int _timeout;
	
	public DiscoveryClient() {
		
	}
	
	public void setTimeout(int timeout) {
		if( timeout < 0 ) {
			this._timeout = 0; // makes no sense, because effectively disables the active discovery of slaves
		} else {
			this._timeout = timeout;
		}
	}
	
	public int getTimeout() {
		return this._timeout;
	}
	
	public Set<InetSocketAddress> findSlaves() {
		// all active discovery stuff here
		
		
		return null;
	}
	
}
