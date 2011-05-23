package jadex.application.runtime.impl;

import jadex.application.ApplicationComponentFactory;
import jadex.application.runtime.ISpace;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.bridge.service.clock.ITimedObject;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.component.ComponentServiceContainer;
import jadex.commons.SReflect;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.component.ComponentInterpreter;
import jadex.xml.annotation.XMLClassname;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  The application interpreter provides a closed environment for components.
 *  If components spawn other components, these will automatically be added to
 *  the context.
 *  When the context is deleted all components will be destroyed.
 *  An component must only be in one application context.
 */
public class ApplicationInterpreter extends ComponentInterpreter
{
	//-------- constructors --------
	
	/**
	 *  Create a new context.
	 */
	public ApplicationInterpreter(final IComponentDescription desc, final IModelInfo model, final String config, 
		final IComponentAdapterFactory factory, final IExternalAccess parent, final Map arguments, RequiredServiceBinding[] bindings, final Future inited)
	{
		super(desc, model, config, factory, parent, arguments, bindings, inited);
	}
	
}
