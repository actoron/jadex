package deco4mas.coordinate.environment;

import jadex.application.model.MSpaceInstance;
import jadex.application.runtime.IApplication;
import jadex.application.space.envsupport.MEnvSpaceInstance;
import jadex.application.space.envsupport.MEnvSpaceType;
import jadex.application.space.envsupport.MObjectType;
import jadex.application.space.envsupport.environment.AvatarMapping;
import jadex.application.space.envsupport.environment.EnvironmentEvent;
import jadex.application.space.envsupport.environment.IPerceptGenerator;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Grid2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.bridge.IComponentIdentifier;
import jadex.javaparser.SimpleValueFetcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import test.GetPropertyThread;
import deco.lang.dynamics.MASDynamics;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInformation;
import deco4mas.mechanism.ICoordinationMechanism;

public class CoordinationSpace extends Grid2D
{

	/** The list of currently supported / active coordination mechanisms. */
	// TODO: HACK! Change to private!
	public ArrayList<ICoordinationMechanism> activeCoordinationMechanisms = new ArrayList<ICoordinationMechanism>();
	private MASDynamics masDnyModel = null;
	// Contains the "RoleDefinitionsForPerceive" for each agent type.
	private Map<String, Map<String, Set<Object[]>>> agentData = new HashMap<String, Map<String, Set<Object[]>>>();

	// private ArrayList<IAgentIdentifier> agentIdentifierList = new
	// ArrayList<IAgentIdentifier>();

	// -------- constructors --------

	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 * 
	 * @param clockService
	 *            the clock service
	 * @param timeCoefficient
	 *            the time coefficient for time differences.
	 * @param areaSize
	 *            the size of the 2D area
	 */
	public CoordinationSpace()
	{
		super(CoordinationSpace.class.getName(), null);

	}

	@Override
	public void initSpace(IApplication context, MSpaceInstance config) throws Exception
	{
		super.initSpace(context, config);

		initSpaces();
		initDeco4mas();
		for (ICoordinationMechanism icord : activeCoordinationMechanisms)
		{
			icord.start();
		}

	}

	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 * 
	 * @param actionexecutor
	 *            executor for agent actions
	 * @param areasize
	 *            the size of the 2D area
	 */
	// public CoordinationSpace(IVector2 areasize, String neighborhood) {
	// super(DEFAULT_NAME, areasize, BORDER_TORUS, neighborhood);
	// initSpace();
	// initDeco4mas();
	// }

	/**
	 * Creates a new {@link ContinuousSpace2D} with a special ID.
	 * 
	 * @param name
	 *            the name of this space
	 * @param areasize
	 *            the size of the 2D area
	 * @param actionexecutor
	 *            executor for agent actions
	 */
	// public CoordinationSpace(Object name, IVector2 areasize, String
	// bordermode, String neighborhood)
	// {
	// super(areasize==null? null: new Vector2Int(areasize.getXAsInteger(),
	// areasize.getYAsInteger()), bordermode);
	// this.setProperty("name", name);
	// super.setNeighborhood(neighborhood==null?
	// Grid2D.NEIGHBORHOOD_VON_NEUMANN: neighborhood);
	// this.objectsygridpos = new MultiCollection();
	// System.out.println("Called 33");
	// initCoordinationMechanisms();
	// }
	/**
	 * Used to CONSUME!!! Coordination Events, that are produced by the Agent
	 * State Interpreter, and to pass them to the Coordination Medium
	 * (Endpoint).
	 * 
	 * @param obj
	 */
	public void perceiveCoordinationEvent(Object obj)
	{
		if (obj instanceof CoordinationInformation)
		{
			CoordinationInformation ci = (CoordinationInformation) obj;
			for (ICoordinationMechanism mechanism : activeCoordinationMechanisms)
			{
				if (mechanism.getRealisationName().equals(ci.getValueByName(Constants.DML_REALIZATION_NAME)))
				{
					mechanism.perceiveCoordinationEvent(obj);
				}
			}
		}
	}

	// setter and getter for coordinationMedium

	/**
	 * Used to publish Coordination Events that are produced by the Coordination
	 * Mechanism, i.e. those coordination information that have been produced by
	 * the medium and that can be perceived by the agents via their Coordination
	 * Information Interpreter.
	 * 
	 * @param objFperce
	 */
	public void publishCoordinationEvent(Object obj)
	{
		// pass event the currently used coordination medium
		// System.out.println("#CoordinationSpace# EnvironentEvent fired...");
		ISpaceObject newObj = this.createSpaceObject("CoordinationSpaceObject", ((CoordinationInformation) obj).getValues(), null);
		newObj.setProperty(Constants.ROLE_DEFINITIONS_FOR_PERCEIVE, agentData);
		this.fireEnvironmentEvent(new EnvironmentEvent(CoordinationEvent.COORDINATE_START, this, newObj, new String(
			"Coordinate Event Nr...."), null));
	}

	/**
	 * Responsible to initialize the space itself.
	 */
	private void initSpaces()
	{
		// this object type is used within the deco4mas coordination.
		MObjectType mobject = new MObjectType();
		mobject.setName("CoordinationSpaceObject");
		this.addSpaceObjectType("CoordinationSpaceObject", mobject);
	}

	/**
	 * Responsible to initialize all aspects related to deco4MAS.
	 */
	private void initDeco4mas()
	{
		// TODO: Start a Thread for every Coordination-Mechanism!
		// System.out.println("#CoordinationSpace# Called initDecom4masParticipants Media..");

		// GetPropertyThread th = new GetPropertyThread(this);
		// th.run();
		//	
		//			
		//		
		// try {
		// th.join();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		//		
		// }
		// System.out.println(" - Thread finished!!!");

		masDnyModel = new InitDeco4mas(this).start();
	}

	/**
	 * This methods causes the creation of the
	 * INIT_Deco4MAS_Coordination-Percept. This percept is perceived by those
	 * Agent which participate as "active parts/members", means those agents
	 * that create percepts. The percept triggers a plan within the agents and
	 * initializes the "Agent-State-Interpreter"
	 * /"Agent Behaviour Observation Component".
	 */

	// public void initParticipatingAgents(MASDynamics masDyn ,ArrayList<String>
	// fromAgents, ArrayList<String> toAgents){
	private void initParticipatingAgent(IComponentIdentifier ai)
	{
		//		
		// Map<String, Object[]> res = new
		// InitBDIAgentForCoordination().startInits(ai, this.getContext(), this,
		// masDnyModel);
		// agentData.put(getAgentType(ai, this.getContext()), res);
		new InitBDIAgentForCoordination().startInits(ai, this.getContext(), this, masDnyModel);
		// agentData.put(getAgentType(ai, this.getContext()), res);

	}

	// /**
	// * Called when an agent was added.
	// */
	// public void agentAdded(IComponentIdentifier aid) {
	// synchronized (monitor) {
	// // Possibly add or create avatar(s) if any.
	// if (initialavatars != null && initialavatars.containsKey(aid)) {
	// Object[] ia = (Object[]) initialavatars.get(aid);
	// String objecttype = (String) ia[0];
	// Map props = (Map) ia[1];
	// if (props == null)
	// props = new HashMap();
	// props.put(ISpaceObject.PROPERTY_OWNER, aid);
	// createSpaceObject(objecttype, props, null);
	// } else {
	// // String agenttype = ((ApplicationContext)
	// getContext()).getAgentType(aid);
	// String agenttype = ((IApplication) getContext()).getComponentType(aid);
	// if (agenttype != null && avatarmappings.getCollection(agenttype) != null)
	// {
	// for (Iterator it = avatarmappings.getCollection(agenttype).iterator();
	// it.hasNext();) {
	// AvatarMapping mapping = (AvatarMapping) it.next();
	// if (mapping.isCreateAvatar()) {
	// Map props = new HashMap();
	// props.put(ISpaceObject.PROPERTY_OWNER, aid);
	// // createSpaceObject(mapping.getAvatarType(), props, null);
	// createSpaceObject(mapping.getObjectType(), props, null);
	// }
	// }
	// }
	// }
	//
	// if (perceptgenerators != null) {
	// for (Iterator it = perceptgenerators.keySet().iterator(); it.hasNext();)
	// {
	// IPerceptGenerator gen = (IPerceptGenerator)
	// perceptgenerators.get(it.next());
	// gen.componentAdded(aid, this);
	// }
	// }
	// // init Agent for deco4MAS participation
	// initParticipatingAgent(aid);
	// }
	// }

	/**
	 * Called when an component was added.
	 */
	public void componentAdded(IComponentIdentifier aid, String type)
	{
		synchronized (monitor)
		{
			// Possibly add or create avatar(s) if any.
			if (initialavatars != null && initialavatars.containsKey(aid))
			{
				Object[] ia = (Object[]) initialavatars.get(aid);
				String objecttype = (String) ia[0];
				Map props = (Map) ia[1];
				if (props == null)
					props = new HashMap();
				props.put(ISpaceObject.PROPERTY_OWNER, aid);
				createSpaceObject(objecttype, props, null);
			} else
			{
				String componenttype = context.getComponentType(aid);
				if (componenttype != null && avatarmappings.getCollection(componenttype) != null)
				{
					for (Iterator it = avatarmappings.getCollection(componenttype).iterator(); it.hasNext();)
					{
						AvatarMapping mapping = (AvatarMapping) it.next();
						if (mapping.isCreateAvatar())
						{
							Map props = new HashMap();
							props.put(ISpaceObject.PROPERTY_OWNER, aid);
							createSpaceObject(mapping.getObjectType(), props, null);
						}
					}
				}
			}

			if (perceptgenerators != null)
			{
				for (Iterator it = perceptgenerators.keySet().iterator(); it.hasNext();)
				{
					IPerceptGenerator gen = (IPerceptGenerator) perceptgenerators.get(it.next());
					gen.componentAdded(aid, this);
				}
			}
			// init Agent for deco4MAS participation
			initParticipatingAgent(aid);
		}
	}

	/**
	 * @return the agentData
	 */
	public Map<String, Map<String, Set<Object[]>>> getAgentData()
	{
		return agentData;
	}
}
