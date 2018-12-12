package jadex.tools.comanalyzer;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIDefaults;

import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.tools.comanalyzer.table.TablePanel;


/**
 * Base class for tool components.
 */
public abstract class ToolTab extends JPanel implements IComponentListListener, IMessageListListener
{

	/** Icon paths */
	private static final String COMANALYZER_IMAGES = "/jadex/tools/comanalyzer/images/";

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]{
			"start", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "start.png"),
			"stop", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "stop.png"),
			"arrow2l", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "arrow2l.png"),
			"arrow1l", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "arrow1l.png"),
			"slider", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "message_slider.png"),
			"arrow1r", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "arrow1r.png"),
			"arrow2r", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "arrow2r.png"),
			"hide_dummy", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "hide_dummy.png"),
			"show_dummy", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "show_dummy.png"),
			"hide_ignored", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "hide_ignored.png"),
			"show_ignored", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "show_ignored.png"),
			"hide_dead", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "hide_dead.png"),
			"show_dead", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "show_dead.png"),
			"show_zero", SGUI.makeIcon(TablePanel.class, COMANALYZER_IMAGES + "show_zero.png"),
			"hide_zero", SGUI.makeIcon(TablePanel.class, COMANALYZER_IMAGES + "hide_zero.png"),
			"delete_messagefilter", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "delete_messagefilter.png"),
			"delete_agentfilter", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "delete_agentfilter.png"),
			"refresh", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "refresh2.png"),
			"clear", SGUI.makeIcon(ToolTab.class, COMANALYZER_IMAGES + "clear_panel.png")
	});

	// -------- attributes --------

	/** The messagelist held by the tool. */
	protected List messagelist;

	/** The agentlist held by the tool */
	protected List componentlist;

	/** The plugin. */
	protected ComanalyzerPlugin plugin;

	/** The name of this tool component. */
	protected String name;

	/** The icon of this tool component. */
	protected Icon icon;

	/** Flag indicating if tool is active. */
	protected boolean active;

	/** The common toolbar actions. */
	protected Action[] actions;

	/**
	 * Create a new tool component.
	 * @param pluginThe plugin.
	 * @param name The name of the tool component.
	 * @param icon The icon of the tool component.
	 */
	public ToolTab(ComanalyzerPlugin plugin, String name, Icon icon)
	{
		this.messagelist = new ArrayList();
		this.componentlist = new ArrayList();
		this.name = name;
		this.icon = icon;
		this.plugin = plugin;

	}

	// -------- ToolTab methods --------

	/**
	 * Get the tool panel of this component.
	 */
	public ToolPanel getToolPanel()
	{
		return this.plugin.tpanel;
	}

	/**
	 * Get the plugin of this component.
	 */
	public ComanalyzerPlugin getPlugin()
	{
		return this.plugin;
	}

	/**
	 * @return The global paint map.
	 */
	public PaintMaps getPaintMaps()
	{
		return this.plugin.paintmaps;
	}

	/**
	 * Get the name this component.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Get the icon of this component.
	 */
	public Icon getIcon()
	{
		return this.icon;
	}

	/**
	 * (De-)Activate the tool tab.
	 */
	public void setActive(boolean active)
	{
		this.active = active;

		// Set action settings regardless of change, as setActive() is also
		// called initially.
		if(active)
		{
			STARTSTOP_ACTION.putValue(Action.SHORT_DESCRIPTION, "Deactivate Tab");
			STARTSTOP_ACTION.putValue(Action.SMALL_ICON, icons.getIcon("stop"));
		}
		else
		{
			STARTSTOP_ACTION.putValue(Action.SHORT_DESCRIPTION, "Activate Tab");
			STARTSTOP_ACTION.putValue(Action.SMALL_ICON, icons.getIcon("start"));
		}

		// enable the off-line controles accordingly
		REFRESH.setEnabled(!active);
		CLEAR.setEnabled(!active);
		FIRST_MESSAGE.setEnabled(!active);
		OPEN_SLIDER.setEnabled(!active);
		PREVIOUS_MESSAGE.setEnabled(!active);
		NEXT_MESSAGE.setEnabled(!active);
		LAST_MESSAGE.setEnabled(!active);

		// refresh tool tab if active
		if(active)
			refresh();
	}

	/**
	 * @return <code>true</code> if the tooltab is active.
	 */
	public boolean isActive()
	{
		return active;
	}

	// -------- ToolTab templates --------

	/**
	 * Receives the actual canvas of the tooltab.
	 * @return The canvas for displaying the messages
	 */
	public abstract ToolCanvas getCanvas();

	/**
	 * Get the (menu/toolbar) actions of the tooltab.
	 */
	public Action[] getActions()
	{
		if(this.actions == null)
		{
			List actionlist = new ArrayList();
			actionlist.add(STARTSTOP_ACTION);
			actionlist.add(null);
			actionlist.add(REFRESH);
			actionlist.add(CLEAR);
			actionlist.add(null);
			actionlist.add(FIRST_MESSAGE);
			actionlist.add(PREVIOUS_MESSAGE);
			actionlist.add(OPEN_SLIDER);
			actionlist.add(NEXT_MESSAGE);
			actionlist.add(LAST_MESSAGE);
			actionlist.add(null);
			actionlist.add(SHOW_DUMMY);
			actionlist.add(SHOW_IGNORED);
			actionlist.add(SHOW_DEAD);
			actionlist.add(SHOW_ZERO);
			actionlist.add(null);
			actionlist.add(DELETE_MESSAGE_FILTER);
			actionlist.add(DELETE_AGENT_FILTER);

			actions = (Action[])actionlist.toArray((new Action[actionlist.size()]));
		}
		return this.actions;
	}

	/**
	 * Clear the view when refreshing.
	 */
	public void clear()
	{
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
				componentlist.clear();
				messagelist.clear();
				getCanvas().clear();
				getCanvas().repaintCanvas();
//			}
//		});
	}

	/**
	 * Refresh the tooltabs presentation. Clear internal agent and messagelists
	 * and add all the agents and messages from plugin
	 */
	public void refresh()
	{
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
				// TODO: update changed and add only new messages

				getCanvas().clear();

				componentlist.clear();
				componentlist.addAll(SUtil.arrayToList(getPlugin().getAgents()));
				for(Iterator iterator = componentlist.iterator(); iterator.hasNext();)
				{
					Component agent = (Component)iterator.next();
					if(agent.isVisible())
					{
						getCanvas().updateComponent(agent, false);
					}
				}

				messagelist.clear();
				messagelist.addAll(SUtil.arrayToList(getPlugin().getMessages()));
				for(Iterator iter = messagelist.iterator(); iter.hasNext();)
				{
					Message message = (Message)iter.next();
					if(message.isVisible())
					{
						getCanvas().updateMessage(message, false);
					}
				}

				getCanvas().repaintCanvas();
//			}
//		});

	}

	//-------- IMessageListListener interface--------

	/**
	 * Update the view as new messages have been recorded.
	 */
	public void messagesAdded(final Message[] messages)
	{
		//	System.out.println("Messages added: "+SUtil.arrayToString(messages));

		if(!isActive())
			return;

		// awt invoker is called from refreshtask.
		for(int i = 0; i < messages.length; i++)
		{
			messagelist.add(messages[i]);
			getCanvas().updateMessage(messages[i], false);

		}
		getCanvas().repaintCanvas();
	}

	/**
	 * Update the view as messages have been changed due to filter operations.
	 */
	public void messagesChanged(final Message[] messages)
	{
//		System.out.println("Messages changed: " + SUtil.arrayToString(messages));

//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
				for(int i = 0; i < messages.length; i++)
				{
					if(messagelist.contains(messages[i]))
					{
						getCanvas().updateMessage(messages[i], true);
					}
				}
				getCanvas().repaintCanvas();
//			}
//		});
	}

	/**
	 * Update the view as messages have been removed.
	 */
	public void messagesRemoved(final Message[] messages)
	{
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
				for(int i = 0; i < messages.length; i++)
				{
					messagelist.remove(messages[i]);
					getCanvas().removeMessage(messages[i]);
				}
				getCanvas().repaintCanvas();
//			}
//		});
	}

	//-------- IAgentListListener interface--------

	/**
	 * Update the view as for agents have been added.
	 */
	public void componentsAdded(final Component[] agents)
	{
		if(!isActive())
			return;

//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
				for(int i = 0; i < agents.length; i++)
				{
					componentlist.add(agents[i]);
					getCanvas().updateComponent(agents[i], false);
				}
				getCanvas().repaintCanvas();
//			}
//		});
	}

	/**
	 * Update the view as for agents have changed due to filter operaions.
	 */
	public void componentsChanged(final Component[] agents)
	{
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{

				for(int i = 0; i < agents.length; i++)
				{
					if(componentlist.contains(agents[i]))
					{
						getCanvas().updateComponent(agents[i], true);
					}
				}
				getCanvas().repaintCanvas();
//			}
//		});

	}

	/**
	 * Update the view as for agents have been removed.
	 */
	public void componentsRemoved(final Component[] agents)
	{
//		SwingUtilities.invokeLater(new Runnable()
//		{
//			public void run()
//			{
				for(int i = 0; i < agents.length; i++)
				{
					componentlist.remove(agents[i]);
					getCanvas().removeComponent(agents[i]);
				}
				getCanvas().repaintCanvas();
//			}
//		});

	}

	//-------- helper methods --------

	/**
	 * Refresh the toolbar of each tooltab to account for global filter states
	 */
	protected void refreshToolBar()
	{
		boolean show_dummy = true;
		boolean show_ignored = true;
		boolean show_dead = true;
		boolean show_zero = true;

		ComponentFilter[] afs = plugin.getAgentFilter();
		for(int i = 0; i < afs.length; i++)
		{
			if(afs[i].containsValue(Component.STATE, Component.STATE_DUMMY))
			{
				show_dummy = false;
			}
			if(afs[i].containsValue(Component.STATE, Component.STATE_IGNORED))
			{
				show_ignored = false;
			}
			if(afs[i].containsValue(Component.STATE, Component.STATE_DEAD))
			{
				show_dead = false;
			}
			if(afs[i].containsValue(Component.MESSAGE_VISIBLE, Integer.valueOf(Component.NO_MESSAGES)))
			{
				show_zero = false;
			}
		}

		SHOW_DUMMY.putValue(Action.SHORT_DESCRIPTION, show_dummy ? "Hide dummy agent." : "Show dummy agent.");
		SHOW_DUMMY.putValue(Action.SMALL_ICON, show_dummy ? icons.getIcon("hide_dummy") : icons.getIcon("show_dummy"));

		SHOW_IGNORED.putValue(Action.SHORT_DESCRIPTION, show_ignored ? "Hide ignored agents." : "Show ignored agents.");
		SHOW_IGNORED.putValue(Action.SMALL_ICON, show_ignored ? icons.getIcon("hide_ignored") : icons.getIcon("show_ignored"));

		SHOW_DEAD.putValue(Action.SHORT_DESCRIPTION, show_dead ? "Hide dead agents." : "Show dead agents.");
		SHOW_DEAD.putValue(Action.SMALL_ICON, show_dead ? icons.getIcon("hide_dead") : icons.getIcon("show_dead"));

		SHOW_ZERO.putValue(Action.SHORT_DESCRIPTION, show_zero ? "Hide agents without messages." : "Show agents without messages.");
		SHOW_ZERO.putValue(Action.SMALL_ICON, show_zero ? icons.getIcon("hide_zero") : icons.getIcon("show_zero"));
	}

	/**
	 * Changes a filter for a given parameter name and value by switching If the
	 * combination is already contained by the filter it is removed, otherwise
	 * it is added.
	 */
	protected void changeAgentFilter(String name, Object value)
	{
		boolean add_filter = true;
		List filters = new ArrayList();

		ComponentFilter[] afs = plugin.getAgentFilter();
		for(int i = 0; i < afs.length; i++)
		{
			if(afs[i].containsValue(name, value))
			{
				add_filter = false;
			}
			else
			{
				filters.add(afs[i]);
			}
		}
		if(add_filter)
		{
			ComponentFilter filter = new ComponentFilter();
			filter.addValue(name, value);
			filters.add(filter);
		}

		plugin.setAgentFilter((ComponentFilter[])filters.toArray(new ComponentFilter[filters.size()]));
		refreshToolBar();
		plugin.applyAgentFilter();
	}

	// -------- global actions --------

	/** Activate / deactivate tooltab */
	protected final AbstractAction STARTSTOP_ACTION = new AbstractAction("Activate Tab", icons.getIcon("start"))
	{
		public void actionPerformed(ActionEvent e)
		{
			setActive(!active);
		}
	};

	/** Refresh the internal messages list and update the view */
	protected final AbstractAction REFRESH = new AbstractAction("Refresh", icons.getIcon("refresh"))
	{
		public void actionPerformed(ActionEvent e)
		{
			refresh();
		}
	};

	/** Clears the canvas */
	protected final AbstractAction CLEAR = new AbstractAction("Clear", icons.getIcon("clear"))
	{
		public void actionPerformed(ActionEvent e)
		{
			clear();
		}
	};

	/** Move to first message */
	protected final AbstractAction FIRST_MESSAGE = new AbstractAction("Move to first message", icons.getIcon("arrow2l"))
	{

		public void actionPerformed(ActionEvent e)
		{
			// first remove all messages
			messagesRemoved(plugin.getMessages());

			// add all the agents
			componentlist.addAll(SUtil.arrayToList(plugin.getAgents()));
			componentsChanged(plugin.getAgents());

			for(int i = 0; i < plugin.getMessageList().size(); i++)
			{
				Message message = plugin.getMessage(i);
				messagelist.add(message);
				// add dont work when tool not active
				messagesChanged(new Message[]{message});
				if(message.getEndpoints() != null)
				{
					break;
				}
			}

		}
	};

	/** Move to previous message */
	protected final AbstractAction PREVIOUS_MESSAGE = new AbstractAction("Move to previous message", icons.getIcon("arrow1l"))
	{

		public void actionPerformed(ActionEvent e)
		{

			for(int i = messagelist.size() - 1; i >= 0; --i)
			{
				Message message = (Message)messagelist.get(i);
				messagesRemoved(new Message[]{message});
				if(message.getEndpoints() != null)
				{
					break;
				}
			}

		}
	};

	/** Open message slide */
	protected final AbstractAction OPEN_SLIDER = new AbstractAction("Open Message Slider", icons.getIcon("slider"))
	{

		public void actionPerformed(ActionEvent e)
		{
			JButton jb = (JButton)e.getSource();
			JPopupMenu popup = new MessageSliderMenu("Message Slider", ToolTab.this);
			popup.show(jb, (int)(jb.getWidth() - popup.getPreferredSize().getWidth()) / 2, (int)-popup.getPreferredSize().getHeight());

		}
	};

	/** Move to next message */
	protected final AbstractAction NEXT_MESSAGE = new AbstractAction("Move to next message", icons.getIcon("arrow1r"))
	{

		public void actionPerformed(ActionEvent e)
		{

			if(componentlist.size() == 0)
			{
				componentlist.addAll(plugin.getAgentList().getList());
				componentsChanged(plugin.getAgents());
			}

			for(int i = messagelist.size() + 1; i < plugin.getMessageList().size(); i++)
			{
				Message message = plugin.getMessage(i);
				messagelist.add(message);
				// add dont work when tool not active
				messagesChanged(new Message[]{message});
				if(message.getEndpoints() != null)
				{
					break;
				}
			}
		}
	};

	/** Move to last messsage */
	protected final AbstractAction LAST_MESSAGE = new AbstractAction("Move to last message", icons.getIcon("arrow2r"))
	{

		public void actionPerformed(ActionEvent e)
		{
			refresh();

		}
	};

	/** Show/hide dummy agent */
	protected final AbstractAction SHOW_DUMMY = new AbstractAction("Hide dummy agent.", icons.getIcon("hide_dummy"))
	{

		public void actionPerformed(ActionEvent e)
		{
			changeAgentFilter(Component.STATE, Component.STATE_DUMMY);
		}
	};

	/** Show/hide ignored agents */
	protected final AbstractAction SHOW_IGNORED = new AbstractAction("Hide ignored agents.", icons.getIcon("hide_ignored"))
	{

		public void actionPerformed(ActionEvent e)
		{
			changeAgentFilter(Component.STATE, Component.STATE_IGNORED);
		}
	};

	/** Show/hide dead agents */
	protected final AbstractAction SHOW_DEAD = new AbstractAction("Hide dead agents.", icons.getIcon("hide_dead"))
	{

		public void actionPerformed(ActionEvent e)
		{
			changeAgentFilter(Component.STATE, Component.STATE_DEAD);
		}
	};

	/** Show/hide agents with no messages */
	protected final AbstractAction SHOW_ZERO = new AbstractAction("Hide agents without messages", icons.getIcon("hide_zero"))
	{
		public void actionPerformed(ActionEvent ae)
		{
			changeAgentFilter(Component.MESSAGE_VISIBLE, Integer.valueOf(Component.NO_MESSAGES));
		}
	};

	/** Delete message filter */
	protected final AbstractAction DELETE_MESSAGE_FILTER = new AbstractAction("Delete message filter", icons.getIcon("delete_messagefilter"))
	{

		public void actionPerformed(ActionEvent e)
		{
			plugin.removeMessageFilter();
			plugin.applyMessageFilter();

		}
	};

	/** Delete individual agentfilter */
	protected final AbstractAction DELETE_AGENT_FILTER = new AbstractAction("Delete agent filter", icons.getIcon("delete_agentfilter"))
	{

		public void actionPerformed(ActionEvent e)
		{
			plugin.removeAgentFilter();
			plugin.applyAgentFilter();
		}
	};

}
