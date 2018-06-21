package jadex.wfms.simulation;

import jadex.wfms.simulation.stateset.ActivityStateController;
import jadex.wfms.simulation.stateset.IParameterStateSet;
import jadex.wfms.simulation.stateset.ProcessStateController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
	
	/** The task validation map */
	private Map taskValidationInfo;
	
	/** State change listeners */
	public Set stateListeners;
	
	public Scenario(String name, Map parameterMap)
	{
		this.name = name;
		this.parameterMap = parameterMap;
		this.taskValidationInfo = new HashMap();
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
	 *  Sets the validation information for a task.
	 *  
	 *  @param taskName Name of the task.
	 *  @param activationConstraint Constraint on the number of activations 
	 */
	public void setTaskValidationInfo(String taskName, ActivationConstraint activationConstraint)
	{
		taskValidationInfo.put(taskName, activationConstraint);
	}
	
	/**
	 *  Removes the validation information for a task.
	 *  
	 *  @param taskName Name of the task. 
	 */
	public void removeTaskValidationInfo(String taskName)
	{
		taskValidationInfo.remove(taskName);
	}
	
	/**
	 *  Gets the validation information for a task.
	 *  
	 *  @param taskName Name of the task.
	 *  @return The Task validation information. 
	 */
	public ActivationConstraint getTaskValidationInfo(String taskName)
	{
		return (ActivationConstraint) taskValidationInfo.get(taskName);
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
