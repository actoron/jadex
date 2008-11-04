package jadex.tools.comanalyzer.diagram;

import jadex.commons.collection.SortedList;
import jadex.tools.comanalyzer.Agent;
import jadex.tools.comanalyzer.AgentFilterMenu;
import jadex.tools.comanalyzer.Message;
import jadex.tools.comanalyzer.MessageFilterMenu;
import jadex.tools.comanalyzer.ToolCanvas;
import jadex.tools.comanalyzer.ToolTab;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JScrollPane;

import edu.uci.ics.jung.graph.util.Pair;


/**
 * The container for the diagram.
 */
public class DiagramCanvas extends ToolCanvas
{

	// -------- attributes --------

	/** The panel for agents */
	protected AgentCanvas header;

	/** The panel for messages */
	protected MessageCanvas detail;

	/** Internal agentlist for display */
	protected SortedList visible_agents;

	/** Internal messagelist for display */
	protected SortedMap visible_messages;

	/** The autoscroll for the diagramm */
	protected boolean autoScroll = true;

	// -------- constructors --------

	/**
	 * Constructor for the container
	 * 
	 * @param tooltab The tooltab.
	 */
	public DiagramCanvas(final ToolTab tooltab)
	{
		super(tooltab);

		visible_agents = new SortedList();
		visible_messages = new TreeMap();

		// init agent and messages panel
		header = new AgentCanvas(this);
		detail = new MessageCanvas(this);

		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(detail);
		scroll.setColumnHeaderView(header);

		this.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER, scroll);

		// change cursor when over an agent
		header.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				Agent selectedAgent = header.getAgent(e.getX(), e.getY());
				if((selectedAgent != null))
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				else
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});

		// show poup on trigger or display element on doubleclick
		header.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getButton() == MouseEvent.BUTTON3)
				{
					Agent selectedAgent = header.getAgent(e.getX(), e.getY());
					if((selectedAgent != null))
					{
						AgentFilterMenu mpopup = new AgentFilterMenu(tooltab.getPlugin(), selectedAgent);
						mpopup.show(e.getComponent(), e.getX(), e.getY());
					}

				}
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
				{
					Agent selectedAgent = header.getAgent(e.getX(), e.getY());
					if((selectedAgent != null))
					{
						tooltab.getToolPanel().showElementDetails(selectedAgent.getParameters());
					}
				}
			}

		});

		// change cursor when over a message
		detail.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e)
			{
				Message selectedMessage = detail.getMessage(e.getX(), e.getY());
				if(selectedMessage != null)
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				else
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});

		// show poup on trigger or display element on doubleclick
		detail.addMouseListener(new MouseAdapter()
		{

			public void mouseClicked(MouseEvent e)
			{
				if(e.getButton() == MouseEvent.BUTTON3)
				{
					Message mess = detail.getMessage(e.getX(), e.getY());
					if(mess != null)
					{
						MessageFilterMenu mpopup = new MessageFilterMenu(tooltab.getPlugin(), mess);
						mpopup.show(e.getComponent(), e.getX(), e.getY());
					}

				}
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
				{
					Message mess = detail.getMessage(e.getX(), e.getY());
					if((mess != null))
					{
						tooltab.getToolPanel().showElementDetails(mess.getParameters());
					}
				}
			}
		});

	}

	// -------- ToolCanvas methods --------

	/**
	 * Update a message by adding it, if the message can be displayed or
	 * removing it if present.
	 * 
	 * @param message The message to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new messages)
	 */
	public void updateMessage(Message message, boolean isPresent)
	{

		Pair newPair = message.getEndpoints();

		if(newPair != null)
		{
			Agent sender = (Agent)newPair.getFirst();
			Agent receiver = (Agent)newPair.getSecond();
			// check if message is already displayed
			if(visible_messages.containsKey(message))
			{
				// check if the message should be redirected
				Pair oldPair = (Pair)visible_messages.get(message);
				if(oldPair.getFirst().equals(sender) && oldPair.getSecond().equals(receiver))
				{
					return; // already displayed
				}
				else
				{
					// remove message, since the message is redirected
					removeMessage(message);
				}
			}
			// now add the message with sender and receiver
			// given by displayMessage
			addMessage(message, sender, receiver);
		}
		else if(isPresent)
		{
			removeMessage(message);
		}
		return;

	}

	/**
	 * Removes a message.
	 * 
	 * @param message The message to remove.
	 */
	public void removeMessage(Message message)
	{
		visible_messages.remove(message);
	}

	/**
	 * Updates an agent by adding it, if the agent can be displayed or removing
	 * it if present.
	 * 
	 * @param agent The agent to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new agents)
	 */
	public void updateAgent(Agent agent, boolean update)
	{
		if(agent.isVisible())
		{
			if(!visible_agents.contains(agent))
			{
				addAgent(agent);
			}
		}
		else if(update)
		{
			removeAgent(agent);
		}
		return;
	}

	/**
	 * Removes an agent.
	 * 
	 * @param agent The agent to remove.
	 */
	public void removeAgent(Agent agent)
	{
		visible_agents.remove(agent);
	}

	/**
	 * This method repaint both canvas checking the size of the scrollbars. The
	 * right procedure to follow is to call method setPreferredSize() the
	 * revalidate() method.
	 */
	public void repaintCanvas()
	{

		int horDim = (visible_agents.size() * 80);
		int vertDim = 20 + (visible_messages.size() * 20);
		header.setPreferredSize();
		detail.setPreferredSize();

		if(autoScroll)
		{
			Point p = new Point(0, detail.getSize().height);
			detail.scrollRectToVisible(new Rectangle(p));
		}

		// detail.setSize(new Dimension(horDim,vertDim));
		revalidate();
		repaint();

	}

	/**
	 * Clear the diagramm by removing all messages and agents.
	 */
	public void clear()
	{
		visible_agents.clear();
		visible_messages.clear();
	}

	// -------- DiagramCanvas methods --------

	/**
	 * @return Returns <code>true</code> if autoscroll is on
	 */
	public boolean isAutoScroll()
	{
		return autoScroll;
	}

	/**
	 * @param autoScroll The autoScroll to set.
	 */
	public void setAutoScroll(boolean autoScroll)
	{
		this.autoScroll = autoScroll;
	}

	/**
	 * Adds the agent.
	 * @param agent The agent to add.
	 */
	public void addAgent(Agent agent)
	{
		visible_agents.add(agent);
	}

	/**
	 * Add message with given sender and receiver (for redirection)
	 * 
	 * @param message The message to add.
	 * @param sender The sender in the presentation.
	 * @param receiver The receiver in the presentation. (e.g. dummy)
	 */
	public void addMessage(Message message, Agent sender, Agent receiver)
	{
		visible_messages.put(message, new Pair(sender, receiver));
	}

}
