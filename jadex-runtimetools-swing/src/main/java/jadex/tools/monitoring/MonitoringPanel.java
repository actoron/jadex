package jadex.tools.monitoring;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreeSelectionModel;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.idtree.IdTreeCellRenderer;
import jadex.base.gui.idtree.IdTreeModel;
import jadex.base.gui.idtree.IdTreeNode;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;

/**
 *  The security settings panel.
 */
public class MonitoringPanel	implements IServiceViewerPanel
{
	//-------- attributes --------
	
	/** The monitoring service. */
	protected IMonitoringService monservice;
	
	/** The tree of events. */
	protected JPanel inner;
	
	//-------- methods --------
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public IFuture<Void> init(final IControlCenter jcc, IService service)
	{
		this.monservice	= (IMonitoringService)service;
		this.inner	= new JPanel(new BorderLayout());

		final IdTreeModel<List<IMonitoringEvent>> tm = new IdTreeModel<List<IMonitoringEvent>>();
		final IdTreeNode<List<IMonitoringEvent>> root = new IdTreeNode<List<IMonitoringEvent>>("root", null, tm, false, null, null, null);
		tm.setRoot(root);
		JTree tree = new JTree(tm);
		tree.setCellRenderer(new IdTreeCellRenderer());
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		ToolTipManager.sharedInstance().registerComponent(tree);
		
		monservice.subscribeToEvents(null).addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IIntermediateResultListener<IMonitoringEvent>()
		{
			public void intermediateResultAvailable(IMonitoringEvent event)
			{
//				if(event.getType().indexOf(IMonitoringEvent.SOURCE_CATEGORY_SERVICE)==-1)
//					return;
				
				String origin = event.getCause().getOrigin();
				
//				System.out.println("received callid: "+callid);
				
				IdTreeNode<List<IMonitoringEvent>> call = tm.getNode(origin);
//				String name = event.getCause().getSourceId().equals(origin)? event.getCause().getSourceName()+" ("+origin+")": origin;
				String name = event.getCause().getSourceId().equals(origin)? event.getCause().getSourceId()+" ("+origin+")": origin;
				if(call==null)
				{
					call = new IdTreeNode<List<IMonitoringEvent>>(origin, name, tm, null, null, null, null);
					root.add(call);
				}
				else if(origin.equals(call.getName()) && event.getCause().getSourceId().equals(origin))
				{
					call.setName(name);
				}
				
				String srcid = event.getCause().getSourceId();
				String trgid = event.getCause().getTargetId();
				
				IdTreeNode<List<IMonitoringEvent>> parent = tm.getNode(srcid);
				IdTreeNode<List<IMonitoringEvent>> child = tm.getNode(trgid);
				
//				System.out.println("event : "+srcid+" "+(parent!=null)+" "+trgid+" "+(child!=null));
				
				// Create child
				if(child==null)
				{
					// Parent not found, create parent and add parent on call level
//					child = new MyIdTreeNode<List<IMonitoringEvent>>(trgid, event.getCause().getTargetName()+" "+trgid, tm, null, null, null, new ArrayList<IMonitoringEvent>()); 
					String desc = event.getSourceIdentifier().toString();
					if(event.getSourceDescription()!=null)
						desc += event.getSourceDescription();
					child = new MyIdTreeNode<List<IMonitoringEvent>>(trgid, desc, tm, null, null, null, new ArrayList<IMonitoringEvent>()); 
				}
				else
				{
					// If child exists check whether it has been added to top-level call -> remove, now has correct parent
					if(child.getParent().equals(call))
					{
						call.remove(child);
					}
				}

				// Create parent
				if(parent==null)
				{
					// Parent not found, create parent and add parent on call level
//					String srcname = event.getCause().getSourceName()!=null? event.getCause().getSourceName(): "unknown";
					parent = new MyIdTreeNode<List<IMonitoringEvent>>(srcid, null, tm, null, null, null, new ArrayList<IMonitoringEvent>()); 
					call.add(parent);
				}

				// Add event on child
				List<IMonitoringEvent> evs = child.getObject();
//				if(evs==null)
//					System.out.println("huch");
				evs.add(event);
				
				// Add child on parent
//				System.out.println("parent "+parent.getId()+" "+parent.getChildCount());
				parent.add(child);
//				else if(parent.hashCode()!=child.getParent().hashCode())
//				{
//					throw new RuntimeException("Wrong parent");
//				}
			}
			
			public void resultAvailable(Collection<IMonitoringEvent> result)
			{
				System.out.println("ra");
			}
			
			public void finished()
			{
				System.out.println("finfi");
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		}));
		
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				root.removeAllChildren();
			}
		});
		
		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		south.add(clear);
		
		inner.add(new JScrollPane(tree), BorderLayout.CENTER);
		inner.add(south, BorderLayout.SOUTH);
		
//		return ret;
		return IFuture.DONE;
	}

	/**
	 *  Informs the plugin that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		return IFuture.DONE;
	}

	/**
	 *  Get the component.
	 */
	public JComponent getComponent()
	{
		return inner;
	}
		
	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "monitoring";
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture<Void> setProperties(Properties props)
	{
//		cbshowchars.setSelected(props.getBooleanProperty("showchars"));
//		((JTabbedPane)inner).setSelectedIndex(props.getIntProperty("selected_tab"));
//		if(props.getProperty("sph")!=null)
//			sph.setDividerLocation(props.getDoubleProperty("sph"));
//		if(props.getProperty("spv")!=null)
//			spv.setDividerLocation(props.getDoubleProperty("spv"));
		return IFuture.DONE;
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture<Properties> getProperties()
	{
		Properties	props	= new Properties();
//		props.addProperty(new Property("showchars", Boolean.toString(cbshowchars.isSelected())));
//		props.addProperty(new Property("selected_tab", ""+((JTabbedPane)inner).getSelectedIndex()));
//		props.addProperty(new Property("sph", ""+sph.getProportionalDividerLocation()));
//		props.addProperty(new Property("spv", ""+spv.getProportionalDividerLocation()));
		return new Future<Properties>(props);
	}
	
}

/**
 * 
 */
class MyIdTreeNode<T> extends IdTreeNode<T>
{
	/**
	 *  Create a new node.
	 */
	public MyIdTreeNode(String key, String name, IdTreeModel<T> tm, Boolean leaf,
		Icon icon, String tooltip, T object)
	{
		super(key, name, tm, leaf, icon, tooltip, object);
//		System.out.println("created: "+getId());
	}
	
	/**
	 *  Get the tooltip.
	 */
	public String getTooltipText()
	{
		List<IMonitoringEvent> evs = (List<IMonitoringEvent>)getObject();
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append("nodeid=").append(getId()).append("<br>");
	
//		boolean first = true;
		if(evs!=null && !evs.isEmpty())
		{
			for(IMonitoringEvent ev: evs)
			{
//				if(first)
//				{
//					first = false;
//					buf.append("chainid=").append(ev.getCause().getChainId()).append("<br>");
//				}
				
//				buf.append(ev.getSourceIdentifier()+" "+ev.getType());
				buf.append(ev.getCause().getOrigin()+" "+ev.getSourceIdentifier()+" "+ev.getType()+" "+ev.getCause().getSourceId()+" "+ev.getCause().getTargetId());
				buf.append("<br>");
			}
		}
		
		buf.append("</html>");
		String ret = buf.toString();
		return ret;
	}
}
