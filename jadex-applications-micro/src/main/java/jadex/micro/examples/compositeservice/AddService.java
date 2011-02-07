package jadex.micro.examples.compositeservice;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;

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
