//Erbt von AbstractJCCPlugin
package jadex.distributed.tools.distributionmonitor;

import java.net.InetSocketAddress;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;

import jadex.bridge.IMessageService;
import jadex.commons.SGUI;
import jadex.distributed.service.DiscoveryService;
import jadex.distributed.service.IDiscoveryService;
import jadex.distributed.service.IDiscoveryServiceListener;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.libtool.LibraryPlugin;
import jadex.tools.starter.StarterPlugin;

public class DistributionMonitorPlugin extends AbstractJCCPlugin implements IDiscoveryServiceListener {

	protected Set<InetSocketAddress> machines;
	protected JComponent view;
	protected IDiscoveryService discoveryService;
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
//		"conversation",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/libcenter.png"),
//		"conversation_sel", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/libcenter_sel.png"),
//		"help",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/help.gif"),
		//"icon",	SGUI.makeIcon(DistributionMonitorPlugin.class, "/jadex/distributed/tools/distributionmonitor/images/icon.png")
		"icon",	SGUI.makeIcon(DistributionMonitorPlugin.class, "/jadex/distributed/tools/distributionmonitor/images/icon.png")
		
	});
	
	public DistributionMonitorPlugin() {
		view = builtView(); // view aufbauen, damit sie zu jeder Zeit durch createView() abgeholt werden kann
		// beim DiscoverService registrieren, um laufend über Änderungen informiert zu werden
		discoveryService = (IDiscoveryService) getJCC().getServiceContainer().getService(IDiscoveryService.class);
		discoveryService.register(this);
	}
	
	private JComponent builtView() {
		//DistributionMonitorPlatformList left = new DistributionMonitorPlatformList(); // common pattern: use JPanels to group items; use extended JComponent to praint
		JPanel left = new JPanel();
		// left mit einzelnen Items füllen
		
		JPanel right = new JPanel();
		
		// left JPanel lists all found platforms
		// get list of platforms and their current resources
		
		
		// right JPanel show ... I don't know yet
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, left, right); // true makes the JSplitPane more responsive to the user
		split.setOneTouchExpandable(true); // ability to collapse and show one side quickly
		return split;
	}
	
	@Override
	public boolean isLazy() {
		return false; // während einer Demo soll alles gut und schnell laufen
	}

	@Override
	public String getHelpID() {
		return "tools.distributionmonitor"; // TODO ist wahrscheinlich nicht so wichtig, aber: wofür wird das benötigt?
	}

	@Override
	public String getName() {
		return "Distribution Monitor";
	}

	@Override
	public Icon getToolIcon(boolean selected) {
		//return selected? icons.getIcon("icon"): icons.getIcon("icon");
		return icons.getIcon("icon");
	}
	
	
	/* What are the parts of a JCC plugin
	 * There are three parts
	 *  - tool bar: created by method createToolBar():JComponent[]
	 *  - menu bar: created by method createMenuBar():JMenu[]
	 *  - view: created by method createView():JComponent
	 * If you only want a view, but no tool bar or menu bar, then just don't write them!
	 * The class AbstractJCCPlugin implements all of them with a return of null, which means that
	 * non of the parts is build and displayed. Besides that, it is ALWAYS a good idea to at least
	 * provide a own createView():JComponent method to display something useful to the user.
	 */
	
	
	@Override
	public JComponent createView() {
		return this.view;
	}

	/*** Three methods to implement the IDiscoveryServiceListener interface ***/
	@Override
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
	}
}