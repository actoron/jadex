package jadex.noplatform.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.ArgumentsResultsComponentFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.ComponentLifecycleFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.component.impl.MonitoringComponentFeature;
import jadex.bridge.component.impl.NFPropertyComponentFeature;
import jadex.bridge.component.impl.PropertiesComponentFeature;
import jadex.bridge.component.impl.RemoteExecutionComponentFeature;
import jadex.bridge.component.impl.SubcomponentsComponentFeature;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.ProvidedServicesComponentFeature;
import jadex.bridge.service.component.RequiredServicesComponentFeature;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.commons.SReflect;
import jadex.micro.MicroModelLoader;
import jadex.micro.features.impl.MicroExecutionComponentFeature;
import jadex.micro.features.impl.MicroInjectionComponentFeature;
import jadex.micro.features.impl.MicroLifecycleComponentFeature;
import jadex.micro.features.impl.MicroPojoComponentFeature;
import jadex.micro.features.impl.MicroServiceInjectionComponentFeature;

public class MicroAgentFactory extends jadex.micro.MicroAgentFactory
{
	/** The default component features. */
	public static final Collection<IComponentFeatureFactory> DEFAULT_FEATURES;
	
	static
	{
		Collection<IComponentFeatureFactory>	def_features	= new ArrayList<IComponentFeatureFactory>();
		
		// exchanged
		def_features.add(new ComponentFeatureFactory(IExecutionFeature.class, MicroExecutionComponentFeature.class));
		
		//def_features.add(new ComponentFeatureFactory(IMonitoringComponentFeature.class, MonitoringComponentFeature.class));
		def_features.add(new ComponentFeatureFactory(IArgumentsResultsFeature.class, ArgumentsResultsComponentFeature.class));
		//def_features.add(PropertiesComponentFeature.FACTORY);	// After args for logging
		//def_features.add(new ComponentFeatureFactory(IRequiredServicesFeature.class, RequiredServicesComponentFeature.class));
		//def_features.add(new ComponentFeatureFactory(IProvidedServicesFeature.class, ProvidedServicesComponentFeature.class));
		def_features.add(new ComponentFeatureFactory(ISubcomponentsFeature.class, SubcomponentsComponentFeature.class, new Class[]{IProvidedServicesFeature.class}, null));
		//def_features.add(new ComponentFeatureFactory(IMessageFeature.class, MessageComponentFeature.class));
		//def_features.add(RemoteExecutionComponentFeature.FACTORY);	// After message for adding handler
		//def_features.add(NFPropertyComponentFeature.FACTORY);
		
		// exchanged
		def_features.add(MicroLifecycleComponentFeature.FACTORY);
		
		// added
		def_features.add(MicroPojoComponentFeature.FACTORY);
		def_features.add(MicroInjectionComponentFeature.FACTORY);
		def_features.add(MicroServiceInjectionComponentFeature.FACTORY);
		
		DEFAULT_FEATURES	= Collections.unmodifiableCollection(def_features);
	}
	
	/**
	 *  Create a new agent factory for startup.
	 *  @param platform	The platform.
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public MicroAgentFactory(String providerid)
	{
		super(providerid);
		this.loader = new MicroModelLoader();
	}
	
	/**
	 *  Get the standard micro features.
	 *  @return The standard features for a micro component.
	 */
	protected Collection<IComponentFeatureFactory> getStandardFeatures()
	{
		if(features==null)
			features = SComponentFactory.orderComponentFeatures(SReflect.getUnqualifiedClassName(getClass()), Arrays.asList(SComponentFactory.DEFAULT_FEATURES));
		return features;
	}
}
