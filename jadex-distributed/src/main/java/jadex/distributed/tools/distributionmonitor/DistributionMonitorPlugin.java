package jadex.distributed.tools.distributionmonitor;

import jadex.commons.SGUI;
import jadex.distributed.service.monitor.IMonitorService;
import jadex.distributed.service.monitor.IMonitorServiceListener;
import jadex.distributed.service.monitor.Workload;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.common.plugin.IControlCenter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;

public class DistributionMonitorPlugin extends AbstractJCCPlugin implements IMonitorServiceListener {

	protected Map<InetSocketAddress, Workload> machineWorkloads;
	protected JComponent view; // das Hauptfenster im JCC; hier wird alles gezeichnet: sidebar, main content, ...
	protected IMonitorService monitorService; // erst bei init(...) gesetzt, denn erst ab da ist der jcc verfügbar, der dann den Container/Platform geben kann
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"icon",	SGUI.makeIcon(DistributionMonitorPlugin.class, "/jadex/distributed/tools/distributionmonitor/images/icon.png")	
	});
	
	public DistributionMonitorPlugin() {
		this.machineWorkloads = new HashMap<InetSocketAddress, Workload>(); // unnecessary due monitorService should automatically initiate the variable with a call to updateWorkloadAll(...)
		view = builtView(); // view schon hier aufbauen, damit zu jeder Zeit createView() aufgerufen werden kann
	}
	
	private JComponent builtView() {
		PlatformList listView = new PlatformList(this.machineWorkloads); // left shows found platforms and their current resources status
		// here non-sense, because no workload is currently listed
		
		JPanel right = new JPanel(); // common pattern: use JPanels to group items; use extended JComponent to praint
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, listView, right); // true makes the JSplitPane more responsive to the user
		split.setOneTouchExpandable(true); // ability to collapse and show one side quickly
		return split;
	}
	
	@Override
	public void init(IControlCenter jcc) {
		super.init(jcc);
		
		this.monitorService = (IMonitorService)getJCC().getServiceContainer().getService(IMonitorService.class);
		this.monitorService.register(this); // register at IMonitorService to recieve up to date management information
	}

	@Override
	public boolean isLazy() {
		return false; // so everything will run as fast as possible in a demo
	}

	@Override
	public String getHelpID() {
		return "tools.distributionmonitor"; // probably not so important, but for what is this good for?
	}

	@Override
	public String getName() {
		return "Distribution Monitor"; // and what is this good for?
	}

	@Override
	public Icon getToolIcon(boolean selected) {
		//return selected? icons.getIcon("icon"): icons.getIcon("icon");
		// two different icons can be supplied; one when this plug in is currently selected, and one when it is not selected
		return icons.getIcon("icon"); // for now just one icon
	}
	
	
	/* What are the parts of a JCC plugin
	 * There are three parts
	 *  - tool bar: created by method createToolBar():JComponent[]
	 *  - menu bar: created by method createMenuBar():JMenu[]
	 *  - view: created by method createView():JComponent
	 * If you only want a view, but no tool bar or menu bar, then just don't overwrite the methods!
	 * The class AbstractJCCPlugin implements all of them with a return value of null, which means that
	 * the part in question isn't build and displayed. Besides that, it is ALWAYS a good idea to at least
	 * provide a own createView():JComponent method to display something useful to the user.
	 */
	/** Methods for AbstractJCCPlugin **/
	@Override
	public JComponent createView() {
		return this.view;
	}

	
	/** Three methods for IMonitorServiceListener **/
	@Override
	public void removeWorkloadSingle(InetSocketAddress machine, Workload workload) {
		this.machineWorkloads.remove(machine);
	}

	@Override
	public void updateWorkloadAll(Map<InetSocketAddress, Workload> machineWorkloads) {
		/*
		   Unfortunately the constructor calls builtView, which in turn creates a PlatformList, which
		   keeps a reference on the empty machineWorkloads set, created in the constructor.
		   Changing the reference in PlatformList is possible, but not an elegant solution.
		   Here we go another way: copy the entries to the workload variable
		 */
		//this.machineWorkloads = machineWorkloads;
		
		
		// repaint an Liste der Plattformen ausführen
	}

	@Override
	public void updateWorkloadSingle(InetSocketAddress machine, Workload workload) {
		this.machineWorkloads.put(machine, workload); // put is very nice: only updates value when key already present
		
	}

	/*** Three methods to implement the IDiscoveryServiceListener interface ***/
	/*@Override
	public void addMachine(InetSocketAddress machine) {
		machines.add(machine);
		view.repaint();
	}

	@Override
	public void addMachines(Set<InetSocketAddress> machines) {
		this.machines = machines;
		view.repaint();
	}

	@Override
	public void removeMachine(InetSocketAddress machine) {
		machines.remove(machine);
		view.repaint();
	}*/	
}