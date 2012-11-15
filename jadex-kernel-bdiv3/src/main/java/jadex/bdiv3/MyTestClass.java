package jadex.bdiv3;


public class MyTestClass
{
	protected int testfield;

	public MyTestClass()
	{
		body();
	}
	
	public void body()
	{
		System.out.println("---------------- start ----------------");
		testfield = 25;
		System.out.println("---------------- end ----------------");
	}
	
	public void writeField(Object v, String n, Object o) 
	{
		System.out.println("called write field: "+v+" "+n+" "+o);
	}
	
//	public void writeField(Object val, String fieldname, Object obj) throws Exception
//	{
//		BDIAgent agent = ((BDIAgent)getClass().getDeclaredField("__agent").get(this));
//		agent.writeField(val, fieldname, obj);
//	}
	
//	public static void main(String[] args)
//	{
//		System.out.println("called");
//	}
}
