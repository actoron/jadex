package jadex.micro.examples.compositeservice;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;

/**
 * 
 */
public class SubService extends BasicService implements ISubService
{
	/**
	 * 
	 */
	public SubService(IInternalAccess comp)
	{
		super(comp.getServiceProvider().getId(), ISubService.class, null);
	}
	
	/**
	 * 
	 */
	public IFuture sub(double a, double b)
	{
		return new Future(new Double(a-b));
	}
}
