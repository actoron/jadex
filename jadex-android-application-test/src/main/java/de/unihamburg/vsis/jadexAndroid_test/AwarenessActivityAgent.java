package de.unihamburg.vsis.jadexAndroid_test;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.BasicResultSelector;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.xml.annotation.XMLClassname;

@Agent
@RequiredServices({
		@RequiredService(name = "clockservice", type = IClockService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)),
		@RequiredService(name = "rms", type = IRemoteServiceManagementService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL)) })
public class AwarenessActivityAgent extends MicroAgent {

	public static AwarenessActivityAgent instance;

	@Override
	public IFuture<Void> executeBody() {
		instance = this;
		return new Future<Void>();
	}

	public IFuture getRemoteComponents(final IComponentIdentifier id) {
		final Future ret = new Future();

		getRequiredService("rms").addResultListener(
				new DefaultResultListener<Object>() {
					
					@XMLClassname("get-rms")
					@SuppressWarnings("unchecked")
					@Override
					public void resultAvailable(Object result) {
						IRemoteServiceManagementService rms = (IRemoteServiceManagementService) result;
						rms.getServiceProxies(
								id,
								SServiceProvider.getSearchManager(false,
										RequiredServiceInfo.SCOPE_GLOBAL),
								SServiceProvider.getVisitDecider(false,
										RequiredServiceInfo.SCOPE_GLOBAL),
								new BasicResultSelector()).addResultListener(
								new DelegationResultListener(ret));
					}
				});

		return ret;
	}

}
