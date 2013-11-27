package jadex.commons.future;


public class CascadingFuture<E> extends Future<E>
{
	protected int stacksize;
//	protected List<IResultListener<E>>	listeners	= new LinkedList<IResultListener<E>>();
//	protected E	result;
	protected boolean	hasresult;
	
	public void	setCascadingResult(E result)
	{
		System.out.println("setResult");
		assert !hasresult;
		this.result	= result;
		hasresult	= true;
		unstack();
	}

	public void	addCascadingResultListener(IResultListener<E> listener)
	{
		listeners.add(listener);
		System.out.println("addListener: "+listeners);
		unstack();
	}

	protected void	unstack()
	{
		stacksize++;
		while(/*stacksize==1 &&*/ hasresult && !listeners.isEmpty())
		{
			E	res	= result;
			IResultListener<E>	lis	= listeners.remove(0);
			result	= null;
			hasresult	= false;
			System.out.println("notifyListener");
			lis.resultAvailable(res);
		}
		stacksize--;
	}
}
