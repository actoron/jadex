package jadex.tools.comanalyzer;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SGUI;
import jadex.commons.SUtil;

import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


/**
 * A listener for the events of an observed agent.
 * 
 * TODO: include, combine and rewrite methods from the introspector ToolTab to
 * have one generic listener for all tools receiving events from agents (e.g.
 * jadex.tools.common.ToolEventListener)
 */
public class ComanalyzerListener //implements IToolManagement
{
	// -------- attributes --------

	/** The agent access. */
	protected IExternalAccess agent;

	/** The agent to observe. */
	protected IComponentIdentifier observed;

	/** The currently registered listeners. */
	protected Map listeners;

	/** The current set of event types that are registered . */
	protected Set typeset;

	/** the tool panel for error message dialog */
	protected ToolPanel tool;

	/** Is the listener active. */
	protected boolean isactive;

	// -------- constructors --------

	/**
	 * Create a listener for the observed agent.
	 * 
	 * @param agent The agent access.
	 * @param observed The observed agent.
	 * @param tool The tool panal.
	 */
	public ComanalyzerListener(IExternalAccess agent, IComponentIdentifier observed, ToolPanel tool)
	{
		this.agent = agent;
		this.observed = observed;
		this.tool = tool;
		this.typeset = new HashSet();
		this.listeners = new HashMap();
	}

	// -------- methods --------

	/**
	 * Dispatch a goal and display errors (if any).
	 * 
	 * @param goal The goal to dispatch.
	 * @param errortitle The title to use for an error dialog.
	 * @param errormessage An optional error message displayed before the
	 * exception.
	 */
	public void dispatchGoal(IGoal goal, final String errortitle, final String errormessage)
	{
		goal.addGoalListener(new IGoalListener()
		{
			public void goalFinished(AgentEvent ae)
			{
				IGoal goal = (IGoal)ae.getSource();
				if(!goal.isSucceeded())
				{

					final String text;
					if(errormessage == null && goal.getException() == null)
					{
						text = errortitle;
					}
					else if(errormessage != null && goal.getException() == null)
					{
						text = errormessage;
					}
					else if(errormessage == null && goal.getException() != null)
					{
						text = "" + goal.getExcludeMode();
					}
					else
					// if(errormessage!=null &&goal.getException()!=null)
					{
						text = errormessage + "\n" + goal.getExcludeMode();
					}
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							JOptionPane.showMessageDialog(SGUI.getWindowParent(tool), 
								SUtil.wrapText(text), errortitle, JOptionPane.ERROR_MESSAGE);
						}
					});
				}
				goal.removeGoalListener(this);
			}

			public void goalAdded(AgentEvent ae)
			{
				// NOP
			}
		});
		agent.dispatchTopLevelGoal(goal);
	}

	// -------- tool methods --------

	/**
	 * Get the name of the observed agent.
	 */
	public String getAgentName()
	{
		return observed.getName();
	}

	/**
	 * Register for event propagation.
	 * @param listener The listener.
	 * @param types The system event types.
	 * /
	public void addChangeListener(ISystemEventListener listener, String[] types)
	{
		if(types == null)
			throw new NullPointerException("Types may not be null.");

		listeners.put(listener, types);
		updateRegistration();
	}*/

	/**
	 * Deregister event propagation.
	 * @param listener The listener.
	 * /
	public void removeChangeListener(ISystemEventListener listener)
	{
		listeners.remove(listener);
		updateRegistration();
	}*/

	// -------- helper methods --------

	/**
	 * Check if registration has to be done/changed after listeners have been
	 * added/removed.
	 * /
	protected void updateRegistration()
	{

		// Determine new type set.
		Set typeset2 = new HashSet();
		for(Iterator i = listeners.values().iterator(); i.hasNext();)
		{
			String[] types = (String[])i.next();
			for(int j = 0; j < types.length; j++)
				typeset2.add(types[j]);
		}

		// Does the subscription has to be changed?
		if(!typeset2.equals(typeset))
		{
			// Remember new type set.
			typeset = typeset2;
			String[] types = (String[])typeset.toArray(new String[typeset.size()]);

			// System.out.println("Update registration for " + observed);

			final SniffOn reg = new SniffOn(ComanalyzerAdapter.TOOL_COMANALYZER);
			reg.setEventTypes(types);

			if(observed.equals(agent.getAgentIdentifier()))
			{
				// include option for catching internal messages for tool debugging?
				LocalToolRequestPlan.handleLocalToolRequest(agent, reg, new ShortcutToolReply(this, null));
			}
			else
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						IGoal reggoal = agent.getGoalbase().createGoal("tool_request");
						reggoal.getParameter("tool").setValue(ComanalyzerListener.this);
						reggoal.getParameter("agent").setValue(observed);
						reggoal.getParameter("request").setValue(reg);

						try
						{
							agent.dispatchTopLevelGoalAndWait(reggoal);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				});
			}
		}
	}*/

	/**
	 * Deregister with the agent.
	 * /
	protected void cleanupRegistration()
	{
		// System.out.println("Cleanup registration for " + observed);

		SniffOff dereg = new SniffOff(ComanalyzerAdapter.TOOL_COMANALYZER);
		IGoal dereggoal = agent.getGoalbase().createGoal("tool_request");
		dereggoal.getParameter("tool").setValue(this);
		dereggoal.getParameter("agent").setValue(observed);
		dereggoal.getParameter("request").setValue(dereg);
		dispatchGoal(dereggoal, "Error Closing Tool", "Problem while deregistering tool");
	}*/

	// -------- IToolManagement interface --------

	/**
	 * The globally unique tool id is used to route messages to the
	 * corresponding tools.
	 */
	public String getId()
	{
		return getAgentName() + "_toolpanel@" + hashCode();
	}

	/**
	 * Activate the tool.
	 */
	public void activate()
	{
		// System.out.println("call for activate() from " + observed);
		this.isactive = true;

	}

	/**
	 * The observed agent has changed and the tool needs to be updated.
	 * /
	public void update(final ToolRequest state)
	{

		SystemEvent[] ces = ((SniffState)state).getSystemEvents();

		ISystemEventListener[] cls = (ISystemEventListener[])listeners.keySet().toArray(new ISystemEventListener[listeners.size()]);

		for(int i = 0; i < cls.length; i++)
		{

			try
			{
				cls[i].systemEventsOccurred(ces);
			}
			catch(Exception e)
			{
				System.err.println("Exception of " + cls[i] + " during handling of " + SUtil.arrayToString(ces));
				e.printStackTrace();
			}
		}
	}*/

	/**
	 * Used to check if management of the tool is still required.
	 */
	public boolean isActive()
	{
		return isactive;
	}

	/**
	 * Called when the tool should be deactivated (e.g. when the agent dies).
	 */
	public void deactivate()
	{
		// System.out.println("call for deactivate() from " + observed);
		try
		{
			// removed: cleanup registration isnt handled anymore on agent death
			// AgentDeathException exception isnt thrown !!!
			// cleanupRegistration();
			isactive = false;
		}
		catch(ComponentTerminatedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Get the component.
	 * 
	 * @return The component.
	 */
	public Component getComponent()
	{
		return tool;
	}

}
