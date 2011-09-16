package jadex.standalone;

import jadex.base.fipa.IDF;
import jadex.base.service.deployment.IDeploymentService;
import jadex.base.service.simulation.ISimulationService;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentFactoryExtensionService;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IMessageService;
import jadex.bridge.ISettingsService;
import jadex.bridge.service.clock.IClockService;
import jadex.bridge.service.execution.IExecutionService;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.concurrent.IThreadPool;
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
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Imports({
	"jadex.base.service.settings.*",
	"jadex.bridge.service.threadpool.*",
	"jadex.commons.concurrent.*",
	"jadex.bridge.service.execution.*",
	"jadex.bridge.service.library.*",
	"jadex.commons.*",
	"jadex.base.service.remote.*",
	"jadex.bridge.service.clock.*",
	"jadex.base.service.message.*",
	"jadex.base.service.message.transport.*",
	"jadex.base.service.message.transport.localmtp.*",
	"jadex.base.service.message.transport.niotcpmtp.*",
	"jadex.bridge.*",
	"jadex.base.fipa.*",
	"jadex.base.service.message.transport.codecs.*",
	"jadex.standalone.service.*",
	"jadex.base.service.simulation.*",
	"jadex.component.*",
	"jadex.base.service.deployment.*",
	"jadex.micro.*",
	"jadex.extension.envsupport.*",
	"jadex.extension.agr.*",
	"jadex.benchmarking.services.*"
})

@Arguments({
	@Argument(name="platformname", clazz=String.class, defaultvalue="\"jadex\""),
	@Argument(name="config", clazz=String.class, defaultvalue="\"auto\""),
	@Argument(name="autoshutdown", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="adapterfactory", clazz=Class.class, defaultvalue="ComponentAdapterFactory.class"),
	@Argument(name="welcome", clazz=boolean.class, defaultvalue="true"),
	
	@Argument(name="awareness", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="gui", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="saveonexit", clazz=boolean.class, defaultvalue="true"),
	@Argument(name="simulation", clazz=boolean.class, defaultvalue="false"),
	@Argument(name="libpath", clazz=String[].class),
	@Argument(name="tcpport", clazz=int.class, defaultvalue="9876"),
	@Argument(name="niotcpport", clazz=int.class, defaultvalue="8765"),
	@Argument(name="awaincludes", clazz=String.class, defaultvalue="\"\""),
	@Argument(name="awaexcludes", clazz=String.class, defaultvalue="\"\""),
	@Argument(name="parametercopy", clazz=boolean.class, defaultvalue="true")
})

@ComponentTypes({
	@ComponentType(name="kernel_component", filename="jadex/component/KernelComponentAgent.class"),
	@ComponentType(name="kernel_application", filename="jadex/application/KernelApplication.component.xml"),
	@ComponentType(name="kernel_micro", filename="jadex/micro/KernelMicroAgent.class"),
	@ComponentType(name="kernel_bdi", filename="jadex/bdi/KernelBDI.component.xml"),
	@ComponentType(name="kernel_bdibpmn", filename="jadex/bdibpmn/KernelBDIBPMN.component.xml"),
	@ComponentType(name="kernel_bpmn", filename="jadex/bpmn/KernelBPMN.component.xml"),
	@ComponentType(name="kernel_multi", filename="jadex/micro/KernelMultiAgent.class"),
	@ComponentType(name="rms", filename="jadex/base/service/remote/RemoteServiceManagementAgent.class"),
	@ComponentType(name="awa", filename="jadex/base/service/awareness/AwarenessAgent.class"),
	@ComponentType(name="jcc", filename="jadex/tools/jcc/JCCAgent.class")
})

@ProvidedServices({
	@ProvidedService(type=ISettingsService.class, implementation=@Implementation(expression="new SettingsService($args.platformname, $component, $args.saveonexit)")),
	@ProvidedService(type=IThreadPool.class, implementation=@Implementation(expression="new ThreadPoolService(new ThreadPool(new DefaultThreadPoolStrategy(0, 20, 30000, 0)), $component.getServiceProvider())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IExecutionService.class, implementation=@Implementation(expression="$args.simulation? new SyncExecutionService($component.getServiceProvider()): new AsyncExecutionService($component.getServiceProvider())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=ILibraryService.class, implementation=@Implementation(expression="new LibraryService($args.libpath, $component.getServiceProvider())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IClockService.class, implementation=@Implementation(expression="$args.simulation? new ClockService(new ClockCreationInfo(IClock.TYPE_EVENT_DRIVEN, \"simulation_clock\", System.currentTimeMillis(), 100), $component.getServiceProvider()): new ClockService(new ClockCreationInfo(IClock.TYPE_SYSTEM, \"system_clock\", System.currentTimeMillis(), 100), $component.getServiceProvider())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="new MessageService($component.getExternalAccess(), $component.getLogger(), new ITransport[]{new LocalTransport($component.getServiceProvider()), new NIOTCPTransport($component.getServiceProvider(), $args.niotcpport, $component.getLogger())}, new MessageType[]{new FIPAMessageType()}, null, new CodecFactory())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IComponentManagementService.class, implementation=@Implementation(expression="new ComponentManagementService($component.getExternalAccess(), $component.getComponentAdapter(), $args.componentfactory, $args.parametercopy)", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IDF.class, implementation=@Implementation(expression="new DirectoryFacilitatorService($component.getServiceProvider())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=ISimulationService.class, implementation=@Implementation(expression="new SimulationService($component)")),
	@ProvidedService(type=IDeploymentService.class, implementation=@Implementation(expression="new DeploymentService($component.getServiceProvider())")),
	@ProvidedService(name="envextension", type=IComponentFactoryExtensionService.class, implementation=@Implementation(expression="new EnvSupportExtensionService()")),	// expression to avoid compile-time dependency
	@ProvidedService(name="agrextension", type=IComponentFactoryExtensionService.class, implementation=@Implementation(expression="new AGRExtensionService()"))	// expression to avoid compile-time dependency
})

@RequiredServices({
	@RequiredService(name="factoryservices", type=IComponentFactory.class, multiple=true, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})

@Configurations({
	@Configuration(name="auto", arguments={
		@NameValue(name="tcpport", value="0"),
		@NameValue(name="niotcpport", value="0"),
		@NameValue(name="platformname", value="null")
	}, components={
		@Component(name="kernels", type="kernel_multi", daemon=true),
		@Component(name="rms", type="rms", daemon=true),
		@Component(name="awa", type="awa", daemon=true, number="Boolean.TRUE.equals($args.get(\"awareness\")) ? 1 : 0",
			arguments={@NameValue(name="includes", value="$args.awaincludes"),
				@NameValue(name="excludes", value="$args.awaexcludes")}),
		@Component(name="jcc", type="jcc", daemon=true, number="Boolean.TRUE.equals($args.get(\"gui\")) ? 1 : 0")
	}),
	@Configuration(name="fixed", components={
		@Component(name="kernels", type="kernel_multi", daemon=true),
		@Component(name="rms", type="rms", daemon=true),
		@Component(name="awa", type="awa", daemon=true, number="Boolean.TRUE.equals($args.get(\"awareness\")) ? 1 : 0",
			arguments={@NameValue(name="includes", value="$args.awaincludes"),
				@NameValue(name="excludes", value="$args.awaexcludes")}),
		@Component(name="jcc", type="jcc", daemon=true, number="Boolean.TRUE.equals($args.get(\"gui\")) ? 1 : 0")
	}),
	@Configuration(name="allkernels", arguments={
		@NameValue(name="tcpport", value="0"),
		@NameValue(name="niotcpport", value="0"),
		@NameValue(name="platformname", value="null")
	}, components={
		@Component(name="kernel_component", type="kernel_component", daemon=true),
		@Component(name="kernel_application", type="kernel_application", daemon=true),
		@Component(name="kernel_micro", type="kernel_micro", daemon=true),
		@Component(name="kernel_bdibpmn", type="kernel_bdibpmn", daemon=true),
		@Component(name="kernel_bpmn", type="kernel_bpmn", daemon=true),
		@Component(name="rms", type="rms", daemon=true),
		@Component(name="awa", type="awa", daemon=true, number="Boolean.TRUE.equals($args.get(\"awareness\")) ? 1 : 0",
			arguments={@NameValue(name="includes", value="$args.awaincludes"),
				@NameValue(name="excludes", value="$args.awaexcludes")}),
		@Component(name="jcc", type="jcc", daemon=true, number="Boolean.TRUE.equals($args.get(\"gui\")) ? 1 : 0")
	}),
	@Configuration(name="allkernels_fixed", components={
		@Component(name="kernel_component", type="kernel_component", daemon=true),
		@Component(name="kernel_application", type="kernel_application", daemon=true),
		@Component(name="kernel_micro", type="kernel_micro", daemon=true),
		@Component(name="kernel_bdibpmn", type="kernel_bdibpmn", daemon=true),
		@Component(name="kernel_bpmn", type="kernel_bpmn", daemon=true),
		@Component(name="rms", type="rms", daemon=true),
		@Component(name="awa", type="awa", daemon=true, number="Boolean.TRUE.equals($args.get(\"awareness\")) ? 1 : 0",
			arguments={@NameValue(name="includes", value="$args.awaincludes"),
				@NameValue(name="excludes", value="$args.awaexcludes")}),
		@Component(name="jcc", type="jcc", daemon=true, number="Boolean.TRUE.equals($args.get(\"gui\")) ? 1 : 0")
	})
})
public class PlatformAgent extends MicroAgent
{
}
