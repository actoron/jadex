package eis.jadex;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.IEnvironmentListener;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IPlatform;
import jadex.commons.collection.MultiCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eis.AgentListener;
import eis.EnvironmentInterfaceStandard;
import eis.EnvironmentListener;
import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.EntityException;
import eis.exceptions.EnvironmentInterfaceException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.exceptions.RelationException;
import eis.iilang.Action;
import eis.iilang.ActionResult;
import eis.iilang.EnvironmentCommand;
import eis.iilang.EnvironmentEvent;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;

/**
 *  Implementation draft for a delegation implementation of EIS.
 *  todo: make thread safe.
 */
public class JadexDelegationEisImpl extends EnvironmentInterfaceStandard
{
	//-------- constants --------
	
	/** Basic entity type (as EIS is untyped). */
	public static final String ENTITY = "entity";
	
	//-------- attributes --------
	
	/** The space. */
	protected AbstractEnvironmentSpace space;
	
	/** Listener to listener support. */
	protected Map envlisteners;

	/** Agent listeners for percept handling. */
	protected MultiCollection agentlisteners;
	
	/** Registered agents. */
	protected List regagents;
	
	//-------- constructors --------
	
	/**
	 *  Create a new environment space.
	 */
	public JadexDelegationEisImpl(AbstractEnvironmentSpace space)
	{
		this.space = space;
		this.envlisteners = Collections.synchronizedMap(new HashMap());
		this.agentlisteners = new MultiCollection();
		this.regagents = Collections.synchronizedList(new ArrayList());
		space.addSpaceObjectType(ENTITY, null);
	}
	
	//-------- methods --------
	
	/**
	 * Attaches an environment-listener.
	 * If the listener is already attached, nothing will happen.
	 * @param listener
	 */
	public void attachEnvironmentListener(final EnvironmentListener listener) 
	{
		IEnvironmentListener lis = new IEnvironmentListener()
		{
			public void dispatchEnvironmentEvent(
				jadex.adapter.base.envsupport.environment.EnvironmentEvent event)
			{
				if(jadex.adapter.base.envsupport.environment.EnvironmentEvent.OBJECT_CREATED.equals(event.getType()))
				{
					listener.handleNewEntity((String)event.getSpaceObject().getId());
				}
				else if(jadex.adapter.base.envsupport.environment.EnvironmentEvent.OBJECT_DESTROYED.equals(event.getType()))
				{
					listener.handleDeletedEntity((String)event.getSpaceObject().getId());
				}
				else
				{
					// todo:
					System.out.println("support me: "+event);
				}
			}
		};
		
		envlisteners.put(listener, lis);
		
		space.addEnvironmentListener(lis);
	}

	/**
	 * Detaches an environment-listener.
	 * If the listener is not attached, nothing will happen.
	 */
	public void detachEnvironmentListener(EnvironmentListener listener) 
	{
		IEnvironmentListener lis = (IEnvironmentListener)envlisteners.remove(listener);
		if(lis!=null)
			space.removeEnvironmentListener(lis);
	}
	
	/**
	 * Attaches an agent-listener.
	 * If the agent has not been registered nothing will happen.
	 * @param agent
	 * @param listener
	 */
	public void attachAgentListener(String agent, AgentListener listener) 
	{
		agentlisteners.put(agent, listener);
	}
	
	/**
	 * Detaches an agent-listener.
	 * If the agent has not been registered and/or the listener does not exist nothing will happen.
	 * @param agent
	 * @param listener
	 */
	public void detachAgentListener(String agent, AgentListener listener) 
	{
		agentlisteners.remove(agent, listener);
	}
	
	/**
	 *  Get agent listeners for a specific agent.
	 */
	public AgentListener[] getAgentListeners(String agent)
	{
		return (AgentListener[])agentlisteners.getCollection(agent).toArray(new AgentListener[0]);
	}
	
	/**
	 * Notifies agents about a percept.
	 * @param percept is the percept
	 * @param agents is the array of agents that are to be notified about the event.
	 * If the array is empty, all registered agents will be notified. The array has to 
	 * contain only registered agents.
	 * @throws AgentException is thrown if at least one of the agents in the array is not
	 * registered.
	 */
	protected void notifyAgents(Percept percept, String...agents) throws EnvironmentInterfaceException 
	{
		// todo:
	}
	
	/**
	 * Sends a percept to an agent/several agents via a given array of entities.
	 * @param percept
	 * @param entity
	 * @throws EnvironmentInterfaceException
	 */
	protected void notifyAgentsViaEntity(Percept percept, String...pEntities) throws EnvironmentInterfaceException 
	{
		// todo:
	} 
	
	
	/**
	 * Notifies all listeners about an entity that is free.
	 * @param entity is the free entity.
	 */
	protected void notifyFreeEntity(String entity) 
	{
		// todo:
	}

	
	
	/**
	 * Notifies all listeners about an entity that has been newly created.
	 * @param entity is the new entity.
	 */
	protected void notifyNewEntity(String entity) 
	{
		// todo:
	}

	
	/**
	 * Notifies all listeners about an entity that has been deleted.
	 * @param entity is the deleted entity.
	 */
	protected void notifyDeletedEntity(String entity) 
	{
		// todo:
	}

	/**
	 * Notifies the listeners about an environment-event.
	 * @param event
	 */
	protected void notifyEnvironmentEvent(EnvironmentEvent event) 
	{
		// todo:
	}

	/*
	 * Registering functionality. Registering and unregistering agents.
	 */

	/**
	 * Registers an agent to the environment.
	 * @param agent the identifier of the agent.
	 * @throws PlatformException if the agent has already been registered.
	 */
	public void registerAgent(String agent) throws AgentException 
	{
		regagents.add(agent);
	}

	/**
	 * Unregisters an agent from the environment.
	 * @param agent the identifier of the agent.
	 * @throws AbstractException if the agent has not registered before.
	 */
	public void unregisterAgent(String agent) throws AgentException 
	{
		regagents.remove(agent);
	}
	
	/*
	 * Entity functionality. Adding and removing entities.
	 */
	
	/**
	 * Retrieves the list of registered agents.
	 * @return a list of agent-ids.
	 */
	public List getAgents() 
	{
		return regagents;
//		return SUtil.arrayToList(space.getAgents());
	}

	/** 
	 * Adds an entity to the environment.
	 * @param entity is the identifier of the entity that is to be added.
	 * @throws PlatformException is thrown if the entity already exists.
	 */
	public void addEntity(String entity) throws EntityException 
	{
		space.createSpaceObject(ENTITY, null, null);
	}

	/**
	 * Deletes an entity, by removing its id from the internal list, and disassociating 
	 * it from the respective agent.
	 * @param entity the id of the entity that is to be removed.
	 * @throws PlatformException if the agent does not exist.
	 */
	// TODO use freeEntity here
	public void deleteEntity(String entity) throws EntityException 
	{
		space.destroySpaceObject(entity);
	}

	
		
	/**
	 * Retrieves the list of entities.
	 * @return a list of entity-ids.
	 */
	public List<String> getEntities() 
	{
		ISpaceObject[] ents = space.getSpaceObjectsByType(ENTITY);
		List ret = new ArrayList();
		if(ents!=null)
		{
			for(int i=0; i<ents.length; i++)
			{
				ret.add(ents[i].getId());
			}
		}
		return ret;
	}
	
	/*
	 * Agents-entity-relation manipulation functionality.
	 */

	/**
	 * Associates an entity with an agent.
	 * @param agent the id of the agent.
	 * @param entity the id of the entity.
	 * @throws PlatformException if the entity is not free, and if it or the agent does not exist.
	 */
	public void associateEntity(String agent, String entity) throws RelationException 
	{
		space.setOwner(entity, convertStringToAgentIdentifier(agent));
	}

	/**
	 * Frees an entity from its associated agent.
	 * @param entity the id of the entity to be freed.
	 * @throws PlatformException is thrown if the entity does not exist, or if it is not associated.
	 */
	public void freeEntity(String entity) throws RelationException 
	{
		space.setOwner(entity, null);
	}
	
	/**
	 * Frees an agent from the agents-entities-relation.
	 * @param agent is the agent to be freed.
	 * @throws RelationException is thrown if the agent has not been registered.
	 */
	public void freeAgent(String agent) throws RelationException 
	{
		// todo:
	}

	/**
	 * Returns the entities associated to a given agent.
	 * @param agent is the agent.
	 * @return a set of entities.
	 * @throws AgentException 
	 */
	public Set<String> getAssociatedEntities(String agent) throws AgentException 
	{
		ISpaceObject[] obs = space.getAvatars(convertStringToAgentIdentifier(agent));
		Set ret = new HashSet();
		if(obs!=null)
		{
			for(int i=0; i<obs.length; i++)
			{
				ret.add(obs[i].getId().toString());
			}
		}
		return ret;
	}

	/**
	 * Returns the agents associated to a given entity.
	 * @param entity is the entity.
	 * @return a set of agents.
	 * @throws AgentException 
	 */
	protected Set<String> getAssociatedAgents(String entity) throws EntityException 
	{
		Set ret = new HashSet();
		ret.add(space.getSpaceObject(entity).getProperty(ISpaceObject.PROPERTY_OWNER));
		return ret;
	}

	/**
	 * Retrieves the list of free entities.
	 * @return a list of entity-ids.
	 */
	public List getFreeEntities() 
	{
		// todo:
		return null; 
	}

	/*
	 * Acting/perceiving functionality.
	 */

	/**
	 * Lets an agent perform an agent.
	 * <p/>
	 * This method firstly determines the entities through which the agent should act.
	 * Secondly Java-reflection is used to determine the methods that belong to the
	 * given action. Finally, those methods are invoked and the return-values are gathered.
	 * 
	 * @param agent is the agent that is supposed to act.
	 * @param action is the action. The action's name determines the name of the method that is called.
	 * @param entities is an array of entities through which an agent is supposed to act. If the 
	 * array is empty, all entities are used.
	 * @return a list of action-results.
	 * @throws PerceiveOrActFailureException is thrown if the agent has not been registered,
	 * if the agent has no associated entities, if at least one of the given entities is not 
	 * associated, or if at least one one the actions fails.
	 */
	// TODO use freeAgent here
	// TODO maybe use isConnencted here
	public List<ActionResult> performAction(String agent, Action action, String...entities)
		throws ActException, NoEnvironmentException 
	{
		LinkedList paramvals = action.getParameters();
		List ret = new ArrayList();
		ISpaceAction spact = space.getSpaceAction(action.getName());
		String[] propnames = (String[])spact.getPropertyNames().toArray(new String[0]);
		Map ps = new HashMap();
		
		if(paramvals.size()!=propnames.length)
			throw new RuntimeException("Parameter problem: "+paramvals+" "+propnames);
		
		for(int i=0; i<propnames.length; i++)
		{
			Parameter param = (Parameter)paramvals.get(i);
			Object paramval;
			if(param instanceof Identifier)
				paramval = ((Identifier)param).getValue();
			else if(param instanceof Numeral)
				paramval = ((Numeral)param).getValue();
			else
				throw new RuntimeException("Unknown parameter type: "+param);
			ps.put(propnames[i], paramval);
		}
		
		Object val = space.performSpaceAction(action.getName(), ps);
		if(val!=null)
			ret.add(new ActionResult(val.toString(), null));
		return ret;
	}
	
	/** 
	 * Gets all percepts.
	 * <p/>
	 * Either returns the percepts of all associated entities of or a subset.
	 * 
	 * @param agent the agent that requests the percepts.
	 * @return a list of percepts
	 * @throws PerceiveException if the agent is not registered or if the agents requests percepts from an entity that is not associated.
	 */
	// TODO maybe use isConnencted here
	public List<Percept> getAllPercepts(String agent, String...entities) 
		throws PerceiveException, NoEnvironmentException 
	{
		// todo:
		return null;
	}
	
	/**
	 * Gets all percepts of an entity.
	 * <p/>
	 * This method must be overridden.
	 * 
	 * @param entity is the entity whose percepts should be retrieved.
	 * @return a list of percepts.
	 */
	protected List<Percept> getAllPerceptsFromEntity(String entity)
	{
		return null;
	}

	/*
	 * Management functionality.
	 */
	
	/**
	 * Invoked to manage the environment and/or its execution.
	 * 
	 * @param command is the command that is to be executed.
	 * @param args is an array of arguments to the command
	 */
	public void manageEnvironment(EnvironmentCommand command, String... args) 
		throws ManagementException,NoEnvironmentException
	{
	}

	/*
	 * Misc functionality.
	 */
	
	/**
	 * Releases the environment interface.
	 */
	public void release()
	{
	}
	
	/**
	 * Returns true if the interface is connected to the environment
	 * @return true or false
	 */
	public boolean isConnected()
	{
		return true;
	}
	
	/**
	 *  Create an agent identifier from a string.
	 *  @param name The name.
	 *  @return The agent identifier.
	 */
	protected IComponentIdentifier convertStringToAgentIdentifier(String name)
	{
		IPlatform platform = ((IApplicationContext)space.getContext()).getPlatform();
		IComponentExecutionService	ces	= (IComponentExecutionService)platform.getService(IComponentExecutionService.class);
		return ces.createComponentIdentifier(name, name.contains("@")? false: true, null);
	}
	

}


