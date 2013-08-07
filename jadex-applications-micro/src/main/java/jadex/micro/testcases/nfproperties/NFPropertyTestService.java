package jadex.micro.testcases.nfproperties;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.IFuture;

@Service
public class NFPropertyTestService implements ICoreDependentService
{
	@ServiceStart
	public IFuture<Void> x()
	{
		System.out.println("SSTASD");
		return IFuture.DONE;
	}

	@NFProperties(@NFProperty(name="methodspeed", type=MethodSpeedProperty.class))
	public IFuture<Void> testMethod()
	{
		return IFuture.DONE;
	}
}
