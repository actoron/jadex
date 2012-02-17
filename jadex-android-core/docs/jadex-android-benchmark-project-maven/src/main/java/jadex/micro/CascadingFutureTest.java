package jadex.micro;

import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.DefaultResultListener;

public class CascadingFutureTest
{
	protected static ThreadLocal<CascadingFuture<String>>	CFUTURE	= new ThreadLocal<CascadingFuture<String>>();
	
	protected static CascadingFuture<String>	sync(String s, int i)
	{
		final CascadingFuture<String>	ret	= CFUTURE.get()!=null ? CFUTURE.get() : new CascadingFuture<String>();
		if(CFUTURE.get()==null)
			CFUTURE.set(ret);
		
		ret.setCascadingResult("sync"+s+i);
		return ret;
	}
	
	protected static CascadingFuture<String>	recurse(final int i)
	{
		final CascadingFuture<String>	ret	= CFUTURE.get()!=null ? CFUTURE.get() : new CascadingFuture<String>();
		if(CFUTURE.get()==null)
			CFUTURE.set(ret);
		
		if(i>0)
		{
			sync("A", i).addCascadingResultListener(new DelegationResultListener<String>(ret)
			{
				public void customResultAvailable(final String res1)
				{
					sync("B", i).addCascadingResultListener(new DelegationResultListener<String>(ret)
					{
						public void customResultAvailable(final String res2)
						{
							recurse(i-1).addCascadingResultListener(new DelegationResultListener<String>(ret)
							{
								public void customResultAvailable(String res3)
								{
									ret.setCascadingResult("rec"+i+"(syncA="+res1+" "+"syncB="+res2+" "+res3+")");
								}
							});
						}
					});
				}
			});
		}
		else
		{
			long	time	= System.nanoTime();
			long sum	= 0;
			for(int j=0; j<10000; j++)
			{
				sum	+= Thread.currentThread().getStackTrace().length;
			}
			System.out.println("stack: "+sum/10000+", time "+(System.nanoTime()-time)/10000/1000/1000.0+" ms");
//			Thread.dumpStack();
			ret.setCascadingResult("rec"+i);
		}
		return ret;
	}
	
	public static void main(String[] args)
	{
		recurse(3).addCascadingResultListener(new DefaultResultListener<String>()
		{
			public void resultAvailable(String result)
			{
				System.out.println("Result: "+result);
			}
		});
	}
}
