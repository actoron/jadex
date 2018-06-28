package jadex.micro.testcases.semiautomatic.nfproperties;

import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.IFuture;

/**
 *  Service with nf props.
 */
@Service
public class NFPropertyTestService implements ICoreDependentService
{
	/**
	 *  Init method.
	 */
	@ServiceStart
	public IFuture<Void> x()
	{
//		System.out.println("SSTASD");
		return IFuture.DONE;
	}

	/**
	 *  Example method.
	 */
	@NFProperties(@NFProperty(name="methodspeed", value=MethodSpeedProperty.class))
	public IFuture<Void> testMethod()
	{
		return IFuture.DONE;
	}
}
