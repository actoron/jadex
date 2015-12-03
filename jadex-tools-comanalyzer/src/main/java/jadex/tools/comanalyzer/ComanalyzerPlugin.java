package jadex.tools.comanalyzer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

import jadex.base.gui.asynctree.INodeListener;
import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.componenttree.IActiveComponentTreeNode;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IRemoteMessageListener;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageListener;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.IFilter;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.tools.comanalyzer.chart.ChartPanel;
import jadex.tools.comanalyzer.diagram.DiagramPanel;
import jadex.tools.comanalyzer.graph.GraphPanel;
import jadex.tools.comanalyzer.table.TablePanel;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;


/**
 * The comanalyzer plugin.
 */
public class ComanalyzerPlugin extends AbstractJCCPlugin
{
	//-------- constants --------

	// todo: 
	/** The system event types. */
	protected static final String[] TYPES = new String[]{"messageeventsent", "messageeventreceived"};

	/** The icon paths */
	protected static final String COMANALYZER_IMAGES = "/jadex/tools/comanalyzer/images/";

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"comanalyzer", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "new_comanalyzer.png"),
		"comanalyzer_sel", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "new_comanalyzer_sel.png"),
		"start_observing", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "start_observing.png"),
		"stop_observing", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "stop_observing.png"),
		"introspect_agent", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "new_comanalyzer.png"),
		"close_comanalyzer", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "close_comanalyzer.png"),
		"agent_introspected", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "overlay_introspected.png"),
		"agent_dead", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "overlay_dead.png"),
		"agent_dummy", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "agent_dummy.png"),
		"load", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "load.png"),
		"save", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "save2.png"),
		"clear", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "litter.png")
	});

	/** Refresh immediately. */
	protected static final long REFRESHI = Long.MIN_VALUE;

	/** Refresh every 1 second */
	protected static final long REFRESH1 = 1000;

	/** Refresh every 5 seconds */
	protected static final long REFRESH5 = 5000;

	/** Refresh every 10 seconds */
	protected static final long REFRESH10 = 10000;

	/** Refresh auto sensored (experimental). */
	protected static final long REFRESHA = Long.MAX_VALUE;

	// -------- attributes --------

	/** How long should the refresh process wait */
	protected long sleep = REFRESHI;

	/** Timer for RefreshTask */
	protected Timer timer;

	/** The refresh task. */
	protected RefreshTask refresh_task;

	/** The split panel. */
	protected JSplitPane split;

	/** The agent tree table. */
	protected ComponentTreePanel comptree;

	/** The checkbox items for selecting default values. */
	protected JCheckBoxMenuItem[] checkboxes;

	/** The ToolPanel. */
	protected ToolPanel tpanel;

	/** The ToolTabs. */
	protected ToolTab table;

	protected ToolTab diagram;

	protected ToolTab graph;

	protected ToolTab chart;

	/** The currently registered listeners (listener->ComanalyzerListener). */
	protected Map listeners;

	/** The global list of recognized agents. */
	protected ComponentList componentlist;

	/** The global list of recorded messages. */
	protected MessageList messagelist;

	/** The global messagefilter */
	protected MessageFilter[] messagefilter;

	/** The global agentfilter */
	protected ComponentFilter[] agentfilter;

	/** The agentfilter for zero messages*/
	protected static final ComponentFilter[] zeromessages = new ComponentFilter[] {new ComponentFilter(Component.MESSAGE_VISIBLE, Integer.valueOf(Component.NO_MESSAGES))};	
	
	/** Observe all new agents. */
	protected boolean observe_all_new;

	/** The message number counter for loaded and received messages */
	private int messagenr;

	/** The map of shared colors for message and agent representation.. */
	protected PaintMaps paintmaps;
	
	/** The set of registered agent adapters. */
	protected Set observed;
	
	/** The message services (service_id->[service, observed component set]). */
	protected Map	msgservices;
	
	/** The message service listener. */
	protected IMessageListener	listener;
	
	/** The clock service. */
	protected IClockService clockservice;
	
	//-------- constructors --------

	/**
	 * Create a new comanalyzer plugin.
	 */
	public ComanalyzerPlugin()
	{
		this.messagenr = 1;
		this.listeners = new HashMap();
		this.componentlist = new ComponentList();
		this.agentfilter = new ComponentFilter[]{ComponentFilter.EMPTY};
		this.messagelist = new MessageList();
		this.messagefilter = new MessageFilter[]{MessageFilter.EMPTY};
		this.timer = new Timer(true);
		this.observed = new HashSet();
		this.paintmaps = new PaintMaps();
		this.msgservices	= new HashMap();
		this.listener	= new IRemoteMessageListener()
		{
			public IFuture messageReceived(IMessageAdapter msg)
			{
				addMessage(msg);
				return new Future(null);
			}
			public IFuture messageSent(IMessageAdapter msg)
			{
				addMessage(msg);
				return new Future(null);
			}
		};
	}
	
	/** 
	 *  Initialize the plugin.
	 */
	public IFuture<Void> init(IControlCenter jcc)
	{
		final Future<Void> ret = new Future<Void>();
		super.init(jcc).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// Todo: use remote access for clock !?
				SServiceProvider.getService(getJCC().getJCCAccess(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new SwingExceptionDelegationResultListener<IClockService, Void>(ret)
				{
					public void customResultAvailable(IClockService result)
					{
						clockservice = result;
						ret.setResult(null);
					}
				});
			}
		});
		return ret;
	}
	
	/** 
	 *  Shutdown the plugin.
	 */
	public IFuture<Void> shutdown()
	{
		final Future<Void> ret = new Future<Void>();
		comptree.dispose();
		
		CounterResultListener<Void> lis = new CounterResultListener<Void>(msgservices.values().size(), 
			true, new SwingDelegationResultListener<Void>(ret));
		
		for(Iterator it=msgservices.values().iterator(); it.hasNext(); )
		{
			Object[]	entry	= (Object[])it.next();
			((IMessageService)entry[0]).removeMessageListener(listener).addResultListener(lis);
		}
		
		return ret;
	}

	//-------- IControlCenterPlugin interface --------

	/**
	 * Get plugin properties to be saved in a project.
	 */
	public IFuture getProperties()
	{
		Properties	props	= new Properties();
		for(int i=0; i<checkboxes.length; i++)
		{
//			System.out.println(""+checkboxes[i].getText()+" "+checkboxes[i].isSelected());
			props.addProperty(new Property(checkboxes[i].getText(), ""+checkboxes[i].isSelected()));
		}
		return new Future(props);
	}
	

	/**
	 * Set plugin properties loaded from a project.
	 */
	public IFuture setProperties(Properties props)
	{
		for(int i = 0; i < checkboxes.length; i++)
		{
			boolean	selected = props.getBooleanProperty(checkboxes[i].getText());
//			System.out.println(checkboxes[i].getText()+" "+selected);
			// checkboxes[i].setSelected(selected != null ? new
			// Boolean(selected).booleanValue() : false);
			// trigger action to activate panels and set fields
			if(selected)
			{
				checkboxes[i].doClick();
			}
		}
		
//		for(int i=0; i<checkboxes.length; i++)
//		{
//			boolean	selected = props.getBooleanProperty(checkboxes[i].getText());
//			checkboxes[i].setSelected(selected);
//		}

		// activate tooltab on default values
		// cant do it in createView() because properties arent loaded yet
		boolean selected = false;
		ToolTab[] tools = tpanel.tools;
		for(int i = 0; i < tools.length; i++)
		{

			// Select first active tab.
			if(!selected && tools[i].isActive())
			{
				tpanel.tabs.setSelectedIndex(i);
				selected = true;
			}
		}
		
		return IFuture.DONE;
	}

	/**
	 * @return "Comanalyzer"
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Comanalyzer";
	}

	/**
	 * @return The icon of comanalyzer.
	 * @see jadex.base.gui.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected ? icons.getIcon("comanalyzer_sel") : icons.getIcon("comanalyzer");
	}

	/**
	 * Create tool bar.
	 * 
	 * @return The tool bar.
	 */
	public JComponent[] createToolBar()
	{
		List components = new ArrayList();

		components.add(new JMenuButton(START_OBSERVING));
		components.add(new JMenuButton(STOP_OBSERVING));
		components.add(new JToolBar.Separator());
		components.add(new JMenuButton(LOAD_MESSAGES));
		components.add(new JMenuButton(SAVE_MESSAGES));
		components.add(new JToolBar.Separator());
		components.add(new JMenuButton(REMOVE_ALL));

		return (JComponent[])components.toArray((new JComponent[components.size()]));
	}

	/**
	 * Create menu bar.
	 * @return The menu bar.
	 */
	public JMenu[] createMenuBar()
	{
		List components = new ArrayList();

		JMenu m1 = new JMenu("Agents");
		m1.add(new JMenuItem(IGNORE_ALL));
		m1.add(new JMenuItem(OBSERVE_ALL));
		m1.add(new JCheckBoxMenuItem(OBSERVE_ALL_NEW));
		m1.addSeparator();
		m1.add(new JMenuItem(REMOVE_DEAD));

		JMenu m2 = new JMenu("Messages");
		m2.add(new JMenuItem(REMOVE_ALL_MESSAGES));

		JMenu m3 = new JMenu("Panels");
		m3.add(new JCheckBoxMenuItem(ENABLE_TABLE));
		m3.add(new JCheckBoxMenuItem(ENABLE_DIAGRAM));
		m3.add(new JCheckBoxMenuItem(ENABLE_GRAPH));
		m3.add(new JCheckBoxMenuItem(ENABLE_CHART));

		JMenu m4 = new JMenu("Auto Refresh");
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem refresh0 = new JRadioButtonMenuItem(REFRESH_ATONCE);
		refresh0.setSelected(sleep == REFRESHI);
		group.add(refresh0);
		m4.add(refresh0);

		JRadioButtonMenuItem refresh1 = new JRadioButtonMenuItem(REFRESH_1S);
		refresh1.setSelected(sleep == REFRESH1);
		group.add(refresh1);
		m4.add(refresh1);

		JRadioButtonMenuItem refresh5 = new JRadioButtonMenuItem(REFRESH_5S);
		refresh5.setSelected(sleep == REFRESH5);
		group.add(refresh5);
		m4.add(refresh5);

		JRadioButtonMenuItem refreshA = new JRadioButtonMenuItem(REFRESH_AUTO);
		refreshA.setSelected(sleep == REFRESHA);
		group.add(refreshA);
		m4.add(refreshA);

		// add menus to list
		components.add(m1);
		components.add(m2);
		components.add(m3);
		components.add(m4);

		// iterate menu items of all menus to get checkbox items for properties
		List checkboxes = new ArrayList();
		for(Iterator iter = components.iterator(); iter.hasNext();)
		{
			JMenu menu = (JMenu)iter.next();
			for(int i = 0; i < menu.getItemCount(); i++)
			{
				JMenuItem comp = menu.getItem(i);
				if(comp instanceof JCheckBoxMenuItem)
				{
					checkboxes.add(comp);
				}
			}
		}
		this.checkboxes = (JCheckBoxMenuItem[])checkboxes.toArray((new JCheckBoxMenuItem[checkboxes.size()]));

		return (JMenu[])components.toArray((new JMenu[components.size()]));
	}

	/**
	 * Create main panel.
	 * @return The main panel.
	 */
	public JComponent createView()
	{
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		split.setOneTouchExpandable(true);

		comptree = new ComponentTreePanel(getJCC().getPlatformAccess(), getJCC().getJCCAccess(), getJCC().getCMSHandler(), getJCC().getPropertyHandler(), getJCC().getIconCache());
		comptree.setMinimumSize(new Dimension(0, 0));
		split.add(comptree);
		
		comptree.getModel().addNodeListener(new INodeListener()
		{
			public void nodeAdded(ITreeNode node)
			{
				if(node instanceof IActiveComponentTreeNode)
				{
					IComponentDescription ad = ((IActiveComponentTreeNode)node).getDescription();
//					System.out.println("born: "+ad.getName());
					agentBorn(ad);
				}
			}
			
			public void nodeRemoved(ITreeNode node)
			{
				if(node instanceof IActiveComponentTreeNode)
				{
					IComponentDescription ad = ((IActiveComponentTreeNode)node).getDescription();
//					System.out.println("died: "+ad.getName());
					agentDied(ad);
				}
			}
		});
		
		comptree.addNodeHandler(new ShowRemoteControlCenterHandler(getJCC(), getView()));
		
		comptree.addNodeHandler(new ISwingNodeHandler()
		{
			public byte[] getOverlay(ITreeNode node)
			{
				return null;
			}

			public Icon getSwingOverlay(ISwingTreeNode node)
			{
				Icon	ret	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					IComponentDescription ad = ((IActiveComponentTreeNode)node).getDescription();
					Component agent = componentlist.getAgent(ad.getName());
					if(agent!=null && Component.STATE_OBSERVED.equals(agent.getState()))
					{
						ret	= ComanalyzerPlugin.icons.getIcon("agent_introspected");
					}
				}
				return ret;
			}
			
			public Action[] getPopupActions(ISwingTreeNode[] nodes)
			{
				Action[]	ret	= null;
				
				boolean	allcomp	= true;
				for(int i=0; allcomp && i<nodes.length; i++)
				{
					allcomp	= nodes[i] instanceof IActiveComponentTreeNode;
				}
				
				if(allcomp)
				{
					boolean	allob	= true;
					for(int i=0; allob && i<nodes.length; i++)
					{
						IComponentDescription ad = ((IActiveComponentTreeNode)nodes[i]).getDescription();
						Component agent = componentlist.getAgent(ad.getName());
						allob	= agent!=null && Component.STATE_OBSERVED.equals(agent.getState());
					}
					boolean	allig	= true;
					for(int i=0; allig && i<nodes.length; i++)
					{
						IComponentDescription ad = ((IActiveComponentTreeNode)nodes[i]).getDescription();
						Component agent = componentlist.getAgent(ad.getName());
						allig	= agent!=null && Component.STATE_IGNORED.equals(agent.getState());
					}
					
					// Todo: Large icons for popup actions?
					if(allig)
					{
						Action	a	= new AbstractAction((String)START_OBSERVING.getValue(Action.NAME), icons.getIcon("start_observing"))
						{
							public void actionPerformed(ActionEvent e)
							{
								START_OBSERVING.actionPerformed(e);
							}
						};
						ret	= new Action[]{a};
					}
					else if(allob)
					{
						Action	a	= new AbstractAction((String)STOP_OBSERVING.getValue(Action.NAME), icons.getIcon("stop_observing"))
						{
							public void actionPerformed(ActionEvent e)
							{
								STOP_OBSERVING.actionPerformed(e);
							}
						};
						ret	= new Action[]{a};
					}
				}
				
				return ret;
			}
			
			public Action getDefaultAction(ISwingTreeNode node)
			{
				Action	a	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					Component	agent	= componentlist.getAgent(((IActiveComponentTreeNode)node).getDescription().getName());
					if(agent!=null && agent.getState().equals(Component.STATE_OBSERVED))
					{
						a	= STOP_OBSERVING;
					}
					else if(agent!=null && agent.getState().equals(Component.STATE_IGNORED))
					{
						a	= START_OBSERVING;
					}
				}
				return a;
			}
		});

		split.setDividerLocation(150);

		// create the tools
		table = new TablePanel(this);
		diagram = new DiagramPanel(this);
		graph = new GraphPanel(this);
		chart = new ChartPanel(this);
		ToolTab[] tools = new ToolTab[]{table, diagram, graph, chart};
		// add agentlist and messagelist listeners to tooltabs
		for(int i = 0; i < tools.length; i++)
		{
			componentlist.addListener(tools[i]);
			messagelist.addListener(tools[i]);
		}

		tpanel = new ToolPanel(tools);

//		SHelp.setupHelp(tpanel, "tools.comanalyzer");
		split.add(tpanel);

//		agents.getTreetable().addMouseListener(new MouseAdapter()
//		{
//			public void mouseClicked(MouseEvent e)
//			{
//				if(e.getClickCount() == 2)
//				{
//					if(START_OBSERVING.isEnabled())
//						START_OBSERVING.actionPerformed(null);
//					else if(STOP_OBSERVING.isEnabled())
//						STOP_OBSERVING.actionPerformed(null);
//				}
//
//			}
//		});

//		jcc.addAgentListListener(this);
		
//		SServiceProvider.getService(getJCC().getExternalAccess().getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				IComponentManagementService cms = (IComponentManagementService)result;
//				
//				cms.getComponentDescriptions().addResultListener(new IResultListener()
//				{
//					public void resultAvailable(Object result)
//					{
//						IComponentDescription[] res = (IComponentDescription[])result;
//						for(int i=0; i<res.length; i++)
//							agentBorn(res[i]);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//					}
//				});
//				cms.addComponentListener(null, new ICMSComponentListener()
//				{
//					public IFuture componentRemoved(IComponentDescription desc, Map results)
//					{
//						agentDied(desc);
//						return new Future(null);
//					}
//					
//					public IFuture componentAdded(IComponentDescription desc)
//					{
//						agentBorn(desc);
//						return new Future(null);
//					}
//
//					public IFuture componentChanged(IComponentDescription desc)
//					{
//						return new Future(null);
//					}
//				});
//			}
//		});
		
		
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				agents.adjustColumnWidths();
//			}
//		});

		// add dummy agent to agentlist
		Component dummy = Component.DUMMY_COMPONENT;
		applyAgentFilter(dummy);
		componentlist.addAgent(dummy);
				
		return split;
	}

	// -------- IAgentListListener interface --------

	/**
	 * Remove listeners and set agent state.
	 * @param ad The agent description of the agent that has died.
	 */
	public void agentDied(final IComponentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{

				// remove listener if agent is observed
//				if(listeners.containsKey(ad))
//				{
//					removeAgentListener(ad, false);
//				}
				// set agent state and update agent
				Component agent = (Component)componentlist.getAgent(ad.getName());
				agent.setState(Component.STATE_DEAD);
				applyAgentFilter(agent);
			}
		});
	}

	/**
	 * Add the agent to the agentlist
	 * @param ad The agent description of the agent that was born.
	 */
	public void agentBorn(final IComponentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
//				if(observe_all_new)
//				{
//					addAgentListener(ad);
//				}

				boolean updateAgent = true;
				Component agent = componentlist.getAgent(ad.getName());
				if(agent == null)
				{
					agent = new Component(ad);
					updateAgent = false;
				}
				agent.setState(observe_all_new ? Component.STATE_OBSERVED : Component.STATE_IGNORED);

				if(updateAgent)
				{
					applyAgentFilter(agent);
				}
				else
				{
					applyAgentFilter(agent);
					componentlist.addAgent(agent);
				}
			}
		});
	}

	/**
	 * @param ad The agent description of the agent that has changed.
	 */
	public void agentChanged(final IComponentDescription ad)
	{
		// NOP
	}

	//-------- methods --------

	/**
	 * Creates a listener for the agent to obtain internal agent events.
	 * @param desc The agentdescription.
	 * /
	public void addAgentListener(final IComponentDescription desc)
	{
		IComponentIdentifier aid = desc.getName();
		((IComponentManagementService)jcc.getServiceContainer().getService(IComponentManagementService.class))
			.getComponentAdapter(aid, new IResultListener()
			{
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
				}
				public void resultAvailable(Object result)
				{
					// HACK!!!
					// Hack!!!
//					final BDIInterpreter ip = ((ElementFlyweight)result).getInterpreter();
//					BDIInterpreter ip = (BDIInterpreter)((IAgentAdapter)result).getJadexAgent();
//					ComanalyzerAdapter adapter	= (ComanalyzerAdapter)((BDIInterpreter)ip).getToolAdapter(ComanalyzerAdapter.class);
					
					ComanalyzerAdapter adapter = (ComanalyzerAdapter)((IComponentAdapter)result).getToolAdapter(ComanalyzerAdapter.class);
					adapters.put(desc, adapter);
					adapter.addTool(ComanalyzerPlugin.this);
				}
			});
	}*/

	/**
	 * Removes the listener for the agent.
	 * 
	 * @param desc The agentdiscriotion.
	 * @param cleanup <code>true</code> if the listener should be removed from
	 * the agent. (e.g. on agent death it isnt nessesary)
	 * /
	public void removeAgentListener(final IComponentDescription desc, boolean cleanup)
	{
		IComponentIdentifier aid = desc.getName();
		((IComponentManagementService)jcc.getServiceContainer().getService(IComponentManagementService.class))
			.getComponentAdapter(aid, new IResultListener()
			{
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
				}
				public void resultAvailable(Object result)
				{
					// HACK!!!
					// Hack!!!
//					final BDIInterpreter ip = ((ElementFlyweight)result).getInterpreter();
//					BDIInterpreter ip = (BDIInterpreter)((StandaloneAgentAdapter)result).getJadexAgent();
					
					ComanalyzerAdapter adapter = (ComanalyzerAdapter)adapters.remove(desc);
					((IComponentAdapter)result).removeToolAdapter(adapter);
					adapter.removeTool(ComanalyzerPlugin.this);
				}
			});
	}*/

	/**
	 * @return The messagefilter.
	 */
	public MessageFilter[] getMessageFilter()
	{
		return messagefilter;
	}

	/**
	 * @param filter The messagefilter to set.
	 */
	public void setMessageFilter(MessageFilter[] filter)
	{
		this.messagefilter = filter;
	}

	/**
	 * Removes the current messagefilter.
	 */
	public void removeMessageFilter()
	{
		this.messagefilter = new MessageFilter[]{MessageFilter.EMPTY};
	}

	/**
	 * Applies the current messagefilter to all messages.
	 */
	public void applyMessageFilter()
	{
		applyMessageFilter(messagelist.getMessages());
	}

	/**
	 * Applies the current messagefilter to a single message.
	 * 
	 * @param message
	 */
	protected void applyMessageFilter(Message message)
	{
		applyMessageFilter(new Message[]{message});
	}

	/**
	 * Applies the current messagefilter to the given messages and
	 * notifies tools about changed set of messages.
	 * Does not change the master list of messages.
	 * @param messages The messages to apply the filter to.
	 */
	protected void applyMessageFilter(Message[] messages)
	{
		List updated_components = new ArrayList();
		List updated_messages = new ArrayList();

		for(int i=0; i<messages.length; i++)
		{
			if(messages[i].applyFilter(messagefilter))
			{
				updated_messages.add(messages[i]);

				// apply filter to sender and receiver
				// to account for new visibility of the message
				Component sender = messages[i].getSender();
				Component receiver = messages[i].getReceiver();
				if(sender.applyFilter(agentfilter, true))
				{
					if(!updated_components.contains(sender))
					{
						updated_components.add(sender);
					}
				}
				if(receiver.applyFilter(agentfilter, true))
				{
					if(!updated_components.contains(receiver))
					{
						updated_components.add(receiver);
					}
				}
			}
		}


		
		componentlist.fireAgentsChanged((Component[])updated_components
			.toArray(new Component[updated_components.size()]));
		messagelist.fireMessagesChanged((Message[])updated_messages
			.toArray(new Message[updated_messages.size()]));
	}

	/**
	 * @return The agentfilter.
	 */
	public ComponentFilter[] getAgentFilter()
	{
		return agentfilter;
	}

	/**
	 * Sets a new agentfilter.
	 * @param filter The filter to set.
	 */
	public void setAgentFilter(ComponentFilter[] filter)
	{
		this.agentfilter = filter;
	}

	/**
	 * Remove all agent filter except for the standard filters that are
	 * accessible by the standard tool tab toolbar.
	 */
	public void removeAgentFilter()
	{
		List filters = new ArrayList();

		ComponentFilter[] afs = getAgentFilter();
		for(int i = 0; i < afs.length; i++)
		{
			if(afs[i].containsValue(Component.STATE, Component.STATE_DUMMY))
			{
				ComponentFilter af = new ComponentFilter();
				af.addValue(Component.STATE, Component.STATE_DUMMY);
				filters.add(afs[i]);
			}
			if(afs[i].containsValue(Component.STATE, Component.STATE_IGNORED))
			{
				ComponentFilter af = new ComponentFilter();
				af.addValue(Component.STATE, Component.STATE_IGNORED);
				filters.add(afs[i]);
			}
			if(afs[i].containsValue(Component.STATE, Component.STATE_DEAD))
			{
				ComponentFilter af = new ComponentFilter();
				af.addValue(Component.STATE, Component.STATE_DEAD);
				filters.add(afs[i]);
			}
			if(afs[i].containsValue(Component.MESSAGE_VISIBLE, Integer.valueOf(Component.NO_MESSAGES)))
			{
				ComponentFilter af = new ComponentFilter();
				af.addValue(Component.STATE, Integer.valueOf(Component.NO_MESSAGES));
				filters.add(afs[i]);
			}
		}
		this.agentfilter = ((ComponentFilter[])filters.toArray(new ComponentFilter[filters.size()]));

	}

	/**
	 * Applies the current agentfilter to all agents.
	 */
	public void applyAgentFilter()
	{
		applyAgentFilter(componentlist.getAgents());
	}

	/**
	 * Applies the current agentfilter to a single agent.
	 * @param agent The agent the filter applied to.
	 */
	protected void applyAgentFilter(Component agent)
	{
		applyAgentFilter(new Component[]{agent});
	}

	/**
	 * Applies the current agentfilter to the given agents.
	 * 
	 * @param agents The agents to apply the filter to.
	 */
	protected void applyAgentFilter(Component[] agents) {
		List updated_components = new ArrayList();
		List updated_messages = new ArrayList();

		// Hack!!!
		// apply with out zero message filter first
		for (int i = 0; i < agents.length; i++) {
			agents[i].applyFilter(agentfilter, false);	
			if (!agents[i].equals(Component.DUMMY_COMPONENT) && agents[i].getMessages().size() > 0) {
				if (agents[i].isVisible()) {
					Component.DUMMY_COMPONENT.getMessages().removeAll(agents[i].getMessages());
				} else {
					Component.DUMMY_COMPONENT.getMessages().addAll(agents[i].getMessages());

				}
			}			
		}	
		
		for (int i = 0; i < agents.length; i++) {
			agents[i].applyFilter(agentfilter, true);
				if (!agents[i].equals(Component.DUMMY_COMPONENT) && agents[i].getMessages().size() > 0) {
					if (agents[i].isVisible()) {
						Component.DUMMY_COMPONENT.getMessages().removeAll(agents[i].getMessages());
					} else {
						Component.DUMMY_COMPONENT.getMessages().addAll(agents[i].getMessages());

					}
				}
		}
		

		updated_components.addAll(componentlist.getList());		
		updated_messages.addAll(messagelist.getList());

		componentlist.fireAgentsChanged((Component[]) updated_components
				.toArray(new Component[updated_components.size()]));
		messagelist.fireMessagesChanged((Message[]) updated_messages
				.toArray(new Message[updated_messages.size()]));
	}

	/**
	 * @return The messagelist.
	 */
	public MessageList getMessageList()
	{
		return messagelist;
	}

	/**
	 * @return The array of messages.
	 */
	public Message[] getMessages()
	{
		return messagelist.getMessages();
	}

	/**
	 * @param messageNr The message number of the message to be returned.
	 * @return A specific message.
	 */
	public Message getMessage(int messageNr)
	{
		return (Message)messagelist.getList().get(messageNr);

	}

	/**
	 * @return The agentlist.
	 */
	public ComponentList getAgentList()
	{
		return componentlist;
	}

	/**
	 * @return The array of agents.
	 */
	public Component[] getAgents()
	{
		return componentlist.getAgents();
	}

	/**
	 * @return The paint map.
	 */
	public PaintMaps getPaintMaps()
	{
		return paintmaps;
	}

	/**
	 * Entry point for agent notifications, i.e. method is called from
	 * external thread. Hence, it is scheduled on swing thread.
	 * Iterates the list of message attributes and creates message objects. The
	 * new messages are checked against the existing list of messages to skip
	 * such that are already in the system (like a message was first recorded
	 * form the sender than the message recorded from the receiver is skipped)
	 * @param message_maps The list of attribute maps for creating messages.
	 */
	protected void addMessage(final IMessageAdapter message)//, String direction)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(isAddMessage(message))
				{
					final List messages_added = new ArrayList();
					IComponentIdentifier sid;
	
					// processing every message map
					MessageType mt = message.getMessageType();
	
					sid = (IComponentIdentifier)message.getValue(mt.getSenderIdentifier());
					Object recs = message.getValue(mt.getReceiverIdentifier());
					
					if(recs instanceof IComponentIdentifier)
					{
						IComponentIdentifier rid = (IComponentIdentifier)recs;
						if(!isDuplicate(message, rid))
						{
							Message msg = createMessage(message, sid, rid);
	//						System.out.println("Added: "+msg);
							messages_added.add(msg);
						}
					}
					else
					{
						Iterator rids = SReflect.getIterator(recs);
						while(rids.hasNext())
						{
							IComponentIdentifier rid = (IComponentIdentifier)rids.next();
							if(!isDuplicate(message, rid))
							{
								Message msg = createMessage(message, sid, rid);
		//						System.out.println("Added: "+msg);
								messages_added.add(msg);
							}
						}
					}
					
					// return if there are no messages to add
					if(!messages_added.isEmpty())
					{
						// delegate to refresh task or just fire the update
						if(sleep != REFRESHI && refresh_task != null)
						{
							if(sleep == REFRESHA)
							{
								// experimental auto refresh
								// the refresh task is initialized inside
								scheduleAutoRefresh();
							}
							refresh_task.fireMessagesAdded((Message[])messages_added.toArray(new Message[messages_added.size()]));
						}
						else
						{
							messagelist.fireMessagesAdded((Message[])messages_added.toArray(new Message[messages_added.size()]));
						}
					}
				}
			}
		});	
	}
	
	/**
	 * Checks if this message is already in messagelist (same id and same
	 * receiver)
	 * @param source The attribute map for the message.
	 * @return <code>true</code> if the message is already in the messagelist.
	 */
	protected boolean isDuplicate(IMessageAdapter newmsg, IComponentIdentifier rec)
	{
		boolean ret = false;
		Message[] messages = messagelist.getMessages();
		for(int i=0; i<messages.length && !ret; i++)
		{
			Object xid1 = messages[i].getParameter(Message.XID);
			Object xid2 = newmsg.getValue(Message.XID);
			if(xid1!=null && xid2!=null && xid1.equals(xid2))
			{
				IComponentIdentifier oldrec = (IComponentIdentifier)messages[i].getParameter(Message.RECEIVER);
				if(oldrec.equals(rec))
				{
					// set duration of existing message
					String start = (String)messages[i].getParameter(Message.DATE);
					if(start!=null)
					{
						long duration = clockservice.getTime() - Long.parseLong(start);
//						long duration = ((AgentControlCenter)getJCC()).getAgent().getTime() - new Long(start).longValue();
//						long duration = getJCC().getAgent().getTime() - start.getTime();
						messages[i].setDuration(duration);
					}
					
					// prevent including in messagelist
					ret = true;
				}
			}
		}
		return ret;
	}

	/**
	 * Creates a message object with the corresponding sender and receiver
	 * agents and adds them to the message and agentlist.
	 * 
	 * @param source The message attribute map.
	 * @param sid The sender agent id.
	 * @param rid The receivers agent id.
	 * @return
	 */
	protected Message createMessage(IMessageAdapter msg, IComponentIdentifier sid, IComponentIdentifier rid)//, String direction)
	{
		final Message message = new Message(msg, messagenr++, rid);
		message.applyFilter(messagefilter);

		// add to messagelist
		messagelist.addMessage(message);

		// add sender to agentlist/tree if not present
		Component sender = componentlist.getAgent(sid);
		if(sender == null)
		{
			// add to agent tree table
			sender = new Component(new CMSComponentDescription(sid, "unknown-component-type", false, false, false, false, false, null, null, null, null, -1, null, null, false));
			sender.setState(Component.STATE_DEAD);
			sender.addMessage(message);
			sender.applyFilter(agentfilter, true);
			componentlist.addAgent(sender);
		}
		else
		{
			// apply filter with new message
			sender.addMessage(message);
			applyAgentFilter(sender);
		}

		// add receiver to agentlist/tree if not present
		Component receiver = componentlist.getAgent(rid);
		if(receiver == null)
		{
			receiver = new Component(new CMSComponentDescription(rid, "unknown-component-type", false, false, false, false, false, null, null, null, null, -1, null, null, false));
			receiver.setState(Component.STATE_DEAD);
			receiver.addMessage(message);
			receiver.applyFilter(agentfilter, true);
			componentlist.addAgent(receiver);
		}
		else
		{
			// apply filter with new message
			receiver.addMessage(message);
			applyAgentFilter(receiver);
		}

		// save sender and receiver to message
		message.setSender(sender);
		message.setReceiver(receiver);

		// create paint map
		paintmaps.createColor(message);

		return message;
	}

	/**
	 * Schedule the refresh time by retrieving the last duration for the update
	 * of the tools.
	 * 
	 * @experimental
	 */
	protected void scheduleAutoRefresh()
	{
		// factor for calculating the period
		final int DURATION_FACTOR = 5;
		// number of executions to calc the average duration from
		final int AVERAGE_COUNT = 3;
		// period tolerance needed for new task (between calculated period and
		// period from current task)
		final double PERIOD_TOLERANCE = 0.1;
		// limit at which the tooltabs are beeing deactivated
		final long REFRESH_LIMIT = REFRESH5;

		// adjust period according to the duration of last execution(s)
		// long period = (long) (refreshtask.lastMessageDuration()!=0 ?
		// refreshtask.lastMessageDuration()* 1000 : refreshtask.getPeriod());
		// long period = refreshtask.lastExecutionDuration()!=0 ?
		// refreshtask.lastExecutionDuration()* 5 : refreshtask.getPeriod();
		long period = 0;
		if(refresh_task.getAverageExecutionDuration(AVERAGE_COUNT) != 0)
		{
			period = refresh_task.getAverageExecutionDuration(AVERAGE_COUNT) * DURATION_FACTOR;
		}
		else
		{
			period = refresh_task.getPeriod();
		}

		// dont schedule a task with a period lower than 1s
		period = period > REFRESH1 ? period : REFRESH1;

		// if period succeeds limit, deactivate tooltabs
		if(period > REFRESH_LIMIT)
		{
			System.err.println("Cancel timer task with period : " + period);

			ToolTab[] tools = tpanel.tools;
			for(int i = 0; i < tools.length; i++)
			{
				tools[i].setActive(false);
			}
			getJCC().setStatusText("Refresh Rate: Automatic OFF (Auto Sensor)");
		}

		// dont schedule a task with same period or if limit succeeded
		if(!(period > REFRESH_LIMIT) && !(period == refresh_task.getPeriod()))
		{
			// if (!(period>REFRESH5) && !(period == refresh_task.getPeriod()))
			// {

			// if period of current task differs from calculated period
			// schedule a new task with calculated period
			if((double)(refresh_task.getPeriod() / Math.abs(period - refresh_task.getPeriod())) > PERIOD_TOLERANCE)
			{
				System.err.println("New timer task with period : " + period);
				refresh_task.cancel();
				// tasks cant be rescheduled. create a new one and pass
				// durations
				refresh_task = new RefreshTask(this, period, refresh_task.getDurations());
				timer.schedule((TimerTask)refresh_task, period, period);

				DecimalFormat df = new DecimalFormat("0.0");
				String rate = df.format((double)period / 1000);
				getJCC().setStatusText("Refresh Rate: " + rate + " s (Auto Sensor)");
			}
		}
	}

	// -------- Actions --------

	final AbstractAction IGNORE_ALL = new AbstractAction("Ignore All")
	{
		public void actionPerformed(ActionEvent e)
		{
			List update = new ArrayList();
			Component[] agents = componentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				if(agents[i].getState().equals(Component.STATE_OBSERVED))
				{
					agents[i].setState(Component.STATE_IGNORED);
					update.add(agents[i]);
					observed.remove(agents[i].getDescription().getName());
					comptree.getModel().fireNodeChanged(comptree.getModel().getNode(agents[i].getDescription().getName()));
				}
			}

			removeMessageListener(update);
			applyAgentFilter();			
		}
	};

	/** Observe all agents */
	final AbstractAction OBSERVE_ALL = new AbstractAction("Observe All")
	{
		public void actionPerformed(ActionEvent e)
		{
			List update = new ArrayList();
			Component[] agents = componentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				if(agents[i].getState().equals(Component.STATE_IGNORED))
				{
					agents[i].setState(Component.STATE_OBSERVED);
					update.add(agents[i]);
					observed.add(agents[i].getDescription().getName());
					comptree.getModel().fireNodeChanged(comptree.getModel().getNode(agents[i].getDescription().getName()));
				}
			}
			addMessageListener(update);
//			applyAgentFilter((Agent[])update.toArray(new Agent[update.size()]));
			applyAgentFilter();				
		}
	};

	/** Observe all new agent */
	final AbstractAction OBSERVE_ALL_NEW = new AbstractAction("Observe All New")
	{
		public void actionPerformed(ActionEvent e)
		{
			observe_all_new = ((JCheckBoxMenuItem)e.getSource()).isSelected();
//			System.out.println("Setting observe all new to: "+observe_all_new);
		}
	};

	/** Removes dead agents including related messages */
	final AbstractAction REMOVE_DEAD = new AbstractAction("Remove Dead")
	{
		public void actionPerformed(ActionEvent e)
		{
			Component[] agents = componentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				if(agents[i].getState().equals(Component.STATE_DEAD))
				{
					// remove messages of dead agent from messagelist
					messagelist.removeMessages((Message[])agents[i].getMessages().toArray(new Message[0]));
					messagelist.fireMessagesRemoved((Message[])agents[i].getMessages().toArray(new Message[0]));
					// remove dead agent from agentlist
					componentlist.removeAgent(agents[i]);
				}
			}
		}
	};

	/** Removes all messages */
	final AbstractAction REMOVE_ALL_MESSAGES = new AbstractAction("Remove All")
	{
		public void actionPerformed(ActionEvent e)
		{
			// remove all messages from messagelist
			messagelist.removeAllMessages();
			// remove messages from agents
			List update = new ArrayList();
			Component[] agents = componentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				agents[i].removeAllMessages();
				update.add(agents[i]);
			}
//			applyAgentFilter((Agent[])update.toArray(new Agent[update.size()]));
			applyAgentFilter();				
		}
	};

	/** Removes all messages and dead agents */
	final AbstractAction REMOVE_ALL = new AbstractAction("Remove All Messages And Dead Agents", icons.getIcon("clear"))
	{
		public void actionPerformed(ActionEvent e)
		{
			// remove all messages from messagelist
			messagenr = 0;
			messagelist.removeAllMessages();
			// remove messages from agents
			Component[] agents = componentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				agents[i].removeAllMessages();
				// remove dead agents from agentlist
				if(agents[i].getState().equals(Component.STATE_DEAD))
				{
					componentlist.removeAgent(agents[i]);
				}
			}
		}
	};

	/** Startup default for the table */
	final AbstractAction ENABLE_TABLE = new AbstractAction("Enable Table")
	{
		public void actionPerformed(ActionEvent e)
		{
			table.setActive(((JCheckBoxMenuItem)e.getSource()).isSelected());
		}
	};

	/** Startup default for the diagram */
	final AbstractAction ENABLE_DIAGRAM = new AbstractAction("Enable Diagram")
	{
		public void actionPerformed(ActionEvent e)
		{
			diagram.setActive(((JCheckBoxMenuItem)e.getSource()).isSelected());
		}
	};

	/** Startup default for the graph */
	final AbstractAction ENABLE_GRAPH = new AbstractAction("Enable Graph")
	{
		public void actionPerformed(ActionEvent e)
		{
			graph.setActive(((JCheckBoxMenuItem)e.getSource()).isSelected());
		}
	};

	/** Startup default for the chart */
	final AbstractAction ENABLE_CHART = new AbstractAction("Enable Chart")
	{
		public void actionPerformed(ActionEvent e)
		{
			chart.setActive(((JCheckBoxMenuItem)e.getSource()).isSelected());
		}
	};

	/** Start observing an agent */
	final AbstractAction START_OBSERVING = new AbstractAction("Observe Component", icons.getIcon("introspect_agent"))
	{
		public void actionPerformed(ActionEvent e)
		{
			List	update	= new ArrayList();
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(paths[i].getLastPathComponent() instanceof IActiveComponentTreeNode)
				{
					IActiveComponentTreeNode node = (IActiveComponentTreeNode)paths[i].getLastPathComponent();
					IComponentDescription desc = node.getDescription();
					observed.add(desc.getName());
					Component agent = componentlist.getAgent(desc.getName());
					agent.setState(Component.STATE_OBSERVED);
					update.add(agent);
					applyAgentFilter(agent);
					comptree.getModel().fireNodeChanged(comptree.getModel().getNode(desc.getName()));
				}
			}
			addMessageListener(update);
		}
	};

	/** Stop observing an agent */
	final AbstractAction STOP_OBSERVING = new AbstractAction("Ignore Component", icons.getIcon("close_comanalyzer"))
	{
		public void actionPerformed(ActionEvent e)
		{
			List	update	= new ArrayList();
			TreePath[]	paths	= comptree.getTree().getSelectionPaths();
			for(int i=0; paths!=null && i<paths.length; i++)
			{
				if(paths[i].getLastPathComponent() instanceof IActiveComponentTreeNode)
				{
					IActiveComponentTreeNode node = (IActiveComponentTreeNode)paths[i].getLastPathComponent();
					IComponentDescription desc = node.getDescription();
					observed.remove(desc.getName());
					Component agent = componentlist.getAgent(desc.getName());
					agent.setState(Component.STATE_IGNORED);
					update.add(agent);
					applyAgentFilter(agent);
					comptree.getModel().fireNodeChanged(comptree.getModel().getNode(desc.getName()));
				}
			}
			removeMessageListener(update);
		}
	};

	/** Save messages to file */
	final AbstractAction SAVE_MESSAGES = new AbstractAction("Save Messages To file", icons.getIcon("save"))
	{
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser fileDialog = new JFileChooser();
			int returnVal = fileDialog.showSaveDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				final String fileName = fileDialog.getSelectedFile().getAbsolutePath();
//				List message_maps = new ArrayList();
//				Message[] messages = messagelist.getMessages();
//				for(int i = 0; i < messages.length; i++)
//				{
//					message_maps.add(messages[i].getParameters());
//				}

//				SServiceProvider.getService(jcc.getJCCAccess().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(comptree)
//				{
//					public void customResultAvailable(Object result)
//					{
//						ClassLoader cl = ((ILibraryService)result).getClassLoader();
						
						// todo: what is the right classloader here?
						getJCC().getClassLoader(null).addResultListener(new SwingDefaultResultListener<ClassLoader>()
						{
							public void customResultAvailable(ClassLoader cl)
							{
								String xml = JavaWriter.objectToXML(new Object[]{componentlist.getAgents(), messagelist.getMessages()}, cl);

								byte buffer[] = xml.getBytes();
								File f = new File(fileName);
								FileOutputStream out = null;

								try
								{
									out = new FileOutputStream(f);
									out.write(buffer);
								}
								catch(FileNotFoundException e1)
								{
									e1.printStackTrace();
								}
								catch(IOException e1)
								{
									e1.printStackTrace();
								}
								finally
								{
									try
									{
										if(out != null)
											out.close();
									}
									catch(IOException e1)
									{
									}
								}
							}
						});
//					}
//				});		
				
			}
		}
	};

	/** Load messages from file */
	final AbstractAction LOAD_MESSAGES = new AbstractAction("Load Messages From File", icons.getIcon("load"))
	{
		public void actionPerformed(ActionEvent e)
		{
			String xml = null;
			JFileChooser fileDialog = new JFileChooser();

			int returnVal = fileDialog.showOpenDialog(null);
			if(returnVal != JFileChooser.APPROVE_OPTION)
				return;

			String fileName = fileDialog.getSelectedFile().getAbsolutePath();

			File f = new File(fileName);
			FileInputStream in = null;

			try
			{
				in = new FileInputStream(f);

				byte buffer[] = new byte[(int)f.length()];
				int len = in.read(buffer);
				xml = new String(buffer, 0, len);

			}
			catch(FileNotFoundException e1)
			{
				e1.printStackTrace();
			}
			catch(IOException e1)
			{
				e1.printStackTrace();
			}
			finally
			{
				try
				{
					if(in != null)
						in.close();
				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}

			if(xml != null)
			{
//				List message_maps = new ArrayList();
//				message_maps = (List)Nuggets.objectFromXML(xml);
//				
//				if(message_maps!=null)
//				{
//					for(int i=0; i<message_maps.size(); i++)
//					{
//						Map mp = (Map)message_maps.get(i);
//						addMessage(m);
//					}
//				}
				final String sxml = xml;
//				SServiceProvider.getService(jcc.getJCCAccess().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(comptree)
//				{
//					public void customResultAvailable(Object result)
//					{
						// todo: what is the right classloader here?
						getJCC().getClassLoader(null).addResultListener(new SwingDefaultResultListener<ClassLoader>()
						{
							public void customResultAvailable(ClassLoader cl)
							{
								//ClassLoader cl = ((ILibraryService)result).getClassLoader();
								
								Object[] stored = (Object[])JavaReader.objectFromXML(sxml, cl);
								
								componentlist.removeAllAgents();
								Component[] agents = (Component[])stored[0];
								for(int i=0; i<agents.length; i++)
									componentlist.addAgent(agents[i]);
								
								messagelist.removeAllMessages();
								Message[] messages = (Message[])stored[1];
								for(int i=0; i<messages.length; i++)
									messagelist.addMessage(messages[i]);
								
								messagelist.fireMessagesAdded(messages);
							}
						});
//					}
//				});
			}
		}
	};

	/** Refresh immediately */
	final AbstractAction REFRESH_ATONCE = new AbstractAction("Immediately")
	{
		public void actionPerformed(ActionEvent e)
		{
			sleep = REFRESHI;
			if(refresh_task != null)
			{ // && refreshtask.isRunning()) {
				refresh_task.cancel();
			}
			getJCC().setStatusText("Refresh Rate: Immediately");
		}
	};

	/** Refresh 1s */
	final AbstractAction REFRESH_1S = new AbstractAction("Every 1 s")
	{
		public void actionPerformed(ActionEvent e)
		{
			sleep = REFRESH1;
			if(refresh_task != null)
			{ // && refreshtask.isRunning()) {
				refresh_task.cancel();
			}
			refresh_task = new RefreshTask(ComanalyzerPlugin.this, REFRESH1);
			timer.schedule(refresh_task, REFRESH1, REFRESH1); // subsequent
			// rate
			getJCC().setStatusText("Refresh Rate: 1 s");
		}
	};

	/** Refresh 5s */
	final AbstractAction REFRESH_5S = new AbstractAction("Every 5 s")
	{
		public void actionPerformed(ActionEvent e)
		{
			sleep = REFRESH5;
			if(refresh_task != null)
			{ // && refreshtask.isRunning()) {
				refresh_task.cancel();
			}
			refresh_task = new RefreshTask(ComanalyzerPlugin.this, REFRESH5);
			timer.schedule(refresh_task, REFRESH5, REFRESH5); // subsequent
			// rate
			getJCC().setStatusText("Refresh Rate: 5 s");
		}
	};

	/** Auto Refresh (experimental) */
	final AbstractAction REFRESH_AUTO = new AbstractAction("Auto Sensor")
	{
		public void actionPerformed(ActionEvent e)
		{
			sleep = REFRESHA;
			if(refresh_task != null)
			{ // && refreshtask.isRunning()) {
				// period=refreshtask.averageExecutionDuration();
				refresh_task.cancel();
			}
			refresh_task = new RefreshTask(ComanalyzerPlugin.this, REFRESH1);
			timer.schedule(refresh_task, REFRESH1, REFRESH1); // subsequent
			// rate
			getJCC().setStatusText("Refresh Rate: 1 s (Auto Sensor)");

		}
	};
	
	/**
	 *  Invoked when a message event has been received.
	 *  @param msg The message adapter.
	 */
	public boolean isAddMessage(IMessageAdapter msg)
	{
		MessageType mt = msg.getMessageType();
		String si = mt.getSenderIdentifier();
		IComponentIdentifier s = (IComponentIdentifier)msg.getValue(si);
		
		boolean add = false;
		if(observe_all_new || observed.contains(s))
		{
			add = true;
		}
		else
		{
			String ris = mt.getReceiverIdentifier();
			Object rs = msg.getValue(ris);
			if(rs instanceof IComponentIdentifier)
			{
				if(observed.contains(rs))
					add = true;
			}
			else if(rs!=null)
			{
				for(Iterator it=SReflect.getIterator(rs); it.hasNext() && !add; )
				{
					if(observed.contains(it.next()))
						add = true;
				}
			}
		}
		
		return add;
	}
	
	/**
	 *  Update message listeners after agents have been added.
	 */	
	protected void addMessageListener(final List added)
	{
		final Map	services	= new HashMap();
		final CounterResultListener	crl	= new CounterResultListener(added.size(), true, new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				for(Iterator it=services.keySet().iterator(); it.hasNext(); )
				{
					IServiceIdentifier	id	= (IServiceIdentifier)it.next();
					Object[]	newentry	= (Object[])services.get(id);
					Object[]	oldentry	= (Object[])msgservices.get(id);
					
					// Do nothing if both sets are the same (e.g. when updates happen asynchronously)
					if(oldentry==null || !oldentry[1].equals(newentry[1]))
					{
						if(oldentry!=null)
						{
							((IMessageService)oldentry[0]).removeMessageListener(listener);
						}
						msgservices.put(id, newentry);
//						System.out.println("Listening on "+id+", "+newentry[1]);
						((IMessageService)newentry[0]).addMessageListener(listener, createMessageFilter((Set)newentry[1]));
					}
				}
			}
		});
		
		SServiceProvider.getService(jcc.getJCCAccess(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				for(int i=0; i<added.size(); i++)
				{
					final Future	fut	= new Future();
					fut.addResultListener(crl);
					Component	comp	= (Component)added.get(i);
					final IComponentIdentifier	cid	= comp.getDescription().getName();
					cms.getExternalAccess(cid).addResultListener(new SwingDelegationResultListener(fut)
					{
						public void customResultAvailable(Object result)
						{
							IExternalAccess	ea	= (IExternalAccess)result;
							SServiceProvider.getService(ea, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
								.addResultListener(new SwingDelegationResultListener(fut)
							{
								public void customResultAvailable(Object result)
								{
									IMessageService	ms	= (IMessageService)result;
									Object[]	newentry;
									if(!services.containsKey(ms.getServiceIdentifier()))
									{
										if(msgservices.containsKey(ms.getServiceIdentifier()))
										{
											Object[]	oldentry	= (Object[])msgservices.get(ms.getServiceIdentifier());
											newentry	= new Object[]{ms, new HashSet((Collection)oldentry[1])};
										}
										else
										{
											newentry	= new Object[]{ms, new HashSet()};
										}
										services.put(ms.getServiceIdentifier(), newentry);
									}
									else
									{
										newentry	= (Object[])services.get(ms.getServiceIdentifier());
									}
									((Set)newentry[1]).add(cid);
									fut.setResult(null);
								}
							});
						}	
					});
				}
			}
		});
	}

	/**
	 *  Update message listeners after agents have been removed.
	 */	
	protected void removeMessageListener(List removed)
	{
		Map services	= new HashMap();
		
		for(int i=0; i<removed.size(); i++)
		{
			IComponentIdentifier	cid	= ((Component)removed.get(i)).getDescription().getName();
			boolean	found	= false;
			for(Iterator it=msgservices.values().iterator(); !found && it.hasNext(); )
			{
				Object[]	oldentry	= (Object[])it.next();
				if(((Set)oldentry[1]).contains(cid))
				{
					found	= true;
					IServiceIdentifier	id	= ((IMessageService)oldentry[0]).getServiceIdentifier();
					Object[]	newentry	= (Object[])services.get(id);
					if(newentry==null)
					{
						newentry	= new Object[]{oldentry[0], new HashSet((Collection)oldentry[1])};
						services.put(id, newentry);
					}
					((Set)newentry[1]).remove(cid);
				}
			}
		}

		for(Iterator it=services.keySet().iterator(); it.hasNext(); )
		{
			IServiceIdentifier	id	= (IServiceIdentifier)it.next();
			Object[]	newentry	= (Object[])services.get(id);
			Object[]	oldentry	= (Object[])msgservices.get(id);
			// Check if registration still present (may be removed by asynchronous updates?)
			if(oldentry!=null)
			{
				((IMessageService)oldentry[0]).removeMessageListener(listener);
				msgservices.remove(id);
//				System.out.println("Listening on "+id+", "+newentry[1]);
			}
			if(!((Set)newentry[1]).isEmpty())
			{
				msgservices.put(id, newentry);
				((IMessageService)newentry[0]).addMessageListener(listener, createMessageFilter((Set)newentry[1]));
			}
		}
	}

	/**
	 *  Create a transferable filter for a remote message listener.
	 */
	protected IFilter createMessageFilter(final Set agents)
	{
		return new IFilter()
		{
			@Classname("msgfilter")
			public boolean filter(Object obj)
			{
				boolean	ret	= true;
				IMessageAdapter	msg	= (IMessageAdapter)obj;
				Object	sender	= msg.getValue(msg.getMessageType().getSenderIdentifier());
				if(!agents.contains(sender))
				{
					Object	recs	= msg.getValue(msg.getMessageType().getReceiverIdentifier());
					if(!agents.contains(recs))
					{
						ret	= false;
						if(SReflect.isIterable(recs))
						{
							for(Iterator it=SReflect.getIterator(recs); !ret && it.hasNext(); )
							{
								ret	= agents.contains(it.next());
							}
						}
					}
				}
				return ret;
			}
		};
	}

	//-------- inner classes --------

	/**
	 * JButton for ControlCenterWindow toolbar TODO Move to AbstractJCCPlugin
	 */
	private class JMenuButton extends JButton
	{
		public JMenuButton(Action a)
		{
			super(a);
			setBorder(null);
			setToolTipText(getText());
			setText(null);
			setEnabled(true);
		}
	}
}
