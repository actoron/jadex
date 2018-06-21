package jadex.platform.service.componentregistry;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

@Service
public interface IHelloService
{	
	public IFuture<String> sayHello(String name);
}
