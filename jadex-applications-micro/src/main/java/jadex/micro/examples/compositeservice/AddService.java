package jadex.micro.examples.compositeservice;

import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;

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
