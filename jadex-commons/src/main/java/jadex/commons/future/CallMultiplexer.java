package jadex.commons.future;

import jadex.commons.IResultCommand;
import jadex.commons.Tuple;

import java.util.HashMap;
import java.util.Map;


public class CallMultiplexer
{
	protected Map futureMap;
	
	public CallMultiplexer()
	{
		futureMap = new HashMap();
	}
	
	public IFuture doCall(IResultCommand call)
	{
		return doCall(null, call);
	}
	
	public IFuture doCall(Object keyargs, IResultCommand call)
	{
		return doCall(keyargs, call, true);
	}
	
	public IFuture doCall(Object keyargs, IResultCommand call, boolean commandaskey)
	{
		if (keyargs != null && keyargs.getClass().isArray())
			keyargs = new Tuple((Object[]) keyargs);
		if (commandaskey)
			keyargs = new Tuple(keyargs, call.getClass());
		
		final Future ret = new Future();
		IFuture res = (IFuture) futureMap.get(keyargs);
		if (res == null)
		{
			final Object key = keyargs;
			res = (IFuture) call.execute(null);
			futureMap.put(key, ret);
			ret.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					futureMap.remove(key);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					resultAvailable(exception);
				}
			});
		}
		res.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				futureMap.remove(ret);
				super.customResultAvailable(result);
			}
		});
		return ret;
	}
}
