package jadex.bdiv3;


public class MyTestClass
{
//	public int testfield = inc();

//	@Belief(dependson="testfield")
//	protected int testfield2 = testfield+3;

//	protected int testfield3 = getVal();

	protected int[] myints = new int[3];

	protected Object[] myobs = new Object[2];
	
	public MyTestClass()
	{
		myints[0] = 3;
		
		myobs[1] = new Object();
//		System.out.println("hello");
//		System.out.println(testfield+" "+testfield2+" "+testfield3);
//		testfield = 22;
	}
	
//	public int getVal()
//	{
//		return 99;
//	}
//	
//	public int inc()
//	{
//		return testfield2++;
//	}
//	
//	public int getTestfield()
//	{
//		return testfield;
//	}

	public static void main(String[] args)
	{
		MyTestClass tm = new MyTestClass();
	}
}
