package jadex.bdi.examples.hunterprey_classic;

import jadex.bridge.IAgentIdentifier;


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
	public Observer(String name, IAgentIdentifier aid, Location location)
	{
		// Constructor using required slots (change if desired).
		setName(name);
		setAID(aid);
		setLocation(location);
	}

}

