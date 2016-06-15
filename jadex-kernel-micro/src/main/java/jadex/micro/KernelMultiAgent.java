package jadex.micro;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IMultiKernelNotifierService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Multi kernel.
 */
@Arguments({
	@Argument(name="defaultkernels", description= "Kernel default locations.", 
		clazz=String[].class, defaultvalue="null"),
	@Argument(name="ignorekernels", description="Kernels that are ignored.",
		clazz=String[].class, defaultvalue="new String[] {}"),//{\"KernelBDI.component.xml\"}"),
	@Argument(name="ignoreextensions", description="File extensions that are ignored.",
		clazz=String[].class, defaultvalue="new String[] {\".png\", \".jpg\", \".dll\", \".gif\", \".exe\", \".doc\", \".docx\", \".txt\" }"),
	@Argument(name="kerneluriregex", description="Regular expression identifying kernel URIs  (ignored on android as there is only the DEX file).",
		clazz=String.class, defaultvalue="jadex.commons.SReflect.isAndroid() ? \".*\" : \".*[Kk]ernel.*\"")})
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(expression="new jadex.kernelbase.MultiFactory($args.defaultkernels, $args.ignorekernels, $args.ignoreextensions)")),
	@ProvidedService(type=IMultiKernelNotifierService.class, implementation=@Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(jadex.bridge.service.types.factory.IComponentFactory.class)"))
})
@ComponentTypes({
	@ComponentType(name="KernelMicro", filename="jadex/micro/KernelMicroAgent.class")
})
@Configurations({
	@Configuration(name="default", components={
		@Component(name="kernel_micro", type="KernelMicro")
	})
})
@Agent
@Properties(@NameValue(name="system", value="true"))
public class KernelMultiAgent
{
}
