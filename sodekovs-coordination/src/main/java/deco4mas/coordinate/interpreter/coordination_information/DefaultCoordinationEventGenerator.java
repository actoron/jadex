/**
 * 
 */
package deco4mas.coordinate.interpreter.coordination_information;

/**
 * @author Ante Vilenica
 *
 */

import jadex.application.runtime.IApplication;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.environment.EnvironmentEvent;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.PerceptType;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector1;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector1Double;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SimplePropertyObject;
import jadex.commons.collection.MultiCollection;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import deco4mas.coordinate.environment.CoordinationEvent;
import deco4mas.coordinate.environment.CoordinationSpace;

/**
 * Percept generator for agents that have to be coordinated.
 */
public class DefaultCoordinationEventGenerator extends SimplePropertyObject implements ICoordinationEventGenerator {
	// -------- constants --------

	/** The maxrange property. */
	public static String PROPERTY_MAXRANGE = "range";

	/** The maxrange property. */
	public static String PROPERTY_RANGE = "range_property";

	/** The percept types property. */
	public static String PROPERTY_PERCEPTTYPES = "percepttypes";

	/** The appeared percept type. */
	public static String APPEARED = "appeared";

	/** The disappeared percept type. */
	public static String DISAPPEARED = "disappeared";

	/** The created percept type. */
	public static String CREATED = "created";

	/** The destroyed percept type. */
	public static String DESTROYED = "destroyed";

	/** The moved percept type. */
	public static String MOVED = "moved";

	/** The coordinate percept type. Used for common information to be published/transported. */
	public static String COORDINATE_INFO = "coordinate_percept";

	/** The coordinate percept type. Used to initialize the coordination participants. */
	public static String COORDINATE_INIT_PARTICIPANTS = "coordination_init_participants";

	/** Empty spaceobjects array. */
	protected static final ISpaceObject[] EMPTY_SPACEOBJECTS = new ISpaceObject[0];

	// -------- attributes --------

	/** The percept receiving agent types. */
	protected Map actiontypes;

	// -------- IPerceptGenerator --------

	@Override
	public void componentAdded(IComponentIdentifier component, IEnvironmentSpace space) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentRemoved(IComponentIdentifier component, IEnvironmentSpace space) {
		// TODO Auto-generated method stub

	}

	// -------- IEnvironmentListener --------

	/**
	 * Dispatch an environment event to this listener.
	 * 
	 * @param event
	 *            The event.
	 */
	public void dispatchEnvironmentEvent(EnvironmentEvent event) {
		CoordinationSpace space = (CoordinationSpace) event.getSpace();

		IComponentIdentifier eventowner = (IComponentIdentifier) event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);

		if (CoordinationEvent.COORDINATE_BROADCAST.equals(event.getType())) {
			ISpaceObject[] objects = (ISpaceObject[]) space.getNearObjects(new Vector2Double(0.0, 0.0), new Vector1Double(20.0)).toArray(new ISpaceObject[0]);

			for (int i = 0; i < objects.length; i++) {
				IComponentIdentifier owner = (IComponentIdentifier) objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
				IVector2 objpos = (IVector2) objects[i].getProperty(Space2D.PROPERTY_POSITION);
				if (owner != null) {
					String percepttype = getPerceptType(space, ((IApplication) event.getSpace().getContext()).getComponentType(owner), event.getSpaceObject().getType(), COORDINATE_INFO);
					if (percepttype != null) {
						((AbstractEnvironmentSpace) event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner, objects[i]);
					}
				}

				if (eventowner != null) {
					String percepttype = getPerceptType(space, ((IApplication) event.getSpace().getContext()).getComponentType(eventowner), objects[i].getType(), COORDINATE_INFO);
					if (percepttype != null) {
						((AbstractEnvironmentSpace) event.getSpace()).createPercept(percepttype, objects[i], eventowner, event.getSpaceObject());
					}
				}
			}
		} else if (event.getType().startsWith(CoordinationEvent.COORDINATE_DIRECT)) {
			List<IComponentIdentifier> receiver = space.getReceiverData().get(event.getType());

			if (receiver != null) {
				for (IComponentIdentifier receiverIdentifier : receiver) {
					String percepttype = getPerceptType(space, space.getContext().getComponentType(receiverIdentifier), event.getSpaceObject().getType(), COORDINATE_INFO);
					if (percepttype != null) {
						space.createPercept(percepttype, event.getSpaceObject(), receiverIdentifier, space.getAvatar(receiverIdentifier));
					}
				}
			}
		}
	}

	/**
	 * Get the percept type.
	 * 
	 * @param space
	 *            The space.
	 * @param agenttype
	 *            The agent type.
	 * @param objecttype
	 *            The object type.
	 * @param actiontype
	 *            The action type.
	 * @return The matching percept.
	 */
	protected String getPerceptType(IEnvironmentSpace space, String agenttype, String objecttype, String actiontype) {
		String ret = null;

		// if(agenttype.equals("Collector") && objecttype.equals("garbage") &&
		// actiontype.equals(APPEARED))
		// System.out.println("here");

		Object[] percepttypes = getPerceptTypes();
		for (int i = 0; i < percepttypes.length; i++) {
			PerceptType pt = (PerceptType) space.getPerceptType(((String[]) percepttypes[i])[0]);
			if (pt == null)
				throw new RuntimeException("Unknown percept type: " + pt);
			if ((pt.getComponentTypes() == null || pt.getComponentTypes().contains(agenttype)) && (pt.getObjectTypes() == null || pt.getObjectTypes().contains(objecttype))
					&& (getActionTypes(pt) == null || getActionTypes(pt).contains(actiontype))) {
				ret = pt.getName();
			}
		}

		if (ret == null) {
			// System.out.println("No percept found for: "+agenttype+" "+objecttype+" "+actiontype);
		}

		return ret;
	}

	/**
	 * Get the action types for a percept.
	 * 
	 * @param pt
	 *            The percept type.
	 */
	protected Set getActionTypes(PerceptType pt) {
		if (actiontypes == null) {
			actiontypes = new MultiCollection(new HashMap(), HashSet.class);
			Object[] percepttypes = getPerceptTypes();
			for (int i = 0; i < percepttypes.length; i++) {
				String[] per = (String[]) percepttypes[i];
				for (int j = 1; j < per.length; j++) {
					actiontypes.put(per[0], per[j]);
				}
			}
		}
		Set ret = (Set) actiontypes.get(pt.getName());
		return ret == null ? Collections.EMPTY_SET : ret;
	}

	/**
	 * Get the percept types defined for this generator.
	 * 
	 * @return The percept types.
	 */
	protected Object[] getPerceptTypes() {
		return (Object[]) getProperty(PROPERTY_PERCEPTTYPES);
	}

	/**
	 * Get the range.
	 * 
	 * @return The range.
	 */
	protected IVector1 getRange(ISpaceObject avatar) {
		Object tmp = avatar.getProperty(getRangePropertyName());
		return tmp == null ? getDefaultRange() : tmp instanceof Number ? new Vector1Double(((Number) tmp).doubleValue()) : (IVector1) tmp;
	}

	/**
	 * Get the default range.
	 * 
	 * @return The range.
	 */
	protected IVector1 getDefaultRange() {
		Object tmp = getProperty(PROPERTY_MAXRANGE);
		return tmp == null ? Vector1Double.ZERO : tmp instanceof Number ? new Vector1Double(((Number) tmp).doubleValue()) : (IVector1) tmp;
	}

	/**
	 * Get the range property name.
	 * 
	 * @return The range property name.
	 */
	protected String getRangePropertyName() {
		Object tmp = getProperty(PROPERTY_RANGE);
		return tmp == null ? "range" : (String) tmp;
	}

	/**
	 * 
	 * / protected boolean isObjectInRange(Space2D space, ISpaceObject source, ISpaceObject target) { IVector2 pos1 = (IVector2)source.getProperty(Space2D.POSITION); IVector2 pos2 =
	 * (IVector2)target.getProperty(Space2D.POSITION); return !getRange(source).greater(space.getDistance(pos1, pos2)); }
	 */
}
