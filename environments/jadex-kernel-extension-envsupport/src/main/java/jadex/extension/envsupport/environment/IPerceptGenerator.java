package jadex.extension.envsupport.environment;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.IPropertyObject;

/**
 *  Interface for percept generators.
 *  Percept generators listen of the environment for interesting
 *  events and process them to percepts for components. A percept
 *  is meant as a piece of information that is of interest for
 *  an component.
 */
public interface IPerceptGenerator extends IEnvironmentListener, IPropertyObject
{
	/**
	 *  Called when an component was added to the space.
	 *  @param component The component identifier.
	 *  @param space The space.
	 */
	public void componentAdded(IComponentDescription component, IEnvironmentSpace space);
	
	/**
	 *  Called when an component was remove from the space.
	 *  @param component The component identifier.
	 *  @param space The space.
	 */
	public void componentRemoved(IComponentDescription component, IEnvironmentSpace space);
}
