package jadex.micro.examples.compositeservice;

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
	public AddService(IServiceProvider provider)
	{
		super(provider.getId(), IAddService.class, null);
	}
	
	/**
	 * 
	 */
	public IFuture add(double a, double b)
	{
		if(!isValid())
			throw new RuntimeException();
			
		Future ret = new Future();
		ret.setResult(new Double(a+b));
		return ret;
	}
}
