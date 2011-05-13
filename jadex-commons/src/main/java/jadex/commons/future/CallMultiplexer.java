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
		
		IFuture ret = (IFuture) futureMap.get(keyargs);
		if (ret == null)
		{
			final Object key = keyargs;
			ret = (IFuture) call.execute(null);
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
		return ret;
	}
}
