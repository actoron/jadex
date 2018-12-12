package jadex.bdi.examples.hunterprey_classic;

import jadex.bridge.IComponentIdentifier;


/**
 *  Editable Java class for concept Observer of hunterprey ontology.
 */
public class Observer extends Creature
{
	//-------- constructors --------

	/**
	 *  Create a new Observer.
	 */
	public Observer()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new Observer.
	 */
	public Observer(String name, IComponentIdentifier aid, Location location)
	{
		// Constructor using required slots (change if desired).
		setName(name);
		setAID(aid);
		setLocation(location);
	}

}

