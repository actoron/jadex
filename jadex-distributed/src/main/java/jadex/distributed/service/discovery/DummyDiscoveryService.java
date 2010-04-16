package jadex.distributed.service.discovery;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * Erstmal nur einen dummy discovery service erzeugen, der einfach nur eine feste Liste von IP:Port Daten übergibt. Wenn du dann noch Zeit hast, kannst du dich um eine richtige ZeroConf konfiguration
 * kümmern. Jede Plattform braucht einen discovery service, egal ob Server oder Client
 * 
 * @author daniel
 * 
 */
public class DummyDiscoveryService implements IService, IDiscoveryService {

	private Set<InetSocketAddress> machines; // List of known machines
	private Set<IDiscoveryServiceListener> listeners; // List of listeners to inform when the set of available machines changed
	
	private IServiceContainer container; // wft do I need the container for!?! the logger is not used right now

	public DummyDiscoveryService(IServiceContainer container) { // wtf !?! I don't need the container (yet); how knows, maybe someday ...
		this.container = container;
		this.machines = new HashSet<InetSocketAddress>();
	}

	public DummyDiscoveryService() {
		this.machines = new HashSet<InetSocketAddress>();
		this.listeners = new HashSet<IDiscoveryServiceListener>();
	}

	@Override
	public void startService() {
		// TODO mit discover andere Platformen finden
		// dazu einen seperaten thread starten, der die Liste der machines laufend up to date hält
		// also nicht nur hinzufügen, sondern es müssen plattformen auch entfernet werden, wenn Plattformen nicht mehr verfügbar sind
		// wie? durch einen leasetime ansatz oder durch einen heartbeat Mechanismus
		// => beide nicht adäquat, denn eine Plattform darf nicht einfach verschwinden; es muss eine 'graceful degradation' geben
		// also, dass die Plattform sagt 'so jetzt gehe ich', damit die Anwendung auf andere Platformen verteilt werden kann

		// Datei mit IP und Port Daten öffnen
		File file = new File("src/main/java/jadex/distributed/service/discovery/discovery_config.txt"); // TODO pfad für production environment anpassen

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
			String line; // line is e.g. '234.12.345.12:1254'
			while ((line = reader.readLine()) != null) {
				// ignore comments starting with '#'; ignore empty lines
				if (line.indexOf("#") != -1 || line.equals(""))
					continue;
				String ip = line.substring(0, line.indexOf(":"));
				String port = line.substring(line.indexOf(":") + 1, line.length());
				machines.add(new InetSocketAddress(InetAddress.getByName(ip), Integer.valueOf(port)));
			}
		} catch (FileNotFoundException e) { // try-catch behandlung hier noch sehr ugyl, aber egal
			System.out.println("File not found beim discovery service");
			e.printStackTrace();
			System.exit(1); // hard, but ok
		} catch (IOException e) {
			System.out.println("File not found beim discovery service; ein kleiner IO-Fehler");
			e.printStackTrace();
			System.exit(1); // hard, but ok
		}
		
		// NOTFALL LÖSUNG FÜR JETZT
		// 
	}

	@Override
	public void shutdownService(IResultListener listener) {
		// Is here something special needed to do? I don't know yet...
	}

	// TODO ein Thread muss addMachine() und removeMachine() aufrufen, um Liste laufend abzudaten
	// natürlich nicht zu vergessen: multithread programming erfordert nebenläufigkeits-schutz!!! machines variable muss kontrolliert manipuliert werden
	private void addMachine(InetSocketAddress machine) {
		// mache allen listenern die neue machine bekannt
		/*
		Iterator<IDiscoveryServiceListener> it = this.listeners.iterator();
		while (it.hasNext()) {
			it.next().addMachine(machine);
		}
		*/
		
		for (IDiscoveryServiceListener listener : this.listeners) {
			listener.notifyIDiscoveryListener();
		}
		
	}

	private void removeMachine(InetSocketAddress machine) {
		// eine List und eine ArrayList bringen schon passende Methoden um ein Element einfach zu entfernen
		// wieso eigentlich eine List? Muss es wirklich eine geordnete Datenstruktur sein? Eine Set würde schon reichen
		// ausserdem wird ein element nie doppelt vorkommen, bzw. dies würde keinen sinn machen, wäre also auch ein zusätzlicher konsistenzschutz gewessen
		// dann muss aber natürlich verhindert werden, dass diese nebenläufig gelesen+geschrieben wird, da das zu einem inkonsistenten zustand führen kann

		for (IDiscoveryServiceListener listener : this.listeners) {
			listener.notifyIDiscoveryListener();
		}
	}

	
	/**
	 * A listener uses this method to registers itself. The listener is automatically notified when the set of available machines changes.
	 * 
	 * @param listener
	 *            - the object which is interested in getting notified when the set of available machines changes
	 */
	public void register(IDiscoveryServiceListener listener) {
		if (listener != null) { // is does not make sense to register null; even worse: is a error, becaue in Java it is not possible to call anything on null; well, in Objective-C this is possible
								// due to the dynamic nature of the language
			synchronized (this.listeners) {
				this.listeners.add(listener);
			}
		}
	}

	/**
	 * A listener uses this method to unregister, so it is no longer informed, when the list of machines changes.
	 * 
	 * @param listener
	 *            - listener object which doesn't want to be informed anymore when the list of known machines changes
	 */
	public void unregister(IDiscoveryServiceListener listener) {
		synchronized (this.listeners) {
			this.listeners.remove(listener); // works also when listener is not a memeber or included of this.listeners			
		}
	}

	@Override
	public Set<InetAddress> getMachineAddresses() {
		//return this.machines;
		return null;
	}
}