package jadex.platform;

import static jadex.base.IPlatformConfiguration.ASYNCEXECUTION;
import static jadex.base.IPlatformConfiguration.AWADELAY;
import static jadex.base.IPlatformConfiguration.AWAEXCLUDES;
import static jadex.base.IPlatformConfiguration.AWAINCLUDES;
import static jadex.base.IPlatformConfiguration.AWAMECHANISMS;
import static jadex.base.IPlatformConfiguration.AWARENESS;
import static jadex.base.IPlatformConfiguration.BASECLASSLOADER;
import static jadex.base.IPlatformConfiguration.BINARYMESSAGES;
import static jadex.base.IPlatformConfiguration.CHAT;
import static jadex.base.IPlatformConfiguration.CLI;
import static jadex.base.IPlatformConfiguration.CLICONSOLE;
import static jadex.base.IPlatformConfiguration.CONTEXTSERVICECLASS;
import static jadex.base.IPlatformConfiguration.GUI;
import static jadex.base.IPlatformConfiguration.JCCPLATFORMS;
import static jadex.base.IPlatformConfiguration.LIBPATH;
import static jadex.base.IPlatformConfiguration.LOCALTRANSPORT;
import static jadex.base.IPlatformConfiguration.LOGGING;
import static jadex.base.IPlatformConfiguration.LOGGING_LEVEL;
import static jadex.base.IPlatformConfiguration.MAVEN_DEPENDENCIES;
import static jadex.base.IPlatformConfiguration.PROGRAM_ARGUMENTS;
import static jadex.base.IPlatformConfiguration.RELAYADDRESSES;
import static jadex.base.IPlatformConfiguration.RELAYFORWARDING;
import static jadex.base.IPlatformConfiguration.RELAYTRANSPORT;
import static jadex.base.IPlatformConfiguration.RSPUBLISH;
import static jadex.base.IPlatformConfiguration.RSPUBLISHCOMPONENT;
import static jadex.base.IPlatformConfiguration.SAVEONEXIT;
import static jadex.base.IPlatformConfiguration.SIMULATION;
import static jadex.base.IPlatformConfiguration.STRICTCOM;
import static jadex.base.IPlatformConfiguration.TCPPORT;
import static jadex.base.IPlatformConfiguration.TCPTRANSPORT;
import static jadex.base.IPlatformConfiguration.THREADPOOLCLASS;
import static jadex.base.IPlatformConfiguration.THREADPOOLDEFER;
import static jadex.base.IPlatformConfiguration.UNIQUEIDS;
import static jadex.base.IPlatformConfiguration.WELCOME;
import static jadex.base.IPlatformConfiguration.WSPORT;
import static jadex.base.IPlatformConfiguration.WSPUBLISH;
import static jadex.base.IPlatformConfiguration.WSTRANSPORT;

import java.util.logging.Level;

import jadex.base.IPlatformConfiguration;
import jadex.bridge.ClassInfo;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentFactory;
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
import jadex.platform.service.filetransfer.FileTransferAgent;
import jadex.platform.service.library.LibraryAgent;
import jadex.platform.service.monitoring.MonitoringAgent;
import jadex.platform.service.registry.AutoConfigRegistryAgent;
import jadex.platform.service.registry.PeerRegistrySynchronizationAgent;
import jadex.platform.service.registry.SuperpeerRegistrySynchronizationAgent;
import jadex.platform.service.security.SecurityAgent;
import jadex.platform.service.settings.SettingsAgent;
import jadex.platform.service.simulation.SimulationAgent;
import jadex.platform.service.transport.tcp.TcpTransportAgent;

/**
 *	Basic standalone platform services provided as a micro agent. 
 */
@Arguments(
{
	@Argument(name=IPlatformConfiguration.PLATFORM_NAME, clazz=String.class, defaultvalue="\"jadex\""),
	@Argument(name=IPlatformConfiguration.CONFIGURATION_NAME, clazz=String.class, defaultvalue="\"auto\""),
	@Argument(name=IPlatformConfiguration.AUTOSHUTDOWN, clazz=boolean.class, defaultvalue="false"), // todo: does not count children hierarchically
	@Argument(name=IPlatformConfiguration.PLATFORM_COMPONENT, clazz=ClassInfo.class, defaultvalue="new jadex.bridge.ClassInfo(jadex.platform.service.cms.PlatformComponent.class)"),
	@Argument(name=WELCOME, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=PROGRAM_ARGUMENTS, clazz=String[].class),
	
	@Argument(name=GUI, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=CLI, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=CLICONSOLE, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=SAVEONEXIT, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=JCCPLATFORMS, clazz=String.class, defaultvalue="null"),
	@Argument(name=LOGGING, clazz=boolean.class, defaultvalue="false"),
	@Argument(name=LOGGING_LEVEL, clazz=Level.class, defaultvalue="java.util.logging.Level.SEVERE"),
	@Argument(name=SIMULATION, clazz=Boolean.class),
	@Argument(name=ASYNCEXECUTION, clazz=Boolean.class),
	@Argument(name=THREADPOOLDEFER, clazz=boolean.class, defaultvalue="true"),
	
	@Argument(name=UNIQUEIDS, clazz=boolean.class, defaultvalue="true"),
	
	@Argument(name=LIBPATH, clazz=String.class),
	@Argument(name=BASECLASSLOADER, clazz=ClassLoader.class),

	@Argument(name=CHAT, clazz=boolean.class, defaultvalue="true"),
	
	@Argument(name=AWARENESS, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=AWAMECHANISMS, clazz=String.class, defaultvalue="\"Multicast, Local\""),
//	@Argument(name=AWAMECHANISMS, clazz=String.class, defaultvalue="\"Broadcast, Multicast, Message, Relay, Local\""),
	@Argument(name=AWADELAY, clazz=long.class, defaultvalue="20000"),
	@Argument(name=AWAINCLUDES, clazz=String.class, defaultvalue="\"\""),
	@Argument(name=AWAEXCLUDES, clazz=String.class, defaultvalue="\"\""),

	@Argument(name=BINARYMESSAGES, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=STRICTCOM, clazz=boolean.class, defaultvalue="false"),
	
//	@Argument(name=USESECRET, clazz=Boolean.class),
//	@Argument(name=PRINTSECRET, clazz=boolean.class, defaultvalue="true"),
//	@Argument(name=NETWORKNAME, clazz=String.class),
//	@Argument(name=NETWORKSECRET, clazz=String.class),
//	@Argument(name=ROLES, clazz=Map.class),

	
	// TODO. Setting default values here doesn't work any more!
	@Argument(name=LOCALTRANSPORT, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=TCPTRANSPORT, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=TCPPORT, clazz=int.class, defaultvalue="0"),
	@Argument(name=WSTRANSPORT, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=WSPORT, clazz=int.class, defaultvalue="-1"),
	@Argument(name=RELAYTRANSPORT, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=RELAYADDRESSES, clazz=String.class, defaultvalue="\"ws://ssp1@ngrelay1.actoron.com:80\""),	// TODO: wss
	@Argument(name=RELAYFORWARDING, clazz=boolean.class, defaultvalue="false"),
//	@Argument(name=RELAYSECURITY, clazz=boolean.class, defaultvalue="$args.relayaddress.indexOf(\"https://\")==-1 ? false : true"),
//	@Argument(name=RELAYAWAONLY, clazz=boolean.class, defaultvalue="false"),
//	@Argument(name=SSLTCPTRANSPORT, clazz=boolean.class, defaultvalue="false"),
//	@Argument(name=SSLTCPPORT, clazz=int.class, defaultvalue="44334"),

	@Argument(name=THREADPOOLCLASS, clazz=String.class),//, defaultvalue="null"),

	@Argument(name=CONTEXTSERVICECLASS, clazz=String.class),//, defaultvalue="null"),
	
	@Argument(name=WSPUBLISH, clazz=boolean.class, defaultvalue="false"),
	
	@Argument(name=RSPUBLISH, clazz=boolean.class, defaultvalue="false"),
	@Argument(name=RSPUBLISHCOMPONENT, clazz=String.class, defaultvalue="jadex.commons.SReflect.chooseAvailableResource(jadex.bridge.service.types.publish.IPublishService.DEFAULT_RSPUBLISH_COMPONENTS)"),

	@Argument(name=MAVEN_DEPENDENCIES, clazz=boolean.class, defaultvalue="false"),

	@Argument(name="kernel_multi", clazz=Boolean.class, defaultvalue="true"),
	@Argument(name="kernel_micro", clazz=Boolean.class, defaultvalue="false"),
	@Argument(name="kernel_component", clazz=Boolean.class, defaultvalue="false"),
	@Argument(name="kernel_application", clazz=Boolean.class, defaultvalue="false"),
	@Argument(name="kernel_bdiv3", clazz=Boolean.class, defaultvalue="false"),
	@Argument(name="kernel_bdi", clazz=Boolean.class, defaultvalue="false"),
	@Argument(name="kernel_bpmn", clazz=Boolean.class, defaultvalue="false"),
	@Argument(name="kernel_bpmn", clazz=Boolean.class, defaultvalue="false"),
	@Argument(name="kernel_bdibpmn", clazz=Boolean.class, defaultvalue="false"),
	@Argument(name="kernel_gpmn", clazz=Boolean.class, defaultvalue="false"),
	
	@Argument(name="sensors", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="mon", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="df", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="clock", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="simul", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="filetransfer", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="security", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="library", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="settings", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="context", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="address", clazz=boolean.class, defaultvalue="true"),
	
	@Argument(name="superpeer", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="supersuperpeer", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="superpeerclient", clazz=boolean.class),//, defaultvalue="$args.superpeer==null && $args.supersuperpeer? true: !$args.superpeer && !$args.supersuperpeer"),
	@Argument(name="acr", clazz=boolean.class, defaultvalue="false")
})

@ComponentTypes({
	@ComponentType(name="monitor", clazz=MonitoringAgent.class), //filename="jadex/platform/service/monitoring/MonitoringAgent.class"),
	@ComponentType(name="kernel_component", clazz=KernelComponentAgent.class), //filename="jadex/micro/KernelComponentAgent.class"),
	@ComponentType(name="kernel_application", filename="jadex/application/KernelApplication.component.xml"),
	@ComponentType(name="kernel_micro", clazz=KernelMicroAgent.class), // filename="jadex/micro/KernelMicroAgent.class"),
	@ComponentType(name="kernel_bdiv3", filename="jadex/bdiv3/KernelBDIV3Agent.class"),
	@ComponentType(name="kernel_bdi", filename="jadex/bdiv3x/KernelBDIX.component.xml"),
	@ComponentType(name="kernel_bdibpmn", filename="jadex/bdibpmn/KernelBDIBPMN.component.xml"),
	@ComponentType(name="kernel_bpmn", filename="jadex/micro/KernelBpmnAgent.class"),
	@ComponentType(name="kernel_gpmn", filename="jadex/gpmn/KernelGPMN.component.xml"),
	@ComponentType(name="kernel_multi", clazz=KernelMultiAgent.class), //filename="jadex/micro/KernelMultiAgent.class"),
	@ComponentType(name="chat", filename="jadex/platform/service/chat/ChatAgent.class"),
	@ComponentType(name="awa", clazz=AwarenessManagementAgent.class), //filename="jadex/platform/service/awareness/management/AwarenessManagementAgent.class"),
	@ComponentType(name="jcc", filename="jadex/tools/jcc/JCCAgent.class"),
	@ComponentType(name="rspublish", filename="%{$args.rspublishcomponent}"),
//	@ComponentType(name="rspublish", filename="jadex/extension/rs/publish/ExternalRSPublishAgent.class"),
//	@ComponentType(name="rspublish_grizzly", filename="jadex/extension/rs/publish/GrizzlyRSPublishAgent.class"),
//	@ComponentType(name="rspublish_jetty", filename="jadex/extension/rs/publish/JettyRSPublishAgent.class"),
	@ComponentType(name="wspublish", filename="jadex/extension/ws/publish/WSPublishAgent.class"),
	@ComponentType(name="cli", filename="jadex/platform/service/cli/CliAgent.class"),
	@ComponentType(name="sensor", clazz=SensorHolderAgent.class), //filename="jadex/platform/sensor/SensorHolderAgent.class")
	@ComponentType(name="df", clazz=DirectoryFacilitatorAgent.class),
	@ComponentType(name="clock", clazz=ClockAgent.class),
	@ComponentType(name="simulation", clazz=SimulationAgent.class),
	@ComponentType(name="filetransfer", clazz=FileTransferAgent.class),
	@ComponentType(name="security", clazz=SecurityAgent.class),
	@ComponentType(name="library", clazz=LibraryAgent.class),
	@ComponentType(name="settings", clazz=SettingsAgent.class),
	@ComponentType(name="address", clazz=TransportAddressAgent.class),
	@ComponentType(name="context", clazz=ContextAgent.class),
	@ComponentType(name="registrypeer", clazz=PeerRegistrySynchronizationAgent.class),
	@ComponentType(name="registrysuperpeer", clazz=SuperpeerRegistrySynchronizationAgent.class),
	@ComponentType(name="compregistry", filename="jadex/platform/service/componentregistry/ComponentRegistryAgent.class"),
	@ComponentType(name="tcp", clazz=TcpTransportAgent.class),
	@ComponentType(name="ws", filename="jadex/platform/service/message/websockettransport/WebSocketTransportAgent.class"),
	@ComponentType(name="rt", filename="jadex/platform/service/message/relaytransport/RelayTransportAgent.class"),
	@ComponentType(name="acr", clazz=AutoConfigRegistryAgent.class)
})

@ProvidedServices({
	@ProvidedService(type=IThreadPoolService.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.JavaThreadPool(false), $component.getIdentifier())", proxytype=Implementation.PROXYTYPE_RAW)),
	// hack!!! no daemon here (possibly fixed?)
	@ProvidedService(type=IDaemonThreadPoolService.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.JavaThreadPool(true), $component.getIdentifier())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IExecutionService.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(expression="($args.asyncexecution!=null && !$args.asyncexecution.booleanValue()) || ($args.asyncexecution==null && $args.simulation!=null && $args.simulation.booleanValue())? new jadex.platform.service.execution.SyncExecutionService($component): new jadex.platform.service.execution.AsyncExecutionService($component)", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IComponentManagementService.class, name="cms", implementation=@Implementation(expression="new jadex.platform.service.cms.ComponentManagementService($platformaccess, $bootstrapfactory, $args.uniqueids)"))
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
	}, components={
		@Component(name="library", type="library", daemon=Boolean3.TRUE, number="$args.library? 1 : 0"),
		@Component(name="context", type="context", daemon=Boolean3.TRUE, number="$args.context? 1 : 0"),
		@Component(name="settings", type="settings", daemon=Boolean3.TRUE, number="$args.settings? 1 : 0"),
		//@Component(name="%{jadex.base.IPlatformConfiguration.MONITORINGCOMP}", type="monitor", daemon=Boolean3.TRUE, number="$args.get(jadex.base.IPlatformConfiguration.MONITORINGCOMP)? 1 : 0"),
		@Component(name="mon", type="monitor", daemon=Boolean3.TRUE, number="$args.mon? 1 : 0"),
		
		@Component(name="kernel_multi", type="kernel_multi", daemon=Boolean3.TRUE, number="$args.kernel_multi? 1 : 0"),
		@Component(name="kernel_micro", type="kernel_micro", daemon=Boolean3.TRUE, number="$args.kernel_micro? 1 : 0"),
		@Component(name="kernel_component", type="kernel_component", daemon=Boolean3.TRUE, number="$args.kernel_component? 1 : 0"),
		@Component(name="kernel_application", type="kernel_application", daemon=Boolean3.TRUE, number="$args.kernel_application? 1 : 0"),
		@Component(name="kernel_bdiv3", type="kernel_bdiv3", daemon=Boolean3.TRUE, number="$args.kernel_bdiv3? 1 : 0"),
		@Component(name="kernel_bdi", type="kernel_bdi", daemon=Boolean3.TRUE, number="$args.kernel_bdi? 1 : 0"),
		@Component(name="kernel_bpmn", type="kernel_bpmn", daemon=Boolean3.TRUE, number="$args.kernel_bpmn? 1 : 0"),
		@Component(name="kernel_bdibpmn", type="kernel_bdibpmn", daemon=Boolean3.TRUE, number="$args.kernel_bdibpmn? 1 : 0"),
		@Component(name="kernel_gpmn", type="kernel_gpmn", daemon=Boolean3.TRUE, number="$args.kernel_gpmn? 1 : 0"),

//		@Component(name="compregistry", type="compregistry", daemon=Boolean3.TRUE, number="$args.compregistry? 1 : 0"),
		
		@Component(name="clock", type="clock", daemon=Boolean3.TRUE, number="$args.clock? 1 : 0", arguments=@NameValue(name="simulation", value="$args.simulation")),
		@Component(name="security", type="security", daemon=Boolean3.TRUE, number="$args.security? 1 : 0"),
		@Component(name="address", type="address", daemon=Boolean3.TRUE, number="$args.address? 1 : 0"),
		@Component(name="simulation", type="simulation", daemon=Boolean3.TRUE, number="$args.simul? 1 : 0"),
		@Component(name="filetransfer", type="filetransfer", daemon=Boolean3.TRUE, number="$args.filetransfer? 1 : 0"),
		
		@Component(name="awa", type="awa", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.get(\"awareness\")) ? 1 : 0"),
		
		//@Component(name="registrysuperpeer", type="registrysuperpeer", daemon=Boolean3.TRUE , number="$args.superpeer || $args.supersuperpeer? 1 : 0"),
		//@Component(name="registrypeer", type="registrypeer", daemon=Boolean3.TRUE , number="$args.superpeerclient? 1: $args.getArguments().get(\"superpeerclient\")==null && !$args.superpeer && !$args.supersuperpeer? 1 : 0"),
		
		@Component(name="chat", type="chat", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.get(\"chat\")) ? 1 : 0"),
		@Component(name="jcc", type="jcc", number="Boolean.TRUE.equals($args.get(\"gui\")) ? 1 : 0"),
		@Component(name="rspub", type="rspublish", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.rspublish)? 1: 0"),
		@Component(name="wspub", type="wspublish", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.wspublish)? 1: 0"),
		@Component(name="cli", type="cli", daemon=Boolean3.TRUE, number="jadex.commons.SReflect.classForName0(\"jadex.platform.service.cli.CliAgent\", jadex.platform.service.library.LibraryService.class.getClassLoader())!=null && Boolean.TRUE.equals($args.cli)? 1: 0"),
		
		@Component(name="df", type="df", daemon=Boolean3.TRUE, number="$args.df? 1 : 0"),
		@Component(name="sensors", type="sensor", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.sensors)? 1: 0"),
		@Component(name="tcp", type="tcp", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.tcptransport)? 1: 0"),
		@Component(name="ws", type="ws", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.wstransport)? 1: 0"),
		@Component(name="rt", type="rt", daemon=Boolean3.TRUE, number="Boolean.TRUE.equals($args.relaytransport)? 1: 0"),
		
		@Component(name="acr", type="acr", daemon=Boolean3.TRUE, number="$args.acr? 1 : 0")
	})
})
@Agent
public class PlatformAgent
{
	// where should the defaults be defined (here or in the config)
//	@Arguments
//	public static jadex.bridge.modelinfo.Argument[] getArguments()
//	{
//		return PlatformConfigurationHandler.getArguments();
//	}
}
