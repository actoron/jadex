package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Description("This agent is an empty minimal calculator.")
@RequiredServices({
	@RequiredService(name="addservice", type=IAddService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)),
	@RequiredService(name="subservice", type=ISubService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})
public class EmptyCalculatorAgent
{
}

