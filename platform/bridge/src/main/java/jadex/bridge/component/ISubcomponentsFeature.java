package jadex.bridge.component;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.commons.future.IFuture;

/**
 *  Allows a component to have subcomponents.
 */
public interface ISubcomponentsFeature
{
	/**
	 *  Create a subcomponent.
	 *  @param component The instance info.
	 */
	public IFuture<IComponentIdentifier> createChild(final ComponentInstanceInfo component);
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public String getLocalType();
	
	/**
	 *  Get the file name of a component type.
	 *  @param ctype The component type.
	 *  @return The file name of this component type.
	 */
	public String getComponentFilename(final String ctype);
}
