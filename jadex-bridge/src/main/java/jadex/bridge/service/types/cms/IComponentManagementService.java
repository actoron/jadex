package jadex.bridge.service.types.cms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.ParameterInfo;
import jadex.bridge.service.annotation.Service;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;

/**
 *  General interface for components that the container can execute.
 */
@Service(system=true)
public interface IComponentManagementService
{
	//-------- management methods --------
	
	/**
	 *  Load a component model.
	 *  @param name The component name.
	 *  @return The model info of the 
	 */
	@ParameterInfo("modelinfo")
	public IFuture<IModelInfo> loadComponentModel(@ParameterInfo("filename") String filename, @ParameterInfo("rid") IResourceIdentifier rid);

	/**
	 *  Create a new component on the platform.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(String model, CreationInfo info);

	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(String name, String model, CreationInfo info);
	
	/**
	 *  Create a new component on the platform.
	 *  This method allows for retrieving intermediate results of the component via
	 *  status events.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The status events of the components. Consists of CMSCreatedEvent, (CMSIntermediateResultEvent)*, CMSTerminatedEvent
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponent(CreationInfo info, String name, String model);

	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @param resultlistener The result listener (if any). Will receive the results of the component execution, after the component has terminated.
	 *  @return The id of the component as future result, when the component has been created and initialized.
	 *  @deprecated Use other createComponent methods.
	 */
	public IFuture<IComponentIdentifier> createComponent(String name, String model, CreationInfo info, 
		IResultListener<Collection<Tuple2<String, Object>>> resultlistener);
	
	/**
	 *  Destroy (forcefully terminate) an component on the platform.
	 *  @param componentid	The component to destroy.
	 */
	public IFuture<Map<String, Object>> destroyComponent(IComponentIdentifier componentid);

	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> suspendComponent(IComponentIdentifier componentid);
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent(IComponentIdentifier componentid);
	
	//-------- debugging methods --------
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 *  @param listener Called when the step is finished (result will be the component description).
	 */
	public IFuture<Void> stepComponent(IComponentIdentifier componentid, String stepinfo);
	
	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param componentid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public IFuture<Void> setComponentBreakpoints(IComponentIdentifier componentid, String[] breakpoints);
	
	//-------- information methods --------
	
	/**
	 *  Get the root identifier (platform).
	 *  @return The root identifier.
	 */
	public IFuture<IComponentIdentifier> getRootIdentifier();
	
	/**
	 *  Get the component identifiers.
	 *  @return The component identifiers.
	 *  This method should be used with caution when the agent population is large.
	 */
	public IFuture<IComponentIdentifier[]> getComponentIdentifiers();
	
	/**
	 *  Get the component description of a single component.
	 *  @param cid The component identifier.
	 *  @return The component description of this component.
	 */
	public IFuture<IComponentDescription> getComponentDescription(IComponentIdentifier cid);
	
	/**
	 *  Get all component descriptions.
	 *  @return The component descriptions of the platform.
	 */
	public IFuture<IComponentDescription[]> getComponentDescriptions();
	
	/**
	 * Search for components matching the given description.
	 * @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con);

	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con, boolean remote);
	
	//-------- listener methods --------
	
	/**
     *  Add an component listener.
     *  The listener is registered for component changes.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be added.
     */
    public IFuture<Void> addComponentListener(IComponentIdentifier comp, ICMSComponentListener listener);
    
    /**
     *  Remove a listener.
     *  @param comp  The component to be listened on (or null for listening on all components).
     *  @param listener  The listener to be removed.
     */
    public IFuture<Void> removeComponentListener(IComponentIdentifier comp, ICMSComponentListener listener);

    //-------- external access methods --------
    
	/**
	 *  Get the external access of a component.
	 *  @param cid The component identifier.
	 *  @param listener The result listener (receives an IExternalAccess object).
	 */
	public IFuture<IExternalAccess> getExternalAccess(IComponentIdentifier cid);

	//-------- parent/child component accessors --------
	
	/**
	 *  Get the parent component of a component.
	 *  @param cid The component identifier.
	 *  @return The parent component identifier.
	 *  @deprecated Use cid.getParent() instead
	 */
	public IFuture<IComponentIdentifier> getParent(IComponentIdentifier cid);
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(IComponentIdentifier cid);
//	public IIntermediateFuture<IComponentIdentifier> getChildren(IComponentIdentifier cid);
	
	/**
	 *  Get the children components of a component.
	 *  @param cid The component identifier.
	 *  @return The children component descriptions.
	 */
	public IFuture<IComponentDescription[]> getChildrenDescriptions(IComponentIdentifier cid);
	
	//-------- create methods for cms objects --------
	
	// todo: remove all following methods 
	// (they are synchronous and not necessary, direct constructor creation)
	
//	/**
//	 *  Create component identifier (name assumed being local).
//	 *  @param name The name.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name);
//	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name, boolean local);
//
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name, boolean local, String[] addresses);
//	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name, IComponentIdentifier parent, String[] addresses);
//	
//	/**
//	 *  Create a component identifier that is allowed on the platform.
//	 *  @param name The base name.
//	 *  @return The component identifier.
//	 */
//	public IComponentIdentifier generateComponentIdentifier(String name, String platformname);
//	
//	/**
//	 * Create a component description.
//	 * @param id The component identifier.
//	 * @param state The state.
//	 * @param ownership The ownership.
//	 * @param type The component type.
//	 * @param parent The parent.
//	 * @return The component description.
//	 */
//	public IComponentDescription createComponentDescription(IComponentIdentifier id, String state, 
//		String ownership, String type, String modelname, String localtype);
//	
//	/**
//	* Create a search constraints object.
//	* @param maxresults The maximum number of results.
//	* @param maxdepth The maximal search depth.
//	* @return The search constraints.
//	*/
//	public ISearchConstraints createSearchConstraints(int maxresults, int maxdepth);
	
//	/**
//	 *  Get the component adapter for a component identifier.
//	 *  @param aid The component identifier.
//	 *  @param listener The result listener.
//	 */
//    // Todo: Hack!!! remove
//	@Excluded
//	public IFuture<IComponentAdapter> getComponentAdapter(IComponentIdentifier cid);
	
	/**
	 * 
	 */
	public static class CMSStatusEvent
	{
		/** The cid. */
		protected IComponentIdentifier cid;
		
		/** Optional properties. */
		protected Map<String, Object> properties;

		/**
		 *  Create a new CMSStatusEvent.
		 */
		public CMSStatusEvent()
		{
		}
		
		/**
		 *  Create a new CMSStatusEvent.
		 */
		public CMSStatusEvent(IComponentIdentifier cid)
		{
			this.cid = cid;
		}

		/**
		 *  Get the componentIdentifier.
		 *  @return The componentIdentifier.
		 */
		public IComponentIdentifier getComponentIdentifier()
		{
			return cid;
		}

		/**
		 *  Set the componentIdentifier.
		 *  @param cid The componentIdentifier to set.
		 */
		public void setComponentIdentifier(IComponentIdentifier cid)
		{
			this.cid = cid;
		}

		/**
		 *  Get the properties.
		 *  return The properties.
		 */
		public Map<String, Object> getProperties()
		{
			return properties;
		}

		/**
		 *  Set the properties. 
		 *  @param properties The properties to set.
		 */
		public void setProperties(Map<String, Object> properties)
		{
			this.properties = properties;
		}
		
		/**
		 *  Add a property.
		 */
		public void setProperty(String name, Object value)
		{
			if(properties==null)
				properties = new HashMap<String, Object>();
			properties.put(name, value);
		}
		
		/**
		 *  Get a property.
		 */
		public Object getProperty(String name)
		{
			return properties==null? null: properties.get(name);
		}

		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return SReflect.getInnerClassName(getClass())+ "[cid=" + cid + ", properties=" + properties + "]";
		}
	}
	
	/**
	 * 
	 */
	public static class CMSCreatedEvent extends CMSStatusEvent
	{
//		/** The cid. */
//		protected IComponentIdentifier cid;

		/**
		 *  Create a new CMSCreatedEvent. 
		 */
		public CMSCreatedEvent()
		{
		}
		
		/**
		 *  Create a new CMSCreatedEvent. 
		 */
		public CMSCreatedEvent(IComponentIdentifier cid)
		{
			super(cid);
		}

//		/**
//		 *  Get the componentIdentifier.
//		 *  @return The componentIdentifier.
//		 */
//		public IComponentIdentifier getComponentIdentifier()
//		{
//			return cid;
//		}
//
//		/**
//		 *  Set the componentIdentifier.
//		 *  @param cid The componentIdentifier to set.
//		 */
//		public void setComponentIdentifier(IComponentIdentifier cid)
//		{
//			this.cid = cid;
//		}
	}
	
	/**
	 * 
	 */
	public static class CMSIntermediateResultEvent extends CMSStatusEvent
	{
		/** The name of the result. */
		protected String name;
		
		/** The value of the result. */
		protected Object value;

		/**
		 *  Create a new CMSIntermediateResultEvent. 
		 */
		public CMSIntermediateResultEvent()
		{
		}
		
		/**
		 *  Create a new CMSIntermediateResultEvent. 
		 */
		public CMSIntermediateResultEvent(IComponentIdentifier cid, String name, Object value)
		{
			super(cid);
			this.name = name;
			this.value = value;
		}

		/**
		 *  Get the name.
		 *  @return The name.
		 */
		public String getName()
		{
			return name;
		}

		/**
		 *  Set the name.
		 *  @param name The name to set.
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 *  Get the value.
		 *  @return The value.
		 */
		public Object getValue()
		{
			return value;
		}

		/**
		 *  Set the value.
		 *  @param value The value to set.
		 */
		public void setValue(Object value)
		{
			this.value = value;
		}
	}
	
	/**
	 * 
	 */
	public static class CMSTerminatedEvent extends CMSStatusEvent
	{
		/** The cid. */
		protected Map<String, Object> results;

		/**
		 *  Create a new CMSCreatedEvent. 
		 */
		public CMSTerminatedEvent()
		{
		}
		
		/**
		 *  Create a new CMSCreatedEvent. 
		 */
		public CMSTerminatedEvent(IComponentIdentifier cid, Map<String, Object> results)
		{
			super(cid);
			this.results = results;
		}

		/**
		 *  Get the results.
		 *  @return The results.
		 */
		public Map<String, Object> getResults()
		{
			return results;
		}

		/**
		 *  Set the results.
		 *  @param results The results to set.
		 */
		public void setResults(Map<String, Object> results)
		{
			this.results = results;
		}
	}
}
