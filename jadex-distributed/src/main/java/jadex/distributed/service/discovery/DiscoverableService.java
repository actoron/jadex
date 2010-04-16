package jadex.distributed.service.discovery;

import java.io.IOException;
import java.net.UnknownHostException;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;

public class DiscoverableService implements IService {

	private DiscoveryResponder _drespoonder;
	
	public DiscoverableService() {
		try {
			this._drespoonder = new DiscoveryResponder();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/*** For IService: startService, shutdownService(IResultListener) ***/
	@Override
	public void shutdownService(IResultListener listener) {
		try {
			this._drespoonder.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startService() {
		try {
			this._drespoonder.start();
			System.out.println("DISCOVERABLESERVICE gestartet, DiscoveryResponder aktiv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
