package jadex.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

public class SFilter
{
	public static final <T> IFuture<Collection<T>> applyFilter(Collection<T> input, IAsyncFilter<T> filter)
	{
		final Future<Collection<T>> ret = new Future<>();
		
		if (input != null)
		{
			final List<T> results = new ArrayList<>();
			AtomicInteger count = new AtomicInteger(0);
			final int max = input.size();
			for (final T in : input)
			{
				filter.filter(in).addResultListener(new IResultListener<Boolean>()
				{
					public void resultAvailable(Boolean result)
					{
						int num = count.incrementAndGet();
						if (Boolean.TRUE.equals(result))
							results.add(in);
						if (num >= max)
							ret.setResult(results);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						count.set(max);
						ret.setException(exception);
					}
				});
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
}
