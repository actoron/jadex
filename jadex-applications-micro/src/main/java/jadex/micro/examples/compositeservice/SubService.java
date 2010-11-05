package jadex.micro.examples.compositeservice;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.commons.service.IServiceProvider;

/**
 * 
 */
public class SubService extends BasicService implements ISubService
{
	/**
	 * 
	 */
	public SubService(IServiceProvider provider)
	{
		super(provider.getId(), IAddService.class, null);
	}
	
	/**
	 * 
	 */
	public IFuture sub(double a, double b)
	{
		Future ret = new Future();
		ret.setResult(new Double(a-b));
		return ret;
	}
}
