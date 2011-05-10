package jadex.standalone;

import jadex.base.fipa.IDF;
import jadex.base.service.deployment.IDeploymentService;
import jadex.base.service.simulation.ISimulationService;
import jadex.bridge.IComponentFactory;
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
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Arguments({
	@Argument(name="platformname", typename="String", defaultvalue="\"jadex\""),
	@Argument(name="saveonexit", typename="boolean", defaultvalue="true"),
	@Argument(name="simulation", typename="boolean", defaultvalue="false"),
	@Argument(name="libpath", typename="String[]"),
	@Argument(name="tcpport", typename="int", defaultvalue="9876"),
	@Argument(name="niotcpport", typename="int", defaultvalue="8765"),
	@Argument(name="awaincludes", typename="String"),
	@Argument(name="awaexcludes", typename="String"),
})

@ComponentTypes({
	@ComponentType(name="kernel_application", filename="jadex/application/KernelApplication.component.xml"),
	@ComponentType(name="kernel_micro", filename="jadex/micro/KernelMicro.component.xml"),
	@ComponentType(name="kernel_bdi", filename="jadex/bdi/KernelBDI.component.xml"),
	@ComponentType(name="kernel_bdibpmn", filename="jadex/bdibpmn/KernelBDIBPMN.component.xml"),
	@ComponentType(name="kernel_bpmn", filename="jadex/bpmn/KernelBPMN.component.xml"),
	@ComponentType(name="rms", filename="jadex/base/service/remote/RemoteServiceManagementAgent.class"),
	@ComponentType(name="awa", filename="jadex/base/service/awareness/AwarenessAgent.class"),
	@ComponentType(name="jcc", filename="jadex/tools/jcc/JCCAgent.class"),
	@ComponentType(name="CreationBDI", filename="jadex/bdi/benchmarks/AgentCreation.agent.xml"),
	@ComponentType(name="CreationMicro", filename="jadex/micro/benchmarks/AgentCreationAgent.class"),
	@ComponentType(name="CreationBPMN", filename="jadex/bpmn/benchmarks/AgentCreation2.bpmn")
})

@ProvidedServices({
	@ProvidedService(type=ISettingsService.class, implementation=@Implementation(expression="new jadex.base.service.settings.SettingsService($args.platformname, $component, $args.saveonexit)")),
	@ProvidedService(type=IThreadPool.class, implementation=@Implementation(expression="new jadex.bridge.service.threadpool.ThreadPoolService(new jadex.commons.concurrent.ThreadPool(new jadex.commons.concurrent.DefaultThreadPoolStrategy(0, 20, 30000, 0)), $component.getServiceProvider())", direct=true)),
	@ProvidedService(type=IExecutionService.class, implementation=@Implementation(expression="$args.simulation? new jadex.bridge.service.execution.SyncExecutionService($component.getServiceProvider()): new jadex.bridge.service.execution.AsyncExecutionService($component.getServiceProvider())", direct=true)),
	@ProvidedService(type=ILibraryService.class, implementation=@Implementation(expression="new jadex.bridge.service.library.LibraryService($args.libpath, $component.getServiceProvider(), jadex.commons.SUtil.createHashMap(new Object[]{jadex.base.service.remote.RemoteServiceManagementService.REMOTE_EXCLUDED}, new Object[]{new String[]{\"getClassLoader\"}}))", direct=true)),
	@ProvidedService(type=IClockService.class, implementation=@Implementation(expression="$args.simulation? new jadex.bridge.service.clock.ClockService(new jadex.bridge.service.clock.ClockCreationInfo(jadex.bridge.service.clock.IClock.TYPE_EVENT_DRIVEN, \"simulation_clock\", System.currentTimeMillis(), 100), $component.getServiceProvider(), jadex.commons.SUtil.createHashMap(new Object[]{jadex.base.service.remote.RemoteServiceManagementService.REMOTE_UNCACHED}, new Object[]{new String[]{\"getState\", \"getTime\", \"getTick\", \"getStarttime\", \"getDelta\", \"getDilation\", \"getNextTimer\", \"getTimers\", \"getClockType\", \"advanceEvent\"}})): new jadex.bridge.service.clock.ClockService(new jadex.bridge.service.clock.ClockCreationInfo(jadex.bridge.service.clock.IClock.TYPE_SYSTEM, \"simulation_clock\", System.currentTimeMillis(), 100), $component.getServiceProvider(), jadex.commons.SUtil.createHashMap(new Object[]{jadex.base.service.remote.RemoteServiceManagementService.REMOTE_UNCACHED}, new Object[]{new String[]{\"getState\", \"getTime\", \"getTick\", \"getStarttime\", \"getDelta\", \"getDilation\", \"getNextTimer\", \"getTimers\", \"getClockType\", \"advanceEvent\"}}))", direct=true)),
	@ProvidedService(type=IMessageService.class, implementation=@Implementation(expression="new jadex.base.service.message.MessageService($component.getExternalAccess(), new jadex.base.service.message.transport.ITransport[]{new jadex.base.service.message.transport.localmtp.LocalTransport($component.getServiceProvider()), new jadex.base.service.message.transport.niotcpmtp.NIOTCPTransport($component.getServiceProvider(), $args.niotcpport, $component.getLogger()),new jadex.bridge.MessageType[]{new jadex.base.fipa.FIPAMessageType()}, null, new jadex.base.service.message.transport.codecs.CodecFactory(null, new Class[]{jadex.base.service.message.transport.codecs.GZIPCodec.class, jadex.base.service.message.transport.codecs.JadexXMLCodec.class}))", direct=true)),
//	@ProvidedService(type=IComponentManagementService.class, implementation=@Implementation(expression="new jadex.standalone.service.ComponentManagementService($component.getExternalAccess(), $component.getComponentAdapter())", direct=true)),
//	@ProvidedService(type=IDF.class, implementation=@Implementation(expression="new jadex.standalone.service.DirectoryFacilitatorService($component.getServiceProvider())", direct=true)),
//	@ProvidedService(type=ISimulationService.class, implementation=@Implementation(expression="new jadex.base.service.simulation.SimulationService($component,SUtil.createHashMap(new Object[]{RemoteServiceManagementService.REMOTE_UNCACHED}, new Object[]{new String[]{\"getMode\", \"isExecuting\"}})))", direct=true)),
//	@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(expression="new jadex.component.ComponentComponentFactory($component.getServiceProvider())", direct=true)),
//	@ProvidedService(type=IDeploymentService.class, implementation=@Implementation(expression="new jadex.base.service.deployment.DeploymentService($component.getServiceProvider())")),
})

@RequiredServices({
	@RequiredService(name="factoryservices", type=IComponentFactory.class, multiple=true)
})

@Configurations({
	@Configuration(name="all_kernels auto (rms, awa, jcc)", components={
		@Component(name="kernel_application", type="kernel_component", daemon=true),
		@Component(name="kernel_micro", type="kernel_micro", daemon=true),
		@Component(name="kernel_bdibpmn", type="kernel_bdibpmn", daemon=true),
		@Component(name="kernel_bpmn", type="kernel_bpmn", daemon=true),
		@Component(name="rms", type="rms", daemon=true),
		@Component(name="awa", type="awa", daemon=true, 
			arguments={@NameValue(name="includes", value="$args.awaincludes"),
				@NameValue(name="excludes", value="$args.awaexcludes")}),
		@Component(name="jcc", type="jcc", daemon=true)
	})
})
public class PlattformAgent extends MicroAgent
{
}
