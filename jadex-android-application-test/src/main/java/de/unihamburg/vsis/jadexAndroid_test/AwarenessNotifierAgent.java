package de.unihamburg.vsis.jadexAndroid_test;

import jadex.base.service.awareness.discovery.IDiscoveryService;
import jadex.base.service.awareness.management.DiscoveryInfo;
import jadex.base.service.awareness.management.IManagementService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.ISettingsService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import android.os.Bundle;
import android.os.Message;

/**
 * Agent that sends multicasts to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments({
		// @Argument(name="address", clazz=String.class,
		// defaultvalue="\"224.0.0.0\"",
		// description="The ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255)."),
		// @Argument(name="port", clazz=int.class, defaultvalue="55667",
		// description="The port used for finding other agents."),
		@Argument(name = "delay", clazz = long.class, defaultvalue = "10000", description = "The delay between sending awareness infos (in milliseconds)."),
		@Argument(name = "fast", clazz = boolean.class, defaultvalue = "true", description = "Flag for enabling fast startup awareness (pingpong send behavior)."),
		@Argument(name = "autocreate", clazz = boolean.class, defaultvalue = "true", description = "Set if new proxies should be automatically created when discovering new components."),
		@Argument(name = "autodelete", clazz = boolean.class, defaultvalue = "true", description = "Set if proxies should be automatically deleted when not discovered any longer."),
		@Argument(name = "proxydelay", clazz = long.class, defaultvalue = "15000", description = "The delay used by proxies."),
		@Argument(name = "includes", clazz = String.class, defaultvalue = "\"\"", description = "A list of platforms/IPs/hostnames to include (comma separated). Matches start of platform/IP/hostname."),
		@Argument(name = "excludes", clazz = String.class, defaultvalue = "\"\"", description = "A list of platforms/IPs/hostnames to exclude (comma separated). Matches start of platform/IP/hostname.") })
@ComponentTypes({
		@ComponentType(name = "broadcastdis", filename = "jadex/base/service/awareness/discovery/ipbroadcast/BroadcastDiscoveryAgent.class"),
		@ComponentType(name = "multicastdis", filename = "jadex/base/service/awareness/discovery/ipmulticast/MulticastDiscoveryAgent.class"),
		@ComponentType(name = "scannerdis", filename = "jadex/base/service/awareness/discovery/ipscanner/ScannerDiscoveryAgent.class"),
		@ComponentType(name = "registrydis", filename = "jadex/base/service/awareness/discovery/registry/RegistryDiscoveryAgent.class") })
@Configurations({
		@Configuration(name = "Frequent updates (10s)", arguments = @NameValue(name = "delay", value = "10000"), components = { @Component(name = "broadcastdis", type = "broadcastdis")
		// @Component(name="multicastdis", type="multicastdis")
		// @Component(name="scannerdis", type="scannerdis")
		// @Component(name="registrydis", type="registrydis")
		}),
		@Configuration(name = "Medium updates (20s)", arguments = @NameValue(name = "delay", value = "20000"), components = { @Component(name = "broadcastdis", type = "broadcastdis")
		// @Component(name="multicastdis", type="multicastdis")
		// @Component(name="scannerdis", type="scannerdis")
		// @Component(name="registrydis", type="registrydis")
		}),
		@Configuration(name = "Seldom updates (60s)", arguments = @NameValue(name = "delay", value = "60000"), components = { @Component(name = "broadcastdis", type = "broadcastdis")
		// @Component(name="multicastdis", type="multicastdis")
		// @Component(name="scannerdis", type="scannerdis")
		// @Component(name="registrydis", type="registrydis")
		}) })
@Properties(@NameValue(name = "componentviewer.viewerclass", value = "\"jadex.base.service.awareness.gui.AwarenessAgentPanel\""))
@ProvidedServices(@ProvidedService(type = IManagementService.class, implementation = @Implementation(expression = "$component")))
@RequiredServices({
		@RequiredService(name = "cms", type = IComponentManagementService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)),
		// @RequiredService(name="clock", type=IClockService.class,
		// scope=RequiredServiceInfo.SCOPE_PLATFORM),
		@RequiredService(name = "settings", type = ISettingsService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)),
		@RequiredService(name = "discoveries", type = IDiscoveryService.class, multiple = true, binding = @Binding(scope = RequiredServiceInfo.SCOPE_COMPONENT)) })
@Service(IManagementService.class)
public class AwarenessNotifierAgent extends
		jadex.base.service.awareness.management.AwarenessManagementAgent {

	@Override
	public IFuture agentCreated() {
		if (AwarenessActivity.handler != null) {
			Bundle data = new Bundle();
			data.putString("text", "Awareness Notifier Agent started.");
			Message message = AwarenessActivity.handler.obtainMessage();
			message.setData(data);
			message.sendToTarget();
		}
		return super.agentCreated();
	}

	@Override
	public void executeBody() {
		super.executeBody();
	}

	@Override
	public IFuture<IComponentIdentifier> createProxy(IComponentIdentifier cid) {
		if (AwarenessActivity.handler != null) {
			Bundle data = new Bundle();
			data.putString("text",
					cid.getName() + " auf " + cid.getPlatformName());
			data.putSerializable("identifier", new RemoteComponentIdentifier(
					cid));
			data.putString("method", "add");
			Message message = AwarenessActivity.handler.obtainMessage();
			message.setData(data);
			message.sendToTarget();
		}
		return super.createProxy(cid);
	}

	@Override
	public IFuture<Void> deleteProxy(DiscoveryInfo dif) {
		if (AwarenessActivity.handler != null) {
			Bundle data = new Bundle();
			data.putString("text",
					dif.cid.getName() + " auf " + dif.cid.getPlatformName()
							+ " nicht mehr da");
			data.putSerializable("identifier", new RemoteComponentIdentifier(
					dif.cid));
			data.putString("method", "remove");
			Message message = AwarenessActivity.handler.obtainMessage();
			message.setData(data);
			message.sendToTarget();
		}
		return super.deleteProxy(dif);
	}

}
