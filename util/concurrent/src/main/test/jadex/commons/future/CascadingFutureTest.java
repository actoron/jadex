package jadex.commons.future;

public class CascadingFutureTest
{
	protected static IFuture<String>	sync(String s, int i)
	{
		System.out.println("Sync"+s+i);
		return new Future<String>(s+i);
	}
	
	protected static IFuture<String>	recurse(final int i)
	{
		System.out.println("rec"+i);
		final Future<String>	ret	= new Future<String>();
		
		if(i>0)
		{
			sync("A", i).addResultListener(new DelegationResultListener<String>(ret)
			{
				public void customResultAvailable(final String res1)
				{
					sync("B", i).addResultListener(new DelegationResultListener<String>(ret)
					{
						public void customResultAvailable(final String res2)
						{
							recurse(i-1).addResultListener(new DelegationResultListener<String>(ret)
							{
								public void customResultAvailable(String res3)
								{
									ret.setResult("rec"+i+"(syncA="+res1+" "+"syncB="+res2+" "+res3+")");
								}
							});
						}
					});
				}
			});
		}
		else
		{
			Thread.dumpStack();
			ret.setResult("rec"+i);
		}
		System.out.println("rec"+i+ "end");
		return ret;
	}
	
	public static void main(String[] args)
	{
		recurse(10).addResultListener(new DefaultResultListener<String>()
		{
			public void resultAvailable(String result)
			{
				System.out.println("Result: "+result);
			}
		});
	}
}
