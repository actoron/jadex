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
	
	protected String	__globalname	= "huhu";
	
	
//	public MyTestClass(String name, List values)
//	{
//		BDIAgent.addInitArgs(this, MyTestClass.class, new Class<?>[]{String.class,  List.class}, new Object[]{name, values});
//		
////		if(initcalls==null)
////		{
////			initcalls	= new ArrayList<Tuple3<Class<?>,Class<?>[], Object[]>>();
////		}
////		
////		initcalls.add(new Tuple3<Class<?>, Class<?>[], Object[]>(MyTestClass.class, new Class<?>[]{String.class,  List.class}, new Object[]{name, values}));
//		
////		myints[0] = 3;
////		
////		myobs[1] = new Object();
////		System.out.println("hello");
////		System.out.println(testfield+" "+testfield2+" "+testfield3);
////		testfield = 22;
//	}
	
	public int getVal()
	{
		String	belief	= "haha";
		return (Integer)BDIAgent.getAbstractBeliefValue(__globalname, belief, byte.class);
	}
	
	Integer	n	= new Integer(39784374);
	Character	c 	= new Character('d');
	Long	l	= new Long(247895);
	Float	f	= new Float(378156);
	Double	d	= new Double(91347);
	
	public byte getbVal()
	{
		return (byte)n.intValue();
	}

	public short getsVal()
	{
		return (short)n.intValue();
	}

	public int getiVal()
	{
		return n;
	}

	public long getlVal()
	{
		return l;
	}

	public float getfVal()
	{
		return f;
	}

	public double getdVal()
	{
		return d;
	}

	public boolean getboVal()
	{
		return false;
	}

	public char getcVal()
	{
		return c;
	}

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
