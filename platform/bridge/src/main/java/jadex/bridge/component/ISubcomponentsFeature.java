package jadex.bridge.component;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Allows a component to have subcomponents.
 */
public interface ISubcomponentsFeature extends IExternalSubcomponentsFeature
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
	
	/**
	 *  Get the childcount.
	 *  @return the childcount.
	 */
	public int getChildcount();
//	
//	/**
//	 *  Inc the child count.
//	 */
//	public int incChildcount();
//	
//	/**
//	 *  Dec the child count.
//	 */
//	public int decChildcount();

}
