package jadex.bridge.component;

import java.util.Collection;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;

/**
 *  Allows a component to have subcomponents.
 */
public interface ISubcomponentsFeature
{
//	/**
//	 *  Create a subcomponent.
//	 *  @param component The instance info.
//	 */
//	public IFuture<IComponentIdentifier> createChild(ComponentInstanceInfo component);
	
//	/**
//	 *  Add a new component as subcomponent of this component.
//	 *  @param component The model or pojo of the component.
//	 */
//	public IFuture<IExternalAccess> createComponent(Object component, CreationInfo info, IResultListener<Collection<Tuple2<String, Object>>> resultlistener);
//	
//	/**
//	 *  Add a new component as subcomponent of this component.
//	 *  @param component The model or pojo of the component.
//	 */
//	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithResults(Object component, CreationInfo info);
//	
//	/**
//	 *  Create a new component on the platform.
//	 *  @param name The component name or null for automatic generation.
//	 *  @param model The model identifier (e.g. file name).
//	 *  @param info Additional start information such as parent component or arguments (optional).
//	 *  @return The id of the component and the results after the component has been killed.
//	 */
//	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(Object component, CreationInfo info);
	
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
