package jadex.bridge.component;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  External perspective of the subcomponents feature.
 */
public interface IExternalSubcomponentsFeature extends IExternalComponentFeature
{
	/**
	 *  Get the model name of a component type.
	 *  @param ctype The component type.
	 *  @return The model name of this component type.
	 */
	public IFuture<String> getFileName(String ctype);
	
//	/**
//	 *  Get the local type name of this component as defined in the parent.
//	 *  @return The type of this component type.
//	 */
//	public String getLocalType();
	
	/**
	 *  Get the local type name of this component as defined in the parent.
	 *  @return The type of this component type.
	 */
	public IFuture<String> getLocalTypeAsync();
	
	/**
	 *  Get the children (if any) component identifiers.
	 *  @param type The local child type.
	 *  @param parent The parent (null for this).
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(String type, IComponentIdentifier parent);
}
