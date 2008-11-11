package jadex.tools.comanalyzer;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.impl.ElementFlyweight;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.ILibraryService;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.MessageType;
import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.commons.SGUI;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IResultListener;
import jadex.tools.comanalyzer.chart.ChartPanel;
import jadex.tools.comanalyzer.diagram.DiagramPanel;
import jadex.tools.comanalyzer.graph.GraphPanel;
import jadex.tools.comanalyzer.table.TablePanel;
import jadex.tools.common.AgentTreeTable;
import jadex.tools.common.GuiProperties;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;
import jadex.tools.common.jtreetable.TreeTableNodeType;
import jadex.tools.common.plugin.AbstractJCCPlugin;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.tree.TreePath;

import nuggets.Nuggets;


/**
 * The comanalyzer plugin.
 */
public class ComanalyzerPlugin extends AbstractJCCPlugin implements jadex.tools.common.plugin.IAgentListListener
{
	//-------- constants --------

	// todo: 
	/** The system event types. */
	protected static final String[] TYPES = new String[]{"messageeventsent", "messageeventreceived"};

	/** The icon paths */
	protected static final String COMANALYZER_IMAGES = "/jadex/tools/comanalyzer/images/";

	protected static final String COMMON_IMAGES = "/jadex/tools/common/images/";

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]{"comanalyzer", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "new_comanalyzer.png"), "comanalyzer_sel",
			SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "new_comanalyzer_sel.png"), "agent_ignored", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "new_agent.gif"),
			"introspect_agent", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "new_comanalyzer.png"), "close_comanalyzer",
			SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "close_comanalyzer.png"), "agent_introspected",
			SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "new_agent_introspected.gif"), "agent_dead", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "new_agent_gray.gif"),
			"agent_unknown", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "agent_unknown.png"), "agent_dummy",
			SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "agent_dummy.png"), "load", SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "load.png"), "save",
			SGUI.makeIcon(ComanalyzerPlugin.class, COMANALYZER_IMAGES + "save2.png"), "clear", SGUI.makeIcon(ToolTab.class, COMMON_IMAGES + "litter2.png"),});

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
	protected AgentTreeTable agents;

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
	protected AgentList agentlist;

	/** The global list of recorded messages. */
	protected MessageList messagelist;

	/** The global messagefilter */
	protected MessageFilter[] messagefilter;

	/** The global agentfilter */
	protected AgentFilter[] agentfilter;

	/** Observe all new agents. */
	protected boolean observe_all_new;

	/** The message number counter for loaded and received messages */
	private int messagenr;

	/** The map of shared colors for message and agent representation.. */
	protected PaintMaps paintmaps;
	
	/** The map of registered agent adapters. */
	protected Map adapters;
	
	// -------- constructors --------

	/**
	 * Create a new comanalyzer plugin.
	 */
	public ComanalyzerPlugin()
	{
		this.messagenr = 1;
		this.listeners = new HashMap();
		this.agentlist = new AgentList();
		this.agentfilter = new AgentFilter[]{AgentFilter.EMPTY};
		this.messagelist = new MessageList();
		this.messagefilter = new MessageFilter[]{MessageFilter.EMPTY};
		this.timer = new Timer();
		this.adapters = new HashMap();
		this.paintmaps = new PaintMaps();
	}

	// -------- IControlCenterPlugin interface --------

	/**
	 * Get plugin properties to be saved in a project.
	 */
	public Properties getProperties()
	{
		Properties	props	= new Properties();
		for(int i=0; i<checkboxes.length; i++)
		{
//			System.out.println(""+checkboxes[i].getText()+" "+checkboxes[i].isSelected());
			props.addProperty(new Property(checkboxes[i].getText(), ""+checkboxes[i].isSelected()));
		}
		return props;
	}
	

	/**
	 * Set plugin properties loaded from a project.
	 */
	public void setProperties(Properties props)
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

	}

	/**
	 * @return "Comanalyzer"
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#getName()
	 */
	public String getName()
	{
		return "Comanalyzer";
	}

	/**
	 * @return The icon of comanalyzer.
	 * @see jadex.tools.common.plugin.IControlCenterPlugin#getToolIcon()
	 */
	public Icon getToolIcon(boolean selected)
	{
		return selected ? icons.getIcon("comanalyzer_sel") : icons.getIcon("comanalyzer");
	}

	/**
	 * @return The help id of the perspective.
	 * @see jadex.tools.jcc.AbstractJCCPlugin#getHelpID()
	 */
	public String getHelpID()
	{
		return "tools.comanalyzer";
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

		agents = new AgentTreeTable(null);
		agents.setMinimumSize(new Dimension(0, 0));
		split.add(agents);
		agents.getTreetable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Change agent node type to enable introspected icon for agents.
		agents.addNodeType(new TreeTableNodeType(AgentTreeTable.NODE_AGENT, new Icon[0], new String[]{"name", "address"}, new String[]{"Name", "Address"})
		{
			public Icon selectIcon(Object value)
			{
				Icon ret;
				IAMSAgentDescription ad = (IAMSAgentDescription)((DefaultTreeTableNode)value).getUserObject();
				Agent agent = agentlist.getAgent(ad.getName());

				if(agent.getState().equals(Agent.STATE_OBSERVED))
				{
					ret = ComanalyzerPlugin.icons.getIcon("agent_introspected");
				}
				else if(agent.getState().equals(Agent.STATE_IGNORED))
				{
					ret = ComanalyzerPlugin.icons.getIcon("agent_ignored");
				}
				else if(agent.getState().equals(Agent.STATE_DEAD))
				{
					ret = ComanalyzerPlugin.icons.getIcon("agent_dead");
				}
				else if(agent.getState().equals(Agent.STATE_UNKNOWN))
				{
					ret = ComanalyzerPlugin.icons.getIcon("agent_unknown");
				}
				else if(agent.getState().equals(Agent.STATE_DUMMY))
				{
					ret = ComanalyzerPlugin.icons.getIcon("agent_dummy");
				}
				else
				{
					// default
					ret = AgentTreeTable.icons.getIcon(AgentTreeTable.NODE_AGENT);
				}

				return ret;
			}
		});
		agents.getNodeType(AgentTreeTable.NODE_AGENT).addPopupAction(START_OBSERVING);
		agents.getNodeType(AgentTreeTable.NODE_AGENT).addPopupAction(STOP_OBSERVING);
		agents.getTreetable().getSelectionModel().setSelectionInterval(0, 0);
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
			agentlist.addListener(tools[i]);
			messagelist.addListener(tools[i]);
		}

		tpanel = new ToolPanel(tools);

		GuiProperties.setupHelp(tpanel, "tools.comanalyzer");
		split.add(tpanel);

		agents.getTreetable().addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() == 2)
				{
					if(START_OBSERVING.isEnabled())
						START_OBSERVING.actionPerformed(null);
					else if(STOP_OBSERVING.isEnabled())
						STOP_OBSERVING.actionPerformed(null);
				}

			}
		});

		jcc.addAgentListListener(this);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				agents.adjustColumnWidths();
			}
		});

		// add dummy agent to agentlist
		Agent dummy = Agent.DUMMY_AGENT;
		applyAgentFilter(dummy);
		agentlist.addAgent(dummy);

		return split;
	}

	// -------- IAgentListListener interface --------

	/**
	 * Remove listeners and set agent state.
	 * @param ad The agent description of the agent that has died.
	 */
	public void agentDied(final IAMSAgentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{

				// remove listener if agent is observed
				if(listeners.containsKey(ad))
				{
					removeAgentListener(ad, false);
				}
				// set agent state and update agent
				Agent agent = (Agent)agentlist.getAgent(ad.getName());
				agent.setState(Agent.STATE_DEAD);
				applyAgentFilter(agent);
				// update agenttree
				agents.updateAgent(ad);
			}
		});
	}

	/**
	 * Add the agent to the agentlist
	 * @param ad The agent description of the agent that was born.
	 */
	public void agentBorn(final IAMSAgentDescription ad)
	{
		// Update components on awt thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(observe_all_new)
				{
					addAgentListener(ad);
				}

				boolean updateAgent = true;
				Agent agent = agentlist.getAgent(ad.getName());
				if(agent == null)
				{
					agent = new Agent(ad.getName());
					updateAgent = false;
				}
				agent.setState(observe_all_new ? Agent.STATE_OBSERVED : Agent.STATE_IGNORED);

				if(updateAgent)
				{
					applyAgentFilter(agent);
					agents.updateAgent(ad);
				}
				else
				{
					applyAgentFilter(agent);
					agentlist.addAgent(agent);
					agents.addAgent(ad);
				}
			}
		});
	}

	/**
	 * @param ad The agent description of the agent that has changed.
	 */
	public void agentChanged(final IAMSAgentDescription ad)
	{
		// NOP
	}

	// -------- methods --------

	/**
	 * Creates a listener for the agent to obtain internal agent events.
	 * @param desc The agentdescription.
	 */
	public void addAgentListener(final IAMSAgentDescription desc)
	{
		IAgentIdentifier aid = desc.getName();
		((IAMS)getJCC().getAgent().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE))
			.getExternalAccess(aid, new IResultListener()
			{
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
				}
				public void resultAvailable(Object result)
				{
					// HACK!!!
					// Hack!!!
					final BDIInterpreter ip = ((ElementFlyweight)result).getInterpreter();
//					BDIInterpreter ip = (BDIInterpreter)((IAgentAdapter)result).getJadexAgent();
					ComanalyzerAdapter adapter	= (ComanalyzerAdapter)((BDIInterpreter)ip).getToolAdapter(ComanalyzerAdapter.class);
					adapters.put(desc, adapter);
					adapter.addTool(ComanalyzerPlugin.this);
				}
			});
	}

	/**
	 * Removes the listener for the agent.
	 * 
	 * @param desc The agentdiscriotion.
	 * @param cleanup <code>true</code> if the listener should be removed from
	 * the agent. (e.g. on agent death it isnt nessesary)
	 */
	public void removeAgentListener(final IAMSAgentDescription desc, boolean cleanup)
	{
		IAgentIdentifier aid = desc.getName();
		((IAMS)getJCC().getAgent().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE))
			.getExternalAccess(aid, new IResultListener()
			{
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
				}
				public void resultAvailable(Object result)
				{
					// HACK!!!
					// Hack!!!
					final BDIInterpreter ip = ((ElementFlyweight)result).getInterpreter();
//					BDIInterpreter ip = (BDIInterpreter)((StandaloneAgentAdapter)result).getJadexAgent();
					ComanalyzerAdapter adapter = (ComanalyzerAdapter)adapters.remove(desc);
					ip.removeToolAdapter(adapter);
					adapter.removeTool(ComanalyzerPlugin.this);
				}
			});
	}

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
		List updated_agents = new ArrayList();
		List updated_messages = new ArrayList();

		for(int i=0; i<messages.length; i++)
		{
			if(messages[i].applyFilter(messagefilter))
			{
				updated_messages.add(messages[i]);

				// apply filter to sender and receiver
				// to account for new visibility of the message
				Agent sender = messages[i].getSender();
				Agent receiver = messages[i].getReceiver();
				if(sender.applyFilter(agentfilter))
				{
					if(!updated_agents.contains(sender))
					{
						updated_agents.add(sender);
					}
				}
				if(receiver.applyFilter(agentfilter))
				{
					if(!updated_agents.contains(receiver))
					{
						updated_agents.add(receiver);
					}
				}
			}
		}

		agentlist.fireAgentsChanged((Agent[])updated_agents
			.toArray(new Agent[updated_agents.size()]));
		messagelist.fireMessagesChanged((Message[])updated_messages
			.toArray(new Message[updated_messages.size()]));
	}

	/**
	 * @return The agentfilter.
	 */
	public AgentFilter[] getAgentFilter()
	{
		return agentfilter;
	}

	/**
	 * Sets a new agentfilter.
	 * @param filter The filter to set.
	 */
	public void setAgentFilter(AgentFilter[] filter)
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

		AgentFilter[] afs = getAgentFilter();
		for(int i = 0; i < afs.length; i++)
		{
			if(afs[i].containsValue(Agent.STATE, Agent.STATE_DUMMY))
			{
				AgentFilter af = new AgentFilter();
				af.addValue(Agent.STATE, Agent.STATE_DUMMY);
				filters.add(afs[i]);
			}
			if(afs[i].containsValue(Agent.STATE, Agent.STATE_IGNORED))
			{
				AgentFilter af = new AgentFilter();
				af.addValue(Agent.STATE, Agent.STATE_IGNORED);
				filters.add(afs[i]);
			}
			if(afs[i].containsValue(Agent.STATE, Agent.STATE_DEAD))
			{
				AgentFilter af = new AgentFilter();
				af.addValue(Agent.STATE, Agent.STATE_DEAD);
				filters.add(afs[i]);
			}
			if(afs[i].containsValue(Agent.MESSAGE_VISIBLE, new Integer(Agent.NO_MESSAGES)))
			{
				AgentFilter af = new AgentFilter();
				af.addValue(Agent.STATE, new Integer(Agent.NO_MESSAGES));
				filters.add(afs[i]);
			}
		}
		this.agentfilter = ((AgentFilter[])filters.toArray(new AgentFilter[filters.size()]));

	}

	/**
	 * Applies the current agentfilter to all agents.
	 */
	public void applyAgentFilter()
	{
		applyAgentFilter(agentlist.getAgents());
	}

	/**
	 * Applies the current agentfilter to a single agent.
	 * @param agent The agent the filter applied to.
	 */
	protected void applyAgentFilter(Agent agent)
	{
		applyAgentFilter(new Agent[]{agent});
	}

	/**
	 * Applies the current agentfilter to the given agents.
	 * @param agents The agents to apply the filter to.
	 */
	protected void applyAgentFilter(Agent[] agents)
	{
		boolean addDummy = false;
		List updated_agents = new ArrayList();
		List updated_messages = new ArrayList();

		for(int i = 0; i < agents.length; i++)
		{
			if(agents[i].applyFilter(agentfilter))
			{
				updated_agents.add(agents[i]);

				// add or remove agents messages from the dummy's messageslist
				// and fire update for the messages
				if(!agents[i].equals(Agent.DUMMY_AGENT) && agents[i].getMessages().size() > 0)
				{
					addDummy = true;
					if(agents[i].isVisible())
					{
						Agent.DUMMY_AGENT.getMessages().removeAll(agents[i].getMessages());
					}
					else
					{
						Agent.DUMMY_AGENT.getMessages().addAll(agents[i].getMessages());
					}
					updated_messages.addAll(agents[i].getMessages());
				}
				// if dummy changes, fire update for the redirected messages
				if(agents[i].equals(Agent.DUMMY_AGENT))
				{
					updated_messages.addAll(agents[i].getMessages());
				}

			}
		}

		// dummy can change visibility by adding or removing messages
		if(addDummy && Agent.DUMMY_AGENT.applyFilter(agentfilter))
		{
			updated_agents.add(Agent.DUMMY_AGENT);
		}

		agentlist.fireAgentsChanged((Agent[])updated_agents.toArray(new Agent[updated_agents.size()]));
		messagelist.fireMessagesChanged((Message[])updated_messages.toArray(new Message[updated_messages.size()]));
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
	public AgentList getAgentList()
	{
		return agentlist;
	}

	/**
	 * @return The array of agents.
	 */
	public Agent[] getAgents()
	{
		return agentlist.getAgents();
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
				final List messages_added = new ArrayList();
				IAgentIdentifier sid;

				// processing every message map
				MessageType mt = message.getMessageType();

				sid = (IAgentIdentifier)message.getValue(mt.getSenderIdentifier());
				Iterator rids = SReflect.getIterator(message.getValue(mt.getReceiverIdentifier()));

				while(rids.hasNext())
				{
					IAgentIdentifier rid = (IAgentIdentifier)rids.next();
					if(!isDuplicate(message, rid))
					{
						Message msg = createMessage(message, sid, rid);
//						System.out.println("Added: "+msg);
						messages_added.add(msg);
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
		});	
	}
	
	/**
	 * Checks if this message is already in messagelist (same id and same
	 * receiver)
	 * @param source The attribute map for the message.
	 * @return <code>true</code> if the message is already in the messagelist.
	 */
	protected boolean isDuplicate(IMessageAdapter newmsg, IAgentIdentifier rec)
	{
		boolean ret = false;
		Message[] messages = messagelist.getMessages();
		for(int i=0; i<messages.length && !ret; i++)
		{
			Object xid1 = messages[i].getParameter(Message.XID);
			Object xid2 = newmsg.getValue(Message.XID);
			if(xid1!=null && xid2!=null && xid1.equals(xid2))
			{
				IAgentIdentifier oldrec = (IAgentIdentifier)messages[i].getParameter(Message.RECEIVER);
				if(oldrec.equals(rec))
				{
					// set duration of existing message
					String start = (String)messages[i].getParameter(Message.DATE);
					if(start!=null)
					{
						long duration = getJCC().getAgent().getTime() - new Long(start).longValue();
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
	protected Message createMessage(IMessageAdapter msg, IAgentIdentifier sid, IAgentIdentifier rid)//, String direction)
	{
		Message message = new Message(msg, messagenr++, rid);
		message.applyFilter(messagefilter);

		// add to messagelist
		messagelist.addMessage(message);

		// add sender to agentlist if not present
		Agent sender = agentlist.getAgent(sid);
		if(sender == null)
		{
			sender = new Agent(sid);
			sender.setState(Agent.STATE_DEAD);
			sender.addMessage(message);
			sender.applyFilter(agentfilter);
			agentlist.addAgent(sender);

			// add to agent tree table
			IAMS ams = (IAMS)getJCC().getAgent().getPlatform().getService(IAMS.class);
			IAMSAgentDescription desc = ams.createAMSAgentDescription(sender.getAid());
			agents.addAgent(desc);
		}
		else
		{
			// apply filter with new message
			sender.addMessage(message);
			applyAgentFilter(sender);
		}

		// add receiver to agentlist if not present
		Agent receiver = agentlist.getAgent(rid);
		if(receiver == null)
		{
			receiver = new Agent(rid);
			receiver.setState(Agent.STATE_DEAD);
			receiver.addMessage(message);
			receiver.applyFilter(agentfilter);
			agentlist.addAgent(receiver);

			// add to agent tree table
			IAMS ams = (IAMS)getJCC().getAgent().getPlatform().getService(IAMS.class);
			IAMSAgentDescription ad = ams.createAMSAgentDescription(receiver.getAid());
			agents.addAgent(ad);

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
			Agent[] agents = agentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				if(agents[i].getState().equals(Agent.STATE_OBSERVED))
				{
					agents[i].setState(Agent.STATE_IGNORED);
					update.add(agents[i]);

					IAMS ams = (IAMS)getJCC().getAgent().getPlatform().getService(IAMS.class);
					IAMSAgentDescription desc = ams.createAMSAgentDescription(agents[i].getAid());
					removeAgentListener(desc, true);
					ComanalyzerPlugin.this.agents.updateAgent(desc);
				}
			}

			applyAgentFilter((Agent[])update.toArray(new Agent[update.size()]));
		}
	};

	/** Observe all agents */
	final AbstractAction OBSERVE_ALL = new AbstractAction("Observe All")
	{
		public void actionPerformed(ActionEvent e)
		{
			List update = new ArrayList();
			Agent[] agents = agentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				if(agents[i].getState().equals(Agent.STATE_IGNORED))
				{
					agents[i].setState(Agent.STATE_OBSERVED);
					update.add(agents[i]);

					IAMS ams = (IAMS)getJCC().getAgent().getPlatform().getService(IAMS.class);
					IAMSAgentDescription desc = ams.createAMSAgentDescription(agents[i].getAid());
					addAgentListener(desc);
					ComanalyzerPlugin.this.agents.updateAgent(desc);
				}
			}
			applyAgentFilter((Agent[])update.toArray(new Agent[update.size()]));
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
			Agent[] agents = agentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				if(agents[i].getState().equals(Agent.STATE_DEAD))
				{
					// remove messages of dead agent from messagelist
					messagelist.removeMessages((Message[])agents[i].getMessages().toArray(new Message[0]));
					messagelist.fireMessagesRemoved((Message[])agents[i].getMessages().toArray(new Message[0]));
					// remove dead agent from agentlist
					agentlist.removeAgent(agents[i]);
					// remove dead agent from agentree
					IAMS ams = (IAMS)getJCC().getAgent().getPlatform().getService(IAMS.class);
					IAMSAgentDescription desc = ams.createAMSAgentDescription(agents[i].getAid());
					ComanalyzerPlugin.this.agents.removeAgent(desc);
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
			Agent[] agents = agentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				agents[i].removeAllMessages();
				update.add(agents[i]);
			}
			applyAgentFilter((Agent[])update.toArray(new Agent[update.size()]));
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
			Agent[] agents = agentlist.getAgents();
			for(int i = 0; i < agents.length; i++)
			{
				agents[i].removeAllMessages();
				// remove dead agents from agentlist and agentree
				if(agents[i].getState().equals(Agent.STATE_DEAD))
				{
					agentlist.removeAgent(agents[i]);
					IAMS ams = (IAMS)getJCC().getAgent().getPlatform().getService(IAMS.class);
					IAMSAgentDescription desc = ams.createAMSAgentDescription(agents[i].getAid());
					ComanalyzerPlugin.this.agents.removeAgent(desc);
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
	final AbstractAction START_OBSERVING = new AbstractAction("Observe Agent", icons.getIcon("introspect_agent"))
	{
		public void actionPerformed(ActionEvent e)
		{
			if(!isEnabled())
				return;
			split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTreeTableNode node = (DefaultTreeTableNode)agents.getTreetable().getTree().getSelectionPath().getLastPathComponent();
			final IAMSAgentDescription desc = (IAMSAgentDescription)node.getUserObject();
			addAgentListener(desc);
			split.setCursor(Cursor.getDefaultCursor());

//			SwingUtilities.invokeLater(new Runnable()
//			{
//				public void run()
//				{
					split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					Agent agent = agentlist.getAgent(desc.getName());
					agent.setState(Agent.STATE_OBSERVED);
					applyAgentFilter(agent);

					agents.updateAgent(desc);

					split.setCursor(Cursor.getDefaultCursor());
//				}
//			});

		}

		public boolean isEnabled()
		{
			boolean ret = false;
			TreePath path = agents.getTreetable().getTree().getSelectionPath();
			if(path != null)
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
				// ret = node != null && node.getUserObject() instanceof
				// AMSAgentDescription &&
				// !listeners.containsKey(node.getUserObject());
				if(node != null && node.getUserObject() instanceof IAMSAgentDescription)
				{
					IAMSAgentDescription desc = (IAMSAgentDescription)node.getUserObject();
					Agent agent = agentlist.getAgent(desc.getName());
					ret = agent.getState().equals(Agent.STATE_IGNORED);
				}
			}
			return ret;
		}
	};

	/** Stop observing an agent */
	final AbstractAction STOP_OBSERVING = new AbstractAction("Ignore Agent", icons.getIcon("close_comanalyzer"))
	{
		public void actionPerformed(ActionEvent e)
		{
			if(!isEnabled())
				return;
			split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			DefaultTreeTableNode node = (DefaultTreeTableNode)agents.getTreetable().getTree().getSelectionPath().getLastPathComponent();
			final IAMSAgentDescription desc = (IAMSAgentDescription)node.getUserObject();
			removeAgentListener(desc, true);
			split.setCursor(Cursor.getDefaultCursor());

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					split.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					Agent agent = agentlist.getAgent(desc.getName());
					agent.setState(Agent.STATE_IGNORED);
					applyAgentFilter(agent);

					agents.updateAgent(desc);

					split.setCursor(Cursor.getDefaultCursor());
				}
			});

		}

		public boolean isEnabled()
		{
			boolean ret = false;
			TreePath path = agents.getTreetable().getTree().getSelectionPath();
			if(path != null)
			{
				DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
				// ret = node != null && node.getUserObject() instanceof
				// AMSAgentDescription &&
				// listeners.containsKey(node.getUserObject());
				if(node != null && node.getUserObject() instanceof IAMSAgentDescription)
				{
					IAMSAgentDescription desc = (IAMSAgentDescription)node.getUserObject();
					Agent agent = agentlist.getAgent(desc.getName());
					ret = agent.getState().equals(Agent.STATE_OBSERVED);
				}
			}
			return ret;
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
				String fileName = fileDialog.getSelectedFile().getAbsolutePath();
//				List message_maps = new ArrayList();
//				Message[] messages = messagelist.getMessages();
//				for(int i = 0; i < messages.length; i++)
//				{
//					message_maps.add(messages[i].getParameters());
//				}
				ClassLoader cl = ((ILibraryService)jcc.getAgent().getPlatform().getService(ILibraryService.class)).getClassLoader();
				String xml = Nuggets.objectToXML(new Object[]{agentlist.getAgents(), messagelist.getMessages()}, cl);

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
				ClassLoader cl = ((ILibraryService)jcc.getAgent().getPlatform().getService(ILibraryService.class)).getClassLoader();
				Object[] stored = (Object[])Nuggets.objectFromXML(xml, cl);
				
				agentlist.removeAllAgents();
				Agent[] agents = (Agent[])stored[0];
				for(int i=0; i<agents.length; i++)
					agentlist.addAgent(agents[i]);
				
				messagelist.removeAllMessages();
				Message[] messages = (Message[])stored[1];
				for(int i=0; i<messages.length; i++)
					messagelist.addMessage(messages[i]);
				
				messagelist.fireMessagesAdded(messages);
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
