package jadex.distributed.service;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;

//Erstmal nur einen dummy discovery service erzeugen, der einfach nur eine feste Liste von IP:Port Daten übergibt. Wenn du dann noch Zeit hast, kannst du dich um eine richtige ZeroConf konfiguration kümmern.
// Jede Plattform braucht einen discovery service, egal ob Server oder Client
class DiscoveryService implements IService {


	@Override
	public void shutdownService(IResultListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startService() {
		// TODO Auto-generated method stub
		
	}
	
}
