package de.unihamburg.vsis.jadexAndroid_test;

import jadex.base.service.awareness.management.AwarenessManagementAgent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.awareness.IManagementService;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
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
		@Argument(name="mechanisms", clazz=String[].class, defaultvalue="new String[]{\"Bluetooth\"}", description="The discovery mechanisms."),
		@Argument(name = "delay", clazz = long.class, defaultvalue = "10000", description = "The delay between sending awareness infos (in milliseconds)."),
		@Argument(name = "fast", clazz = boolean.class, defaultvalue = "true", description = "Flag for enabling fast startup awareness (pingpong send behavior)."),
		@Argument(name = "autocreate", clazz = boolean.class, defaultvalue = "true", description = "Set if new proxies should be automatically created when discovering new components."),
		@Argument(name = "autodelete", clazz = boolean.class, defaultvalue = "true", description = "Set if proxies should be automatically deleted when not discovered any longer."),
		@Argument(name = "proxydelay", clazz = long.class, defaultvalue = "15000", description = "The delay used by proxies."),
		@Argument(name = "includes", clazz = String.class, defaultvalue = "\"\"", description = "A list of platforms/IPs/hostnames to include (comma separated). Matches start of platform/IP/hostname."),
		@Argument(name = "excludes", clazz = String.class, defaultvalue = "\"\"", description = "A list of platforms/IPs/hostnames to exclude (comma separated). Matches start of platform/IP/hostname.") })
@ComponentTypes({
		@ComponentType(name="Broadcast", filename="jadex/base/service/awareness/discovery/ipbroadcast/BroadcastDiscoveryAgent.class"),
		@ComponentType(name="Multicast", filename="jadex/base/service/awareness/discovery/ipmulticast/MulticastDiscoveryAgent.class"),
		@ComponentType(name="Scanner", filename="jadex/base/service/awareness/discovery/ipscanner/ScannerDiscoveryAgent.class"),
		@ComponentType(name="Registry", filename="jadex/base/service/awareness/discovery/registry/RegistryDiscoveryAgent.class"),
		@ComponentType(name="Bluetooth", filename = "jadex/base/service/awareness/discovery/bluetoothp2p/BluetoothP2PDiscoveryAgent.class") 
		})
@Configurations({
		@Configuration(name = "Frequent updates (10s)", arguments = @NameValue(name = "delay", value = "10000"), components = { @Component(name = "Bluetooth", type = "Bluetooth")
		// @Component(name="multicastdis", type="multicastdis")
		// @Component(name="scannerdis", type="scannerdis")
		// @Component(name="registrydis", type="registrydis")
		}),
		@Configuration(name = "Medium updates (20s)", arguments = @NameValue(name = "delay", value = "20000"), components = { @Component(name = "Bluetooth", type = "Bluetooth")
		// @Component(name="multicastdis", type="multicastdis")
		// @Component(name="scannerdis", type="scannerdis")
		// @Component(name="registrydis", type="registrydis")
		}),
		@Configuration(name = "Seldom updates (60s)", arguments = @NameValue(name = "delay", value = "60000"), components = { @Component(name = "Bluetooth", type = "Bluetooth")
		// @Component(name="multicastdis", type="multicastdis")
		// @Component(name="scannerdis", type="scannerdis")
		// @Component(name="registrydis", type="registrydis")
		}) })
@Properties(@NameValue(name = "componentviewer.viewerclass", value = "\"jadex.base.service.awareness.gui.AwarenessAgentPanel\""))
@ProvidedServices(@ProvidedService(type = IManagementService.class, implementation = @Implementation(expression = "$component")))
@Service(IManagementService.class)
public class AwarenessNotifierAgent extends
		AwarenessManagementAgent {

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
	public IFuture<Void> executeBody() {
		return super.executeBody();
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
	public IFuture<Void> deleteProxy(IComponentIdentifier id) {
		if (AwarenessActivity.handler != null) {
			Bundle data = new Bundle();
			data.putString("text",
					id.getName() + " auf " + id.getPlatformName()
							+ " nicht mehr da");
			data.putSerializable("identifier", new RemoteComponentIdentifier(
					id));
			data.putString("method", "remove");
			Message message = AwarenessActivity.handler.obtainMessage();
			message.setData(data);
			message.sendToTarget();
		}
		return super.deleteProxy(id);
	}

}
