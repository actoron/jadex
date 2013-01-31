package jadex.bdiv3;


public class MyTestClass
{
	protected int testfield = 3+7;

	protected int testfield2 = testfield+3;

	protected int testfield3 = getVal();

	public MyTestClass()
	{
		System.out.println("hello");
		testfield = 22;
	}
	
	public int getVal()
	{
		return 99;
	}
	
}
