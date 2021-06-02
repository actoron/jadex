package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.service.ServiceScope;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Description("This agent is an empty minimal calculator.")
@RequiredServices({
	@RequiredService(name="addservice", type=IAddService.class, scope=ServiceScope.PLATFORM),
	@RequiredService(name="subservice", type=ISubService.class, scope=ServiceScope.PLATFORM)
})
@Agent
public class EmptyCalculatorAgent
{
}

