package jadex.bridge.component;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

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
	 *  Starts a new component.
	 *  
	 *  @param infos Start information.
	 *  @return The acces to the component.
	 */
	public IFuture<IExternalAccess> createComponent(CreationInfo info);
	
	/**
	 *  Starts a new component while continuously receiving status events (create, result updates, termination).
	 *  
	 *  @param infos Start information.
	 *  @return Status events.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithEvents(CreationInfo info);
	
	/**
	 *  Starts a set of new components, in order of dependencies.
	 *  
	 *  @param infos Start information.
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public IIntermediateFuture<IExternalAccess> createComponents(CreationInfo... infos);
	
	/**
	 *  Stops a set of components, in order of dependencies.
	 *  
	 *  @param infos Start information.
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public IIntermediateFuture<Tuple2<IComponentIdentifier, Map<String, Object>>> killComponents(IComponentIdentifier... cids);
	
	/**
	 * Search for subcomponents matching the given description.
	 * @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con);
	
	/**
	 *  Get the children (if any) component identifiers.
	 *  @param type The local child type.
	 *  @param parent The parent (null for this).
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(String type, IComponentIdentifier parent);
}
