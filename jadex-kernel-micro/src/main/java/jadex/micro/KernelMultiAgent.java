package jadex.micro;

import jadex.bridge.IComponentFactory;
import jadex.bridge.IMultiKernelNotifierService;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
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
public class KernelMultiAgent extends MicroAgent
{
}
