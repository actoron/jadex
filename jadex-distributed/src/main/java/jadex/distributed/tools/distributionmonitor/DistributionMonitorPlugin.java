package jadex.distributed.tools.distributionmonitor;

import jadex.commons.SGUI;
import jadex.distributed.service.monitor.IMonitorService;
import jadex.distributed.service.monitor.IMonitorServiceListener;
import jadex.distributed.service.monitor.PlatformInfo;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.common.plugin.IControlCenter;

import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;

public class DistributionMonitorPlugin extends AbstractJCCPlugin implements IMonitorServiceListener {

	private MyDefaultListModel _model;
	
	private IMonitorService _monitorService; // not set in constructor, but in init(jcc) method
	
	private JComponent _view; // main _view of the distribution plug in; it contains the sidebar/leftView and the right main content _view
	private JPanel _listView; // the left sidebar of the mein _view
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"icon",	SGUI.makeIcon(DistributionMonitorPlugin.class, "/jadex/distributed/tools/distributionmonitor/images/icon.png")	
	});
	
	// TODO why not give a plugin a reference to the IControlCenter in the constructor? why defer this step to the init(IControlCenter) method?
	public DistributionMonitorPlugin() {
		//this._platformInfos = new HashSet<PlatformInfo>();
		_view = buildView(); // I don't when createView() will be called, so initialize the _view now to prevent any complications
		// GOTO init(IControlCenter), the initialization finishes there
	}
	
	private JComponent buildView() {
		JPanel right = new JPanel();
		
		_model = new MyDefaultListModel(); // call later model.addElement(Object) when new PlatformInfo objects are available, and call model.platformChanged(...) to notify listeners about this
		JList list = new JList(_model);
		list.setCellRenderer(new PlatformInfoLabel());
		JScrollPane scroll = new JScrollPane(list);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, scroll, right); // true makes the JSplitPane more responsive to the user
		split.setOneTouchExpandable(true); // ability to collapse and show one side quickly
		return split; // to set main _view
	}
	
	@Override
	public void init(IControlCenter jcc) {
		super.init(jcc); // calls the three create-methods createView(), createMenuBar(), and createToolBar()
		this._monitorService = (IMonitorService)getJCC().getServiceContainer().getService(IMonitorService.class);
		this._monitorService.register(this); // _monitorService calls this.notifyIMonitorListenerAdd(PlatformInfo[]) in the background
		_model.platformChanged(); // force a (re-)paint of the left sidebar
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
	 *  - _view: created by method createView():JComponent
	 * If you only want a _view, but no tool bar or menu bar, then just don't overwrite the methods!
	 * The class AbstractJCCPlugin implements all of them with a return value of null, which means that
	 * the part in question isn't build and displayed. Besides that, it is ALWAYS a good idea to at least
	 * provide a own createView():JComponent method to display something useful to the user.
	 */
	/** Methods for AbstractJCCPlugin **/
	@Override
	public JComponent createView() {
		return this._view;
	}

	/** For IMonitorServiceListener: notifyIMonitorListenerChange() + notifyIMonitorListenerChange(PlatformInfo) + notifyIMonitorListenerAdd(PlatformInfo) + notifyIMonitorListenerAdd(PlatformInfo[]) + notifyIMonitorListenerRemove(PlatformInfo) **/
	@Override
	public void notifyIMonitorListenerChange() { // called by the MonitorService to indicate that the state one, some, or all platforms changed; and these changes are reflected by changed field values in the PlatformInfo objects
		System.out.println("GUI wurde notified");
		_model.platformChanged(); // a JList model automatically notifies a JList that it needs to repaint
	}

	@Override
	public void notifyIMonitorListenerChange(PlatformInfo platformInfo) {
		_model.platformChanged(platformInfo); // a JList model automatically notifies a JList that it needs to repaint
	}
	
	@Override
	public void notifyIMonitorListenerAdd(PlatformInfo platformInfo) { // called by the MonitorService to indicate that a new slave platform joined the group of platforms and its current state is represented by the passed PlatformInfo object
		_model.addElement(platformInfo); // a JList model automatically notifies a JList that it needs to repaint
	}

	@Override
	public void notifyIMonitorListenerAdd(PlatformInfo[] platformInfo) {
		for (PlatformInfo info : platformInfo) {
			_model.addElement(info); // a JList model automatically notifies a JList that it needs to repaint
		}
	}
	
	@Override
	public void notifyIMonitorListenerRemove(PlatformInfo platformInfo) { // called by the MonitorService to indicate that a slave leaved the group; the PlatformInfo object formerly representing the state of the slave is now obsolete and can be removed from the IMonitorServiceListener
		_model.removeElement(platformInfo); // a JList model automatically notifies a JList that it needs to repaint
	}
	
	private static class MyDefaultListModel extends DefaultListModel {
		public void platformChanged(PlatformInfo platform) {
			int index = this.indexOf(platform);
			this.fireContentsChanged(this, index, index);
		}
		
		public void platformChanged() {
			int index = this.getSize();
			System.out.println("MYLIST im platformChanged");
			this.fireContentsChanged(this, 0, index);
		}
	}
}