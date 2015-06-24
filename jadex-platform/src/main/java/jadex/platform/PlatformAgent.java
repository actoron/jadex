package jadex.platform;

import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.bridge.service.types.persistence.IPersistenceService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.Boolean3;
import jadex.micro.KernelComponentAgent;
import jadex.micro.KernelMicroAgent;
import jadex.micro.KernelMultiAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.sensor.SensorHolderAgent;
import jadex.platform.service.address.TransportAddressAgent;
import jadex.platform.service.awareness.management.AwarenessManagementAgent;
import jadex.platform.service.clock.ClockAgent;
import jadex.platform.service.context.ContextAgent;
import jadex.platform.service.df.DirectoryFacilitatorAgent;
import jadex.platform.service.dht.DistributedServiceRegistryAgent;
import jadex.platform.service.filetransfer.FileTransferAgent;
import jadex.platform.service.library.LibraryAgent;
import jadex.platform.service.marshal.MarshalAgent;
import jadex.platform.service.message.MessageAgent;
import jadex.platform.service.monitoring.MonitoringAgent;
import jadex.platform.service.remote.RemoteServiceManagementAgent;
import jadex.platform.service.security.SecurityAgent;
import jadex.platform.service.settings.SettingsAgent;
import jadex.platform.service.simulation.SimulationAgent;

import java.util.Map;
import java.util.logging.Level;

/**
 *	Basic standalone platform services provided as a micro agent. 
 */
@Arguments(
{
	@Argument(name="platformname", clazz=String.class, defaultvalue="\"jadex\""),
	@Argument(name="configname", clazz=String.class, defaultvalue="\"auto\""),
	@Argument(name="autoshutdown", clazz=boolean.class, defaultvalue="false"), // todo: does not count children hierarchically
	@Argument(name="platformcomponent", clazz=Class.class, defaultvalue="jadex.platform.service.cms.PlatformComponent.class"),
	@Argument(name="welcome", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="programarguments", clazz=String[].class),
	
	@Argument(name="gui", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="cli", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="cliconsole", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="saveonexit", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="jccplatforms", clazz=String.class, defaultvalue="null"),
	@Argument(name="logging", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="logging_level", clazz=Level.class, defaultvalue="java.util.logging.Level.SEVERE"),
	@Argument(name="simulation", clazz=Boolean.class),
	@Argument(name="asyncexecution", clazz=Boolean.class),
	@Argument(name="threadpooldefer", clazz=boolean.class, defaultvalue="true"),
	
	@Argument(name="persist", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="uniqueids", clazz=boolean.class, defaultvalue="true"),
	
	@Argument(name="libpath", clazz=String.class),
	@Argument(name="baseclassloader", clazz=ClassLoader.class),

	@Argument(name="chat", clazz=boolean.class, defaultvalue="true"),
	
	@Argument(name="awareness", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="awamechanisms", clazz=String.class, defaultvalue="\"Broadcast, Multicast, Message, Relay, Local\""),
	@Argument(name="awadelay", clazz=long.class, defaultvalue="20000"),
	@Argument(name="awaincludes", clazz=String.class, defaultvalue="\"\""),
	@Argument(name="awaexcludes", clazz=String.class, defaultvalue="\"\""),

	@Argument(name="binarymessages", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="strictcom", clazz=boolean.class, defaultvalue="false"),
	
	@Argument(name="usepass", clazz=Boolean.class),
	@Argument(name="printpass", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="trustedlan", clazz=Boolean.class),
	@Argument(name="networkname", clazz=String.class),
	@Argument(name="networkpass", clazz=String.class),
	@Argument(name="virtualnames", clazz=Map.class),
	@Argument(name="validityduration", clazz=long.class),

	@Argument(name="localtransport", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="tcptransport", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="tcpport", clazz=int.class, defaultvalue="9876"),
	@Argument(name="niotcptransport", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="niotcpport", clazz=int.class, defaultvalue="8765"),
	@Argument(name="relaytransport", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="relayaddress", clazz=String.class, defaultvalue="jadex.platform.service.message.transport.httprelaymtp.SRelay.DEFAULT_ADDRESS"),
	@Argument(name="relaysecurity", clazz=boolean.class, defaultvalue="$args.relayaddress.indexOf(\"https://\")==-1 ? false : true"),
	@Argument(name="ssltcptransport", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="ssltcpport", clazz=int.class, defaultvalue="44334"),

	@Argument(name="wspublish", clazz=boolean.class, defaultvalue="false"),
	
	@Argument(name="rspublish", clazz=boolean.class, defaultvalue="false"),

	@Argument(name="kernels", clazz=String.class, defaultvalue="\"multi\""),
	
	@Argument(name="maven_dependencies", clazz=boolean.class, defaultvalue="false"),
		
	@Argument(name="sensors", clazz=boolean.class, defaultvalue="false"),
	
	@Argument(name="threadpoolclass", clazz=String.class, defaultvalue="null"),

	@Argument(name="contextserviceclass", clazz=String.class, defaultvalue="null"),
	
	@Argument(name="monitoringcomp", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="df", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="clock", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="message", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="simul", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="filetransfer", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="marshal", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="security", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="library", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="settings", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="context", clazz=boolean.class, defaultvalue="true"),
//	@Argument(name="persistence", clazz=boolean.class, defaultvalue="true")
	@Argument(name="address", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="providedht", clazz=boolean.class, defaultvalue="false")
})

@ComponentTypes({
//	@ComponentType(name="system", clazz=SystemAgent.class),
	@ComponentType(name="monitor", clazz=MonitoringAgent.class), //filename="jadex/platform/service/monitoring/MonitoringAgent.class"),
	@ComponentType(name="kernel_component", clazz=KernelComponentAgent.class), //filename="jadex/micro/KernelComponentAgent.class"),
	@ComponentType(name="kernel_application", filename="jadex/application/KernelApplication.component.xml"),
	@ComponentType(name="kernel_micro", clazz=KernelMicroAgent.class), // filename="jadex/micro/KernelMicroAgent.class"),
	@ComponentType(name="kernel_bdiv3", filename="jadex/bdiv3/KernelBDIV3Agent.class"),
	@ComponentType(name="kernel_bdi", filename="jadex/bdi/KernelBDI.component.xml"),
	@ComponentType(name="kernel_bdibpmn", filename="jadex/bdibpmn/KernelBDIBPMN.component.xml"),
//	@ComponentType(name="kernel_bpmn", filename="jadex/bpmn/KernelBPMN.component.xml"),
	@ComponentType(name="kernel_bpmn", filename="jadex/micro/KernelBpmnAgent.class"),
	@ComponentType(name="kernel_gpmn", filename="jadex/gpmn/KernelGPMN.component.xml"),
	@ComponentType(name="kernel_multi", clazz=KernelMultiAgent.class), //filename="jadex/micro/KernelMultiAgent.class"),
	@ComponentType(name="rms", clazz=RemoteServiceManagementAgent.class), //filename="jadex/platform/service/remote/RemoteServiceManagementAgent.class"),
	@ComponentType(name="chat", filename="jadex/platform/service/chat/ChatAgent.class"),
	@ComponentType(name="awa", clazz=AwarenessManagementAgent.class), //filename="jadex/platform/service/awareness/management/AwarenessManagementAgent.class"),
	@ComponentType(name="jcc", filename="jadex/tools/jcc/JCCAgent.class"),
	@ComponentType(name="rspublish", filename="jadex/extension/rs/publish/RSPublishAgent.class"),
	@ComponentType(name="wspublish", filename="jadex/extension/ws/publish/WSPublishAgent.class"),
	@ComponentType(name="cli", filename="jadex/platform/service/cli/CliAgent.class"),
	@ComponentType(name="sensor", clazz=SensorHolderAgent.class), //filename="jadex/platform/sensor/SensorHolderAgent.class")
	@ComponentType(name="df", clazz=DirectoryFacilitatorAgent.class),
	@ComponentType(name="clock", clazz=ClockAgent.class),
	@ComponentType(name="message", clazz=MessageAgent.class),
	@ComponentType(name="simulation", clazz=SimulationAgent.class),
	@ComponentType(name="filetransfer", clazz=FileTransferAgent.class),
	@ComponentType(name="marshal", clazz=MarshalAgent.class),
	@ComponentType(name="security", clazz=SecurityAgent.class),
	@ComponentType(name="library", clazz=LibraryAgent.class),
//	@ComponentType(name="dependency", clazz=DependencyAgent.class),
	@ComponentType(name="settings", clazz=SettingsAgent.class),
	@ComponentType(name="context", clazz=ContextAgent.class),
//	@ComponentType(name="persistence", filename="jadex/platform/service/persistence/PersistenceAgent.class") // problem because the cms is also the persistence service
	@ComponentType(name="address", clazz=TransportAddressAgent.class),
	@ComponentType(name="distregistry", clazz=DistributedServiceRegistryAgent.class),
})

@ProvidedServices({
	@ProvidedService(type=IThreadPoolService.class, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.ThreadPool(new jadex.commons.DefaultPoolStrategy(0, 20, 30000, 0, $args.threadpooldefer)), $component.getComponentIdentifier())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IDaemonThreadPoolService.class, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.ThreadPool(true, new jadex.commons.DefaultPoolStrategy(0, 20, 30000, 0, $args.threadpooldefer)), $component.getComponentIdentifier())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IMarshalService.class, implementation=@Implementation(expression="new jadex.platform.service.marshal.MarshalService($component)", proxytype=Implementation.PROXYTYPE_RAW)),
//	@ProvidedService(type=IContextService.class, implementation=@Implementation(expression="$args.contextserviceclass!=null ? jadex.commons.SReflect.classForName0($args.contextserviceclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : jadex.commons.SReflect.isAndroid() ? jadex.platform.service.context.AndroidContextService.class.getConstructor(new Class[]{jadex.bridge.IComponentIdentifier.class}).newInstance(new Object[]{$component.getComponentIdentifier()}): jadex.platform.service.context.ContextService.class.getConstructor(new Class[]{jadex.bridge.IComponentIdentifier.class}).newInstance(new Object[]{$component.getComponentIdentifier()})")),
	@ProvidedService(type=IExecutionService.class, implementation=@Implementation(expression="	($args.asyncexecution!=null && !$args.asyncexecution.booleanValue()) || ($args.asyncexecution==null && $args.simulation!=null && $args.simulation.booleanValue())? new jadex.platform.service.execution.SyncExecutionService($component): new jadex.platform.service.execution.AsyncExecutionService($component)", proxytype=Implementation.PROXYTYPE_RAW)),
//	@ProvidedService(type=ISettingsService.class, implementation=@Implementation(SettingsService.class)),
//	@ProvidedService(type=IDependencyService.class, implementation=@Implementation(expression="$args.maven_dependencies ? jadex.platform.service.dependency.maven.MavenDependencyResolverService.class.newInstance(): new jadex.platform.service.library.BasicDependencyService()")),
//	@ProvidedService(type=ILibraryService.class, implementation=@Implementation(expression="jadex.commons.SReflect.isAndroid() ? jadex.platform.service.library.AndroidLibraryService.class.newInstance() : $args.libpath==null? new jadex.platform.service.library.LibraryService(): new jadex.platform.service.library.LibraryService(new java.net.URLClassLoader(jadex.commons.SUtil.toURLs($args.libpath), $args.baseclassloader==null ? jadex.platform.service.library.LibraryService.class.getClassLoader() : $args.baseclassloader))")),
//	@ProvidedService(type=IClockService.class, implementation=@Implementation(expression="$args.simulation==null || !$args.simulation.booleanValue()? new jadex.platform.service.clock.ClockService(new jadex.platform.service.clock.ClockCreationInfo(jadex.bridge.service.types.clock.IClock.TYPE_SYSTEM, \"system_clock\", System.currentTimeMillis(), 100), $component, $args.simulation): new jadex.platform.service.clock.ClockService(new jadex.platform.service.clock.ClockCreationInfo(jadex.bridge.service.types.clock.IClock.TYPE_EVENT_DRIVEN, \"simulation_clock\", System.currentTimeMillis(), 100), $component, $args.simulation)", proxytype=Implementation.PROXYTYPE_RAW)),
//	@ProvidedService(type=ISecurityService.class, implementation=@Implementation(expression="new jadex.platform.service.security.SecurityService($args.usepass, $args.printpass, $args.trustedlan, $args.networkname==null? null: new String[]{$args.networkname}, $args.networkpass==null? null: new String[]{$args.networkpass}, null, $args.virtualnames, $args.validityduration)")),
//	@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="new jadex.platform.service.message.MessageService($component.getExternalAccess(), $component.getLogger(), new jadex.platform.service.message.transport.ITransport[]{$args.localtransport? new jadex.platform.service.message.transport.localmtp.LocalTransport($component): null, $args.tcptransport? new jadex.platform.service.message.transport.tcpmtp.TCPTransport($component, $args.tcpport): null, $args.niotcptransport? new jadex.platform.service.message.transport.niotcpmtp.NIOTCPTransport($component, $args.niotcpport, $component.getLogger()): null, $args.ssltcptransport? jadex.platform.service.message.transport.ssltcpmtp.SSLTCPTransport.create($component, $args.ssltcpport): null, $args.relaytransport? new jadex.platform.service.message.transport.httprelaymtp.HttpRelayTransport($component, $args.relayaddress, $args.relaysecurity): null}, new jadex.bridge.service.types.message.MessageType[]{new jadex.bridge.fipa.FIPAMessageType()}, null, $args.binarymessages? jadex.bridge.fipa.SFipa.JADEX_BINARY: jadex.bridge.fipa.SFipa.JADEX_XML, $args.binarymessages? new jadex.platform.service.message.transport.codecs.CodecFactory(null, new Class[]{jadex.platform.service.message.transport.codecs.JadexBinaryCodec.class, jadex.platform.service.message.transport.codecs.GZIPCodec.class} ): new jadex.platform.service.message.transport.codecs.CodecFactory(), $args.strictcom)", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IComponentManagementService.class, name="cms", implementation=@Implementation(expression="jadex.commons.SReflect.classForName0(\"jadex.platform.service.persistence.PersistenceComponentManagementService\", jadex.platform.service.library.LibraryService.class.getClassLoader())!=null ? jadex.platform.service.persistence.PersistenceComponentManagementService.create($args.platformaccess, $args.componentfactory, $args.persist, $args.uniqueids) : new jadex.platform.service.cms.ComponentManagementService($args.platformaccess, $args.componentfactory, $args.uniqueids)")),
//	@ProvidedService(type=IDF.class, implementation=@Implementation(DirectoryFacilitatorService.class)),
//	@ProvidedService(type=ISimulationService.class, implementation=@Implementation(SimulationService.class)),
//	@ProvidedService(type=IDeploymentService.class, implementation=@Implementation(DeploymentService.class)),
	@ProvidedService(type=IPersistenceService.class, implementation=@Implementation(expression="jadex.commons.SReflect.classForName0(\"jadex.platform.service.persistence.PersistenceComponentManagementService\", jadex.platform.service.library.LibraryService.class.getClassLoader())!=null ? $component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(jadex.bridge.service.types.cms.IComponentManagementService.class) : null")),
})

@RequiredServices(
{
	@RequiredService(name="factoryservices", type=IComponentFactory.class, multiple=true, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})

@Properties(
{
	@NameValue(name="componentviewer.viewerclass", value="jadex.commons.SReflect.classForName0(\"jadex.base.gui.componentviewer.DefaultComponentServiceViewerPanel\", jadex.platform.service.library.LibraryService.class.getClassLoader())"),
	@NameValue(name="logging.level", value="$args.logging ? java.util.logging.Level.INFO : $args.logging_level")
})

@Configurations(
{
	@Configuration(name="auto", arguments={
		@NameValue(name="tcpport", value="0"),
		@NameValue(name="niotcpport", value="0"),
		@NameValue(name="ssltcpport", value="0"),
		@NameValue(name="platformname", value="null")
	}, components={
//		@Component(name="system", type="system", daemon=Boolean3.TRUE),
//		@Component(name="marshal", type="marshal", daemon=Boolean3.TRUE, number="$args.marshal? 1 : 0"),
		@Component(name="library", type="library", daemon=Boolean3.TRUE, number="$args.library? 1 : 0", arguments={
			@NameValue(name="libpath", value="$args.libpath"),
			@NameValue(name="baseclassloader", value="$args.baseclassloader"),
			@NameValue(name="maven_dependencies", value="$args.maven_dependencies")
		}),
		@Component(name="context", type="context", daemon=Boolean3.TRUE, number="$args.context? 1 : 0", arguments={
			@NameValue(name="contextserviceclass", value="$args.contextserviceclass"),
		}),
		@Component(name="settings", type="settings", daemon=Boolean3.TRUE, number="$args.settings? 1 : 0"),
//		@Component(name="persistence", type="persistence", daemon=Boolean3.TRUE, number="jadex.commons.SReflect.classForName0(\"jadex.platform.service.persistence.PersistenceComponentManagementService\", jadex.platform.service.library.LibraryService.class.getClassLoader())!=null? 1 : 0"),
		
		@Component(name="mon", type="monitor", daemon=Boolean3.TRUE, number="$args.monitoringcomp? 1 : 0"),
		@Component(name="sensors", type="sensor", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.sensors)? 1: 0"),
		@Component(name="kernels", type="kernel_multi", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"multi\")!=-1? 1 : 0"),
		@Component(name="kernel_micro", type="kernel_micro", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"micro\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_component", type="kernel_component", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"component\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_application", type="kernel_application", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"application\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_bdiv3", type="kernel_bdiv3", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"v3\")!=-1 ? 1 : 0"),
		@Component(name="kernel_bdi", type="kernel_bdi", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"bdi\")!=-1 && $args.get(\"kernels\").indexOf(\"bdibpmn\")==-1 ? 1 : 0"),
		@Component(name="kernel_bdibpmn", type="kernel_bdibpmn", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"bdibpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_bpmn", type="kernel_bpmn", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"bpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_gpmn", type="kernel_gpmn", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"gpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		
		@Component(name="address", type="address", daemon=Boolean3.TRUE, number="$args.address? 1 : 0"),
		@Component(name="clock", type="clock", daemon=Boolean3.TRUE, number="$args.clock? 1 : 0", arguments=@NameValue(name="simulation", value="$args.simulation")),
		@Component(name="security", type="security", daemon=Boolean3.TRUE, number="$args.security? 1 : 0", arguments={
			@NameValue(name="usepass", value="$args.usepass"),
			@NameValue(name="printpass", value="$args.printpass"),
			@NameValue(name="trustedlan", value="$args.trustedlan"),
			@NameValue(name="networkname", value="$args.networkname"),
			@NameValue(name="networkpass", value="$args.networkpass"),
			@NameValue(name="virtualnames", value="$args.virtualnames"),
			@NameValue(name="validityduration", value="$args.validityduration")
		}),
		@Component(name="message", type="message", daemon=Boolean3.TRUE, number="$args.message? 1 : 0", arguments={
			@NameValue(name="localtransport", value="$args.localtransport"),
			@NameValue(name="tcptransport", value="$args.tcptransport"),
			@NameValue(name="niotcptransport", value="$args.niotcptransport"),
			@NameValue(name="ssltcptransport", value="$args.ssltcptransport"),
			@NameValue(name="relaytransport", value="$args.relaytransport"),
			@NameValue(name="tcpport", value="$args.tcpport"),
			@NameValue(name="niotcpport", value="$args.niotcpport"),
			@NameValue(name="ssltcpport", value="$args.ssltcpport"),
			@NameValue(name="relayaddress", value="$args.relayaddress"),
			@NameValue(name="relaysecurity", value="$args.relaysecurity"),
			@NameValue(name="binarymessages", value="$args.binarymessages"),
			@NameValue(name="strictcom", value="$args.strictcom"),
		}),
		@Component(name="simulation", type="simulation", daemon=Boolean3.TRUE, number="$args.simul? 1 : 0"),
		@Component(name="filetransfer", type="filetransfer", daemon=Boolean3.TRUE, number="$args.filetransfer? 1 : 0"),
		
		@Component(name="rms", type="rms", daemon=Boolean3.TRUE),
		@Component(name="awa", type="awa", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.get(\"awareness\")) ? 1 : 0",
			arguments={
				@NameValue(name="mechanisms", value="$args.awamechanisms"),
				@NameValue(name="delay", value="$args.awadelay"),
				@NameValue(name="includes", value="$args.awaincludes"),
				@NameValue(name="excludes", value="$args.awaexcludes")}),
		@Component(name="chat", type="chat", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.get(\"chat\")) ? 1 : 0"),
		@Component(name="jcc", type="jcc", number="Boolean.TRUE.equals($args.get(\"gui\")) ? 1 : 0",
			arguments={
				@NameValue(name="saveonexit", value="$args.saveonexit"),
				@NameValue(name="platforms", value="$args.jccplatforms")}),
		@Component(name="rspub", type="rspublish", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.rspublish)? 1: 0"),
		@Component(name="wspub", type="wspublish", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.wspublish)? 1: 0"),
		@Component(name="cli", type="cli", daemon=Boolean3.TRUE, number="jadex.commons.SReflect.classForName0(\"jadex.platform.service.cli.CliAgent\", jadex.platform.service.library.LibraryService.class.getClassLoader())!=null && Boolean.TRUE.equals($args.cli)? 1: 0",
			arguments={@NameValue(name="console", value="$args.cliconsole")}),
		
		@Component(name="df", type="df", daemon=Boolean3.TRUE, number="$args.df? 1 : 0"),
		@Component(name="distregistry", type="distregistry", daemon=Boolean3.TRUE, number="$args.providedht? 1 : 0"),
	}),
	@Configuration(name="fixed", arguments={
		//@NameValue(name="tcpport", value="0"),
		//@NameValue(name="ssltcpport", value="0"),
		//@NameValue(name="niotcpport", value="0"),
		//@NameValue(name="platformname", value="null"),
		//@NameValue(name="kernels", value="\"component,micro,application,bdi,bdiv3,bpmn,gpmn\"")
	}, components={
//		@Component(name="system", type="system", daemon=Boolean3.TRUE),
//		@Component(name="marshal", type="marshal", daemon=Boolean3.TRUE, number="$args.marshal? 1 : 0"),
		@Component(name="library", type="library", daemon=Boolean3.TRUE, number="$args.library? 1 : 0", arguments={
			@NameValue(name="libpath", value="$args.libpath"),
			@NameValue(name="baseclassloader", value="$args.baseclassloader"),
			@NameValue(name="maven_dependencies", value="$args.maven_dependencies")
		}),
		@Component(name="context", type="context", daemon=Boolean3.TRUE, number="$args.context? 1 : 0", arguments={
			@NameValue(name="contextserviceclass", value="$args.contextserviceclass"),
		}),
		@Component(name="settings", type="settings", daemon=Boolean3.TRUE, number="$args.settings? 1 : 0"),
//		@Component(name="persistence", type="persistence", daemon=Boolean3.TRUE, number="jadex.commons.SReflect.classForName0(\"jadex.platform.service.persistence.PersistenceComponentManagementService\", jadex.platform.service.library.LibraryService.class.getClassLoader())!=null? 1 : 0"),
		
		@Component(name="mon", type="monitor", daemon=Boolean3.TRUE, number="$args.monitoringcomp? 1 : 0"),
		@Component(name="sensors", type="sensor", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.sensors)? 1: 0"),
		@Component(name="kernels", type="kernel_multi", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"multi\")!=-1? 1 : 0"),
		@Component(name="kernel_micro", type="kernel_micro", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"micro\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_component", type="kernel_component", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"component\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_application", type="kernel_application", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"application\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_bdiv3", type="kernel_bdiv3", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"v3\")!=-1 ? 1 : 0"),
		@Component(name="kernel_bdi", type="kernel_bdi", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"bdi\")!=-1 && $args.get(\"kernels\").indexOf(\"bdibpmn\")==-1 ? 1 : 0"),
		@Component(name="kernel_bdibpmn", type="kernel_bdibpmn", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"bdibpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_bpmn", type="kernel_bpmn", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"bpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_gpmn", type="kernel_gpmn", daemon=Boolean3.TRUE, number="$args.get(\"kernels\").indexOf(\"gpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		
		@Component(name="address", type="address", daemon=Boolean3.TRUE, number="$args.address? 1 : 0"),
		@Component(name="clock", type="clock", daemon=Boolean3.TRUE, number="$args.clock? 1 : 0", arguments=@NameValue(name="simulation", value="$args.simulation")),
		@Component(name="security", type="security", daemon=Boolean3.TRUE, number="$args.security? 1 : 0", arguments={
			@NameValue(name="usepass", value="$args.usepass"),
			@NameValue(name="printpass", value="$args.printpass"),
			@NameValue(name="trustedlan", value="$args.trustedlan"),
			@NameValue(name="networkname", value="$args.networkname"),
			@NameValue(name="networkpass", value="$args.networkpass"),
			@NameValue(name="virtualnames", value="$args.virtualnames"),
			@NameValue(name="validityduration", value="$args.validityduration")
		}),
		@Component(name="message", type="message", daemon=Boolean3.TRUE, number="$args.message? 1 : 0", arguments={
			@NameValue(name="localtransport", value="$args.localtransport"),
			@NameValue(name="tcptransport", value="$args.tcptransport"),
			@NameValue(name="niotcptransport", value="$args.niotcptransport"),
			@NameValue(name="ssltcptransport", value="$args.ssltcptransport"),
			@NameValue(name="relaytransport", value="$args.relaytransport"),
			@NameValue(name="tcpport", value="$args.tcpport"),
			@NameValue(name="niotcpport", value="$args.niotcpport"),
			@NameValue(name="ssltcpport", value="$args.ssltcpport"),
			@NameValue(name="relayaddress", value="$args.relayaddress"),
			@NameValue(name="relaysecurity", value="$args.relaysecurity"),
			@NameValue(name="binarymessages", value="$args.binarymessages"),
			@NameValue(name="strictcom", value="$args.strictcom"),
		}),
		@Component(name="simulation", type="simulation", daemon=Boolean3.TRUE, number="$args.simul? 1 : 0"),
		@Component(name="filetransfer", type="filetransfer", daemon=Boolean3.TRUE, number="$args.filetransfer? 1 : 0"),
		
		@Component(name="rms", type="rms", daemon=Boolean3.TRUE),
		@Component(name="awa", type="awa", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.get(\"awareness\")) ? 1 : 0",
			arguments={
				@NameValue(name="mechanisms", value="$args.awamechanisms"),
				@NameValue(name="includes", value="$args.awaincludes"),
				@NameValue(name="excludes", value="$args.awaexcludes")}),
		@Component(name="chat", type="chat", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.get(\"chat\")) ? 1 : 0"),
		@Component(name="jcc", type="jcc", number="Boolean.TRUE.equals($args.get(\"gui\")) ? 1 : 0",
			arguments={
				@NameValue(name="saveonexit", value="$args.saveonexit"),
				@NameValue(name="platforms", value="$args.jccplatforms")}),
		@Component(name="rspub", type="rspublish", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.rspublish)? 1: 0"),
		@Component(name="wspub", type="wspublish", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.wspublish)? 1: 0"),
		@Component(name="cli", type="cli", daemon=Boolean3.TRUE, number="jadex.commons.SReflect.classForName0(\"jadex.platform.service.cli.CliAgent\", jadex.platform.service.library.LibraryService.class.getClassLoader())!=null && Boolean.TRUE.equals($args.cli)? 1: 0",
			arguments={@NameValue(name="console", value="$args.cliconsole")}),
		
		@Component(name="df", type="df", daemon=Boolean3.TRUE, number="$args.df? 1 : 0"),
		@Component(name="distregistry", type="distregistry", daemon=Boolean3.TRUE, number="$args.providedht? 1 : 0"),
	})
})
@Agent
public class PlatformAgent
{
}
