package jadex.distributed.tools.distributionmonitor;

import jadex.distributed.service.Workload;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

public class PlatformList extends JPanel {
	
	private Map<InetSocketAddress, Workload> machineWorkloads;
	private Map<InetSocketAddress, PlatformItem> items; // für jeden workload ein item, beide müssen synchron bleiben; bei jedem repaint darauf checken und evtl. neue Items erzeugen oder alte entfernen
	
	// WARNUNG eine HashMap ist nicht threadsicher, da nicht synchronized!!! mal sehen wozu das noch führen wird ...
	
	public PlatformList(Map<InetSocketAddress, Workload> machineWorkloads) {
		this.machineWorkloads = machineWorkloads;
		
		// initiere items mit der ersten Liste der workloads, damit von Anfang an synchron sind
		Set<InetSocketAddress> machines = this.machineWorkloads.keySet();
		
	}	
	
	
	
}
