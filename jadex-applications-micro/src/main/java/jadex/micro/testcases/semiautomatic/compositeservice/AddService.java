package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class AddService extends BasicService implements IAddService
{
	/**
	 * 
	 */
	public AddService(IInternalAccess comp)
	{
		super(comp.getServiceProvider().getId(), IAddService.class, null);
	}
	
	/**
	 * 
	 */
	public IFuture add(double a, double b)
	{
		return new Future(new Double(a+b));
	}
}
