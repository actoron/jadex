package jadex.bdiv3;

import jadex.commons.SReflect;

public class MyTestClass
{
	protected int testfield;

	public MyTestClass()
	{
//		Object o = new Object();
//		SReflect.wrapValue(true);
		
//		writeField(true, "a", this);
		testfield = 25;
		System.out.println("end");
	}
	
//	public static void writeField() //throws Exception
//	{
//		System.out.println("called write field");
//	}
	
	public void writeField(Object v, String n, Object o) 
	{
		System.out.println("called write field: "+v+" "+n+" "+o);
	}
	
	public void writeField2() 
	{
		System.out.println("called write field 2");
	}
	
//	public static void main(String[] args) throws Exception
//	{
//		System.out.println("1");
//		testfield = 25;
//		System.out.println("2");
//	}
	
//	public void setField()
//	{
//		testfield = 25;
//	}
//	
//	public void setMethod() throws Exception
//	{
//		writeField(25, "testfield", this);
//	}
	
//	public void writeField(Object val, String fieldname, Object obj) throws Exception
//	{
//		BDIAgent agent = ((BDIAgent)getClass().getDeclaredField("__agent").get(this));
//		agent.writeField(val, fieldname, obj);
//	}
}
