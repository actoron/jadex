package jadex.platform.service.wrapper;

/**
 *  Test java program.
 */
public class TestMain
{
	/**
	 *  Main method with arguments.
	 *  If first arguments equals "fail" an exception is thrown.
	 */
	public static void	main(String[] args)
	{
		if(args.length>0 && "fail".equals(args[0]))
		{
			throw new TestException();
		}
		// else program finishes successfully.
	}
	
	/**
	 *  A test exception for testing failure cases.
	 */
	public static class TestException	extends RuntimeException
	{
		public TestException()
		{
		}
	}
}
