package jadex.bdiv3.android;

import org.ow2.asmdex.ApplicationVisitor;

public class LogClassWriter
{

	public static final String LOG_CLASSNAME = "Ljadex/bdiv3/android/LogClassWriter;";

	public LogClassWriter(MethodInsManager rm, ApplicationVisitor av)
	{
		// TODO Auto-generated constructor stub
	}

	public void addLogClass()
	{
		// TODO Auto-generated method stub
		
	}
	
	public static void log(Object o) {
		System.out.println(o.toString());
	}

}
