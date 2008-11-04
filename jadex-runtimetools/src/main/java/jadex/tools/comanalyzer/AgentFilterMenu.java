package jadex.tools.comanalyzer;

import jadex.commons.SUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;


/**
 * The filter menu for agents. Selected agents are passed to the menu to provide
 * filter options suitable for the given agent(s).
 */
public class AgentFilterMenu extends TitlePopupMenu
{

	/** The ComanalyzerPlugin */
	protected ComanalyzerPlugin plugin;

	/** Array of agents passed to the menu */
	protected Agent[] agents;

	/** Weather to replace an existing filter or to add a new */
	protected boolean replacefilter = true;

	/**
	 * Creates the agent filter menu with a single agent.
	 * 
	 * @param plugin The plugin.
	 * @param agent The agent
	 */
	public AgentFilterMenu(ComanalyzerPlugin plugin, final Agent agent)
	{
		this(plugin, new Agent[]{agent});
	}

	/**
	 * Creates the agent filter menu with an array of agents.
	 * 
	 * @param plugin The plugin.
	 * @param agents The array of agents
	 */
	public AgentFilterMenu(ComanalyzerPlugin plugin, final Agent[] agents)
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
				AgentFilter filter = new AgentFilter();
				for(int i = 0; i < agents.length; i++)
				{
					// exclude dummy since it should always
					// be hidden by the menu (hide dummy agent)
					if(!agents[i].equals(Agent.DUMMY_AGENT))
					{
						filter.addValue(Agent.AID, agents[i].getParameter(Agent.AID));
					}
				}
				// add to existing filter
				List filters = SUtil.arrayToList(plugin.getAgentFilter());
				filters.add(filter);
				plugin.setAgentFilter((AgentFilter[])filters.toArray(new AgentFilter[filters.size()]));
				plugin.applyAgentFilter();
			}
		});

		JMenuItem menu2 = new JMenuItem("Hide other agents");
		menu2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AgentFilter filter = new AgentFilter();
				// get agentlist and remove selected and dummy
				List other = SUtil.arrayToList(plugin.getAgents());
				other.removeAll(SUtil.arrayToList(agents));
				other.remove(Agent.DUMMY_AGENT);
				for(int i = 0; i < other.size(); i++)
				{
					filter.addValue(Agent.AID, ((Agent)other.get(i)).getParameter(Agent.AID));
				}
				// add to existing filter
				List filters = SUtil.arrayToList(plugin.getAgentFilter());
				filters.add(filter);
				plugin.setAgentFilter((AgentFilter[])filters.toArray(new AgentFilter[filters.size()]));
				plugin.applyAgentFilter();
			}
		});

		JMenuItem menu3 = new JMenuItem("Show only this communicating group");
		menu3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AgentFilter filter = new AgentFilter();
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
				other.remove(Agent.DUMMY_AGENT);
				for(int i = 0; i < other.size(); i++)
				{
					filter.addValue(Agent.AID, ((Agent)other.get(i)).getParameter(Agent.AID));
				}
				// add to existing filter
				List filters = SUtil.arrayToList(plugin.getAgentFilter());
				filters.add(filter);
				plugin.setAgentFilter((AgentFilter[])filters.toArray(new AgentFilter[filters.size()]));
				plugin.applyAgentFilter();
			}
		});

		JMenuItem menu4 = new JMenuItem("Hide this communicating group");
		menu4.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AgentFilter filter = new AgentFilter();
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
					filter.addValue(Agent.AID, ((Agent)group.get(i)).getParameter(Agent.AID));
				}
				// add to existing filter
				List filters = SUtil.arrayToList(plugin.getAgentFilter());
				filters.add(filter);
				plugin.setAgentFilter((AgentFilter[])filters.toArray(new AgentFilter[filters.size()]));
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
		boolean disabled = agents.length == 1 && agents[0].equals(Agent.DUMMY_AGENT);
		menu1.setEnabled(!disabled);
		menu2.setEnabled(!disabled);
		menu3.setEnabled(!disabled);
		menu4.setEnabled(!disabled);

	}
}