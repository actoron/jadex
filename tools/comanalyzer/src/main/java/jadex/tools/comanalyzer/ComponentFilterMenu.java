package jadex.tools.comanalyzer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

import jadex.commons.SUtil;


/**
 * The filter menu for agents. Selected agents are passed to the menu to provide
 * filter options suitable for the given agent(s).
 */
public class ComponentFilterMenu extends TitlePopupMenu
{

	/** The ComanalyzerPlugin */
	protected ComanalyzerPlugin plugin;

	/** Array of agents passed to the menu */
	protected Component[] agents;

	/** Weather to replace an existing filter or to add a new */
	protected boolean replacefilter = true;

	/**
	 * Creates the agent filter menu with a single agent.
	 * 
	 * @param plugin The plugin.
	 * @param agent The agent
	 */
	public ComponentFilterMenu(ComanalyzerPlugin plugin, final Component agent)
	{
		this(plugin, new Component[]{agent});
	}

	/**
	 * Creates the agent filter menu with an array of agents.
	 * 
	 * @param plugin The plugin.
	 * @param agents The array of agents
	 */
	public ComponentFilterMenu(ComanalyzerPlugin plugin, final Component[] agents)
	{
		super("Agent Filter");
		this.plugin = plugin;
		this.agents = agents;

		createMenu();

	}

	/**
	 * Creates the menu items
	 */
	protected void createMenu()
	{

		JMenuItem menu0 = new JMenuItem("Remove agent filter");
		menu0.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plugin.removeAgentFilter();
				plugin.applyAgentFilter();
			}
		});

		JMenuItem menu1 = new JMenuItem("Hide selected agents");
		menu1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ComponentFilter filter = new ComponentFilter();
				for(int i = 0; i < agents.length; i++)
				{
					// exclude dummy since it should always
					// be hidden by the menu (hide dummy agent)
					if(!agents[i].equals(Component.DUMMY_COMPONENT))
					{
						filter.addValue(Component.AID, agents[i].getParameter(Component.AID));
					}
				}
				// add to existing filter
				List filters = SUtil.arrayToList(plugin.getAgentFilter());
				filters.add(filter);
				plugin.setAgentFilter((ComponentFilter[])filters.toArray(new ComponentFilter[filters.size()]));
				plugin.applyAgentFilter();
			}
		});

		JMenuItem menu2 = new JMenuItem("Hide other agents");
		menu2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ComponentFilter filter = new ComponentFilter();
				// get agentlist and remove selected and dummy
				List other = SUtil.arrayToList(plugin.getAgents());
				other.removeAll(SUtil.arrayToList(agents));
				other.remove(Component.DUMMY_COMPONENT);
				for(int i = 0; i < other.size(); i++)
				{
					filter.addValue(Component.AID, ((Component)other.get(i)).getParameter(Component.AID));
				}
				// add to existing filter
				List filters = SUtil.arrayToList(plugin.getAgentFilter());
				filters.add(filter);
				plugin.setAgentFilter((ComponentFilter[])filters.toArray(new ComponentFilter[filters.size()]));
				plugin.applyAgentFilter();
			}
		});

		JMenuItem menu3 = new JMenuItem("Show only this communicating group");
		menu3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ComponentFilter filter = new ComponentFilter();
				// create list of communicating agents
				List group = new ArrayList();
				for(int i = 0; i < agents.length; i++)
				{
					List ms = agents[i].getMessages();
					for(int j = 0; j < ms.size(); j++)
					{
						group.add(((Message)ms.get(j)).getSender());
						group.add(((Message)ms.get(j)).getReceiver());
					}
				}
				// get agentlist and remove group and dummy
				List other = SUtil.arrayToList(plugin.getAgents());
				other.removeAll(group);
				other.remove(Component.DUMMY_COMPONENT);
				for(int i = 0; i < other.size(); i++)
				{
					filter.addValue(Component.AID, ((Component)other.get(i)).getParameter(Component.AID));
				}
				// add to existing filter
				List filters = SUtil.arrayToList(plugin.getAgentFilter());
				filters.add(filter);
				plugin.setAgentFilter((ComponentFilter[])filters.toArray(new ComponentFilter[filters.size()]));
				plugin.applyAgentFilter();
			}
		});

		JMenuItem menu4 = new JMenuItem("Hide this communicating group");
		menu4.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ComponentFilter filter = new ComponentFilter();
				// create list of communicating agents
				List group = new ArrayList();
				for(int i = 0; i < agents.length; i++)
				{
					List ms = agents[i].getMessages();
					for(int j = 0; j < ms.size(); j++)
					{
						group.add(((Message)ms.get(j)).getSender());
						group.add(((Message)ms.get(j)).getReceiver());
					}
				}

				for(int i = 0; i < group.size(); i++)
				{
					filter.addValue(Component.AID, ((Component)group.get(i)).getParameter(Component.AID));
				}
				// add to existing filter
				List filters = SUtil.arrayToList(plugin.getAgentFilter());
				filters.add(filter);
				plugin.setAgentFilter((ComponentFilter[])filters.toArray(new ComponentFilter[filters.size()]));
				plugin.applyAgentFilter();
			}
		});

		add(menu0);
		addSeparator();
		add(menu1);
		add(menu2);
		addSeparator();
		add(menu3);
		add(menu4);

		// disable the other filter menu items if its only the dummy agent
		boolean disabled = agents.length == 1 && agents[0].equals(Component.DUMMY_COMPONENT);
		menu1.setEnabled(!disabled);
		menu2.setEnabled(!disabled);
		menu3.setEnabled(!disabled);
		menu4.setEnabled(!disabled);

	}
}