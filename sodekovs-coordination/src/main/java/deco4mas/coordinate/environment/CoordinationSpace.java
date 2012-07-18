package deco4mas.coordinate.environment;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.extension.envsupport.MObjectType;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.EnvironmentEvent;
import jadex.extension.envsupport.environment.IPerceptGenerator;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.micro.IMicroExternalAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import deco.lang.dynamics.MASDynamics;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInformation;
import deco4mas.mechanism.ICoordinationMechanism;

@SuppressWarnings("unchecked")
public class CoordinationSpace extends AbstractEnvironmentSpace {

	/** The list of currently supported / active coordination mechanisms. */
	private ArrayList<ICoordinationMechanism> activeCoordinationMechanisms = new ArrayList<ICoordinationMechanism>();

	private MASDynamics masDnyModel = null;
	// Contains the "RoleDefinitionsForPerceive" for each agent type.
	private Map<String, Map<String, Set<Object[]>>> agentData = new HashMap<String, Map<String, Set<Object[]>>>();

	/** Contains the receivers for the direct addressing */
	private Map<String, List<IComponentDescription>> receiverData = new HashMap<String, List<IComponentDescription>>();

	/** Contains the mapping of an agent's name and it's {@link IComponentDescription} */
	private Map<String, IComponentDescription> descriptionMapping = new HashMap<String, IComponentDescription>();

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
	public CoordinationSpace() {
		// super(CoordinationSpace.class.getName(), null);
		this.setProperty("name", CoordinationSpace.class.getName());
	}

	/**
	 * @return the descriptionMapping
	 */
	public Map<String, IComponentDescription> getDescriptionMapping() {
		return descriptionMapping;
	}

	@Override
	public IFuture<Void> initSpace() {
//		super.setInitData(ia, config, fetcher);
		super.initSpace();

		initSpaces();
		initDeco4mas();
		for (ICoordinationMechanism icord : activeCoordinationMechanisms) {
			icord.start();
		}
		
		return IFuture.DONE;
	}

	/**
	 * Used to CONSUME!!! Coordination Events, that are produced by the Agent State Interpreter, and to pass them to the Coordination Medium (Endpoint).
	 * 
	 * @param obj
	 */
	public void perceiveCoordinationEvent(Object obj) {
		if (obj instanceof CoordinationInformation) {
			CoordinationInformation ci = (CoordinationInformation) obj;
			for (ICoordinationMechanism mechanism : activeCoordinationMechanisms) {
				if (mechanism.getRealisationName().equals(ci.getValueByName(Constants.DML_REALIZATION_NAME))) {
					mechanism.perceiveCoordinationEvent(obj);
				}
			}
		}
	}

	/**
	 * Used to publish Coordination Events that are produced by the Coordination Mechanism, i.e. those coordination information that have been produced by the medium and that can be perceived by the
	 * agents via their Coordination Information Interpreter.
	 * 
	 * @param obj
	 */
	public void publishCoordinationEvent(Object obj) {
		// pass event the currently used coordination medium
		// System.out.println("#CoordinationSpace# EnvironentEvent fired...");
		ISpaceObject newObj = this.createSpaceObject("CoordinationSpaceObject", ((CoordinationInformation) obj).getValues(), null);
		newObj.setProperty(Constants.ROLE_DEFINITIONS_FOR_PERCEIVE, agentData);
		this.fireEnvironmentEvent(new EnvironmentEvent(CoordinationEvent.COORDINATE_BROADCAST, this, newObj, new String("Coordinate Event Nr...."), null));
	}

	/**
	 * Used to publish Coordination Events that are produced by the Coordination Mechanism, i.e. those coordination information that have been produced by the medium and that can be perceived by the
	 * agents via their Coordination Information Interpreter. This method is to be used if the coordination information should only be passed to a given list of receiving agents.
	 * 
	 * @param obj
	 * @param receiver
	 *            a {@link List} of receiving agents
	 * @param realizationName
	 *            the name of the realization
	 * @param mechanismEventNumber
	 *            the number of the event in the used mechanism
	 */
	public void publishCoordinationEvent(Object obj, List<IComponentDescription> receiver, String realizationName, Integer mechanismEventNumber) {
		ISpaceObject newObj = this.createSpaceObject("CoordinationSpaceObject", ((CoordinationInformation) obj).getValues(), null);
		newObj.setProperty(Constants.ROLE_DEFINITIONS_FOR_PERCEIVE, agentData);

		String key = CoordinationEvent.COORDINATE_DIRECT;
		if (realizationName != null) {
			key += "::" + realizationName;
		}
		if (mechanismEventNumber != null) {
			key += "::" + mechanismEventNumber;
		}

		receiverData.put(key, receiver);
		this.fireEnvironmentEvent(new EnvironmentEvent(key, this, newObj, "Coordinate Event Nr. " + mechanismEventNumber, null));
	}

	/**
	 * @return the receiverData
	 */
	public Map<String, List<IComponentDescription>> getReceiverData() {
		return receiverData;
	}

	/**
	 * Responsible to initialize the space itself.
	 */
	private void initSpaces() {
		// this object type is used within the deco4mas coordination.
		MObjectType mobject = new MObjectType();
		mobject.setName("CoordinationSpaceObject");
		this.addSpaceObjectType("CoordinationSpaceObject", mobject);
	}

	/**
	 * Responsible to initialize all aspects related to deco4MAS.
	 */
	private void initDeco4mas() {
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
	 * This methods causes the creation of the INIT_Deco4MAS_Coordination-Percept. This percept is perceived by those Agent which participate as "active parts/members", means those agents that create
	 * percepts. The percept triggers a plan within the agents and initializes the "Agent-State-Interpreter" /"Agent Behaviour Observation Component".
	 */
	private void initParticipatingAgent(final IComponentDescription ai) {
		// get the IComponentManagementService
		IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getServiceUpwards(this.getExternalAccess().getServiceProvider(), IComponentManagementService.class).get(
				new ThreadSuspendable());

		// get the external access for the agent
		IFuture<IExternalAccess> fut = cms.getExternalAccess(ai.getName());
		fut.addResultListener(new DefaultResultListener<IExternalAccess>() {

			@Override
			public void resultAvailable(IExternalAccess externalAccess) {
				// check if an external access for an BDI or Micro agent is
				// needed
				if (externalAccess instanceof IBDIExternalAccess) {
					IBDIExternalAccess exta = (IBDIExternalAccess) externalAccess;

					new InitBDIAgentForCoordination().startInits(ai, exta, CoordinationSpace.this, masDnyModel);
				} else if (externalAccess instanceof IMicroExternalAccess) {
					IMicroExternalAccess exta = (IMicroExternalAccess) externalAccess;

					new InitMicroAgentForCoordination().startInits(ai, exta, CoordinationSpace.this, masDnyModel);
				}
			}
		});
	}

	/**
	 * Called when an component was added.
	 */
	@Override
	public void componentAdded(IComponentDescription owner) {
		synchronized (monitor) {
			// Possibly add or create avatar(s) if any.
			List<ISpaceObject> ownedobjs = (List<ISpaceObject>) spaceobjectsbyowner.get(owner);
			if (ownedobjs == null) {
				createAvatar(owner, null, false);
			} else {
				// Init zombie avatars.
				for (Iterator<ISpaceObject> it = ownedobjs.iterator(); it.hasNext();) {
					ISpaceObject obj = it.next();
					if (!spaceobjects.containsKey(obj.getId())) {
						initSpaceObject(obj);
					}
				}
			}

			if (perceptgenerators != null) {
				for (@SuppressWarnings("rawtypes")
				Iterator it = perceptgenerators.keySet().iterator(); it.hasNext();) {
					IPerceptGenerator gen = (IPerceptGenerator) perceptgenerators.get(it.next());
					gen.componentAdded(owner, this);
				}
			}
			// init Agent for deco4MAS participation
			initParticipatingAgent(owner);
		}
	}

	/**
	 * @return the agentData
	 */
	public Map<String, Map<String, Set<Object[]>>> getAgentData() {
		return agentData;
	}

	/**
	 * @return the activeCoordinationMechanisms
	 */
	public ArrayList<ICoordinationMechanism> getActiveCoordinationMechanisms() {
		return activeCoordinationMechanisms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.extension.envsupport.environment.AbstractEnvironmentSpace#createAvatar(jadex.bridge.IComponentDescription, java.lang.String, boolean)
	 */
	@Override
	protected ISpaceObject createAvatar(IComponentDescription owner, String fullname, boolean zombie) {
		descriptionMapping.put(owner.getName().getLocalName(), owner);

		return super.createAvatar(owner, fullname, zombie);
	}
}
