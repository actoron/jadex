package jadex.micro;

import jadex.bridge.IComponentFactory;
import jadex.bridge.IMultiKernelNotifierService;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Multi kernel.
 */
@Arguments({
	@Argument(name="defaultkernels", description= "Kernel default locations.", 
		typename="String[]", defaultvalue="null"),
	@Argument(name="ignorekernels", description="Kernels that are ignored.",
		typename="String[]", defaultvalue="new String[] {\"KernelBDI.component.xml\"}"),
	@Argument(name="ignoreextensions", description="File extensions that are ignored.",
		typename="String[]", defaultvalue="null")})
@ProvidedServices({
	@ProvidedService(type=IMultiKernelNotifierService.class, implementation=@Implementation(expression="new jadex.kernelbase.MultiKernelNotifierService($component)")),
	@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(expression="new jadex.kernelbase.MultiFactory($component, $args.defaultkernels, $args.ignorekernels, $args.ignoreextensions)"))
})
@ComponentTypes({
	@ComponentType(name="KernelMicro", filename="jadex/micro/KernelMicroAgent.class")
})
@Configurations({
	@Configuration(name="default", components={
		@Component(name="kernel_micro", type="KernelMicro")
	})
})
public class KernelMultiAgent extends MicroAgent
{
}
