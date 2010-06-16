package jadex.wfms.simulation;

import jadex.wfms.simulation.stateholder.ActivityStateController;
import jadex.wfms.simulation.stateholder.IParameterStateSet;
import jadex.wfms.simulation.stateholder.ProcessStateController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A test scenario.
 */
public class Scenario implements ChangeListener
{
	/** The scenario name. */
	private String name;
	
	/** The parameter map. */
	private Map parameterMap; 
	
	/** State change listeners */
	public Set stateListeners;
	
	public Scenario(String name, Map parameterMap)
	{
		this.name = name;
		this.parameterMap = parameterMap;
		this.stateListeners = new HashSet();
		for (Iterator it = getParameterSets().iterator(); it.hasNext(); )
			((IParameterStateSet) it.next()).addStateChangeListener(this);
	}
	
	/**
	 * Returns the task->parameters mapping for the scenario.
	 * @return parameter mapping
	 */
	public Map getParameterMap()
	{
		return parameterMap;
	}
	
	/**
	 * Returns the map for parameters of a task.
	 * @return parameters of a task
	 */
	public Map getTaskParameters(String taskName)
	{
		return ((Map) parameterMap.get(taskName));
	}
	
	/**
	 * Returns the specified parameter set of a task.
	 * @return parameter set of a task
	 */
	public IParameterStateSet getTaskParameter(String taskName, String parameterName)
	{
		return (IParameterStateSet) getTaskParameters(taskName).get(parameterName); 
	}
	
	/**
	 * Returns a list of all parameter sets
	 * @return list of all parameter sets
	 */
	public List getParameterSets()
	{
		List sets = new ArrayList();
		for (Iterator it = parameterMap.values().iterator(); it.hasNext(); )
			for (Iterator it2 = ((Map) it.next()).values().iterator(); it2.hasNext(); )
				sets.add(it2.next());
		return sets;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	public ProcessStateController createProcessStateController()
	{
		ProcessStateController pController = new ProcessStateController();
		
		for (Iterator it = parameterMap.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Map.Entry) it.next();
			if (((Map) entry.getValue()).size() > 0)
			{
				ActivityStateController aController = new ActivityStateController((String) entry.getKey());
				
				Collection parameterSets = ((Map) entry.getValue()).values();
				for (Iterator it2 = parameterSets.iterator(); it2.hasNext(); )
					aController.addStateSet((IParameterStateSet) it2.next());
				
				pController.addActivityController(aController);
			}
		}
		
		return pController;
	}
	
	public void addStateChangeListener(ChangeListener listener)
	{
		stateListeners.add(listener);
	}
	
	public void removeStateChangeListener(ChangeListener listener)
	{
		stateListeners.remove(listener);
	}
	
	public void clearStateChangeListeners()
	{
		stateListeners.clear();
	}
	
	public void stateChanged(ChangeEvent e)
	{
		for (Iterator it = stateListeners.iterator(); it.hasNext(); )
			((ChangeListener) it.next()).stateChanged(e);
	}
	
	public String toString()
	{
		return name;
	}
}
