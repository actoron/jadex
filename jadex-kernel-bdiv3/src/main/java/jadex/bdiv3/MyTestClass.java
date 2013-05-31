package jadex.bdiv3;

import jadex.commons.Tuple3;

import java.util.ArrayList;
import java.util.List;


public class MyTestClass
{
//	public int testfield = inc();

//	@Belief(dependson="testfield")
//	protected int testfield2 = testfield+3;

//	protected int testfield3 = getVal();

//	protected int[] myints = new int[3];
//
//	protected Object[] myobs = new Object[2];
	
//	protected BDIAgent	__agent;

//	protected List<Tuple3<Class<?>,Class<?>[], Object[]>>	initcalls;
	
	
	public MyTestClass(String name, List values)
	{
		BDIAgent.addInitArgs(this, MyTestClass.class, new Class<?>[]{String.class,  List.class}, new Object[]{name, values});
		
//		if(initcalls==null)
//		{
//			initcalls	= new ArrayList<Tuple3<Class<?>,Class<?>[], Object[]>>();
//		}
//		
//		initcalls.add(new Tuple3<Class<?>, Class<?>[], Object[]>(MyTestClass.class, new Class<?>[]{String.class,  List.class}, new Object[]{name, values}));
		
//		myints[0] = 3;
//		
//		myobs[1] = new Object();
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

//	public static void main(String[] args)
//	{
//		MyTestClass tm = new MyTestClass();
//	}
}
