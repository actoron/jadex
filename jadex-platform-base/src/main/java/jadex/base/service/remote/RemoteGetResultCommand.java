package jadex.base.service.remote;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 *  Command for remote searching.
 */
public class RemoteGetResultCommand extends RemoteResultCommand
{
	//-------- constructors --------
	
	/**
	 *  Create a new remote search result command.
	 */
	public RemoteGetResultCommand()
	{
	}

	/**
	 *  Create a new remote search result command.
	 */
	public RemoteGetResultCommand(Object result, Exception exception, String callid)
	{
		super(result, exception, callid);
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param lrms The local remote management service.
	 *  @return An optional result command that will be 
	 *  sent back to the command origin. 
	 */
	public IFuture execute(final IMicroExternalAccess component, final Map waitingcalls)
	{
		final Future ret = new Future();
		
		// Post-process results to make them real proxies.
		
		SServiceProvider.getService(component.getServiceProvider(), ILibraryService.class)
			.addResultListener(component.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object res)
			{
				ILibraryService ls = (ILibraryService)res;
				
				ProxyInfo pi = (ProxyInfo)result;
				result = Proxy.newProxyInstance(ls.getClassLoader(), 
					new Class[]{pi.getTargetClass()},
					new RemoteMethodInvocationHandler(component, pi, waitingcalls));
				
				RemoteGetResultCommand.super.execute(component, waitingcalls)
					.addResultListener(component.createResultListener(new DelegationResultListener(ret)));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		}));
		
		return ret;
	}
}

