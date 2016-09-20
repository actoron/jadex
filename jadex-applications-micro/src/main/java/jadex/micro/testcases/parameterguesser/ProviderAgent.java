package jadex.micro.testcases.parameterguesser;

import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent
@Service
@ProvidedServices({
		@ProvidedService(type=IInjectionTestService.class, implementation = @Implementation(InjectionTestService.class))
})
public class ProviderAgent {
}

