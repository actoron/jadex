package jadex.standalone;

import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.deployment.IDeploymentService;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.logging.Level;

@Imports({
	"jadex.base.service.marshal.*",
	"jadex.base.service.settings.*",
	"jadex.base.service.threadpool.*",
	"jadex.bridge.service.types.clock.*",
	"jadex.bridge.service.types.message.*",
	"jadex.commons.concurrent.*",
	"jadex.base.service.execution.*",
	"jadex.base.service.library.*",
	"jadex.commons.*",
	"jadex.base.service.remote.*",
	"jadex.base.service.clock.*",
	"jadex.base.service.message.*",
	"jadex.base.service.message.transport.*",
	"jadex.base.service.message.transport.localmtp.*",
	"jadex.base.service.message.transport.tcpmtp.*",
	"jadex.base.service.message.transport.niotcpmtp.*",
	"jadex.base.service.message.transport.httprelaymtp.*",
	"jadex.base.service.message.transport.httprelaymtp.nio.*",
	"jadex.bridge.*",
	"jadex.bridge.fipa.*",
	"jadex.base.service.message.transport.codecs.*",
	"jadex.standalone.service.*",
	"jadex.base.service.simulation.*",
	"jadex.component.*",
	"jadex.base.service.deployment.*",
	"jadex.micro.*",
	"jadex.extension.envsupport.*",
	"jadex.extension.agr.*",
	"jadex.benchmarking.services.*",
	"java.util.logging.Level",
	"java.net.URLClassLoader",
	"jadex.base.service.security.*",
	"jadex.extension.ws.publish.*",
	"jadex.extension.rs.publish.*"
})


@Arguments({
	@Argument(name="platformname", clazz=String.class, defaultvalue="\"jadex\""),
	@Argument(name="configname", clazz=String.class, defaultvalue="\"auto\""),
	@Argument(name="autoshutdown", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="adapterfactory", clazz=Class.class, defaultvalue="ComponentAdapterFactory.class"),
	@Argument(name="welcome", clazz=boolean.class, defaultvalue="true"),
	
	@Argument(name="gui", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="saveonexit", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="logging_level", clazz=Level.class, defaultvalue="Level.SEVERE"),
	@Argument(name="simulation", clazz=Boolean.class),
	@Argument(name="parametercopy", clazz=boolean.class, defaultvalue="true"),

	@Argument(name="libpath", clazz=String.class),
	@Argument(name="baseclassloader", clazz=ClassLoader.class),

	@Argument(name="awareness", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="awamechanisms", clazz=String[].class, defaultvalue="new String[]{\"Broadcast\", \"Multicast\"}"),
	@Argument(name="awaincludes", clazz=String.class, defaultvalue="\"\""),
	@Argument(name="awaexcludes", clazz=String.class, defaultvalue="\"\""),

	@Argument(name="usepass", clazz=Boolean.class),
	@Argument(name="printpass", clazz=boolean.class, defaultvalue="true"),

	@Argument(name="localtransport", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="tcptransport", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="niotcptransport", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="relaytransport", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="tcpport", clazz=int.class, defaultvalue="9876"),
	@Argument(name="niotcpport", clazz=int.class, defaultvalue="8765"),

	@Argument(name="extensions", clazz=String.class, defaultvalue="\"jadex/extension/envsupport/EnvSupportAgent.class, jadex/extension/agr/AGRAgent.class\""),

	@Argument(name="kernels", clazz=String.class, defaultvalue="\"multi\"")
})

@ComponentTypes({
	@ComponentType(name="extensions", filename="jadex/base/service/extensions/ExtensionsAgent.class"),
	@ComponentType(name="kernel_component", filename="jadex/component/KernelComponentAgent.class"),
	@ComponentType(name="kernel_application", filename="jadex/application/KernelApplication.component.xml"),
	@ComponentType(name="kernel_micro", filename="jadex/micro/KernelMicroAgent.class"),
	@ComponentType(name="kernel_bdi", filename="jadex/bdi/KernelBDI.component.xml"),
	@ComponentType(name="kernel_bdibpmn", filename="jadex/bdibpmn/KernelBDIBPMN.component.xml"),
	@ComponentType(name="kernel_bpmn", filename="jadex/bpmn/KernelBPMN.component.xml"),
	@ComponentType(name="kernel_gpmn", filename="jadex/gpmn/KernelGPMN.component.xml"),
	@ComponentType(name="kernel_multi", filename="jadex/micro/KernelMultiAgent.class"),
	@ComponentType(name="rms", filename="jadex/base/service/remote/RemoteServiceManagementAgent.class"),
	@ComponentType(name="awa", filename="jadex/base/service/awareness/management/AwarenessManagementAgent.class"),
	@ComponentType(name="jcc", filename="jadex/tools/jcc/JCCAgent.class")
})

@ProvidedServices({
	@ProvidedService(type=IMarshalService.class, implementation=@Implementation(expression="new MarshalService($component.getExternalAccess())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=ISettingsService.class, implementation=@Implementation(expression="new SettingsService($args.platformname, $component, $args.saveonexit)")),
	@ProvidedService(type=IThreadPoolService.class, implementation=@Implementation(expression="new ThreadPoolService(new ThreadPool(new DefaultThreadPoolStrategy(0, 20, 30000, 0)), $component.getServiceProvider())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IExecutionService.class, implementation=@Implementation(expression="$args.simulation==null || !$args.simulation.booleanValue()? new AsyncExecutionService($component.getServiceProvider()): new SyncExecutionService($component.getServiceProvider())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IDependencyService.class, implementation=@Implementation(expression="new BasicDependencyService()")),
	@ProvidedService(type=ILibraryService.class, implementation=@Implementation(expression="$args.libpath==null? new LibraryService(): new LibraryService(new URLClassLoader(SUtil.toURLs($args.libpath), $args.baseclassloader==null ? LibraryService.class.getClassLoader() : $args.baseclassloader))")),
	@ProvidedService(type=IClockService.class, implementation=@Implementation(expression="$args.simulation==null || !$args.simulation.booleanValue()? new ClockService(new ClockCreationInfo(IClock.TYPE_SYSTEM, \"system_clock\", System.currentTimeMillis(), 100), $component.getServiceProvider(), $args.simulation): new ClockService(new ClockCreationInfo(IClock.TYPE_EVENT_DRIVEN, \"simulation_clock\", System.currentTimeMillis(), 100), $component.getServiceProvider(), $args.simulation)", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="new MessageService($component.getExternalAccess(), $component.getLogger(), new ITransport[]{$args.localtransport? new LocalTransport($component.getServiceProvider()): null, $args.tcptransport? new TCPTransport($component.getServiceProvider(), $args.tcpport): null, $args.niotcptransport? new NIOTCPTransport($component.getServiceProvider(), $args.niotcpport, $component.getLogger()): null, $args.relaytransport? new HttpRelayTransport($component, SRelay.DEFAULT_ADDRESS): null}, new MessageType[]{new FIPAMessageType()}, null, new CodecFactory())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IComponentManagementService.class, implementation=@Implementation(expression="new DecoupledComponentManagementService($component.getComponentAdapter(), $args.componentfactory, $args.parametercopy)")),
	@ProvidedService(type=IDF.class, implementation=@Implementation(expression="new DirectoryFacilitatorService($component.getServiceProvider())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=ISimulationService.class, implementation=@Implementation(expression="new SimulationService($component)")),
	@ProvidedService(type=IDeploymentService.class, implementation=@Implementation(expression="new DeploymentService($component.getServiceProvider())")),
	@ProvidedService(type=IPublishService.class, name="wspub", implementation=@Implementation(expression="new DefaultWebServicePublishService()")),
	@ProvidedService(type=IPublishService.class, name="rspub", implementation=@Implementation(expression="new DefaultRestServicePublishService()")),
	@ProvidedService(type=ISecurityService.class, implementation=@Implementation(expression="new SecurityService($args.usepass, $args.printpass)"))
})

@RequiredServices({
	@RequiredService(name="factoryservices", type=IComponentFactory.class, multiple=true, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})

@Properties(
{
	@NameValue(name="componentviewer.viewerclass", value="jadex.base.gui.componentviewer.DefaultComponentServiceViewerPanel.class"),
	@NameValue(name="logging.level", value="$args.logging_level")
})

@Configurations({
	@Configuration(name="auto", arguments={
		@NameValue(name="tcpport", value="0"),
		@NameValue(name="niotcpport", value="0"),
		@NameValue(name="platformname", value="null")
	}, components={
		@Component(name="extensions", type="extensions", daemon=true, number="$args.extensions!=null ? 1 : 0", arguments=@NameValue(name="extensions", value="$args.extensions")),
		@Component(name="kernels", type="kernel_multi", daemon=true, number="$args.get(\"kernels\").indexOf(\"multi\")!=-1? 1 : 0"),
		@Component(name="kernel_component", type="kernel_component", daemon=true, number="$args.get(\"kernels\").indexOf(\"component\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_application", type="kernel_application", daemon=true, number="$args.get(\"kernels\").indexOf(\"application\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_micro", type="kernel_micro", daemon=true, number="$args.get(\"kernels\").indexOf(\"micro\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_bdibpmn", type="kernel_bdibpmn", daemon=true, number="$args.get(\"kernels\").indexOf(\"bdi\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_bpmn", type="kernel_bpmn", daemon=true, number="$args.get(\"kernels\").indexOf(\"bpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_gpmn", type="kernel_gpmn", daemon=true, number="$args.get(\"kernels\").indexOf(\"gpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="rms", type="rms", daemon=true),
		@Component(name="awa", type="awa", daemon=true, number="Boolean.TRUE.equals($args.get(\"awareness\")) ? 1 : 0",
			arguments={
				@NameValue(name="mechanisms", value="$args.awamechanisms"),
				@NameValue(name="includes", value="$args.awaincludes"),
				@NameValue(name="excludes", value="$args.awaexcludes")}),
		@Component(name="jcc", type="jcc", daemon=true, number="Boolean.TRUE.equals($args.get(\"gui\")) ? 1 : 0", arguments=@NameValue(name="saveonexit", value="$args.saveonexit"))
	}),
	@Configuration(name="fixed", arguments={
		@NameValue(name="tcpport", value="0"),
		@NameValue(name="niotcpport", value="0"),
		@NameValue(name="platformname", value="null")
	}, components={
		@Component(name="extensions", type="extensions", daemon=true, number="$args.extensions!=null ? 1 : 0", arguments=@NameValue(name="extensions", value="$args.extensions")),
		@Component(name="kernels", type="kernel_multi", daemon=true, number="$args.get(\"kernels\").indexOf(\"multi\")!=-1? 1 : 0"),
		@Component(name="kernel_component", type="kernel_component", daemon=true, number="$args.get(\"kernels\").indexOf(\"component\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_application", type="kernel_application", daemon=true, number="$args.get(\"kernels\").indexOf(\"application\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_micro", type="kernel_micro", daemon=true, number="$args.get(\"kernels\").indexOf(\"micro\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_bdibpmn", type="kernel_bdibpmn", daemon=true, number="$args.get(\"kernels\").indexOf(\"bdi\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_bpmn", type="kernel_bpmn", daemon=true, number="$args.get(\"kernels\").indexOf(\"bpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="kernel_gpmn", type="kernel_gpmn", daemon=true, number="$args.get(\"kernels\").indexOf(\"gpmn\")!=-1 || $args.get(\"kernels\").indexOf(\"all\")!=-1? 1 : 0"),
		@Component(name="rms", type="rms", daemon=true),
		@Component(name="awa", type="awa", daemon=true, number="Boolean.TRUE.equals($args.get(\"awareness\")) ? 1 : 0",
			arguments={
				@NameValue(name="mechanisms", value="$args.awamechanisms"),
				@NameValue(name="includes", value="$args.awaincludes"),
				@NameValue(name="excludes", value="$args.awaexcludes")}),
		@Component(name="jcc", type="jcc", daemon=true, number="Boolean.TRUE.equals($args.get(\"gui\")) ? 1 : 0", arguments=@NameValue(name="saveonexit", value="$args.saveonexit"))
	})
})
public class PlatformAgent extends MicroAgent
{
}
