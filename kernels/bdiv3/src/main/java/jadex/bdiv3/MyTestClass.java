package jadex.bdiv3;

import java.lang.reflect.InvocationHandler;

public class MyTestClass extends java.util.ArrayList
{
	protected InvocationHandler handler;
	
	public MyTestClass()
	{
		Object o = new Object();
		String st = o.toString();
		System.out.println(st);
	}
	
	public MyTestClass(InvocationHandler handler)
	{
		this.handler = handler;
	}

//	public int testfield = inc();

//	@Belief(dependson="testfield")
//	protected int testfield2 = testfield+3;

//	protected int testfield3 = getVal();

//	protected int[] myints = new int[3];
//
//	protected Object[] myobs = new Object[2];
	
//	protected BDIAgent	__agent;

//	protected List<Tuple3<Class<?>,Class<?>[], Object[]>>	initcalls;
	
//	protected String	__globalname	= "huhu";
//	
//	protected BDIAgent	__agent;
	
	
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
	
//	public boolean nix()
//	{
//		Boolean b = Boolean.TRUE;
//		return b;
//	}
	
//	public void call()
//	{
////		String mname = Thread.currentThread().getStackTrace()[1].getMethodName();
//		try
//		{
//			handler.invoke(this, null, null);
//		}
//		catch(Throwable e)
//		{
//			SUtil.rethrowAsUnchecked(e);
//		}
//	}
	
	public String[] abc() 
	{
		return new String[0];
	}
	
	public void call2(String hallo, long l) 
	{
		System.out.println("Hallo: "+hallo+" "+l);
//		String mname = Thread.currentThread().getStackTrace()[1].getMethodName();
		
//		Method m = this.getClass().getMethod("call2", new Class[]{});
		
//		handler.invoke(this, m, null);
	}
	
	public int add(long a, int b) 
	{
		return (int)a+b;
	}
	
//	public static Object getNull()
//	{
//		return null;
//	}
	
//	public void setVal(double val)
//	{
//		String	belief	= "haha";
//		__agent.setAbstractBeliefValue(__globalname, belief, val);
//	}
//	
//	Integer	n	= Integer.valueOf(39784374);
//	Character	c 	= new Character('d');
//	Long	l	= new Long(247895);
//	Float	f	= new Float(378156);
//	Double	d	= new Double(91347);
//	
//	public byte getbVal()
//	{
//		return (byte)n.intValue();
//	}
//
//	public short getsVal()
//	{
//		return (short)n.intValue();
//	}
//
//	public int getiVal()
//	{
//		return n;
//	}
//
//	public long getlVal()
//	{
//		return l;
//	}
//
//	public float getfVal()
//	{
//		return f;
//	}
//
//	public double getdVal()
//	{
//		return d;
//	}
//
//	public boolean getboVal()
//	{
//		return false;
//	}
//
//	public char getcVal()
//	{
//		return c;
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
////		java -classpath "asm.jar;asm-util.jar" org.objectweb.asm.util.ASMifier org/domain/package/YourClass.class
//	}
}
